package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.ScriptSyntaxValidator
import com.example.engine.ValidationResult
import com.example.engine.ValidationSeverity
import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.model.ScriptType
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

private data class FunctionStarterTemplate(
    val id: String,
    val title: String,
    val badge: String,
    val type: ScriptType,
    val name: String,
    val desc: String,
    val body: String,
    val label: String,
    val popupLabel: String = "STATUS",
    val popupMacro: String = "git status\n"
)

private val builtinFunctionTemplates = listOf(
    FunctionStarterTemplate(
        id = "gpush",
        title = "Git Push",
        badge = "GIT",
        type = ScriptType.BASH_FUNCTION,
        name = "gpush",
        desc = "Stage all changes, commit with arg, and push",
        body = "  local msg=\"\${1:-Quick sync}\"\n  git add .\n  git commit -m \"\$msg\"\n  git push\n  echo \"[OK] Pushed: \$msg\"",
        label = "G-PUSH",
        popupLabel = "STAT",
        popupMacro = "git status\n"
    ),
    FunctionStarterTemplate(
        id = "bkup",
        title = "Tar Backup",
        badge = "FILE",
        type = ScriptType.BASH_FUNCTION,
        name = "bkup",
        desc = "Create timestamped .tar.gz archive of a folder",
        body = "  local target=\"\${1:-.}\"\n  local out=\"backup_\$(date +%Y%m%d_%H%M%S).tar.gz\"\n  tar -czvf \"\$out\" \"\$target\"\n  echo \"[OK] Created \$out\"",
        label = "BKUP",
        popupLabel = "LS",
        popupMacro = "ls -lh *.tar.gz\n"
    ),
    FunctionStarterTemplate(
        id = "ffind",
        title = "Grep Find",
        badge = "SEARCH",
        type = ScriptType.BASH_FUNCTION,
        name = "ffind",
        desc = "Recursive case-safe grep skipping binary files",
        body = "  local query=\"\$1\"\n  local dir=\"\${2:-.}\"\n  grep -rnI \"\$query\" \"\$dir\"",
        label = "FFIND",
        popupLabel = "FILES",
        popupMacro = "find . -maxdepth 3\n"
    ),
    FunctionStarterTemplate(
        id = "pserve",
        title = "Python HTTP",
        badge = "PY",
        type = ScriptType.PYTHON_SCRIPT,
        name = "pserve",
        desc = "Serve current directory over local HTTP port",
        body = "#!/usr/bin/env python3\nimport http.server\nimport socketserver\nimport sys\n\nport = int(sys.argv[1]) if len(sys.argv) > 1 else 8080\nwith socketserver.TCPServer(('', port), http.server.SimpleHTTPRequestHandler) as httpd:\n    print(f'Serving on port {port}')\n    httpd.serve_forever()",
        label = "PSERV",
        popupLabel = "PORT",
        popupMacro = "ss -tuln\n"
    ),
    FunctionStarterTemplate(
        id = "pjson",
        title = "JSON Tool",
        badge = "PY",
        type = ScriptType.BASH_FUNCTION,
        name = "pjson",
        desc = "Format and validate JSON file or stdin stream",
        body = "  local file=\"\$1\"\n  if [ -z \"\$file\" ]; then\n    python3 -m json.tool\n  else\n    python3 -m json.tool \"\$file\"\n  fi",
        label = "PJSON",
        popupLabel = "HELP",
        popupMacro = "python3 -m json.tool --help\n"
    ),
    FunctionStarterTemplate(
        id = "withlock",
        title = "Wake-Lock",
        badge = "TERMUX",
        type = ScriptType.BASH_FUNCTION,
        name = "withlock",
        desc = "Hold Termux CPU wake-lock while running a command",
        body = "  termux-wake-lock\n  echo \"[LOCK] Running: \$@\"\n  \"\$@\"\n  local status=\$?\n  termux-wake-unlock\n  return \$status",
        label = "W-LOCK",
        popupLabel = "UNLOCK",
        popupMacro = "termux-wake-unlock\n"
    ),
    FunctionStarterTemplate(
        id = "sysup",
        title = "Pkg Update",
        badge = "ALIAS",
        type = ScriptType.BASH_ALIAS,
        name = "sysup",
        desc = "Update, upgrade, and clean Termux packages",
        body = "pkg update -y && pkg upgrade -y && pkg clean",
        label = "SYS-UP",
        popupLabel = "LIST",
        popupMacro = "pkg list-installed\n"
    ),
    FunctionStarterTemplate(
        id = "blank",
        title = "Blank Custom",
        badge = "NEW",
        type = ScriptType.BASH_FUNCTION,
        name = "my_cmd",
        desc = "Start from scratch with your own shell logic",
        body = "  echo \"Hello from Termux!\"",
        label = "MYCMD",
        popupLabel = "HELP",
        popupMacro = ""
    )
)

