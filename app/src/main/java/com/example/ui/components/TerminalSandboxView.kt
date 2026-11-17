package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
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
    onMoveKey: (fromRow: Int, fromCol: Int, toRow: Int, insertIndex: Int) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val isSystemImeVisible = WindowInsets.ime.getBottom(density) > 0

    // Simulated soft keyboard toggle ("KB Up" / "KB Down") & Live Extra Keys drag-arrange mode
    var isKeyboardUp by remember { mutableStateOf(true) }
    var isArrangeMode by remember { mutableStateOf(false) }

    // Intercept KEYBOARD extra key to actually toggle simulated keyboard visibility
    val handleKeyTap: (KeyItem, Boolean) -> Unit = { item, isPopup ->
        val effectiveKey = if (isPopup && item.popup != null) item.popup.key else item.key
        if (effectiveKey == "KEYBOARD") {
            isKeyboardUp = !isKeyboardUp
        }
        onKeyTapped(item, isPopup)
    }

    // Auto-scroll terminal history to bottom when history changes or IME opens
    LaunchedEffect(history.size, shellInput, isSystemImeVisible) {
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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        val isLandscapeSplit = maxWidth >= 560.dp && maxHeight < 460.dp
        val isCompactVertical = maxHeight < 500.dp || keysLayout.size >= 3
        val showSimulatedKeyboard = isKeyboardUp && !isSystemImeVisible

        if (isLandscapeSplit && showSimulatedKeyboard) {
            // ─── LANDSCAPE SPLIT LAYOUT: Terminal on Left, Extra Keys + Keyboard on Right ───
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Pane: Control Bar + Full-Height Terminal Display
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    TerminalControlBar(
                        mode = mode,
                        isKeyboardUp = isKeyboardUp,
                        isSystemImeVisible = isSystemImeVisible,
                        isArrangeMode = isArrangeMode,
                        onToggleKeyboard = { isKeyboardUp = !isKeyboardUp },
                        onToggleArrangeMode = { isArrangeMode = !isArrangeMode },
                        onModeChanged = onModeChanged,
                        onClearTerminal = onClearTerminal,
                        isCompact = true
                    )
                    TerminalDisplayArea(
                        mode = mode,
                        history = history,
                        shellInput = shellInput,
                        cursorVisible = cursorVisible,
                        timeString = timeString,
                        nanoFilename = nanoFilename,
                        nanoBuffer = nanoBuffer,
                        nanoLineIndex = nanoLineIndex,
                        nanoStatusMessage = nanoStatusMessage,
                        nanoIsModified = nanoIsModified,
                        listState = listState,
                        onShellInputChange = onShellInputChange,
                        onExecuteCommand = onExecuteCommand,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                VerticalDivider(color = TerminalBorder, thickness = 1.dp)

                // Right Pane: Extra Keys + Compact Soft Keyboard
                Column(
                    modifier = Modifier
                        .weight(1.05f)
                        .fillMaxHeight()
                        .background(Color(0xFF000000)),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            if (isArrangeMode) {
                                Text(
                                    text = "Drag keys to reorder • Tap Done when finished",
                                    color = CyberCyan,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0A141E))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            LiveExtraKeysBar(
                                rows = keysLayout,
                                onKeyTapped = handleKeyTap,
                                isCompact = true,
                                isArrangeMode = isArrangeMode,
                                onMoveKey = onMoveKey
                            )
                            TermuxSoftKeyboard(
                                onCharTyped = { char -> onShellInputChange(shellInput + char) },
                                onBackspace = {
                                    if (shellInput.isNotEmpty()) {
                                        onShellInputChange(shellInput.dropLast(1))
                                    }
                                },
                                onEnter = onExecuteCommand,
                                isCompact = true
                            )
                        }
                    }
                }
            }
        } else {
            // ─── PORTRAIT (or Landscape with KB Down / System IME Active) ───
            Column(modifier = Modifier.fillMaxSize()) {
                TerminalControlBar(
                    mode = mode,
                    isKeyboardUp = isKeyboardUp,
                    isSystemImeVisible = isSystemImeVisible,
                    isArrangeMode = isArrangeMode,
                    onToggleKeyboard = { isKeyboardUp = !isKeyboardUp },
                    onToggleArrangeMode = { isArrangeMode = !isArrangeMode },
                    onModeChanged = onModeChanged,
                    onClearTerminal = onClearTerminal,
                    isCompact = isCompactVertical
                )

                TerminalDisplayArea(
                    mode = mode,
                    history = history,
                    shellInput = shellInput,
                    cursorVisible = cursorVisible,
                    timeString = timeString,
                    nanoFilename = nanoFilename,
                    nanoBuffer = nanoBuffer,
                    nanoLineIndex = nanoLineIndex,
                    nanoStatusMessage = nanoStatusMessage,
                    nanoIsModified = nanoIsModified,
                    listState = listState,
                    onShellInputChange = onShellInputChange,
                    onExecuteCommand = onExecuteCommand,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                if (isArrangeMode) {
                    Text(
                        text = "Drag extra keys to rearrange • Tap Arrange when done",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0A141E))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Live Extra Keys Bar always sits cleanly above whichever keyboard is active
                LiveExtraKeysBar(
                    rows = keysLayout,
                    onKeyTapped = handleKeyTap,
                    isCompact = isCompactVertical || isSystemImeVisible,
                    isArrangeMode = isArrangeMode,
                    onMoveKey = onMoveKey
                )

                // Simulated Soft Keyboard yields automatically when Android System IME is active
                AnimatedVisibility(visible = showSimulatedKeyboard) {
                    TermuxSoftKeyboard(
                        onCharTyped = { char -> onShellInputChange(shellInput + char) },
                        onBackspace = {
                            if (shellInput.isNotEmpty()) {
                                onShellInputChange(shellInput.dropLast(1))
                            }
                        },
                        onEnter = onExecuteCommand,
                        isCompact = isCompactVertical
                    )
                }
            }
        }
    }
}

