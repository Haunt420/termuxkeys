package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.PropertiesFormatterEngine
import com.example.model.KeyItem
import com.example.storage.StorageHelper
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExportBottomSheet(
    rows: List<List<KeyItem>>,
    style: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Runnable Command, 1: Raw termux.properties, 2: Setup Tutorial

    val formattedProperties = remember(rows, style) {
        PropertiesFormatterEngine.formatTermuxProperties(rows, style)
    }

    val runnableCommand = remember(rows, style) {
        PropertiesFormatterEngine.generateRunnableBashCommand(rows, style)
    }

    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        if (uri != null) {
            StorageHelper.writeTextToUri(context, uri, formattedProperties)
        }
    }

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
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints {
                val isCompact = maxHeight < 460.dp

                Surface(
                    modifier = Modifier
                        .widthIn(max = 700.dp)
                        .fillMaxWidth()
                        .heightIn(max = maxHeight),
                    shape = RoundedCornerShape(16.dp),
                    color = TerminalSurface,
                    border = BorderStroke(1.dp, TerminalBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(if (isCompact) 12.dp else 16.dp),
                        verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
                    ) {
                        // Top Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = MatrixGreenBright)
                                Column {
                                    Text(
                                        text = "Export to Termux",
                                        color = TextPrimary,
                                        fontSize = if (isCompact) 15.sp else 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Apply extra keys instantly to Termux",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp).testTag("dismiss_export_dialog")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                            }
                        }

                        HorizontalDivider(color = TerminalBorder)

                        // Export Options Tabs
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = TerminalSurfaceVariant,
                            contentColor = TextPrimary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = MatrixGreenBright
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("Runnable Script", fontSize = 11.sp, fontFamily = FontFamily.Monospace) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("termux.properties", fontSize = 11.sp, fontFamily = FontFamily.Monospace) }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                text = { Text("Quick Guide", fontSize = 11.sp, fontFamily = FontFamily.Monospace) }
                            )
                        }

                        // Scrollable Tab Body
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            when (selectedTab) {
                                0 -> {
                                    Text(
                                        text = "Paste this single command into Termux to write ~/.termux/termux.properties and automatically reload:",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF070A0E))
                                            .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = runnableCommand,
                                            color = MatrixGreenBright,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                1 -> {
                                    Text(
                                        text = "Standard properties syntax for ~/.termux/termux.properties:",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF070A0E))
                                            .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = formattedProperties,
                                            color = CyberCyan,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                2 -> {
                                    StepInstruction(step = "1", title = "Copy the Script", desc = "Tap 'Copy Script' on the Runnable Script tab.")
                                    StepInstruction(step = "2", title = "Open Termux", desc = "Switch over to your Termux terminal app.")
                                    StepInstruction(step = "3", title = "Paste & Run", desc = "Long press the terminal window, tap 'Paste', then press Enter.")
                                    StepInstruction(step = "4", title = "Instant Reload", desc = "Settings reload immediately via 'termux-reload-settings'. Your new extra keys bar is active!")
                                }
                            }
                        }

                        // Pinned Bottom Action Row
                        when (selectedTab) {
                            0 -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            StorageHelper.copyToClipboard(context, "Termux Command", runnableCommand)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MatrixGreenBright,
                                            contentColor = TerminalBg
                                        ),
                                        modifier = Modifier.weight(1f).testTag("copy_runnable_cmd_btn")
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Copy Script", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            StorageHelper.shareText(context, "Termux Extra Keys Command", runnableCommand)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share script", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            1 -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            createDocLauncher.launch("termux.properties")
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MatrixGreenBright,
                                            contentColor = TerminalBg
                                        ),
                                        modifier = Modifier.weight(1f).testTag("saf_save_file_btn")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Save via SAF", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            StorageHelper.copyToClipboard(context, "termux.properties", formattedProperties)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Copy Text")
                                    }
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepInstruction(step: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TerminalSurfaceVariant)
            .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(MatrixGreenBright, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = step, color = TerminalBg, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Column {
            Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