/**
 * Multi-step Compose dialog suite for guiding users through creating custom Bash/Python
 * functions & shortcuts, verifying syntax, dry-run testing, and saving to extra-keys rows.
 */
@Composable
fun BashFunctionWizardDialog(
    totalRows: Int,
    onSaveShortcut: (KeyItem, targetRow: Int, fnName: String, fnBody: String) -> Unit,
    onTestInTerminal: (command: String, fnName: String, fnBody: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) } // 1..4

    // Step 1: Identity & Mode
    var selectedTemplateId by remember { mutableStateOf("gpush") }
    var scriptType by remember { mutableStateOf(ScriptType.BASH_FUNCTION) }
    var functionName by remember { mutableStateOf("gpush") }
    var functionDesc by remember { mutableStateOf("Stage all changes, commit with arg, and push") }
    var step1Error by remember { mutableStateOf<String?>(null) }

    // Step 2: Logic & Syntax
    var functionBody by remember {
        mutableStateOf("  local msg=\"\${1:-Quick sync}\"\n  git add .\n  git commit -m \"\$msg\"\n  git push\n  echo \"[OK] Pushed: \$msg\"")
    }
    var detailedValidation by remember { mutableStateOf<ValidationResult?>(null) }

    // Step 3: Keycap & Ergonomics
    var keyLabel by remember { mutableStateOf("G-PUSH") }
    var targetRowIndex by remember { mutableIntStateOf(0) }
    var runImmediately by remember { mutableStateOf(true) }
    var enablePopup by remember { mutableStateOf(true) }
    var popupLabel by remember { mutableStateOf("STAT") }
    var popupMacro by remember { mutableStateOf("git status\n") }

    // Step 4: Dry-Run Test & Code Output
    var sampleArgs by remember { mutableStateOf("Fix layout") }
    var dryRunOutput by remember { mutableStateOf<List<String>?>(null) }
    var showOneLinerInstaller by remember { mutableStateOf(false) }

    val cleanName = functionName.trim()
    val identifierRegex = remember { Regex("^[a-zA-Z_][a-zA-Z0-9_.-]*$") }
    val isNameValid = cleanName.isNotBlank() && identifierRegex.matches(cleanName)

    val fullGeneratedCode = remember(scriptType, cleanName, functionBody) {
        when (scriptType) {
            ScriptType.BASH_FUNCTION -> "# $functionDesc\n$cleanName() {\n$functionBody\n}"
            ScriptType.BASH_ALIAS -> "# $functionDesc\nalias $cleanName='$functionBody'"
            ScriptType.PYTHON_SCRIPT -> {
                val hasShebang = functionBody.trimStart().startsWith("#!")
                val prefix = if (hasShebang) "" else "#!/usr/bin/env python3\n"
                "$prefix# Save as ~/bin/$cleanName.py && chmod +x ~/bin/$cleanName.py\n$functionBody"
            }
            ScriptType.ONE_LINER -> functionBody
        }
    }

    val oneLineTermuxInstaller = remember(scriptType, cleanName, fullGeneratedCode) {
        when (scriptType) {
            ScriptType.PYTHON_SCRIPT ->
                "mkdir -p ~/bin && cat << 'EOF' > ~/bin/$cleanName.py\n$fullGeneratedCode\nEOF\nchmod +x ~/bin/$cleanName.py"
            ScriptType.ONE_LINER -> fullGeneratedCode
            else ->
                "cat << 'EOF' >> ~/.bashrc\n$fullGeneratedCode\nEOF\nsource ~/.bashrc"
        }
    }

    val effectiveKeyCommand = remember(scriptType, cleanName, functionBody, runImmediately) {
        val base = when (scriptType) {
            ScriptType.PYTHON_SCRIPT -> "python3 ~/bin/$cleanName.py"
            ScriptType.ONE_LINER -> functionBody.trim()
            else -> cleanName
        }
        if (runImmediately) "$base\n" else "$base \""
    }

    val generatedKeyItem = remember(effectiveKeyCommand, cleanName, keyLabel, enablePopup, popupLabel, popupMacro) {
        val popup = if (enablePopup && popupMacro.isNotBlank()) {
            KeyPopup(key = popupMacro, display = popupLabel.ifBlank { popupMacro.trim() })
        } else null
        KeyItem(
            key = effectiveKeyCommand,
            display = keyLabel.ifBlank { cleanName },
            popup = popup
        )
    }

    fun applyTemplate(t: FunctionStarterTemplate) {
        selectedTemplateId = t.id
        scriptType = t.type
        functionName = t.name
        functionDesc = t.desc
        functionBody = t.body
        keyLabel = t.label
        popupLabel = t.popupLabel
        popupMacro = t.popupMacro
        enablePopup = t.popupMacro.isNotBlank()
        step1Error = null
        detailedValidation = null
        dryRunOutput = null
    }

    fun validateStep1AndAdvance() {
        if (cleanName.isBlank()) {
            step1Error = "Please enter a function/command name (e.g. 'gpush' or 'my_cmd')."
            return
        }
        if (!identifierRegex.matches(cleanName)) {
            step1Error = "Name '$cleanName' has invalid characters. Start with a letter or '_' and use only letters, digits, '_' or '-'."
            return
        }
        step1Error = null
        currentStep = 2
    }

    fun validateStep2AndAdvance() {
        val check = ScriptSyntaxValidator.validateScript(functionBody, scriptType, cleanName)
        detailedValidation = check
        if (check.isValid) {
            currentStep = 3
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
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints {
                val isCompact = maxHeight < 460.dp

                Surface(
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    color = TerminalSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isCompact) 10.dp else 14.dp),
                        verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 10.dp)
                    ) {
                        // ─── PINNED COMPACT HEADER ───
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
                                        .size(if (isCompact) 24.dp else 28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Code, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                }
                                Column {
                                    Text(
                                        text = "Function & Script Creator",
                                        color = TextPrimary,
                                        fontSize = if (isCompact) 14.sp else 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Step $currentStep of 4 • ${scriptType.displayName}",
                                        color = MatrixGreenBright,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp).testTag("dismiss_bash_wizard_dialog")
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                            }
                        }

                        // ─── CLICKABLE HORIZONTAL STEPPER ───
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StepPill(
                                number = 1,
                                title = "Setup",
                                isActive = currentStep == 1,
                                isDone = currentStep > 1,
                                onClick = { currentStep = 1 }
                            )
                            StepConnector(isDone = currentStep > 1)
                            StepPill(
                                number = 2,
                                title = "Logic",
                                isActive = currentStep == 2,
                                isDone = currentStep > 2,
                                onClick = {
                                    if (currentStep == 1) validateStep1AndAdvance() else currentStep = 2
                                }
                            )
                            StepConnector(isDone = currentStep > 2)
                            StepPill(
                                number = 3,
                                title = "Keycap",
                                isActive = currentStep == 3,
                                isDone = currentStep > 3,
                                onClick = {
                                    when {
                                        currentStep == 1 && isNameValid -> {
                                            val check = ScriptSyntaxValidator.validateScript(functionBody, scriptType, cleanName)
                                            detailedValidation = check
                                            if (check.isValid) currentStep = 3 else currentStep = 2
                                        }
                                        currentStep == 2 -> validateStep2AndAdvance()
                                        currentStep >= 3 -> currentStep = 3
                                    }
                                }
                            )
                            StepConnector(isDone = currentStep > 3)
                            StepPill(
                                number = 4,
                                title = "Test & Save",
                                isActive = currentStep == 4,
                                isDone = false,
                                onClick = {
                                    if (currentStep >= 3 || (isNameValid && ScriptSyntaxValidator.validateScript(functionBody, scriptType, cleanName).isValid)) {
                                        currentStep = 4
                                    }
                                }
                            )
                        }

                        HorizontalDivider(color = TerminalBorder)

                        // ─── SCROLLABLE STEP CONTENT ───
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            when (currentStep) {
                                1 -> {
                                    // ─── STEP 1: Mode, Quick Templates & Function Name (All visible without endless scrolling!) ───
                                    Text(
                                        text = "1. Pick Script Type:",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            ScriptType.BASH_FUNCTION to "Bash Function ()",
                                            ScriptType.PYTHON_SCRIPT to "Python Script (.py)",
                                            ScriptType.BASH_ALIAS to "Bash Alias"
                                        ).forEach { (type, title) ->
                                            FilterChip(
                                                selected = scriptType == type,
                                                onClick = {
                                                    scriptType = type
                                                    detailedValidation = null
                                                },
                                                label = { Text(title, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = MatrixGreen.copy(alpha = 0.25f),
                                                    selectedLabelColor = MatrixGreenBright
                                                )
                                            )
                                        }
                                    }

                                    Text(
                                        text = "2. Quick-Start Templates (tap to load, or customize below):",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        builtinFunctionTemplates.forEach { tmpl ->
                                            CompactTemplateCard(
                                                template = tmpl,
                                                isSelected = selectedTemplateId == tmpl.id,
                                                onClick = { applyTemplate(tmpl) }
                                            )
                                        }
                                    }

                                    Text(
                                        text = "3. Name & Purpose:",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    OutlinedTextField(
                                        value = functionName,
                                        onValueChange = {
                                            functionName = it
                                            step1Error = null
                                        },
                                        label = { Text("Command / Function Name") },
                                        placeholder = { Text("e.g. gpush, bkup, pserve") },
                                        singleLine = true,
                                        isError = step1Error != null || (cleanName.isNotEmpty() && !isNameValid),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MatrixGreenBright,
                                            unfocusedBorderColor = TerminalBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("wizard_fn_name_input")
                                    )

                                    // Live identifier validation & quick sanitize button
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val statusColor = when {
                                            step1Error != null -> RedAccent
                                            isNameValid -> MatrixGreenBright
                                            else -> AmberAccent
                                        }
                                        val statusMsg = when {
                                            step1Error != null -> step1Error!!
                                            cleanName.isBlank() -> "Enter a short command name without spaces."
                                            isNameValid -> "✓ Valid: ${
                                                when (scriptType) {
                                                    ScriptType.BASH_FUNCTION -> "$cleanName() { ... }"
                                                    ScriptType.BASH_ALIAS -> "alias $cleanName='...'"
                                                    ScriptType.PYTHON_SCRIPT -> "python3 ~/bin/$cleanName.py"
                                                    ScriptType.ONE_LINER -> cleanName
                                                }
                                            }"
                                            else -> "Invalid characters in '$cleanName'."
                                        }
                                        Text(
                                            text = statusMsg,
                                            color = statusColor,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (!isNameValid && functionName.isNotBlank()) {
                                            OutlinedButton(
                                                onClick = {
                                                    val sanitized = functionName.trim()
                                                        .replace(Regex("[^a-zA-Z0-9_-]"), "_")
                                                        .let { if (it.isEmpty() || !it[0].isLetter() && it[0] != '_') "fn_$it" else it }
                                                    functionName = sanitized
                                                    step1Error = null
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Auto-fix", fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = functionDesc,
                                        onValueChange = { functionDesc = it },
                                        label = { Text("Description (Optional comment in ~/.bashrc)") },
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

                                2 -> {
                                    // ─── STEP 2: Logic Editor, Quick Snippets & Syntax Verification ───
                                    Text(
                                        text = "Edit ${scriptType.displayName} Logic",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    // Language-aware snippet insertion bar
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Insert:", color = TextSecondary, fontSize = 10.sp)
                                        if (scriptType == ScriptType.PYTHON_SCRIPT) {
                                            ArgChip("import sys") { functionBody = "import sys\n$functionBody" }
                                            ArgChip("sys.argv[1]") { functionBody += " sys.argv[1]" }
                                            ArgChip("def main():") { functionBody += "\ndef main():\n    pass\n" }
                                            ArgChip("__main__") { functionBody += "\nif __name__ == \"__main__\":\n    main()\n" }
                                            ArgChip("print(f\"...\")") { functionBody += "\nprint(f\"Done\")" }
                                        } else {
                                            ArgChip("\$1 (1st Arg)") { functionBody += " \"\$1\"" }
                                            ArgChip("\${1:-default}") { functionBody += " \"\${1:-default}\"" }
                                            ArgChip("\$@ (All Args)") { functionBody += " \"\$@\"" }
                                            ArgChip("if [ -f ]") { functionBody += "\n  if [ -f \"\$1\" ]; then\n    echo \"Found\"\n  fi" }
                                            ArgChip("termux-toast") { functionBody += "\n  termux-toast \"Done\"" }
                                            ArgChip("return 0") { functionBody += "\n  return 0" }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = functionBody,
                                        onValueChange = {
                                            functionBody = it
                                            detailedValidation = null
                                        },
                                        label = {
                                            Text(
                                                when (scriptType) {
                                                    ScriptType.BASH_FUNCTION -> "Body inside $cleanName() { ... }"
                                                    ScriptType.BASH_ALIAS -> "Command for alias $cleanName='...'"
                                                    ScriptType.PYTHON_SCRIPT -> "Python script (~bin/$cleanName.py)"
                                                    ScriptType.ONE_LINER -> "Terminal macro command"
                                                }
                                            )
                                        },
                                        minLines = 2,
                                        maxLines = 6,
                                        textStyle = TextStyle(
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("wizard_fn_body_input")
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                detailedValidation = ScriptSyntaxValidator.validateScript(
                                                    code = functionBody,
                                                    type = scriptType,
                                                    name = cleanName
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (detailedValidation?.isValid == true) MatrixGreenBright else CyberCyan,
                                                contentColor = TerminalBg
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("wizard_verify_syntax_btn")
                                        ) {
                                            Icon(
                                                imageVector = if (detailedValidation?.isValid == true) Icons.Default.CheckCircle else Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (detailedValidation?.isValid == true) "Syntax Clean" else "Verify Syntax",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                functionBody = ""
                                                detailedValidation = null
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                                        ) {
                                            Text("Clear", fontSize = 12.sp)
                                        }
                                    }

                                    // Rich Diagnostic Feedback Card
                                    detailedValidation?.let { res ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (res.isValid) MatrixGreen.copy(alpha = 0.14f) else RedAccent.copy(alpha = 0.14f))
                                                .border(1.dp, if (res.isValid) MatrixGreenBright else RedAccent, RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (res.isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                                        contentDescription = null,
                                                        tint = if (res.isValid) MatrixGreenBright else RedAccent,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = res.summary,
                                                        color = if (res.isValid) MatrixGreenBright else RedAccent,
                                                        fontSize = 11.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                res.issues.forEach { issue ->
                                                    val color = when (issue.severity) {
                                                        ValidationSeverity.ERROR -> RedAccent
                                                        ValidationSeverity.WARNING -> AmberAccent
                                                        ValidationSeverity.INFO -> CyberCyan
                                                    }
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color(0xFF0A0E14), RoundedCornerShape(4.dp))
                                                            .padding(6.dp)
                                                    ) {
                                                        Text(
                                                            text = "${if (issue.line != null) "Line ${issue.line}: " else ""}${issue.message}",
                                                            color = color,
                                                            fontSize = 11.sp,
                                                            fontFamily = FontFamily.Monospace
                                                        )
                                                        if (issue.suggestion != null) {
                                                            Text(
                                                                text = "Tip: ${issue.suggestion}",
                                                                color = TextSecondary,
                                                                fontSize = 10.sp,
                                                                fontFamily = FontFamily.Monospace
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                3 -> {
                                    // ─── STEP 3: Keycap Ergonomics, Execution Mode & Swipe-Up Popup ───
                                    Text(
                                        text = "Design Extra-Key Button & Placement",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    OutlinedTextField(
                                        value = keyLabel,
                                        onValueChange = { keyLabel = it },
                                        label = { Text("Keycap Display Label (1–6 chars ideal)") },
                                        placeholder = { Text("e.g. G-PUSH, BKUP, PSERV") },
                                        singleLine = true,
                                        supportingText = {
                                            val len = keyLabel.trim().length
                                            Text(
                                                text = if (len <= 6) "$len/6 chars (fits 7-col Termux grid cleanly)" else "$len chars (may truncate on narrow phones)",
                                                color = if (len <= 6) MatrixGreenBright else AmberAccent,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MatrixGreenBright,
                                            unfocusedBorderColor = TerminalBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("wizard_key_label_input")
                                    )

                                    // Tap behavior: Run immediately vs Prompt for Args
                                    Text("Tap Behavior:", color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = runImmediately,
                                            onClick = { runImmediately = true },
                                            label = { Text("Run Immediately (↵)", fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MatrixGreen.copy(alpha = 0.25f),
                                                selectedLabelColor = MatrixGreenBright
                                            )
                                        )
                                        FilterChip(
                                            selected = !runImmediately,
                                            onClick = { runImmediately = false },
                                            label = { Text("Prompt for Args", fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                                                selectedLabelColor = CyberCyan
                                            )
                                        )
                                    }

                                    // Row selector
                                    Text("Target Extra-Keys Row:", color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        for (r in 0 until totalRows.coerceAtLeast(1)) {
                                            FilterChip(
                                                selected = targetRowIndex == r,
                                                onClick = { targetRowIndex = r },
                                                label = { Text("Row ${r + 1}", fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                                                    selectedLabelColor = CyberCyan
                                                )
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = TerminalBorder)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Swipe-Up Popup Action", color = AmberAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Secondary command fired when swiping up on key", color = TextSecondary, fontSize = 11.sp)
                                        }
                                        Switch(
                                            checked = enablePopup,
                                            onCheckedChange = { enablePopup = it },
                                            colors = SwitchDefaults.colors(checkedThumbColor = AmberAccent, checkedTrackColor = AmberAccent.copy(alpha = 0.3f))
                                        )
                                    }

                                    if (enablePopup) {
                                        Row(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            ArgChip("git status") { popupLabel = "STAT"; popupMacro = "git status\n" }
                                            ArgChip("ls -la") { popupLabel = "LS"; popupMacro = "ls -la\n" }
                                            ArgChip("clear") { popupLabel = "CLR"; popupMacro = "clear\n" }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = popupLabel,
                                                onValueChange = { popupLabel = it },
                                                label = { Text("Popup Label") },
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = AmberAccent,
                                                    unfocusedBorderColor = TerminalBorder,
                                                    focusedTextColor = TextPrimary,
                                                    unfocusedTextColor = TextPrimary
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = popupMacro,
                                                onValueChange = { popupMacro = it },
                                                label = { Text("Popup Command") },
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = AmberAccent,
                                                    unfocusedBorderColor = TerminalBorder,
                                                    focusedTextColor = TextPrimary,
                                                    unfocusedTextColor = TextPrimary
                                                ),
                                                modifier = Modifier.weight(1.4f)
                                            )
                                        }
                                    }

                                    // Live Keycap Preview
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF000000))
                                            .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            KeyCapButton(
                                                item = generatedKeyItem,
                                                onClick = {},
                                                isEditMode = true,
                                                isTermuxStyle = true,
                                                modifier = Modifier.width(84.dp)
                                            )
                                            Column {
                                                Text("Live Termux Keycap", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                                Text(
                                                    text = "Tap sends: ${effectiveKeyCommand.replace("\n", "↵")}",
                                                    color = MatrixGreenBright,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                if (enablePopup && popupMacro.isNotBlank()) {
                                                    Text(
                                                        text = "Swipe-up sends: ${popupMacro.replace("\n", "↵")}",
                                                        color = AmberAccent,
                                                        fontSize = 10.sp,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                4 -> {
                                    // ─── STEP 4: Interactive Dry-Run, Termux Installer & Save ───
                                    Text(
                                        text = "Test, Install & Save Shortcut",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    // Interactive Dry-Run Simulator right inside Step 4
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = sampleArgs,
                                            onValueChange = { sampleArgs = it },
                                            label = { Text("Sample Arg (\$1)") },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = CyberCyan,
                                                unfocusedBorderColor = TerminalBorder,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        Button(
                                            onClick = {
                                                val arg = sampleArgs.ifBlank { "Quick sync" }
                                                val previewLines = mutableListOf<String>()
                                                previewLines.add("$ $cleanName \"$arg\"")
                                                functionBody.lines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }.forEach { line ->
                                                    val expanded = line
                                                        .replace("\${1:-Quick sync}", arg)
                                                        .replace("\${1:-.}", arg)
                                                        .replace("\${1:-8080}", arg)
                                                        .replace("\$1", arg)
                                                        .replace("\$@", arg)
                                                        .replace("\$msg", arg)
                                                    previewLines.add("  > $expanded")
                                                }
                                                previewLines.add("[OK] Simulated exit code: 0")
                                                dryRunOutput = previewLines
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = TerminalBg)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Dry-Run", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }

                                    dryRunOutput?.let { lines ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF070A0E))
                                                .border(1.dp, MatrixGreenBright, RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                lines.forEachIndexed { idx, line ->
                                                    Text(
                                                        text = line,
                                                        color = if (idx == 0) CyberCyan else if (line.startsWith("[OK]")) MatrixGreenBright else TextPrimary,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val cmd = when (scriptType) {
                                                ScriptType.PYTHON_SCRIPT -> "python3 ~/bin/$cleanName.py $sampleArgs\n"
                                                ScriptType.ONE_LINER -> "$functionBody\n"
                                                else -> "$cleanName $sampleArgs\n".trimEnd() + "\n"
                                            }
                                            onTestInTerminal(cmd, cleanName, functionBody)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("wizard_run_test_btn")
                                    ) {
                                        Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Execute in Full Terminal Sandbox", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    // Code Definition vs One-Line Installer toggle
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (showOneLinerInstaller) "One-Line Termux Installer:" else "Generated Code:",
                                            color = CyberCyan,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        FilterChip(
                                            selected = showOneLinerInstaller,
                                            onClick = { showOneLinerInstaller = !showOneLinerInstaller },
                                            label = { Text(if (showOneLinerInstaller) "Show Clean Code" else "One-Line Installer", fontSize = 10.sp) }
                                        )
                                    }

                                    val displayedSnippet = if (showOneLinerInstaller) oneLineTermuxInstaller else fullGeneratedCode
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
                                                text = displayedSnippet,
                                                color = MatrixGreenBright,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = {
                                                    StorageHelper.copyToClipboard(context, "Termux Function", displayedSnippet)
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(15.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = TerminalBorder)

                        // ─── PINNED STICKY FOOTER (ALWAYS VISIBLE ABOVE IME & IN LANDSCAPE) ───
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
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
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

                            when (currentStep) {
                                1 -> {
                                    Button(
                                        onClick = { validateStep1AndAdvance() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenBright, contentColor = TerminalBg),
                                        modifier = Modifier.testTag("wizard_dialog_next_btn")
                                    ) {
                                        Text("Next: Logic", fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                                2 -> {
                                    Button(
                                        onClick = { validateStep2AndAdvance() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenBright, contentColor = TerminalBg),
                                        modifier = Modifier.testTag("wizard_dialog_next_btn")
                                    ) {
                                        Text("Next: Keycap", fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                                3 -> {
                                    Button(
                                        onClick = { currentStep = 4 },
                                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenBright, contentColor = TerminalBg),
                                        modifier = Modifier.testTag("wizard_dialog_next_btn")
                                    ) {
                                        Text("Next: Test & Save", fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                                4 -> {
                                    Button(
                                        onClick = {
                                            onSaveShortcut(generatedKeyItem, targetRowIndex, cleanName, functionBody)
                                            StorageHelper.copyToClipboard(context, "Termux Function", fullGeneratedCode, showToast = false)
                                            onDismiss()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenBright, contentColor = TerminalBg),
                                        modifier = Modifier.testTag("wizard_save_to_layout_btn")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Save to Row ${targetRowIndex + 1}", fontWeight = FontWeight.Bold)
                                    }
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
private fun StepPill(
    number: Int,
    title: String,
    isActive: Boolean,
    isDone: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    isActive -> CyberCyan.copy(alpha = 0.18f)
                    isDone -> MatrixGreen.copy(alpha = 0.14f)
                    else -> TerminalSurfaceVariant
                }
            )
            .border(
                1.dp,
                when {
                    isActive -> CyberCyan
                    isDone -> MatrixGreenBright.copy(alpha = 0.6f)
                    else -> TerminalBorder
                },
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
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
                Icon(Icons.Default.Check, contentDescription = null, tint = TerminalBg, modifier = Modifier.size(11.dp))
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
            color = when {
                isActive -> CyberCyan
                isDone -> MatrixGreenBright
                else -> TextSecondary
            },
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun StepConnector(isDone: Boolean) {
    Box(
        modifier = Modifier
            .width(10.dp)
            .height(2.dp)
            .background(if (isDone) MatrixGreenBright else Color(0xFF262E3B))
    )
}

@Composable
private fun CompactTemplateCard(
    template: FunctionStarterTemplate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(138.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MatrixGreen.copy(alpha = 0.18f) else TerminalSurfaceVariant)
            .border(1.dp, if (isSelected) MatrixGreenBright else TerminalBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = template.badge,
                color = if (isSelected) MatrixGreenBright else CyberCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = template.name,
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Text(
            text = template.title,
            color = if (isSelected) MatrixGreenBright else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
        Text(
            text = template.desc,
            color = TextSecondary,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            maxLines = 2
        )
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
fun ArgChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E2836))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(text = label, color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}
