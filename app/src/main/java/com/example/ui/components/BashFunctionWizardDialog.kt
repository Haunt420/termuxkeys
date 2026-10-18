package com.example.ui.components

import android.content.Context
import com.example.engine.ScriptSyntaxValidator
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.storage.StorageHelper
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

/**
 * Multi-step Compose dialog suite for guiding users through creating custom bash functions,
 * verifying their syntax, testing execution, and saving them as Termux extra-keys shortcuts.
 */
@Composable
fun BashFunctionWizardDialog(
    totalRows: Int,
    onSaveShortcut: (KeyItem, targetRow: Int) -> Unit,
    onTestInTerminal: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) } // 1: Purpose & Template, 2: Logic & Syntax Verification, 3: Keycap Ergonomics, 4: Sandbox Test & Save

    // Step 1: Function Identity
    var functionName by remember { mutableStateOf("gpush") }
    var functionDesc by remember { mutableStateOf("Stage all changes, commit with argument, and push") }

    // Step 2: Body & Syntax
    var functionBody by remember { mutableStateOf("  local msg=\"\${1:-Quick sync}\"\n  git add .\n  git commit -m \"\$msg\"\n  git push\n  echo \"[OK] Pushed: \$msg\"") }
    var syntaxValidationResult by remember { mutableStateOf<SyntaxCheckResult?>(null) }

    // Step 3: Keycap Design
    var keyLabel by remember { mutableStateOf("G-PUSH") }
    var targetRowIndex by remember { mutableIntStateOf(0) }
    var enablePopup by remember { mutableStateOf(true) }
    var popupLabel by remember { mutableStateOf("STATUS") }
    var popupMacro by remember { mutableStateOf("git status\n") }

    // Step 4: Verification test state
    var hasRunTest by remember { mutableStateOf(false) }
    var testConsoleOutput by remember { mutableStateOf<String?>(null) }

    // Full generated bash function snippet
    val fullFunctionCode = remember(functionName, functionBody) {
        """
# Termux Custom Bash Function
$functionName() {
$functionBody
}
        """.trimIndent()
    }

    // Generated KeyItem
    val generatedKeyItem = remember(functionName, keyLabel, enablePopup, popupLabel, popupMacro) {
        val popup = if (enablePopup && popupMacro.isNotBlank()) {
            KeyPopup(key = popupMacro, display = popupLabel.ifBlank { popupMacro })
        } else null
        KeyItem(
            key = "$functionName\n",
            display = keyLabel.ifBlank { functionName },
            popup = popup
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            shape = RoundedCornerShape(16.dp),
            color = TerminalSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dialog Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text(
                                text = "Bash Function Creator",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Step $currentStep of 4 • Guided Shortcut Suite",
                                color = MatrixGreenBright,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dismiss_bash_wizard_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Step Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepChip(number = 1, title = "Template", isActive = currentStep == 1, isDone = currentStep > 1)
                    StepDivider(isDone = currentStep > 1)
                    StepChip(number = 2, title = "Logic & Syntax", isActive = currentStep == 2, isDone = currentStep > 2)
                    StepDivider(isDone = currentStep > 2)
                    StepChip(number = 3, title = "Keycap", isActive = currentStep == 3, isDone = currentStep > 3)
                    StepDivider(isDone = currentStep > 3)
                    StepChip(number = 4, title = "Verify & Save", isActive = currentStep == 4, isDone = false)
                }

                HorizontalDivider(color = TerminalBorder)

                // Step Content Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (currentStep) {
                        1 -> {
                            // ─── STEP 1: Function Purpose & Pre-configured Templates ───
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Choose a Template or Define a Function",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "A Bash function allows your Termux extra key to execute multiple commands, accept dynamic parameters, and format output seamlessly.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Text("Quick Starter Templates:", color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                                FunctionTemplateItem(
                                    title = "Git Quick Commit & Push",
                                    desc = "Stages changes, accepts commit message argument, and pushes.",
                                    defaultName = "gpush",
                                    defaultBody = "  local msg=\"\${1:-Quick sync}\"\n  git add .\n  git commit -m \"\$msg\"\n  git push\n  echo \"[OK] Pushed: \$msg\"",
                                    defaultLabel = "G-PUSH",
                                    isSelected = functionName == "gpush",
                                    onClick = { name, body, label ->
                                        functionName = name
                                        functionBody = body
                                        keyLabel = label
                                    }
                                )

                                FunctionTemplateItem(
                                    title = "Tar / Gzip Directory Backup",
                                    desc = "Creates a timestamped compressed tarball of target directory.",
                                    defaultName = "bkup",
                                    defaultBody = "  local target=\"\${1:-.}\"\n  local out=\"backup_\$(date +%Y%m%d_%H%M%S).tar.gz\"\n  tar -czvf \"\$out\" \"\$target\"\n  echo \"[OK] Created \$out\"",
                                    defaultLabel = "BKUP",
                                    isSelected = functionName == "bkup",
                                    onClick = { name, body, label ->
                                        functionName = name
                                        functionBody = body
                                        keyLabel = label
                                    }
                                )

                                FunctionTemplateItem(
                                    title = "Recursive Text Search (Grep)",
                                    desc = "Searches for pattern across files with line numbers and skips binaries.",
                                    defaultName = "ffind",
                                    defaultBody = "  local query=\"\$1\"\n  local dir=\"\${2:-.}\"\n  grep -rnI \"\$query\" \"\$dir\"",
                                    defaultLabel = "FFIND",
                                    isSelected = functionName == "ffind",
                                    onClick = { name, body, label ->
                                        functionName = name
                                        functionBody = body
                                        keyLabel = label
                                    }
                                )

                                FunctionTemplateItem(
                                    title = "Python HTTP Quick File Server",
                                    desc = "Launches local Python HTTP file server with port argument and status output.",
                                    defaultName = "pserve",
                                    defaultBody = "  local port=\"\${1:-8080}\"\n  echo \"[OK] Starting Python HTTP Server on port \$port...\"\n  python3 -m http.server \"\$port\"",
                                    defaultLabel = "PSERV",
                                    isSelected = functionName == "pserve",
                                    onClick = { name, body, label ->
                                        functionName = name
                                        functionBody = body
                                        keyLabel = label
                                    }
                                )

                                FunctionTemplateItem(
                                    title = "Python JSON Pretty Formatter",
                                    desc = "Validates and prettifies JSON file or piped stream using Python stdlib.",
                                    defaultName = "pjson",
                                    defaultBody = "  local file=\"\$1\"\n  if [ -z \"\$file\" ]; then\n    python3 -m json.tool\n  else\n    python3 -m json.tool \"\$file\"\n  fi",
                                    defaultLabel = "PJSON",
                                    isSelected = functionName == "pjson",
                                    onClick = { name, body, label ->
                                        functionName = name
                                        functionBody = body
                                        keyLabel = label
                                    }
                                )

                                FunctionTemplateItem(
                                    title = "Termux Wake-Lock Protected Task",
                                    desc = "Acquires Termux CPU wake lock during long compilation or downloads.",
                                    defaultName = "withlock",
                                    defaultBody = "  termux-wake-lock\n  echo \"[LOCK] Running: \$@\"\n  \"\$@\"\n  local status=\$?\n  termux-wake-unlock\n  return \$status",
                                    defaultLabel = "W-LOCK",
                                    isSelected = functionName == "withlock",
                                    onClick = { name, body, label ->
                                        functionName = name
                                        functionBody = body
                                        keyLabel = label
                                    }
                                )

                                OutlinedTextField(
                                    value = functionName,
                                    onValueChange = { functionName = it.replace(" ", "_") },
                                    label = { Text("Function Name (Bash identifier)") },
                                    placeholder = { Text("e.g. gpush, bkup, deploy") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MatrixGreenBright,
                                        unfocusedBorderColor = TerminalBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("wizard_fn_name_input")
                                )

                                OutlinedTextField(
                                    value = functionDesc,
                                    onValueChange = { functionDesc = it },
                                    label = { Text("Description (What does this function do?)") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = TerminalBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        2 -> {
                            // ─── STEP 2: Logic & Syntax Verification ───
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Write & Verify Function Logic",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Edit the shell body and verify syntax before generating your key shortcut.",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )

                                // Helper Insert Chips
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Insert:", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterVertically))
                                    ArgChip(label = "\$1 (1st Arg)") { functionBody += " \"\$1\"" }
                                    ArgChip(label = "\${1:-default}") { functionBody += " \"\${1:-default}\"" }
                                    ArgChip(label = "\$@ (All Args)") { functionBody += " \"\$@\"" }
                                    ArgChip(label = "echo \"...\"") { functionBody += "\n  echo \"Done\"" }
                                    ArgChip(label = "return 0") { functionBody += "\n  return 0" }
                                }

                                // Code Editor Box
                                OutlinedTextField(
                                    value = functionBody,
                                    onValueChange = {
                                        functionBody = it
                                        syntaxValidationResult = null // Reset validation upon edit
                                    },
                                    label = { Text("Function Body (Lines inside $functionName() { ... })") },
                                    minLines = 5,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MatrixGreenBright,
                                        unfocusedBorderColor = TerminalBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("wizard_fn_body_input")
                                )

                                // Verify Syntax Button
                                Button(
                                    onClick = {
                                        syntaxValidationResult = validateBashSyntax(functionName, functionBody)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (syntaxValidationResult?.isValid == true) MatrixGreenBright else CyberCyan,
                                        contentColor = TerminalBg
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("wizard_verify_syntax_btn")
                                ) {
                                    Icon(
                                        imageVector = if (syntaxValidationResult?.isValid == true) Icons.Default.CheckCircle else Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (syntaxValidationResult?.isValid == true) "Syntax Verified Clean" else "Verify Bash Syntax",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Syntax feedback banner
                                if (syntaxValidationResult != null) {
                                    val res = syntaxValidationResult!!
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (res.isValid) MatrixGreen.copy(alpha = 0.15f) else RedAccent.copy(alpha = 0.15f))
                                            .border(1.dp, if (res.isValid) MatrixGreenBright else RedAccent, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(
                                                imageVector = if (res.isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = if (res.isValid) MatrixGreenBright else RedAccent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = res.message,
                                                color = if (res.isValid) MatrixGreenBright else RedAccent,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // ─── STEP 3: Touch Keycap Ergonomics ───
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Touch Keycap Ergonomics & Row Binding",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Configure how the button appears on your Termux extra-keys bar and which row to place it in.",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )

                                OutlinedTextField(
                                    value = keyLabel,
                                    onValueChange = { keyLabel = it },
                                    label = { Text("Keycap Label (1 to 6 characters recommended)") },
                                    placeholder = { Text("e.g. G-PUSH, BKUP, FFIND") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MatrixGreenBright,
                                        unfocusedBorderColor = TerminalBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("wizard_key_label_input")
                                )

                                // Target Row Selection
                                Text("Assign to Extra Keys Row:", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    for (r in 0 until totalRows.coerceAtLeast(1)) {
                                        FilterChip(
                                            selected = targetRowIndex == r,
                                            onClick = { targetRowIndex = r },
                                            label = { Text("Row ${r + 1}") },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                                                selectedLabelColor = CyberCyan
                                            )
                                        )
                                    }
                                }

                                HorizontalDivider(color = TerminalBorder)

                                // Swipe-up Popup Action
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Swipe-Up Popup Action", color = AmberAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Secondary macro dispatched on upward swipe", color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = enablePopup,
                                        onCheckedChange = { enablePopup = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AmberAccent, checkedTrackColor = AmberAccent.copy(alpha = 0.3f))
                                    )
                                }

                                if (enablePopup) {
                                    OutlinedTextField(
                                        value = popupLabel,
                                        onValueChange = { popupLabel = it },
                                        label = { Text("Popup Display Indicator") },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AmberAccent,
                                            unfocusedBorderColor = TerminalBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = popupMacro,
                                        onValueChange = { popupMacro = it },
                                        label = { Text("Popup Command / Macro") },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AmberAccent,
                                            unfocusedBorderColor = TerminalBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // Keycap Preview
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF000000))
                                        .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Termux Keycap Preview:", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        KeyCapButton(
                                            item = generatedKeyItem,
                                            onClick = {},
                                            isTermuxStyle = true,
                                            modifier = Modifier.width(90.dp)
                                        )
                                    }
                                }
                            }
                        }

                        4 -> {
                            // ─── STEP 4: Live Sandbox Test & Save ───
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Verify in Terminal Sandbox & Deploy",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Execute the test run to verify simulation output, then install the keycap to your active layout and copy the shell code.",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )

                                // Test Execution Button
                                Button(
                                    onClick = {
                                        onTestInTerminal("$functionName\n")
                                        hasRunTest = true
                                        testConsoleOutput = "Dispatched '$functionName' into simulated terminal session."
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (hasRunTest) MatrixGreenBright else CyberCyan,
                                        contentColor = TerminalBg
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("wizard_run_test_btn")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (hasRunTest) "Re-run Test in Terminal" else "Test Run in Terminal Sandbox", fontWeight = FontWeight.Bold)
                                }

                                if (hasRunTest) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF070A0E))
                                            .border(1.dp, MatrixGreenBright, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MatrixGreenBright, modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "✓ Command '$functionName' verified in terminal session!",
                                                color = MatrixGreenBright,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // Bash Code Snippet Box
                                Text("Generated ~/.bashrc Code:", color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF070A0E))
                                        .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = fullFunctionCode,
                                            color = MatrixGreenBright,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = {
                                                StorageHelper.copyToClipboard(context, "Bash Function", fullFunctionCode)
                                            },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }

                                // Install Keycap Action Button
                                Button(
                                    onClick = {
                                        onSaveShortcut(generatedKeyItem, targetRowIndex)
                                        StorageHelper.copyToClipboard(context, "Bash Function", fullFunctionCode, showToast = false)
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MatrixGreenBright,
                                        contentColor = TerminalBg
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("wizard_save_to_layout_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Keycap to Row ${targetRowIndex + 1} & Finish", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = TerminalBorder)

                // Dialog Footer: Step Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back")
                        }
                    } else {
                        OutlinedButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                        ) {
                            Text("Cancel")
                        }
                    }

                    if (currentStep < 4) {
                        Button(
                            onClick = {
                                // If leaving Step 2, run syntax check first
                                if (currentStep == 2 && syntaxValidationResult?.isValid != true) {
                                    val check = validateBashSyntax(functionName, functionBody)
                                    syntaxValidationResult = check
                                    if (check.isValid) {
                                        currentStep++
                                    }
                                } else {
                                    currentStep++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixGreenBright,
                                contentColor = TerminalBg
                            ),
                            modifier = Modifier.testTag("wizard_dialog_next_btn")
                        ) {
                            Text("Next Step", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

data class SyntaxCheckResult(
    val isValid: Boolean,
    val message: String
)

fun validateBashSyntax(name: String, body: String): SyntaxCheckResult {
    val validation = ScriptSyntaxValidator.validateBashFunction(name, body)
    val formatted = if (validation.isValid) {
        val warningText = if (validation.warnings.isNotEmpty()) {
            "\n" + validation.warnings.joinToString("\n") { "• Tip: ${it.message}" }
        } else ""
        validation.summary + warningText
    } else {
        validation.errors.joinToString("\n") { error ->
            val loc = if (error.line != null) "Line ${error.line}: " else ""
            val tip = if (error.suggestion != null) " (Tip: ${error.suggestion})" else ""
            "Error: $loc${error.message}$tip"
        }
    }
    return SyntaxCheckResult(validation.isValid, formatted)
}

@Composable
fun StepChip(number: Int, title: String, isActive: Boolean, isDone: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> MatrixGreenBright
                        isActive -> CyberCyan
                        else -> Color(0xFF262E3B)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.Check, contentDescription = null, tint = TerminalBg, modifier = Modifier.size(12.dp))
            } else {
                Text(
                    text = "$number",
                    color = if (isActive) TerminalBg else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Text(
            text = title,
            color = if (isActive) MatrixGreenBright else if (isDone) TextPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun StepDivider(isDone: Boolean) {
    Box(
        modifier = Modifier
            .width(10.dp)
            .height(2.dp)
            .background(if (isDone) MatrixGreenBright else Color(0xFF262E3B))
    )
}

@Composable
fun FunctionTemplateItem(
    title: String,
    desc: String,
    defaultName: String,
    defaultBody: String,
    defaultLabel: String,
    isSelected: Boolean,
    onClick: (name: String, body: String, label: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MatrixGreen.copy(alpha = 0.15f) else TerminalSurfaceVariant)
            .border(1.dp, if (isSelected) MatrixGreenBright else TerminalBorder, RoundedCornerShape(8.dp))
            .clickable { onClick(defaultName, defaultBody, defaultLabel) }
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = if (isSelected) MatrixGreenBright else TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ArgChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E2836))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}
