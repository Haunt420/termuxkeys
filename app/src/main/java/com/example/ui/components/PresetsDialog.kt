package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.KeyPreset
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalKeyBg
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PresetsDialog(
    presets: List<KeyPreset>,
    activePresetName: String,
    onLoadPreset: (KeyPreset) -> Unit,
    onSaveCurrent: (name: String, category: String, desc: String) -> Unit,
    onDeleteCustomPreset: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isSavingMode by remember { mutableStateOf(false) }
    var saveName by remember { mutableStateOf("") }
    var saveCategory by remember { mutableStateOf("Custom") }
    var saveDesc by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints {
                val isCompact = maxHeight < 460.dp

                Surface(
                    modifier = Modifier
                        .widthIn(max = 740.dp)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    color = TerminalSurface,
                    border = BorderStroke(1.dp, TerminalBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isCompact) 10.dp else 14.dp),
                        verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 10.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Preset Library",
                                    color = TextPrimary,
                                    fontSize = if (isCompact) 15.sp else 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Pre-configured and user-saved key sets",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp).testTag("dismiss_presets_dialog")
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                            }
                        }

                        HorizontalDivider(color = TerminalBorder)

                        if (isSavingMode) {
                            // Dedicated scrollable save form so Save/Cancel buttons remain reachable when IME is open
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TerminalSurfaceVariant)
                                    .border(1.dp, TerminalBorder, RoundedCornerShape(10.dp))
                                    .verticalScroll(rememberScrollState())
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Save Layout as Custom Preset",
                                    color = MatrixGreenBright,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )

                                OutlinedTextField(
                                    value = saveName,
                                    onValueChange = { saveName = it },
                                    label = { Text("Preset Name") },
                                    placeholder = { Text("e.g. My Python & Git Setup") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MatrixGreenBright,
                                        unfocusedBorderColor = TerminalBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = saveDesc,
                                    onValueChange = { saveDesc = it },
                                    label = { Text("Description") },
                                    placeholder = { Text("Short description of this layout") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MatrixGreenBright,
                                        unfocusedBorderColor = TerminalBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { isSavingMode = false },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("Cancel")
                                }

                                Button(
                                    onClick = {
                                        if (saveName.isNotBlank()) {
                                            onSaveCurrent(saveName, saveCategory, saveDesc)
                                            isSavingMode = false
                                            saveName = ""
                                            saveDesc = ""
                                        }
                                    },
                                    enabled = saveName.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MatrixGreenBright,
                                        contentColor = TerminalBg
                                    )
                                ) {
                                    Text("Save Preset", fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Save Current Preset Trigger Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TerminalSurfaceVariant)
                                    .border(1.dp, TerminalBorder, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Save Active Configuration",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Store current layout as a custom preset",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { isSavingMode = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberCyan,
                                        contentColor = TerminalBg
                                    ),
                                    modifier = Modifier.testTag("start_save_preset_btn")
                                ) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save Current", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Presets List
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(presets, key = { it.id }) { preset ->
                                    PresetCard(
                                        preset = preset,
                                        isActive = preset.name == activePresetName,
                                        onLoad = {
                                            onLoadPreset(preset)
                                            onDismiss()
                                        },
                                        onDelete = if (preset.isCustom) {
                                            { onDeleteCustomPreset(preset.id) }
                                        } else null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: KeyPreset,
    isActive: Boolean,
    onLoad: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) MatrixGreen.copy(alpha = 0.12f) else TerminalSurfaceVariant)
            .border(
                1.dp,
                if (isActive) MatrixGreenBright else TerminalBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = preset.name,
                        color = if (isActive) MatrixGreenBright else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (preset.isCustom) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AmberAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("CUSTOM", color = AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = preset.category,
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = preset.description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                preset.rows.flatten().take(12).forEach { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TerminalKeyBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.effectiveDisplay,
                            color = TextPrimary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                if (preset.rows.flatten().size > 12) {
                    Text(
                        text = "+${preset.rows.flatten().size - 12} more",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete preset", tint = RedAccent, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = onLoad,
                    enabled = !isActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) TextMuted else MatrixGreenBright,
                        contentColor = TerminalBg
                    )
                ) {
                    Text(if (isActive) "Active Layout" else "Load Layout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
