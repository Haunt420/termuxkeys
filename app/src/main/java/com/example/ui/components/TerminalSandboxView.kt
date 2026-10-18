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
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyItem
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TerminalLine
import com.example.viewmodel.TerminalMode
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // Soft keyboard toggle ("KB Up" / "KB Down"), defaults to true as requested in screenshot!
    var isKeyboardUp by remember { mutableStateOf(true) }

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

    val timeString = remember {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        // Compact Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0E14))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).background(RedAccent, CircleShape))
                Box(modifier = Modifier.size(8.dp).background(AmberAccent, CircleShape))
                Box(modifier = Modifier.size(8.dp).background(MatrixGreenBright, CircleShape))

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "termux",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                // Keyboard toggle button
                FilterChip(
                    selected = isKeyboardUp,
                    onClick = { isKeyboardUp = !isKeyboardUp },
                    label = {
                        Text(
                            text = if (isKeyboardUp) "KB Up" else "KB Down",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isKeyboardUp) Icons.Default.Keyboard else Icons.Default.KeyboardHide,
                            contentDescription = "Toggle Keyboard",
                            modifier = Modifier.size(12.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1E3A5F),
                        selectedLabelColor = Color(0xFF40C4FF)
                    )
                )

                FilterChip(
                    selected = mode == TerminalMode.SHELL,
                    onClick = { onModeChanged(TerminalMode.SHELL) },
                    label = { Text("Shell", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MatrixGreen.copy(alpha = 0.25f),
                        selectedLabelColor = MatrixGreenBright
                    )
                )

                FilterChip(
                    selected = mode == TerminalMode.NANO,
                    onClick = { onModeChanged(TerminalMode.NANO) },
                    label = { Text("Nano", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                        selectedLabelColor = CyberCyan
                    )
                )

                if (mode == TerminalMode.SHELL) {
                    IconButton(
                        onClick = onClearTerminal,
                        modifier = Modifier.size(28.dp).testTag("clear_terminal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Terminal Screen (Pixel clone of user's Termux display)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF000000))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            if (mode == TerminalMode.SHELL) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(history) { line ->
                        if (line.text.startsWith("› ")) {
                            // Syntax highlighted command line (Pixel clone)
                            val cmdBody = line.text.removePrefix("› ")
                            val annotated = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color(0xFFE040FB), fontWeight = FontWeight.Bold)) {
                                    append("› ")
                                }
                                val words = cmdBody.split(" ")
                                words.forEachIndexed { idx, word ->
                                    if (idx == 0 || word == ";echo" || word.startsWith(";")) {
                                        withStyle(SpanStyle(color = Color(0xFF76FF03), fontWeight = FontWeight.Bold)) {
                                            append(word)
                                        }
                                    } else {
                                        withStyle(SpanStyle(color = Color.White)) {
                                            append(word)
                                        }
                                    }
                                    if (idx < words.size - 1) append(" ")
                                }
                            }
                            Text(
                                text = annotated,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        } else {
                            Text(
                                text = line.text,
                                color = if (line.isError) RedAccent else Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Active prompt line matching user screenshot:
                    // ~ › █                                     00:46:46
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "~ ",
                                    color = Color(0xFF40C4FF),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "› ",
                                    color = Color(0xFFE040FB),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                BasicTextField(
                                    value = shellInput,
                                    onValueChange = onShellInputChange,
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    cursorBrush = SolidColor(Color.Transparent),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default,
                                    keyboardActions = KeyboardActions(onDone = { onExecuteCommand() }),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("terminal_shell_input"),
                                    decorationBox = { innerTextField ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            innerTextField()
                                            if (cursorVisible) {
                                                Text(
                                                    text = "█",
                                                    color = Color.White,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                )
                            }

                            // Right-aligned timestamp from screenshot (e.g. 00:46:46)
                            Text(
                                text = timeString,
                                color = Color(0xFF666666),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                // GNU Nano Mode Screen
                Column(modifier = Modifier.fillMaxSize()) {
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
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = nanoFilename,
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (nanoIsModified) "Modified" else "",
                            color = Color(0xFFC0392B),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

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
                                    color = Color(0xFF666666),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = nanoBuffer[index],
                                    color = if (isCurrentLine) MatrixGreenBright else Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                                if (isCurrentLine && cursorVisible) {
                                    Text(
                                        text = "█",
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .padding(top = 2.dp)
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

        // Live Extra Keys Bar (Exact 2 Rows from Screenshot)
        LiveExtraKeysBar(
            rows = keysLayout,
            onKeyTapped = onKeyTapped
        )

        // Simulated Neon Cyan Soft Keyboard ("KB Up", Exact clone from screenshot)
        AnimatedVisibility(visible = isKeyboardUp) {
            TermuxSoftKeyboard(
                onCharTyped = { char ->
                    val newInput = shellInput + char
                    onShellInputChange(newInput)
                },
                onBackspace = {
                    if (shellInput.isNotEmpty()) {
                        onShellInputChange(shellInput.dropLast(1))
                    }
                },
                onEnter = {
                    onExecuteCommand()
                }
            )
        }
    }
}
