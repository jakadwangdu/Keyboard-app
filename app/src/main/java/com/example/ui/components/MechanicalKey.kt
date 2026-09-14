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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

    // 3D Physical key travel physics animation
    val travelOffset by animateFloatAsState(
        targetValue = if (isPressed) 3.5f else 0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 1100f),
        label = "mech_3d_key_travel"
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

    val isRetro95 = theme == KeyboardThemeType.RETRO_95_PIXEL
    val cornerRadius = when (theme) {
        KeyboardThemeType.MINIMAL_DARK, KeyboardThemeType.MINIMAL_LIGHT -> 8.dp
        KeyboardThemeType.AMOLED_BLACK -> 9.dp
        KeyboardThemeType.RETRO_95_PIXEL -> 2.dp
        KeyboardThemeType.WHATSAPP_DARK, KeyboardThemeType.WHATSAPP_LIGHT -> 10.dp
        KeyboardThemeType.ANDROID_17_PILL -> 14.dp
        KeyboardThemeType.IOS_FROSTED_DARK -> 7.dp
        KeyboardThemeType.CYBERPUNK_MECH -> 6.dp
        KeyboardThemeType.RETRO_MODEL_M -> 4.dp
    }

    // 3D Lighting & Bevel Colors
    val basePlateShadowColor = if (theme.isDark) Color(0x99000000) else Color(0x38000000)
    val bottomSkirtColor = if (theme.isDark) {
        keyCapColor.copy(alpha = 0.55f)
    } else {
        keyCapColor.copy(alpha = 0.75f)
    }

    val topRimHighlight = if (theme.isDark) Color(0x2EFFFFFF) else Color(0x40FFFFFF)
    val dishShadow = if (theme.isDark) Color(0x24000000) else Color(0x14000000)

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .padding(horizontal = 2.5.dp, vertical = 2.dp)
            .then(if (testTagId != null) Modifier.testTag(testTagId) else Modifier)
            .pointerInput(isRepeatable) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    currentOnKeyTriggered()

                    if (isRepeatable) {
                        val repeatJob = coroutineScope.launch {
                            delay(350L) // Initial hold threshold
                            while (isActive) {
                                currentOnKeyTriggered()
                                delay(45L) // Continuous fast deletion
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
        val isMinimal = theme == KeyboardThemeType.MINIMAL_DARK || theme == KeyboardThemeType.MINIMAL_LIGHT

        // 1. Layer 0: Deep Plate Cavity & Drop Shadow
        if (!isMinimal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = if (isRetro95) 2.dp else 3.8.dp)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(basePlateShadowColor)
            )
        }

        // 2. Layer 1: 3D Keycap Skirt (Lower Bevel Housing)
        if (!isMinimal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = if (isRetro95) 1.2.dp else 2.4.dp)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(
                        if (isRetro95) {
                            if (isPressed) Color(0xFF808080) else Color(0xFF404040)
                        } else {
                            bottomSkirtColor
                        }
                    )
            )
        }

        // 3. Layer 2: 3D Sculpted Keycap Top Surface with Travel Offset
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = travelOffset.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .then(
                    if (isMinimal) {
                        Modifier.background(if (isPressed) keyCapColor.copy(alpha = 0.8f) else keyCapColor)
                    } else if (theme == KeyboardThemeType.CYBERPUNK_MECH && isAccent) {
                        Modifier.background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFF0055),
                                    Color(0xFF80002A)
                                )
                            )
                        )
                    } else if (isRetro95) {
                        Modifier.background(
                            Brush.verticalGradient(
                                listOf(
                                    if (isPressed) Color(0xFFB4B0A8) else Color(0xFFF0ECE4),
                                    if (isPressed) Color(0xFF9E9A92) else Color(0xFFD4D0C8)
                                )
                            )
                        )
                    } else {
                        // Authentic 3D mechanical keycap gradient with top specular shine & dish curve
                        Modifier.background(
                            Brush.verticalGradient(
                                0.0f to keyCapColor.copy(alpha = 1f),
                                0.15f to keyCapColor,
                                0.75f to (if (theme.isDark) keyCapColor.copy(alpha = 0.92f) else keyCapColor.copy(alpha = 0.95f)),
                                1.0f to (if (theme.isDark) Color(0xFF0A0C0E) else Color(0xFFB0B8C0))
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
                    } else if (isMinimal) {
                        Modifier
                    } else if (theme == KeyboardThemeType.AMOLED_BLACK) {
                        Modifier.border(
                            width = 1.dp,
                            color = if (isAccent) Color(theme.accentHex).copy(alpha = 0.7f) else if (isPressed) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFF2A323D),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                    } else if (theme == KeyboardThemeType.CYBERPUNK_MECH) {
                        Modifier.border(
                            width = 1.dp,
                            color = if (isAccent) Color(theme.accentHex) else Color(theme.keyTextHex).copy(alpha = 0.4f),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                    } else {
                        Modifier.border(
                            width = 0.8.dp,
                            color = if (isPressed) Color(theme.accentHex).copy(alpha = 0.5f) else topRimHighlight,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // 4. Subtle Concave Cylindrical Dish Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(cornerRadius - 2.dp))
                    .then(
                        if (isMinimal) {
                            Modifier.background(Color.Transparent)
                        } else {
                            Modifier.background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        dishShadow
                                    )
                                )
                            )
                        }
                    )
            ) {
                // Top-right superscript number hint
                if (secondaryText != null) {
                    Text(
                        text = secondaryText,
                        fontSize = if (isRetro95) 9.sp else 8.5.sp,
                        fontWeight = if (isRetro95) FontWeight.Bold else FontWeight.Medium,
                        color = if (isRetro95) Color(0xFF606060) else textColor.copy(alpha = 0.42f),
                        fontFamily = if (isRetro95) FontFamily.Monospace else FontFamily.SansSerif,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 3.dp, top = 1.dp)
                    )
                }

                // Center Main Mechanical Key Legend / Icon
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
                            fontSize = if (primaryText.length > 5) 11.sp else if (primaryText.length > 2) 13.sp else 17.5.sp,
                            fontWeight = if (isAccent || isRetro95) FontWeight.Bold else FontWeight.SemiBold,
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
