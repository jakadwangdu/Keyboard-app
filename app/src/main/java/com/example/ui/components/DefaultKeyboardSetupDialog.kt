package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.KeyboardThemeType

@Composable
fun DefaultKeyboardSetupDialog(
    theme: KeyboardThemeType,
    isImeEnabled: Boolean,
    isImeSelected: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isEnabledState by remember { mutableStateOf(isImeEnabled) }
    var isSelectedState by remember { mutableStateOf(isImeSelected) }

    // Re-check status on resume/composition
    LaunchedEffect(Unit) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val enabledMethods = imm?.enabledInputMethodList ?: emptyList()
        isEnabledState = enabledMethods.any { it.packageName == context.packageName }

        val currentIme = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        isSelectedState = currentIme?.contains(context.packageName) == true
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
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("default_keyboard_setup_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF00E676).copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, Color(0xFF00E676), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Keyboard Setup",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Set as Default Keyboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Use this Mechanical Keyboard with tactile clicks and custom themes everywhere across your system (WhatsApp, Chrome, Notes, Instagram).",
                    fontSize = 13.sp,
                    color = Color(0xFF9EABB8),
                    lineHeight = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Step 1: Enable in Settings
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = if (isEnabledState) Icons.Default.CheckCircle else Icons.Default.Settings,
                            contentDescription = null,
                            tint = if (isEnabledState) Color(0xFF00E676) else Color(0xFF8899A6),
                            modifier = Modifier.size(26.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Step 1: Enable in Settings",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = if (isEnabledState) "Active in system keyboards" else "Turn on MechBoard switch",
                                fontSize = 12.sp,
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
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("enable_keyboard_btn")
                        ) {
                            Text(
                                text = if (isEnabledState) "Enabled ✓" else "Enable",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEnabledState) Color(0xFF00E676) else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Step 2: Select as Input Method
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelectedState) Icons.Default.CheckCircle else Icons.Default.Tune,
                            contentDescription = null,
                            tint = if (isSelectedState) Color(0xFF00E676) else Color(0xFF8899A6),
                            modifier = Modifier.size(26.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Step 2: Switch Input Method",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = if (isSelectedState) "Selected as default keyboard" else "Choose MechBoard as active",
                                fontSize = 12.sp,
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
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("select_keyboard_btn")
                        ) {
                            Text(
                                text = if (isSelectedState) "Active ✓" else "Select",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelectedState) Color(0xFF00E676) else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_setup_dialog_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isSelectedState) "Done • Ready to Type" else "Done",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
