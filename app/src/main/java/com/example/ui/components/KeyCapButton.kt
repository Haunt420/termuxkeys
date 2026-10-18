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

@Composable
fun KeyCapButton(
    item: KeyItem,
    onClick: () -> Unit,
    onPopupTriggered: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isEditMode: Boolean = false,
    isCompact: Boolean = false,
    isTermuxStyle: Boolean = true
) {
    var isPressed by remember { mutableStateOf(false) }
    val hasPopup = item.popup != null && item.popup.key.isNotBlank()

    val bgColor = when {
        isSelected -> MatrixGreenBright.copy(alpha = 0.28f)
        isPressed -> Color(0xFF262626)
        isTermuxStyle -> Color(0xFF000000)
        else -> Color(0xFF161B22)
    }

    val borderColor = when {
        isSelected -> MatrixGreenBright
        isPressed -> Color(0xFF555555)
        isTermuxStyle -> Color(0xFF1E1E1E)
        else -> Color(0xFF30363D)
    }

    val textColor = when {
        isSelected -> MatrixGreenBright
        else -> Color.White
    }

    val isMultiline = item.effectiveDisplay.contains("\n")

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = if (isCompact) 36.dp else 42.dp, minHeight = if (isCompact) 34.dp else 40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragStart = { isPressed = true },
                    onDragEnd = { isPressed = false },
                    onDragCancel = { isPressed = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount.y < -20f && hasPopup && onPopupTriggered != null) {
                            onPopupTriggered()
                            isPressed = false
                        }
                    }
                )
            }
            .clickable {
                onClick()
            }
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .testTag("keycap_${item.key}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (hasPopup && !isTermuxStyle) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(AmberAccent, CircleShape)
                    )
                }
            }

            Text(
                text = item.effectiveDisplay,
                color = textColor,
                fontSize = if (isMultiline) 9.sp else if (item.effectiveDisplay.length > 5) 10.sp else 12.sp,
                lineHeight = if (isMultiline) 11.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                maxLines = if (isMultiline) 2 else 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            if (hasPopup && isEditMode && !isCompact && item.popup != null) {
                Text(
                    text = "^${item.popup.effectiveDisplay}",
                    color = AmberAccent,
                    fontSize = 7.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
