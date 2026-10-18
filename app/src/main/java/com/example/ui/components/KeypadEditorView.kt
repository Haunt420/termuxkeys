package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Toolbar: Style & Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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

        // Visual Rows Editor
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(rows) { rowIndex, rowKeys ->
                RowEditorCard(
                    rowIndex = rowIndex,
                    keys = rowKeys,
                    totalRows = rows.size,
                    selectedCol = if (selectedRow == rowIndex) selectedCol else null,
                    onSelectKey = { colIndex -> onSelectKey(rowIndex, colIndex) },
                    onAddKey = { onAddKeyToRow(rowIndex) },
                    onRemoveRow = { onRemoveRow(rowIndex) }
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
}

@Composable
fun RowEditorCard(
    rowIndex: Int,
    keys: List<KeyItem>,
    totalRows: Int,
    selectedCol: Int?,
    onSelectKey: (colIndex: Int) -> Unit,
    onAddKey: () -> Unit,
    onRemoveRow: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TerminalSurface)
            .border(1.dp, TerminalBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Row Header
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

            // Keys in Row with Add button at end
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                keys.forEachIndexed { colIndex, item ->
                    KeyCapButton(
                        item = item,
                        onClick = { onSelectKey(colIndex) },
                        isSelected = selectedCol == colIndex,
                        isEditMode = true
                    )
                }

                // Add Key to Row Button
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
                text = "Tap any keycap above to edit code, label, popup, or position.",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
