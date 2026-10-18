package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyItem
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.MatrixGreenGlow
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TerminalLine
import com.example.viewmodel.TerminalMode
import kotlinx.coroutines.delay

@Composable
fun TerminalSandboxView(
    mode: TerminalMode,
    history: List<TerminalLine>,
    shellInput: String,
    shellCursor: Int,
    nanoFilename: String,
    nanoBuffer: List<String>,
    nanoLineIndex: Int,
    nanoStatusMessage: String,
    nanoIsModified: Boolean,
    keysLayout: List<List<KeyItem>>,
    onModeChanged: (TerminalMode) -> Unit,
    onShellInputChange: (String) -> Unit,
    onExecuteCommand: () -> Unit,
    onKeyTapped: (KeyItem, Boolean) -> Unit,
    onClearTerminal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll terminal history to bottom
    LaunchedEffect(history.size, shellInput) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size)
        }
    }

    // Blinking cursor state
    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorVisible = !cursorVisible
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBg)
    ) {
        // Top Sandbox Control Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TerminalSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Window control traffic lights
                Box(modifier = Modifier.size(10.dp).background(RedAccent, CircleShape))
                Box(modifier = Modifier.size(10.dp).background(AmberAccent, CircleShape))
                Box(modifier = Modifier.size(10.dp).background(MatrixGreenBright, CircleShape))

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "termux-session:1",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Mode Toggle Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = mode == TerminalMode.SHELL,
                    onClick = { onModeChanged(TerminalMode.SHELL) },
                    label = { Text("Shell ($)", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MatrixGreen.copy(alpha = 0.25f),
                        selectedLabelColor = MatrixGreenBright
                    ),
                    modifier = Modifier.testTag("terminal_mode_shell")
                )

                FilterChip(
                    selected = mode == TerminalMode.NANO,
                    onClick = { onModeChanged(TerminalMode.NANO) },
                    label = { Text("GNU Nano", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                        selectedLabelColor = CyberCyan
                    ),
                    modifier = Modifier.testTag("terminal_mode_nano")
                )

                if (mode == TerminalMode.SHELL) {
                    IconButton(
                        onClick = onClearTerminal,
                        modifier = Modifier.size(32.dp).testTag("clear_terminal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Terminal",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Terminal Screen Content Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF070A0E))
                .border(1.dp, TerminalBorder)
                .padding(8.dp)
        ) {
            if (mode == TerminalMode.SHELL) {
                // Shell Mode Screen
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(history) { line ->
                        Text(
                            text = line.text,
                            color = when {
                                line.isSystem -> MatrixGreenGlow
                                line.isCommand -> CyberCyan
                                line.isError -> RedAccent
                                else -> TextPrimary
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    // Active prompt line
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        ) {
                            Text(
                                text = "u0_a123@termux:~$ ",
                                color = MatrixGreenBright,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            BasicTextField(
                                value = shellInput,
                                onValueChange = onShellInputChange,
                                textStyle = TextStyle(
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                cursorBrush = SolidColor(if (cursorVisible) MatrixGreenBright else Color.Transparent),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default,
                                keyboardActions = KeyboardActions(onDone = { onExecuteCommand() }),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("terminal_shell_input"),
                                decorationBox = { innerTextField ->
                                    Box {
                                        innerTextField()
                                        if (shellInput.isEmpty()) {
                                            Text(
                                                text = if (cursorVisible) "█" else "",
                                                color = MatrixGreenBright,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            )

                            IconButton(
                                onClick = onExecuteCommand,
                                modifier = Modifier.size(28.dp).testTag("terminal_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Send Command",
                                    tint = MatrixGreenBright,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // GNU Nano Simulator Mode Screen
                Column(modifier = Modifier.fillMaxSize()) {
                    // Nano Top Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "GNU nano 7.2",
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = nanoFilename,
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (nanoIsModified) "Modified" else "",
                            color = Color(0xFFC0392B),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Nano Buffer Lines
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        items(nanoBuffer.size) { index ->
                            val isCurrentLine = index == nanoLineIndex
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isCurrentLine) Color(0xFF1B2430) else Color.Transparent)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = String.format("%2d ", index + 1),
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = nanoBuffer[index],
                                    color = if (isCurrentLine) MatrixGreenBright else TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                                if (isCurrentLine && cursorVisible) {
                                    Text(
                                        text = "█",
                                        color = MatrixGreenBright,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Nano Status / Prompt Line
                    if (nanoStatusMessage.isNotBlank()) {
                        Text(
                            text = nanoStatusMessage,
                            color = AmberAccent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF161B22))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    // Nano Bottom Standard Shortcut Guide Bars
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = "^G Help   ^O WriteOut   ^W WhereIs   ^K CutText   ^J Justify   ^C CurPos",
                            color = Color(0xFFE6EDF3),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "^X Exit   ^R ReadFile   ^\\ Replace   ^U Uncut     ^T ToSpell   ^_ GoToLine",
                            color = Color(0xFFE6EDF3),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Live Extra Keys Bar attached directly beneath the terminal screen!
        LiveExtraKeysBar(
            rows = keysLayout,
            onKeyTapped = onKeyTapped
        )
    }
}
