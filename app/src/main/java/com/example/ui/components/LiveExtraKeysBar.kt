package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyItem
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.TextMuted

@Composable
fun LiveExtraKeysBar(
    rows: List<List<KeyItem>>,
    onKeyTapped: (KeyItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    isArrangeMode: Boolean = false,
    onMoveKey: ((fromRow: Int, fromCol: Int, toRow: Int, insertIndex: Int) -> Unit)? = null
) {
    val dragController = rememberExtraKeysDragController()
    val haptic = LocalHapticFeedback.current
    val dragEnabled = isArrangeMode && onMoveKey != null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .onGloballyPositioned { dragController.rootCoordinates = it }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF000000))
                .then(
                    if (isArrangeMode) {
                        Modifier.border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 2.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            rows.forEachIndexed { rowIndex, row ->
                val isTargetedRow = dragController.isDragging &&
                    dragController.dropTarget?.rowIndex == rowIndex &&
                    !dragController.isNoOpDrop()
                val shouldFit = row.size in 1..8

                val rowModifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { dragController.updateRowBounds(rowIndex, it) }
                    .then(
                        if (isTargetedRow) {
                            Modifier
                                .background(CyberCyan.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 2.dp)

                if (row.isEmpty()) {
                    Box(
                        modifier = rowModifier
                            .height(if (isCompact) 30.dp else 38.dp)
                            .border(1.dp, CyberCyan.copy(alpha = 0.35f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (dragController.isDragging) "Drop key here" else "Empty Row ${rowIndex + 1}",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else if (shouldFit) {
                    Row(
                        modifier = rowModifier,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        row.forEachIndexed { colIndex, item ->
                            key(item.id) {
                                val isDraggedSource = dragController.isSourceItem(item)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .graphicsLayer { alpha = if (isDraggedSource) 0.25f else 1f }
                                        .then(
                                            if (dragEnabled) {
                                                Modifier.draggableKeySlot(
                                                    controller = dragController,
                                                    item = item,
                                                    rowIndex = rowIndex,
                                                    colIndex = colIndex,
                                                    rows = rows,
                                                    immediateDrag = true,
                                                    haptic = haptic,
                                                    onMoveKey = onMoveKey!!
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                ) {
                                    KeyCapButton(
                                        item = item,
                                        onClick = { if (!isArrangeMode) onKeyTapped(item, false) },
                                        onPopupTriggered = if (!isArrangeMode) {
                                            { onKeyTapped(item, true) }
                                        } else null,
                                        isSelected = false,
                                        isCompact = isCompact,
                                        isTermuxStyle = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = rowModifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        row.forEachIndexed { colIndex, item ->
                            key(item.id) {
                                val isDraggedSource = dragController.isSourceItem(item)
                                Box(
                                    modifier = Modifier
                                        .graphicsLayer { alpha = if (isDraggedSource) 0.25f else 1f }
                                        .then(
                                            if (dragEnabled) {
                                                Modifier.draggableKeySlot(
                                                    controller = dragController,
                                                    item = item,
                                                    rowIndex = rowIndex,
                                                    colIndex = colIndex,
                                                    rows = rows,
                                                    immediateDrag = true,
                                                    haptic = haptic,
                                                    onMoveKey = onMoveKey!!
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                ) {
                                    KeyCapButton(
                                        item = item,
                                        onClick = { if (!isArrangeMode) onKeyTapped(item, false) },
                                        onPopupTriggered = if (!isArrangeMode) {
                                            { onKeyTapped(item, true) }
                                        } else null,
                                        isSelected = false,
                                        isCompact = isCompact,
                                        isTermuxStyle = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        ExtraKeysDragOverlay(
            controller = dragController,
            rows = rows,
            isCompact = isCompact,
            isTermuxStyle = true
        )
    }
}
