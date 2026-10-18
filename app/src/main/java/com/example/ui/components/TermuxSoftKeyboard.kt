package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermuxSoftKeyboard(
    onCharTyped: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isShifted by remember { mutableStateOf(false) }

    val keyBg = Color(0xFF0D1219)
    val keyBorder = Color(0xFF1E2836)
    val textCyan = Color(0xFF40C4FF)
    val symbolMuted = Color(0xFF6B7D96)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF000000))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
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
                    modifier = Modifier.weight(1f),
                    onClick = { onCharTyped(num) }
                )
            }
        }

        // Row 2: Q W E R T Y U I O P with secondary symbols % ^ ~ | [ ] < > { }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val letters = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
            val syms = listOf("%", "^", "~", "|", "[", "]", "<", ">", "{", "}")
            for (i in letters.indices) {
                val char = if (isShifted) letters[i].uppercase() else letters[i]
                KbKey(
                    label = char,
                    subLabel = syms[i],
                    textColor = textCyan,
                    bgColor = keyBg,
                    borderColor = keyBorder,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCharTyped(char)
                        if (isShifted) isShifted = false
                    }
                )
            }
        }

        // Row 3: A S D F G H J K L with secondary symbols @ # & * - + = ( )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val letters = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
            val syms = listOf("@", "#", "&", "*", "-", "+", "=", "(", ")")
            for (i in letters.indices) {
                val char = if (isShifted) letters[i].uppercase() else letters[i]
                KbKey(
                    label = char,
                    subLabel = syms[i],
                    textColor = textCyan,
                    bgColor = keyBg,
                    borderColor = keyBorder,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCharTyped(char)
                        if (isShifted) isShifted = false
                    }
                )
            }
        }

        // Row 4: Shift, Z X C V B N M with symbols ~ $ " ' : ; /, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Shift
            KbKey(
                label = "⇧",
                subLabel = null,
                textColor = if (isShifted) Color.White else textCyan,
                bgColor = if (isShifted) Color(0xFF1E3A5F) else keyBg,
                borderColor = if (isShifted) textCyan else keyBorder,
                modifier = Modifier.weight(1.3f),
                onClick = { isShifted = !isShifted }
            )

            val letters = listOf("z", "x", "c", "v", "b", "n", "m")
            val syms = listOf("~", "$", "\"", "'", ":", ";", "/")
            for (i in letters.indices) {
                val char = if (isShifted) letters[i].uppercase() else letters[i]
                KbKey(
                    label = char,
                    subLabel = syms[i],
                    textColor = textCyan,
                    bgColor = keyBg,
                    borderColor = keyBorder,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCharTyped(char)
                        if (isShifted) isShifted = false
                    }
                )
            }

            // Backspace
            KbKey(
                label = "⌫",
                subLabel = null,
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
                modifier = Modifier.weight(1.3f),
                onClick = onBackspace
            )
        }

        // Row 5: 123, Emoji, comma, spacebar, dot/symbols, Enter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KbKey(
                label = "123",
                subLabel = null,
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
                modifier = Modifier.weight(1.2f),
                onClick = { onCharTyped(";") }
            )

            KbKey(
                label = "☺",
                subLabel = null,
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
                modifier = Modifier.weight(1f),
                onClick = { onCharTyped(":") }
            )

            KbKey(
                label = ",",
                subLabel = null,
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
                modifier = Modifier.weight(1f),
                onClick = { onCharTyped(",") }
            )

            // Spacebar with neon cyan glow underline (Pixel Clone)
            Box(
                modifier = Modifier
                    .weight(4.5f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(keyBg)
                    .border(1.dp, keyBorder, RoundedCornerShape(6.dp))
                    .clickable { onCharTyped(" ") }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
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
                subLabel = "!? ",
                textColor = textCyan,
                bgColor = keyBg,
                borderColor = keyBorder,
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
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
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 9.sp
                )
            }
            Text(
                text = label,
                color = textColor,
                fontSize = if (label.length > 2) 11.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                lineHeight = 15.sp
            )
        }
    }
}
