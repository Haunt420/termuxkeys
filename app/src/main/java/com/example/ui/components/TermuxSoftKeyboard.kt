package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermuxSoftKeyboard(
    onCharTyped: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    var isShifted by remember { mutableStateOf(false) }
    var isSymbolMode by remember { mutableStateOf(false) }

    val keyBg = Color(0xFF0D1219)
    val keyBorder = Color(0xFF1E2836)
    val textCyan = Color(0xFF40C4FF)

    val keyHeight = if (isCompact) 26.dp else 34.dp
    val rowSpacing = if (isCompact) 2.dp else 3.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF000000))
            .padding(horizontal = 4.dp, vertical = if (isCompact) 2.dp else 4.dp),
        verticalArrangement = Arrangement.spacedBy(rowSpacing)
    ) {
        // Row 1: Number Row (1 2 3 4 5 6 7 8 9 0)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            for (num in numbers) {
                KbKey(
                    label = num,
                    subLabel = null,
                    textColor = textCyan,
                    bgColor = keyBg,
                    borderColor = keyBorder,
                    keyHeight = keyHeight,
                    isCompact = isCompact,
                    modifier = Modifier.weight(1f),
                    onClick = { onCharTyped(num) }
                )
            }
        }

        // Row 2: Q..P or Symbols
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val letters = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
            val syms = listOf("%", "^", "~", "|", "[", "]", "<", ">", "{", "}")
            for (i in letters.indices) {
                val char = if (isSymbolMode) syms[i] else if (isShifted) letters[i].uppercase() else letters[i]
                KbKey(
                    label = char,
                    subLabel = if (isSymbolMode || isCompact) null else syms[i],
                    textColor = textCyan,
                    bgColor = keyBg,
                    borderColor = keyBorder,
                    keyHeight = keyHeight,
                    isCompact = isCompact,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCharTyped(char)
                        if (isShifted && !isSymbolMode) isShifted = false
                    }
                )
            }
        }

        // Row 3: A..L or Symbols
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = if (isCompact) 6.dp else 10.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val letters = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
            val syms = listOf("@", "#", "&", "*", "-", "+", "=", "(", ")")
            for (i in letters.indices) {
                val char = if (isSymbolMode) syms[i] else if (isShifted) letters[i].uppercase() else letters[i]
                KbKey(
                    label = char,
                    subLabel = if (isSymbolMode || isCompact) null else syms[i],
                    textColor = textCyan,
                    bgColor = keyBg,
                    borderColor = keyBorder,
                    keyHeight = keyHeight,
                    isCompact = isCompact,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCharTyped(char)
                        if (isShifted && !isSymbolMode) isShifted = false
                    }
                )
            }
        }

        // Row 4: Shift, Z..M or Symbols, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            KbKey(
                label = if (isSymbolMode) "#=" else "⇧",
                subLabel = null,
                textColor = if (isShifted) Color.White else textCyan,
                bgColor = if (isShifted) Color(0xFF1E3A5F) else keyBg,
                borderColor = if (isShifted) textCyan else keyBorder,
                keyHeight = keyHeight,
                isCompact = isCompact,
                modifier = Modifier.weight(1.3f),
                onClick = { if (!isSymbolMode) isShifted = !isShifted }
            )

            val letters = listOf("z", "x", "c", "v", "b", "n", "m")
            val syms = listOf("~", "$", "\"", "'", ":", ";", "/")
            for (i in letters.indices) {
                val char = if (isSymbolMode) syms[i] else if (isShifted) letters[i].uppercase() else letters[i]
                KbKey(
                    label = char,
                    subLabel = if (isSymbolMode || isCompact) null else syms[i],
                    textColor = textCyan,
                    bgColor = keyBg,
                    borderColor = keyBorder,
                    keyHeight = keyHeight,
                    isCompact = isCompact,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCharTyped(char)
                        if (isShifted && !isSymbolMode) isShifted = false
                    }
                )
            }

            KbKey(
                label = "⌫",
                subLabel = null,
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
                keyHeight = keyHeight,
                isCompact = isCompact,
                modifier = Modifier.weight(1.3f),
                onClick = onBackspace
            )
        }

        // Row 5: 123/ABC, /, comma, spacebar, dot, Enter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KbKey(
                label = if (isSymbolMode) "ABC" else "123",
                subLabel = null,
                textColor = if (isSymbolMode) Color.White else textCyan,
                bgColor = if (isSymbolMode) Color(0xFF1E3A5F) else keyBg,
                borderColor = if (isSymbolMode) textCyan else keyBorder,
                keyHeight = keyHeight,
                isCompact = isCompact,
                modifier = Modifier.weight(1.25f),
                onClick = { isSymbolMode = !isSymbolMode }
            )

            KbKey(
                label = "/",
                subLabel = null,
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
                keyHeight = keyHeight,
                isCompact = isCompact,
                modifier = Modifier.weight(1f),
                onClick = { onCharTyped("/") }
            )

            KbKey(
                label = "-",
                subLabel = null,
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
                keyHeight = keyHeight,
                isCompact = isCompact,
                modifier = Modifier.weight(1f),
                onClick = { onCharTyped("-") }
            )

            // Spacebar with neon cyan glow underline
            Box(
                modifier = Modifier
                    .weight(4.4f)
                    .height(keyHeight)
                    .clip(RoundedCornerShape(6.dp))
                    .background(keyBg)
                    .border(1.dp, keyBorder, RoundedCornerShape(6.dp))
                    .clickable { onCharTyped(" ") }
                    .padding(horizontal = 8.dp, vertical = if (isCompact) 4.dp else 6.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(2.dp)
                        .background(textCyan, RoundedCornerShape(1.dp))
                )
            }

            KbKey(
                label = ".",
                subLabel = if (isCompact) null else "!?",
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
                keyHeight = keyHeight,
                isCompact = isCompact,
                modifier = Modifier.weight(1f),
                onClick = { onCharTyped(".") }
            )

            // Enter key
            KbKey(
                label = "↵",
                subLabel = null,
                textColor = Color.White,
                bgColor = Color(0xFF132238),
                borderColor = textCyan.copy(alpha = 0.5f),
                keyHeight = keyHeight,
                isCompact = isCompact,
                modifier = Modifier.weight(1.4f),
                onClick = onEnter
            )
        }
    }
}

@Composable
private fun KbKey(
    label: String,
    subLabel: String?,
    textColor: Color,
    bgColor: Color,
    borderColor: Color,
    keyHeight: Dp,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(keyHeight)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 2.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!subLabel.isNullOrBlank()) {
                Text(
                    text = subLabel,
                    color = Color(0xFF6B7D96),
                    fontSize = 7.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 8.sp
                )
            }
            Text(
                text = label,
                color = textColor,
                fontSize = when {
                    isCompact && label.length > 2 -> 9.sp
                    isCompact -> 12.sp
                    label.length > 2 -> 10.sp
                    else -> 13.sp
                },
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                lineHeight = if (isCompact) 12.sp else 14.sp
            )
        }
    }
}
