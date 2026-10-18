package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyItem
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalKeyBg
import com.example.ui.theme.TerminalKeyBorder
import com.example.ui.theme.TerminalKeyModifier
import com.example.ui.theme.TerminalKeyModifierBorder
import com.example.ui.theme.TerminalKeyPressed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun KeyCapButton(
    item: KeyItem,
    onClick: () -> Unit,
    onPopupTriggered: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isEditMode: Boolean = false,
    isCompact: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val isModifier = item.key in listOf("CTRL", "ALT", "ESC", "TAB")
    val hasPopup = item.popup != null && item.popup.key.isNotBlank()

    val bgColor = when {
        isSelected -> MatrixGreenBright.copy(alpha = 0.25f)
        isPressed -> TerminalKeyPressed
        isModifier -> TerminalKeyModifier
        else -> TerminalKeyBg
    }

    val borderColor = when {
        isSelected -> MatrixGreenBright
        isModifier -> TerminalKeyModifierBorder
        else -> TerminalKeyBorder
    }

    val textColor = when {
        isSelected -> MatrixGreenBright
        isModifier -> CyberCyan
        item.key == "ENTER" -> MatrixGreenBright
        item.key in listOf("UP", "DOWN", "LEFT", "RIGHT") -> MatrixGreenBright
        else -> TextPrimary
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = if (isCompact) 40.dp else 46.dp, minHeight = if (isCompact) 42.dp else 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragStart = { isPressed = true },
                    onDragEnd = { isPressed = false },
                    onDragCancel = { isPressed = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Detect swipe-up gesture for popup action
                        if (dragAmount.y < -25f && hasPopup && onPopupTriggered != null) {
                            onPopupTriggered()
                            isPressed = false
                        }
                    }
                )
            }
            .clickable {
                onClick()
            }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("keycap_${item.key}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Popup indicator badge if configured
            if (hasPopup) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(AmberAccent, CircleShape)
                    )
                }
            }

            Text(
                text = item.effectiveDisplay,
                color = textColor,
                fontSize = if (item.effectiveDisplay.length > 5) 10.sp else if (isCompact) 12.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            if (hasPopup && !isCompact && item.popup != null) {
                Text(
                    text = "^${item.popup.effectiveDisplay}",
                    color = AmberAccent,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
