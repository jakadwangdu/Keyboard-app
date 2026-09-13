package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.KeyboardThemeType
import kotlinx.coroutines.delay

@Composable
fun DefaultKeyboardSetupDialog(
    theme: KeyboardThemeType,
    isImeEnabled: Boolean,
    isImeSelected: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isEnabledState by remember { mutableStateOf(isImeEnabled) }
    var isSelectedState by remember { mutableStateOf(isImeSelected) }
    var testText by remember { mutableStateOf("") }

    // Periodic check for IME status
    LaunchedEffect(Unit) {
        while (true) {
            try {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                val enabledMethods = imm?.enabledInputMethodList ?: emptyList()
                isEnabledState = enabledMethods.any { it.packageName == context.packageName }

                val currentIme = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD
                )
                isSelectedState = currentIme?.contains(context.packageName) == true
            } catch (_: Exception) {}
            delay(1000)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF12161E),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3340)),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .testTag("default_keyboard_setup_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color(0xFF00E676).copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, Color(0xFF00E676), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Keyboard Setup",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Default Keyboard Setup",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Follow the 3 quick steps below to activate and launch MechBoard across all your apps.",
                    fontSize = 12.sp,
                    color = Color(0xFF9EABB8),
                    lineHeight = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step 1: Enable in Settings
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B222C)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isEnabledState) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFF2E394A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isEnabledState) Icons.Default.CheckCircle else Icons.Default.Settings,
                            contentDescription = null,
                            tint = if (isEnabledState) Color(0xFF00E676) else Color(0xFF8899A6),
                            modifier = Modifier.size(22.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Step 1: Enable MechBoard",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = if (isEnabledState) "Active in system settings ✓" else "Turn switch ON in keyboard list",
                                fontSize = 11.sp,
                                color = if (isEnabledState) Color(0xFF00E676) else Color(0xFF8899A6)
                            )
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEnabledState) Color(0xFF263238) else Color(0xFF00E676)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("enable_keyboard_btn")
                        ) {
                            Text(
                                text = if (isEnabledState) "Enabled ✓" else "1. Enable",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEnabledState) Color(0xFF00E676) else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Step 2: Select as Input Method
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B222C)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelectedState) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFF2E394A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelectedState) Icons.Default.CheckCircle else Icons.Default.Tune,
                            contentDescription = null,
                            tint = if (isSelectedState) Color(0xFF00E676) else Color(0xFF8899A6),
                            modifier = Modifier.size(22.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Step 2: Select MechBoard",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = if (isSelectedState) "Selected as active keyboard ✓" else "Tap Select and pick MechBoard",
                                fontSize = 11.sp,
                                color = if (isSelectedState) Color(0xFF00E676) else Color(0xFF8899A6)
                            )
                        }

                        Button(
                            onClick = {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelectedState) Color(0xFF263238) else Color(0xFF00E676)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("select_keyboard_btn")
                        ) {
                            Text(
                                text = if (isSelectedState) "Selected ✓" else "2. Select",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelectedState) Color(0xFF00E676) else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Step 3: Launch / Test Keyboard
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B222C)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Step 3: Launch & Test Typing",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        OutlinedTextField(
                            value = testText,
                            onValueChange = { testText = it },
                            placeholder = { Text("Tap here to pop up keyboard...", fontSize = 12.sp, color = Color(0xFF8899A6)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00E676),
                                unfocusedBorderColor = Color(0xFF2E394A),
                                focusedContainerColor = Color(0xFF12161E),
                                unfocusedContainerColor = Color(0xFF12161E)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .testTag("test_ime_input_field"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                        )

                        Button(
                            onClick = {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "⌨️ Pop Up / Launch Keyboard Now",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dismiss_setup_dialog_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A3340)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Close Setup",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
