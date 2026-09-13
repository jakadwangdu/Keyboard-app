package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icons.AppIcon
import com.example.icons.AppIconGlyph
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun RowScope.MechanicalKey(
    primaryText: String? = null,
    secondaryText: String? = null,
    iconGlyph: AppIconGlyph? = null,
    iconPackType: IconPackType = IconPackType.WHATSAPP_EXPRESSIVE,
    theme: KeyboardThemeType = KeyboardThemeType.WHATSAPP_DARK,
    isAccent: Boolean = false,
    isAlt: Boolean = false,
    weight: Float = 1f,
    height: Dp = 48.dp,
    testTagId: String? = null,
    isRepeatable: Boolean = false,
    onKeyTriggered: () -> Unit = {},
    onLongPress: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val currentOnKeyTriggered by rememberUpdatedState(onKeyTriggered)
    val currentOnLongPress by rememberUpdatedState(onLongPress)
    val coroutineScope = rememberCoroutineScope()

    // Physical key travel physics animation
    val travelOffset by animateFloatAsState(
        targetValue = if (isPressed) 3.2f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 950f),
        label = "mech_key_travel"
    )

    val keyCapColor = when {
        isAccent -> Color(theme.accentHex)
        isAlt -> Color(theme.keyCapAltHex)
        else -> Color(theme.keyCapHex)
    }

    val textColor = when {
        isAccent -> if (theme.isDark && theme != KeyboardThemeType.CYBERPUNK_MECH) Color(0xFF0B141A) else Color.White
        else -> Color(theme.keyTextHex)
    }

    val bevelShadowColor = if (theme.isDark) Color(0x66000000) else Color(0x2B000000)
    val isRetro95 = theme == KeyboardThemeType.RETRO_95_PIXEL
    val cornerRadius = when (theme) {
        KeyboardThemeType.AMOLED_BLACK -> 9.dp
        KeyboardThemeType.RETRO_95_PIXEL -> 2.dp
        KeyboardThemeType.WHATSAPP_DARK, KeyboardThemeType.WHATSAPP_LIGHT -> 10.dp
        KeyboardThemeType.ANDROID_17_PILL -> 14.dp
        KeyboardThemeType.IOS_FROSTED_DARK -> 7.dp
        KeyboardThemeType.CYBERPUNK_MECH -> 5.dp
        KeyboardThemeType.RETRO_MODEL_M -> 4.dp
    }

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .then(if (testTagId != null) Modifier.testTag(testTagId) else Modifier)
            .pointerInput(isRepeatable) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    currentOnKeyTriggered()

                    if (isRepeatable) {
                        val repeatJob = coroutineScope.launch {
                            delay(350L) // Initial hold threshold
                            while (isActive) {
                                currentOnKeyTriggered()
                                delay(45L) // Rapid continuous deletion interval
                            }
                        }
                        waitForUpOrCancellation()
                        repeatJob.cancel()
                    } else {
                        waitForUpOrCancellation()
                    }
                    isPressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 1. Switch Base Under-Bed (Mechanical housing base)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = if (isRetro95) 1.5.dp else 2.8.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(bevelShadowColor)
        )

        // 2. 3D Keycap Top Surface with physical press travel offset
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = travelOffset.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    if (theme == KeyboardThemeType.CYBERPUNK_MECH && isAccent) {
                        Brush.linearGradient(listOf(Color(theme.accentHex), Color(0xFFFF0080)))
                    } else if (isRetro95) {
                        Brush.verticalGradient(
                            listOf(
                                if (isPressed) Color(0xFFC0BCB4) else Color(0xFFE8E5DD),
                                if (isPressed) Color(0xFFB4B0A8) else Color(0xFFD4D0C8)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                keyCapColor.copy(alpha = 1f),
                                keyCapColor.copy(alpha = if (theme.isDark) 0.88f else 0.96f)
                            )
                        )
                    }
                )
                .then(
                    if (isRetro95) {
                        Modifier.border(
                            width = 1.5.dp,
                            color = if (isPressed) Color(0xFF404040) else Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                    } else if (theme == KeyboardThemeType.AMOLED_BLACK) {
                        Modifier.border(
                            width = 1.dp,
                            color = if (isAccent) Color(theme.accentHex).copy(alpha = 0.6f) else if (isPressed) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFF28303C),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                    } else if (theme == KeyboardThemeType.CYBERPUNK_MECH) {
                        Modifier.border(
                            width = 1.dp,
                            color = if (isAccent) Color(theme.accentHex) else Color(theme.keyTextHex).copy(alpha = 0.35f),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                    } else if (theme == KeyboardThemeType.IOS_FROSTED_DARK) {
                        Modifier.border(
                            width = 0.5.dp,
                            color = Color(0x33FFFFFF),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                // Top-right superscript number hint
                if (secondaryText != null) {
                    Text(
                        text = secondaryText,
                        fontSize = if (isRetro95) 9.sp else 8.5.sp,
                        fontWeight = if (isRetro95) FontWeight.Bold else FontWeight.Normal,
                        color = if (isRetro95) Color(0xFF606060) else textColor.copy(alpha = 0.45f),
                        fontFamily = if (isRetro95) FontFamily.Monospace else FontFamily.SansSerif,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 2.dp, top = 1.dp)
                    )
                }

                // Center Main Label / Icon
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconGlyph != null) {
                        AppIcon(
                            glyph = iconGlyph,
                            packType = iconPackType,
                            tint = textColor,
                            size = if (height >= 52.dp) 20.dp else 18.dp
                        )
                    } else if (primaryText != null) {
                        Text(
                            text = primaryText,
                            fontSize = if (primaryText.length > 5) 11.sp else if (primaryText.length > 2) 13.sp else 17.sp,
                            fontWeight = if (isAccent || isRetro95) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            fontFamily = if (isRetro95) FontFamily.Monospace else FontFamily.SansSerif,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
