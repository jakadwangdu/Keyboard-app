package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType
import com.example.ui.theme.AccentColor
import com.example.ui.theme.BackgroundTheme
import com.example.ui.theme.KeySizeOption
import com.example.ui.theme.KeymoldThemeTokens

/**
 * Keymold Theme & Icon Pack Customization Sheet (UI1.md spec).
 * Allows user-selectable Background Theme (AMOLED true black by default, Dim, Light),
 * user-selectable Accents (Amber, Blue, Green, Rose, Violet, White),
 * Key Size (Compact, Fitted, Roomy), and Conversation Icon Packs.
 */
@Composable
fun ThemePickerSheet(
    currentIconPack: IconPackType,
    currentTheme: KeyboardThemeType,
    tokens: KeymoldThemeTokens = KeymoldThemeTokens(),
    onSelectIconPack: (IconPackType) -> Unit,
    onSelectTheme: (KeyboardThemeType) -> Unit,
    onSelectBackgroundTheme: ((BackgroundTheme) -> Unit)? = null,
    onSelectAccentColor: ((AccentColor) -> Unit)? = null,
    onSelectKeySize: ((KeySizeOption) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = tokens.screen),
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.hairline),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(vertical = 12.dp)
                .testTag("theme_picker_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(tokens.accent.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Themes",
                                tint = tokens.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Design & Themes",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.text
                            )
                            Text(
                                text = "Minimal, quiet & classy (UI1.md)",
                                fontSize = 11.sp,
                                color = tokens.muted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(tokens.keySecondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = tokens.text,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = tokens.hairline)

                // 1. Background Theme (UI1.md spec: AMOLED default, Dim, Light)
                Text(
                    text = "BACKGROUND PALETTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.muted,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BackgroundTheme.values().forEach { bgTheme ->
                        val isSelected = tokens.backgroundTheme == bgTheme
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) tokens.accent.copy(alpha = 0.18f) else tokens.key,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 0.5.dp,
                                if (isSelected) tokens.accent else tokens.hairline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectBackgroundTheme?.invoke(bgTheme)
                                    when (bgTheme) {
                                        BackgroundTheme.AMOLED -> onSelectTheme(KeyboardThemeType.AMOLED_BLACK)
                                        BackgroundTheme.DIM -> onSelectTheme(KeyboardThemeType.KEYMOLD_DIM)
                                        BackgroundTheme.LIGHT -> onSelectTheme(KeyboardThemeType.KEYMOLD_LIGHT)
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(bgTheme.bgHex))
                                        .border(1.dp, tokens.muted.copy(alpha = 0.4f), CircleShape)
                                )
                                Text(
                                    text = bgTheme.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) tokens.accent else tokens.text
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 2. Accent Color (UI1.md spec: Amber default, Blue, Green, Rose, Violet, White)
                Text(
                    text = "ACCENT COLOR (ENTER KEY & HIGHLIGHTS)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.muted,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccentColor.values().forEach { acc ->
                        val isSelected = tokens.accentColor == acc
                        val color = Color(acc.accentHex)

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) color else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onSelectAccentColor?.invoke(acc) },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(0.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 3. Key Size Options (Compact 48, Fitted 54, Roomy 60)
                Text(
                    text = "KEY HEIGHT & SPACING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.muted,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeySizeOption.values().forEach { opt ->
                        val isSelected = tokens.keySizeOption == opt
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) tokens.accent.copy(alpha = 0.18f) else tokens.key,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 0.5.dp,
                                if (isSelected) tokens.accent else tokens.hairline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectKeySize?.invoke(opt) }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = opt.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) tokens.accent else tokens.text
                                )
                                Text(
                                    text = "${opt.heightDp} dp",
                                    fontSize = 10.sp,
                                    color = tokens.muted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 4. Conversation Icon Packs Section
                Text(
                    text = "PRELOADED CONVERSATION ICON PACKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.muted,
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (pack in IconPackType.values()) {
                        val isSelected = pack == currentIconPack
                        val packAccent = Color(pack.brandColorHex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) packAccent.copy(alpha = 0.15f) else tokens.key)
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) packAccent else tokens.hairline,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectIconPack(pack) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(packAccent)
                                )
                                Column {
                                    Text(
                                        text = pack.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.text
                                    )
                                    Text(
                                        text = pack.subtitle,
                                        fontSize = 10.5.sp,
                                        color = tokens.muted
                                    )
                                }
                            }

                            if (isSelected) {
                                Text(
                                    text = "Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = packAccent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 5. Classic Styles
                Text(
                    text = "ADDITIONAL THEME PRESETS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.muted,
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (thm in KeyboardThemeType.values()) {
                        val isSelected = thm == currentTheme
                        val themeAccent = Color(thm.accentHex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) themeAccent.copy(alpha = 0.15f) else tokens.key)
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) themeAccent else tokens.hairline,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectTheme(thm) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(thm.backgroundHex))
                                            .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(thm.keyCapHex))
                                            .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(themeAccent)
                                    )
                                }

                                Text(
                                    text = thm.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = tokens.text
                                )
                            }

                            if (isSelected) {
                                Text(
                                    text = "Applied",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