@Composable
private fun TerminalControlBar(
    mode: TerminalMode,
    isKeyboardUp: Boolean,
    isSystemImeVisible: Boolean,
    isArrangeMode: Boolean,
    onToggleKeyboard: () -> Unit,
    onToggleArrangeMode: () -> Unit,
    onModeChanged: (TerminalMode) -> Unit,
    onClearTerminal: () -> Unit,
    isCompact: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0E14))
            .padding(horizontal = 8.dp, vertical = if (isCompact) 2.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(modifier = Modifier.size(7.dp).background(RedAccent, CircleShape))
            Box(modifier = Modifier.size(7.dp).background(AmberAccent, CircleShape))
            Box(modifier = Modifier.size(7.dp).background(MatrixGreenBright, CircleShape))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "termux",
                color = Color(0xFF888888),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = isArrangeMode,
                onClick = onToggleArrangeMode,
                label = {
                    Text(
                        text = if (isArrangeMode) "Done" else "Arrange",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DragIndicator,
                        contentDescription = "Rearrange extra keys",
                        modifier = Modifier.size(12.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                    selectedLabelColor = CyberCyan
                ),
                modifier = Modifier.height(32.dp).testTag("sandbox_arrange_keys_btn")
            )

            if (!isSystemImeVisible) {
                FilterChip(
                    selected = isKeyboardUp,
                    onClick = onToggleKeyboard,
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
                    ),
                    modifier = Modifier.height(32.dp)
                )
            }

            FilterChip(
                selected = mode == TerminalMode.SHELL,
                onClick = { onModeChanged(TerminalMode.SHELL) },
                label = { Text("Shell", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MatrixGreen.copy(alpha = 0.25f),
                    selectedLabelColor = MatrixGreenBright
                ),
                modifier = Modifier.height(32.dp)
            )

            FilterChip(
                selected = mode == TerminalMode.NANO,
                onClick = { onModeChanged(TerminalMode.NANO) },
                label = { Text("Nano", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                    selectedLabelColor = CyberCyan
                ),
                modifier = Modifier.height(32.dp)
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
}

@Composable
private fun TerminalDisplayArea(
    mode: TerminalMode,
    history: List<TerminalLine>,
    shellInput: String,
    cursorVisible: Boolean,
    timeString: String,
    nanoFilename: String,
    nanoBuffer: List<String>,
    nanoLineIndex: Int,
    nanoStatusMessage: String,
    nanoIsModified: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onShellInputChange: (String) -> Unit,
    onExecuteCommand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
                            color = when {
                                line.isError -> RedAccent
                                line.isSystem -> CyberCyan
                                else -> Color.White
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Active prompt line
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
}
