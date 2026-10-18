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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.KeyItem
import com.example.model.KeyPopup
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
fun KeyInspectorDialog(
    item: KeyItem,
    rowIndex: Int,
    colIndex: Int,
    totalRows: Int,
    totalColsInRow: Int,
    onSave: (KeyItem) -> Unit,
    onDelete: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onOpenCatalog: () -> Unit,
    onDismiss: () -> Unit
) {
    var keyText by remember { mutableStateOf(item.key) }
    var displayText by remember { mutableStateOf(item.display) }
    var hasPopup by remember { mutableStateOf(item.popup != null) }
    var popupKeyText by remember { mutableStateOf(item.popup?.key ?: "") }
    var popupDisplayText by remember { mutableStateOf(item.popup?.display ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(TerminalSurface)
                .border(1.dp, TerminalBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Key Inspector",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Row ${rowIndex + 1}, Position ${colIndex + 1}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dismiss_key_inspector")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = TerminalBorder)

                // Reorder controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Position & Move:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onMoveLeft,
                            enabled = colIndex > 0,
                            modifier = Modifier.size(36.dp).testTag("move_key_left")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Move Left", tint = if (colIndex > 0) CyberCyan else TextMuted)
                        }
                        IconButton(
                            onClick = onMoveRight,
                            enabled = colIndex < totalColsInRow - 1,
                            modifier = Modifier.size(36.dp).testTag("move_key_right")
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Move Right", tint = if (colIndex < totalColsInRow - 1) CyberCyan else TextMuted)
                        }
                        IconButton(
                            onClick = onMoveUp,
                            enabled = rowIndex > 0,
                            modifier = Modifier.size(36.dp).testTag("move_key_up")
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Row Up", tint = if (rowIndex > 0) MatrixGreenBright else TextMuted)
                        }
                        IconButton(
                            onClick = onMoveDown,
                            enabled = rowIndex < totalRows - 1,
                            modifier = Modifier.size(36.dp).testTag("move_key_down")
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Row Down", tint = if (rowIndex < totalRows - 1) MatrixGreenBright else TextMuted)
                        }
                    }
                }

                // Primary Key String
                OutlinedTextField(
                    value = keyText,
                    onValueChange = { keyText = it },
                    label = { Text("Key Code / Macro String") },
                    placeholder = { Text("e.g. ESC, CTRL, TAB, |, UP, nano") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MatrixGreenBright,
                        unfocusedBorderColor = TerminalBorder,
                        focusedLabelColor = MatrixGreenBright,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_key_code")
                )

                // Custom Display Label
                OutlinedTextField(
                    value = displayText,
                    onValueChange = { displayText = it },
                    label = { Text("Display Label (Optional)") },
                    placeholder = { Text("Leave blank to use Key Code") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = TerminalBorder,
                        focusedLabelColor = CyberCyan,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_display_label")
                )

                // Quick Browse Catalog Button
                OutlinedButton(
                    onClick = onOpenCatalog,
                    modifier = Modifier.fillMaxWidth().testTag("pick_from_catalog_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pick Shortcut from Exhaustive Catalog", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }

                HorizontalDivider(color = TerminalBorder)

                // Swipe-up Popup Configuration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Swipe-Up Popup Key",
                            color = AmberAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Action dispatched when swiping up on key",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = hasPopup,
                        onCheckedChange = { hasPopup = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AmberAccent,
                            checkedTrackColor = AmberAccent.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("toggle_popup_switch")
                    )
                }

                if (hasPopup) {
                    OutlinedTextField(
                        value = popupKeyText,
                        onValueChange = { popupKeyText = it },
                        label = { Text("Popup Key / Macro") },
                        placeholder = { Text("e.g. F1, ~, | grep , HOME") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberAccent,
                            unfocusedBorderColor = TerminalBorder,
                            focusedLabelColor = AmberAccent,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("input_popup_key")
                    )

                    OutlinedTextField(
                        value = popupDisplayText,
                        onValueChange = { popupDisplayText = it },
                        label = { Text("Popup Display Label (Optional)") },
                        placeholder = { Text("e.g. grep, F1") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberAccent,
                            unfocusedBorderColor = TerminalBorder,
                            focusedLabelColor = AmberAccent,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("input_popup_display")
                    )
                }

                HorizontalDivider(color = TerminalBorder)

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_key_button")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Key", tint = RedAccent)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val popup = if (hasPopup && popupKeyText.isNotBlank()) {
                                    KeyPopup(key = popupKeyText.trim(), display = popupDisplayText.trim())
                                } else null

                                val updated = item.copy(
                                    key = keyText.trim(),
                                    display = displayText.trim(),
                                    popup = popup
                                )
                                onSave(updated)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixGreenBright,
                                contentColor = TerminalBg
                            ),
                            modifier = Modifier.testTag("save_key_button")
                        ) {
                            Text("Apply Changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
