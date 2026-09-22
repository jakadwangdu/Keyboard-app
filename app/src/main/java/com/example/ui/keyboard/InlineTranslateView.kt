package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TranslationEngine
import com.example.model.KeyboardThemeType
import com.example.model.TranslationLanguage
import com.example.model.TranslationLanguages

@Composable
fun InlineTranslateView(
    activeText: String,
    theme: KeyboardThemeType,
    onApplyTranslation: (String) -> Unit,
    onSendTranslation: (String) -> Unit,
    onCloseTranslate: () -> Unit
) {
    var selectedTargetLang by remember { mutableStateOf(TranslationLanguages.supported.first()) }
    var translatedText by remember(activeText, selectedTargetLang) {
        mutableStateOf(TranslationEngine.translate(activeText, selectedTargetLang.code))
    }

    val bgColor = Color(theme.surfaceHex)
    val cardBg = Color(theme.keyCapHex)
    val accentColor = Color(theme.accentHex)
    val textColor = Color(theme.keyTextHex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(10.dp)
            .testTag("inline_translate_view"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header with Source <-> Target Languages
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(30.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.GTranslate,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Source language chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = cardBg,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "🇺🇸 English",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )

                // Target language active chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.22f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "${selectedTargetLang.flag} ${selectedTargetLang.name}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            IconButton(
                onClick = onCloseTranslate,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Translation",
                    tint = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Target language quick scroll bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TranslationLanguages.supported.filter { it.code != "en" }.forEach { lang ->
                val isSelected = lang.code == selectedTargetLang.code
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) accentColor else cardBg,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedTargetLang = lang }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = lang.flag, fontSize = 13.sp)
                        Text(
                            text = lang.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else textColor
                        )
                    }
                }
            }
        }

        // Live Translation Preview Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = cardBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Translation Output",
                        fontSize = 10.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (translatedText.isNotEmpty()) translatedText else "Type in keyboard to preview real-time translation...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (translatedText.isNotEmpty()) textColor else textColor.copy(alpha = 0.5f),
                        lineHeight = 16.sp
                    )
                }

                if (translatedText.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { onApplyTranslation(translatedText) },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Replace", fontSize = 11.sp, color = accentColor, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onSendTranslation(translatedText) },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
