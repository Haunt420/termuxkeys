package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyItem
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun KeypadEditorView(
    rows: List<List<KeyItem>>,
    style: String,
    selectedRow: Int?,
    selectedCol: Int?,
    onSelectKey: (rowIndex: Int, colIndex: Int) -> Unit,
    onAddKeyToRow: (rowIndex: Int) -> Unit,
    onAddNewRow: () -> Unit,
    onRemoveRow: (rowIndex: Int) -> Unit,
    onStyleChanged: (String) -> Unit,
    onOpenPresets: () -> Unit,
    onOpenCatalog: () -> Unit,
    onOpenBashWizard: () -> Unit = {},
    onMoveKey: (fromRow: Int, fromCol: Int, toRow: Int, insertIndex: Int) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var isArrangeMode by remember { mutableStateOf(false) }
    val dragController = rememberExtraKeysDragController()
    val haptic = LocalHapticFeedback.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBg)
            .onGloballyPositioned { dragController.rootCoordinates = it }
    ) {
        val isCompact = maxHeight < 460.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 10.dp)
        ) {
            // Top Toolbar: Horizontally scrollable so controls never clip on narrow or landscape screens
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = isArrangeMode,
                    onClick = { isArrangeMode = !isArrangeMode },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DragIndicator,
                            contentDescription = "Arrange keys",
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isArrangeMode) "Done Dragging" else "Arrange",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                        selectedLabelColor = CyberCyan
                    ),
                    modifier = Modifier.testTag("toggle_arrange_mode_btn")
                )

                Text(
                    text = "Style:",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                FilterChip(
                    selected = style == "default",
                    onClick = { onStyleChanged("default") },
                    label = { Text("Default", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MatrixGreen.copy(alpha = 0.25f),
                        selectedLabelColor = MatrixGreenBright
                    )
                )
                FilterChip(
                    selected = style == "arrows-only",
                    onClick = { onStyleChanged("arrows-only") },
                    label = { Text("Arrows Only", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                        selectedLabelColor = CyberCyan
                    )
                )

                OutlinedButton(
                    onClick = onOpenBashWizard,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberAccent),
                    modifier = Modifier.testTag("editor_bash_wizard_btn")
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bash Fn", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                OutlinedButton(
                    onClick = onOpenPresets,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    modifier = Modifier.testTag("manage_presets_btn")
                ) {
                    Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Presets", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }

            if (isArrangeMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCyan.copy(alpha = 0.12f))
                        .border(1.dp, CyberCyan.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DragIndicator,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Arrange Mode: Drag any keycap left, right, or across rows",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Visual Rows Editor
            LazyColumn(
                modifier = Modifier.weight(1f),
                userScrollEnabled = !dragController.isDragging,
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
            ) {
                itemsIndexed(rows) { rowIndex, rowKeys ->
                    RowEditorCard(
                        rowIndex = rowIndex,
                        keys = rowKeys,
                        allRows = rows,
                        totalRows = rows.size,
                        selectedCol = if (selectedRow == rowIndex) selectedCol else null,
                        isArrangeMode = isArrangeMode,
                        dragController = dragController,
                        haptic = haptic,
                        onSelectKey = { colIndex -> onSelectKey(rowIndex, colIndex) },
                        onAddKey = { onAddKeyToRow(rowIndex) },
                        onRemoveRow = { onRemoveRow(rowIndex) },
                        onMoveKey = onMoveKey
                    )
                }

                item {
                    if (rows.size < 4) {
                        OutlinedButton(
                            onClick = onAddNewRow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("add_new_row_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MatrixGreenBright)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Row (${rows.size + 1}/4)", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Floating dragged keycap + insertion beam overlay
        ExtraKeysDragOverlay(
            controller = dragController,
            rows = rows,
            isCompact = false,
            isTermuxStyle = true
        )
    }
}

@Composable
fun RowEditorCard(
    rowIndex: Int,
    keys: List<KeyItem>,
    allRows: List<List<KeyItem>>,
    totalRows: Int,
    selectedCol: Int?,
    isArrangeMode: Boolean,
    dragController: ExtraKeysDragController,
    haptic: HapticFeedback,
    onSelectKey: (colIndex: Int) -> Unit,
    onAddKey: () -> Unit,
    onRemoveRow: () -> Unit,
    onMoveKey: (fromRow: Int, fromCol: Int, toRow: Int, insertIndex: Int) -> Unit
) {
    val isTargetedRow = dragController.isDragging &&
        dragController.dropTarget?.rowIndex == rowIndex &&
        !dragController.isNoOpDrop()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isTargetedRow) CyberCyan.copy(alpha = 0.08f) else TerminalSurface)
            .border(
                width = if (isTargetedRow) 1.5.dp else 1.dp,
                color = if (isTargetedRow) CyberCyan else TerminalBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MatrixGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ROW ${rowIndex + 1}",
                            color = MatrixGreenBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "(${keys.size} keys)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (totalRows > 1) {
                    IconButton(
                        onClick = onRemoveRow,
                        modifier = Modifier.size(28.dp).testTag("delete_row_${rowIndex}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Row",
                            tint = RedAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Keys in Row with drag-and-drop tracking & Add button at end
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { dragController.updateRowBounds(rowIndex, it) }
                    .horizontalScroll(rememberScrollState(), enabled = !dragController.isDragging),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (keys.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(TerminalSurfaceVariant)
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (dragController.isDragging) "Drop key here" else "Empty Row",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    keys.forEachIndexed { colIndex, item ->
                        key(item.id) {
                            val isDraggedSource = dragController.isSourceItem(item)
                            Box(
                                modifier = Modifier
                                    .graphicsLayer { alpha = if (isDraggedSource) 0.25f else 1f }
                                    .then(
                                        if (isArrangeMode) {
                                            Modifier.border(
                                                width = 1.dp,
                                                color = CyberCyan.copy(alpha = 0.45f),
                                                shape = RoundedCornerShape(5.dp)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .draggableKeySlot(
                                        controller = dragController,
                                        item = item,
                                        rowIndex = rowIndex,
                                        colIndex = colIndex,
                                        rows = allRows,
                                        immediateDrag = isArrangeMode,
                                        haptic = haptic,
                                        onMoveKey = onMoveKey
                                    )
                            ) {
                                KeyCapButton(
                                    item = item,
                                    onClick = { onSelectKey(colIndex) },
                                    isSelected = selectedCol == colIndex,
                                    isEditMode = true
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(46.dp, 48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TerminalSurfaceVariant)
                        .border(1.dp, MatrixGreenBright.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { onAddKey() }
                        .testTag("add_key_to_row_${rowIndex}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Key",
                        tint = MatrixGreenBright,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = if (isArrangeMode) {
                    "Drag any keycap left, right, or across rows to rearrange."
                } else {
                    "Tap key to edit • Long-press & drag (or tap Arrange) to reorder."
                },
                color = if (isArrangeMode) CyberCyan else TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
