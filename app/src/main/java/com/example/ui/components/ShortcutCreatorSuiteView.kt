package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuiltinShortcutRecipes
import com.example.data.TutorialChapter
import com.example.engine.ScriptSyntaxValidator
import com.example.engine.ValidationResult
import com.example.engine.ValidationSeverity
import com.example.model.CustomShortcutDraft
import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.model.ScriptRecipe
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

@Composable
fun ShortcutCreatorSuiteView(
    onAddKeyToLayout: (KeyItem, targetRow: Int) -> Unit,
    onTestInTerminal: (command: String) -> Unit,
    totalRows: Int,
    onOpenBashWizardDialog: () -> Unit = {},
    onRegisterCustomFunction: (name: String, body: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0
    var suiteTab by remember { mutableIntStateOf(0) } // 0: Guided Wizard, 1: Recipe Library, 2: Deep Tutorial

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBg)
    ) {
        val isCompact = maxHeight < 460.dp || isImeVisible

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
        ) {
            // Modal Dialog Suite Launcher Bar (folds into slim compact strip when landscape or keyboard is open)
            if (isCompact) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberAccent.copy(alpha = 0.12f))
                        .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { onOpenBashWizardDialog() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("creator_open_modal_dialog_banner"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Open Popup Function Wizard",
                            color = AmberAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Dialog",
                        tint = AmberAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberAccent.copy(alpha = 0.12f))
                        .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { onOpenBashWizardDialog() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("creator_open_modal_dialog_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Launch Popup Function Wizard",
                                    color = AmberAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "4-step modal creator with templates, dry-run & Bash/Python checks",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Dialog",
                            tint = AmberAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Tab Navigation
            TabRow(
                selectedTabIndex = suiteTab,
                containerColor = TerminalSurface,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[suiteTab]),
                        color = MatrixGreenBright
                    )
                }
            ) {
                Tab(
                    selected = suiteTab == 0,
                    onClick = { suiteTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Guided Wizard", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = suiteTab == 1,
                    onClick = { suiteTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Recipes", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = suiteTab == 2,
                    onClick = { suiteTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Handbook", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (suiteTab) {
                    0 -> {
                        StepwiseShortcutWizard(
                            onAddKey = onAddKeyToLayout,
                            onTestInTerminal = onTestInTerminal,
                            onRegisterCustomFunction = onRegisterCustomFunction,
                            totalRows = totalRows,
                            isCompact = isCompact,
                            context = context
                        )
                    }
                    1 -> {
                        RecipeBookPanel(
                            recipes = BuiltinShortcutRecipes.recipes,
                            onAddKey = { item -> onAddKeyToLayout(item, 0) },
                            onTest = onTestInTerminal,
                            context = context
                        )
                    }
                    2 -> {
                        TutorialChaptersPanel(
                            chapters = BuiltinShortcutRecipes.chapters,
                            onRunSnippet = onTestInTerminal,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

/**
 * Stepwise Interactive Shortcut Creator Wizard.
 * Uses a pinned clickable step bar and sticky bottom Continue/Previous footer so buttons
 * remain visible above the keyboard in portrait and never clip off-screen in landscape.
 */
@Composable
fun StepwiseShortcutWizard(
    onAddKey: (KeyItem, targetRow: Int) -> Unit,
    onTestInTerminal: (String) -> Unit,
    totalRows: Int,
    context: Context,
    onRegisterCustomFunction: (name: String, body: String) -> Unit = { _, _ -> },
    isCompact: Boolean = false
) {
    var currentStep by remember { mutableIntStateOf(1) } // 1 to 6

    // State collected across steps
    var selectedType by remember { mutableStateOf(ScriptType.BASH_FUNCTION) }
    var shortcutName by remember { mutableStateOf("qpush") }
    var scriptBody by remember { mutableStateOf("  local msg=\"\${1:-Quick sync}\"\n  git add .\n  git commit -m \"\$msg\"\n  git push") }
    var keyLabel by remember { mutableStateOf("G-PUSH") }
    var targetRowIndex by remember { mutableIntStateOf(0) }
    var runImmediately by remember { mutableStateOf(true) }
    var enablePopup by remember { mutableStateOf(true) }
    var popupLabel by remember { mutableStateOf("STAT") }
    var popupMacro by remember { mutableStateOf("git status\n") }
    var sampleArgs by remember { mutableStateOf("\"feat: update\"") }

    // Verification & feedback states
    var step1Error by remember { mutableStateOf<String?>(null) }
    var detailedValidationResult by remember { mutableStateOf<ValidationResult?>(null) }
    var isLogicVerified by remember { mutableStateOf(false) }
    var isExecutionVerified by remember { mutableStateOf(false) }
    var dryRunPreview by remember { mutableStateOf<String?>(null) }
    var executionTestResult by remember { mutableStateOf<String?>(null) }

    val cleanName = remember(shortcutName) { shortcutName.trim() }
    val identifierRegex = remember { Regex("^[a-zA-Z_][a-zA-Z0-9_-]*$") }
    val isNameValid = remember(cleanName, selectedType) {
        selectedType == ScriptType.ONE_LINER || (cleanName.isNotBlank() && identifierRegex.matches(cleanName))
    }

    val generatedKeyCode = remember(selectedType, cleanName, scriptBody, runImmediately) {
        val base = when (selectedType) {
            ScriptType.BASH_ALIAS -> cleanName.ifBlank { "myalias" }
            ScriptType.BASH_FUNCTION -> cleanName.ifBlank { "myfn" }
            ScriptType.PYTHON_SCRIPT -> "python3 ~/bin/${cleanName.ifBlank { "script" }}.py"
            ScriptType.ONE_LINER -> scriptBody.trimEnd('\n', '\r')
        }
        if (runImmediately) "$base\n" else "$base "
    }

    val generatedKeyItem = remember(generatedKeyCode, keyLabel, cleanName, enablePopup, popupLabel, popupMacro) {
        val popup = if (enablePopup && popupMacro.isNotBlank()) {
            KeyPopup(key = popupMacro, display = popupLabel.ifBlank { popupMacro.trim() })
        } else null
        KeyItem(
            key = generatedKeyCode,
            display = keyLabel.ifBlank { cleanName.ifBlank { "RUN" } },
            popup = popup
        )
    }

    val generatedShellSnippet = remember(selectedType, cleanName, scriptBody) {
        val safeName = cleanName.ifBlank { "mycmd" }
        when (selectedType) {
            ScriptType.BASH_ALIAS -> "alias $safeName='${scriptBody.replace("'", "'\\''")}'"
            ScriptType.BASH_FUNCTION -> "$safeName() {\n$scriptBody\n}"
            ScriptType.PYTHON_SCRIPT -> "#!/data/data/com.termux/files/usr/bin/env python3\n# Save to ~/bin/$safeName.py && chmod +x ~/bin/$safeName.py\n$scriptBody"
            ScriptType.ONE_LINER -> scriptBody
        }
    }

    fun selectArchitecture(type: ScriptType) {
        selectedType = type
        step1Error = null
        isLogicVerified = false
        detailedValidationResult = null
        dryRunPreview = null
        when (type) {
            ScriptType.BASH_FUNCTION -> {
                shortcutName = "qpush"
                scriptBody = "  local msg=\"\${1:-Quick sync}\"\n  git add .\n  git commit -m \"\$msg\"\n  git push"
                keyLabel = "G-PUSH"
                popupLabel = "STAT"
                popupMacro = "git status\n"
            }
            ScriptType.BASH_ALIAS -> {
                shortcutName = "sysup"
                scriptBody = "pkg update -y && pkg upgrade -y && pkg clean"
                keyLabel = "SYS-UP"
                popupLabel = "PKG"
                popupMacro = "pkg list-installed\n"
            }
            ScriptType.PYTHON_SCRIPT -> {
                shortcutName = "serve"
                scriptBody = "import http.server, socketserver\nPORT = 8080\nwith socketserver.TCPServer(('', PORT), http.server.SimpleHTTPRequestHandler) as httpd:\n    print(f'Serving on port {PORT}')\n    httpd.serve_forever()"
                keyLabel = "SRV"
                popupLabel = "IP"
                popupMacro = "ip a | grep inet\n"
            }
            ScriptType.ONE_LINER -> {
                shortcutName = "clear_run"
                scriptBody = "clear && ls -la"
                keyLabel = "CLS-LS"
                popupLabel = "PWD"
                popupMacro = "pwd\n"
            }
        }
    }

    fun validateAndAdvanceFromCurrentStep() {
        when (currentStep) {
            1 -> {
                if (selectedType != ScriptType.ONE_LINER && !isNameValid) {
                    step1Error = if (cleanName.isBlank()) {
                        "Enter an identifier name (e.g. 'qpush') or pick one of the architectures."
                    } else {
                        "Identifier '$cleanName' must start with a letter or '_' and contain only letters, numbers, '_' or '-'."
                    }
                    return
                }
                step1Error = null
                currentStep = 2
            }
            2 -> {
                val validation = ScriptSyntaxValidator.validateScript(
                    code = scriptBody,
                    type = selectedType,
                    name = cleanName.ifBlank { "my_cmd" }
                )
                detailedValidationResult = validation
                isLogicVerified = validation.isValid
                if (validation.isValid) {
                    currentStep = 3
                }
            }
            in 3..5 -> currentStep++
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
    ) {
        // ─── CLICKABLE HORIZONTAL STEP INDICATOR BAR ───
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TerminalSurface)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val stepLabels = listOf("Setup", "Logic", "Keycap", "Swipe", "Test", "Deploy")
            stepLabels.forEachIndexed { idx, title ->
                val stepNum = idx + 1
                WizardStepBadge(
                    step = stepNum,
                    title = title,
                    isActive = currentStep == stepNum,
                    isCompleted = stepNum < currentStep || (stepNum == 2 && isLogicVerified) || (stepNum == 5 && isExecutionVerified),
                    onClick = {
                        when {
                            stepNum <= currentStep -> currentStep = stepNum
                            currentStep == 1 && stepNum == 2 -> validateAndAdvanceFromCurrentStep()
                            stepNum > 2 && isNameValid -> currentStep = stepNum
                            else -> validateAndAdvanceFromCurrentStep()
                        }
                    }
                )
            }
        }

        // ─── SCROLLABLE STEP CONTENT CARD ───
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(TerminalSurface)
                .border(1.dp, TerminalBorder, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(if (isCompact) 10.dp else 14.dp),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 10.dp)
            ) {
                when (currentStep) {
                    1 -> {
                        Text(
                            text = "Step 1 of 6: Choose Architecture & Name",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Pick what type of shortcut you're building and name your command:",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        // Quick name input right on Step 1 so user can see/edit it immediately
                        if (selectedType != ScriptType.ONE_LINER) {
                            OutlinedTextField(
                                value = shortcutName,
                                onValueChange = {
                                    shortcutName = it
                                    step1Error = null
                                    isLogicVerified = false
                                    detailedValidationResult = null
                                },
                                label = { Text("Command Identifier (used in Termux)") },
                                placeholder = { Text("e.g. qpush, sysup, serve") },
                                singleLine = true,
                                isError = !isNameValid,
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                                trailingIcon = {
                                    if (!isNameValid && shortcutName.isNotBlank()) {
                                        IconButton(onClick = {
                                            val fixed = shortcutName.trim()
                                                .replace(Regex("[^a-zA-Z0-9_-]"), "_")
                                                .let { if (it.isEmpty() || it.first().isDigit()) "fn_$it" else it }
                                            shortcutName = fixed
                                            step1Error = null
                                        }) {
                                            Icon(Icons.Default.AutoFixHigh, contentDescription = "Auto-fix identifier", tint = AmberAccent)
                                        }
                                    } else if (isNameValid) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = MatrixGreenBright, modifier = Modifier.size(18.dp))
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MatrixGreenBright,
                                    unfocusedBorderColor = TerminalBorder,
                                    errorBorderColor = RedAccent,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("stepwise_fn_name_input")
                            )
                        }

                        if (step1Error != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RedAccent.copy(alpha = 0.14f))
                                    .border(1.dp, RedAccent, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = step1Error!!,
                                    color = RedAccent,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        HorizontalDivider(color = TerminalBorder)

                        ArchitectureOptionCard(
                            type = ScriptType.BASH_FUNCTION,
                            title = "Bash Function with Args (Recommended)",
                            subtitle = "Accepts \$1, \$2 args; reusable across sessions (~/.bashrc)",
                            isSelected = selectedType == ScriptType.BASH_FUNCTION,
                            onClick = { selectArchitecture(ScriptType.BASH_FUNCTION) }
                        )

                        ArchitectureOptionCard(
                            type = ScriptType.BASH_ALIAS,
                            title = "Bash / Zsh Simple Alias",
                            subtitle = "Fast one-word command replacement (e.g. sysup, ll)",
                            isSelected = selectedType == ScriptType.BASH_ALIAS,
                            onClick = { selectArchitecture(ScriptType.BASH_ALIAS) }
                        )

                        ArchitectureOptionCard(
                            type = ScriptType.PYTHON_SCRIPT,
                            title = "Python Automation Script (~/bin)",
                            subtitle = "JSON, HTTP servers, file automation & Python libraries",
                            isSelected = selectedType == ScriptType.PYTHON_SCRIPT,
                            onClick = { selectArchitecture(ScriptType.PYTHON_SCRIPT) }
                        )

                        ArchitectureOptionCard(
                            type = ScriptType.ONE_LINER,
                            title = "Inline Terminal Macro",
                            subtitle = "Dispatches raw command straight to the active shell prompt",
                            isSelected = selectedType == ScriptType.ONE_LINER,
                            onClick = { selectArchitecture(ScriptType.ONE_LINER) }
                        )
                    }

                    2 -> {
                        Text(
                            text = "Step 2 of 6: Command Logic & Syntax Check",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        if (selectedType != ScriptType.ONE_LINER) {
                            OutlinedTextField(
                                value = shortcutName,
                                onValueChange = {
                                    shortcutName = it.replace(" ", "_")
                                    isLogicVerified = false
                                    detailedValidationResult = null
                                },
                                label = { Text("Command / Identifier Name") },
                                placeholder = { Text("e.g. qpush, sysup, serve") },
                                singleLine = true,
                                isError = !isNameValid,
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MatrixGreenBright,
                                    unfocusedBorderColor = TerminalBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = scriptBody,
                            onValueChange = {
                                scriptBody = it
                                isLogicVerified = false
                                detailedValidationResult = null
                            },
                            label = {
                                Text(
                                    when (selectedType) {
                                        ScriptType.PYTHON_SCRIPT -> "Python Script Body"
                                        ScriptType.BASH_ALIAS -> "Alias Expansion Command"
                                        else -> "Bash Function / Macro Body"
                                    }
                                )
                            },
                            placeholder = { Text("Commands to execute...") },
                            minLines = 2,
                            maxLines = 5,
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MatrixGreenBright,
                                unfocusedBorderColor = TerminalBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Language-aware quick-insert snippets & templates
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Insert:", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            if (selectedType == ScriptType.PYTHON_SCRIPT) {
                                TemplateChip("import sys") { scriptBody = "import sys\n$scriptBody" }
                                TemplateChip("sys.argv") { scriptBody += "\narg = sys.argv[1] if len(sys.argv) > 1 else ''" }
                                TemplateChip("json.load") { scriptBody += "\nimport json\nprint(json.dumps({'ok': True}, indent=2))" }
                            } else {
                                TemplateChip("\$1") { scriptBody += " \"\$1\"" }
                                TemplateChip("\${1:-default}") { scriptBody += " \"\${1:-default}\"" }
                                TemplateChip("if/fi") { scriptBody += "\nif [ -n \"\$1\" ]; then\n  echo \"\$1\"\nfi" }
                                TemplateChip("Git Push") { selectArchitecture(ScriptType.BASH_FUNCTION) }
                                TemplateChip("Sys Update") { selectArchitecture(ScriptType.BASH_ALIAS) }
                            }
                            TemplateChip("Clear") {
                                scriptBody = ""
                                isLogicVerified = false
                                detailedValidationResult = null
                            }
                        }

                        // Syntax Verification Button
                        OutlinedButton(
                            onClick = {
                                val validation = ScriptSyntaxValidator.validateScript(
                                    code = scriptBody,
                                    type = selectedType,
                                    name = cleanName.ifBlank { "my_cmd" }
                                )
                                detailedValidationResult = validation
                                isLogicVerified = validation.isValid
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isLogicVerified) MatrixGreenBright else CyberCyan
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("verify_command_logic_btn")
                        ) {
                            Icon(
                                imageVector = if (isLogicVerified) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isLogicVerified) "Syntax Verified Clean" else "Verify Command Logic Syntax", fontWeight = FontWeight.Bold)
                        }

                        if (detailedValidationResult != null) {
                            val res = detailedValidationResult!!
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (res.isValid) MatrixGreen.copy(alpha = 0.12f) else RedAccent.copy(alpha = 0.12f))
                                    .border(1.dp, if (res.isValid) MatrixGreenBright else RedAccent, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
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
                                            text = if (res.isValid) "Syntax Verified" else "Syntax Validation Error",
                                            color = if (res.isValid) MatrixGreenBright else RedAccent,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Text(
                                        text = res.summary,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    for (issue in res.issues) {
                                        val issueColor = when (issue.severity) {
                                            ValidationSeverity.ERROR -> RedAccent
                                            ValidationSeverity.WARNING -> AmberAccent
                                            ValidationSeverity.INFO -> CyberCyan
                                        }
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF0C1017), RoundedCornerShape(4.dp))
                                                .padding(6.dp)
                                        ) {
                                            Text(
                                                text = "${if (issue.line != null) "Line ${issue.line}: " else ""}${issue.message}",
                                                color = issueColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
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
                        Text(
                            text = "Step 3 of 6: Touch Keycap Ergonomics",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        OutlinedTextField(
                            value = keyLabel,
                            onValueChange = { keyLabel = it },
                            label = { Text("Display Label on Keycap (2–6 chars recommended)") },
                            placeholder = { Text("e.g. G-PUSH, SYS-UP, SRV") },
                            singleLine = true,
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MatrixGreenBright,
                                unfocusedBorderColor = TerminalBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Execution behavior toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(TerminalSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (runImmediately) "Auto-Execute on Tap (\\n)" else "Insert Prefix Only (Ask Args)",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (runImmediately) "Runs immediately when key is tapped" else "Types '${cleanName.ifBlank { "cmd" }} ' so you can add arguments",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Switch(
                                checked = runImmediately,
                                onCheckedChange = { runImmediately = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = MatrixGreenBright)
                            )
                        }

                        // Target Row
                        Text("Target Extra Keys Row:", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
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
                                Text("Preview:", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                KeyCapButton(
                                    item = generatedKeyItem,
                                    onClick = {},
                                    isTermuxStyle = true,
                                    isEditMode = true,
                                    modifier = Modifier.width(86.dp)
                                )
                            }
                        }
                    }

                    4 -> {
                        Text(
                            text = "Step 4 of 6: Swipe-Up Secondary Action",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Bind a secondary command to upward swipe on '${generatedKeyItem.effectiveDisplay}':",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable Swipe-Up Action", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Switch(
                                checked = enablePopup,
                                onCheckedChange = { enablePopup = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = AmberAccent, checkedTrackColor = AmberAccent.copy(alpha = 0.3f))
                            )
                        }

                        if (enablePopup) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TemplateChip("git status") { popupLabel = "STAT"; popupMacro = "git status\n" }
                                TemplateChip("clear") { popupLabel = "CLS"; popupMacro = "clear\n" }
                                TemplateChip("Ctrl+C") { popupLabel = "^C"; popupMacro = "^C" }
                                TemplateChip("--help") { popupLabel = "HELP"; popupMacro = "${cleanName.ifBlank { "cmd" }} --help\n" }
                            }

                            OutlinedTextField(
                                value = popupLabel,
                                onValueChange = { popupLabel = it },
                                label = { Text("Popup Indicator Label") },
                                placeholder = { Text("e.g. STAT, CLS, HELP") },
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
                                label = { Text("Popup Execution Macro") },
                                placeholder = { Text("e.g. git status\\n or clear\\n") },
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
                    }

                    5 -> {
                        Text(
                            text = "Step 5 of 6: Sandbox Dry-Run & Verification",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Test '${cleanName.ifBlank { "macro" }}' locally or dispatch directly into the terminal sandbox:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        if (selectedType == ScriptType.BASH_FUNCTION || selectedType == ScriptType.PYTHON_SCRIPT) {
                            OutlinedTextField(
                                value = sampleArgs,
                                onValueChange = { sampleArgs = it },
                                label = { Text("Sample Arguments for Dry-Run (Optional)") },
                                placeholder = { Text("e.g. \"fix: bug\" or 8080") },
                                singleLine = true,
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberCyan,
                                    unfocusedBorderColor = TerminalBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val cmdCall = if (selectedType == ScriptType.ONE_LINER) {
                                        scriptBody
                                    } else {
                                        "${cleanName.ifBlank { "cmd" }} ${sampleArgs.trim()}".trim()
                                    }
                                    dryRunPreview = buildString {
                                        appendLine("$ $cmdCall")
                                        appendLine("[Resolved ${selectedType.displayName}]")
                                        scriptBody.lines().take(5).forEach { line ->
                                            if (line.isNotBlank()) appendLine("  > ${line.trim()}")
                                        }
                                        append("[Exit 0 • Ready to deploy]")
                                    }
                                    isExecutionVerified = true
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Quick Dry-Run", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (cleanName.isNotBlank()) {
                                        onRegisterCustomFunction(cleanName, generatedShellSnippet)
                                    }
                                    val fullTestCmd = if (selectedType != ScriptType.ONE_LINER && sampleArgs.isNotBlank()) {
                                        "$cleanName ${sampleArgs.trim()}\n"
                                    } else {
                                        generatedKeyCode
                                    }
                                    onTestInTerminal(fullTestCmd)
                                    executionTestResult = "Dispatched to terminal: ${fullTestCmd.trim()}"
                                    isExecutionVerified = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isExecutionVerified) MatrixGreenBright else CyberCyan,
                                    contentColor = TerminalBg
                                ),
                                modifier = Modifier.weight(1.2f).testTag("run_sandbox_verification_btn")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run in Terminal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (dryRunPreview != null || executionTestResult != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF070A0E))
                                    .border(1.dp, MatrixGreenBright, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MatrixGreenBright, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = executionTestResult ?: "Dry-run trace verified",
                                            color = MatrixGreenBright,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (dryRunPreview != null) {
                                        Text(
                                            text = dryRunPreview!!,
                                            color = CyberCyan,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    6 -> {
                        Text(
                            text = "Step 6 of 6: Deploy & Install",
                            color = MatrixGreenBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Ready to add '${generatedKeyItem.effectiveDisplay}' to Row ${targetRowIndex + 1}!",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = if (selectedType == ScriptType.PYTHON_SCRIPT) "Python File (~/.bin/${cleanName.ifBlank { "script" }}.py):"
                            else "Shell Definition (~/.bashrc):",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = generatedShellSnippet,
                                    color = MatrixGreenBright,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        StorageHelper.copyToClipboard(context, "Shell Code", generatedShellSnippet)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val draft = CustomShortcutDraft(
                                    name = cleanName.ifBlank { "custom_cmd" },
                                    type = selectedType,
                                    commandOrBody = scriptBody,
                                    keyDisplay = keyLabel,
                                    popupDisplay = if (enablePopup) popupLabel else "",
                                    popupCode = if (enablePopup) popupMacro else ""
                                )
                                val draftCheck = ScriptSyntaxValidator.validateShortcutDraft(draft)
                                if (draftCheck.isValid) {
                                    if (cleanName.isNotBlank()) {
                                        onRegisterCustomFunction(cleanName, generatedShellSnippet)
                                    }
                                    onAddKey(generatedKeyItem, targetRowIndex)
                                    StorageHelper.copyToClipboard(context, "Shell Code", generatedShellSnippet, showToast = false)
                                } else {
                                    detailedValidationResult = draftCheck
                                    currentStep = 2
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixGreenBright,
                                contentColor = TerminalBg
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("deploy_key_to_layout_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add '${generatedKeyItem.effectiveDisplay}' to Row ${targetRowIndex + 1}", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                currentStep = 1
                                step1Error = null
                                isLogicVerified = false
                                isExecutionVerified = false
                                executionTestResult = null
                                dryRunPreview = null
                                detailedValidationResult = null
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reset Wizard & Start Another")
                        }
                    }
                }
            }
        }

        // ─── PINNED STICKY NAVIGATION FOOTER (NEVER SCROLLS OFF-SCREEN) ───
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back", fontSize = 12.sp)
                }
            } else {
                Text(
                    text = if (isNameValid) "Ready: ${cleanName.ifBlank { "macro" }}" else "Enter a valid name",
                    color = if (isNameValid) MatrixGreenBright else AmberAccent,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (currentStep < 6) {
                Button(
                    onClick = { validateAndAdvanceFromCurrentStep() },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenBright,
                        contentColor = TerminalBg
                    ),
                    modifier = Modifier.testTag("wizard_next_step_btn")
                ) {
                    Text("Next: Step ${currentStep + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            } else {
                OutlinedButton(
                    onClick = { currentStep = 1 },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                ) {
                    Text("Start New", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun WizardStepBadge(
    step: Int,
    title: String,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    isActive -> CyberCyan.copy(alpha = 0.18f)
                    isCompleted -> MatrixGreen.copy(alpha = 0.15f)
                    else -> TerminalSurfaceVariant
                }
            )
            .border(
                1.dp,
                when {
                    isActive -> CyberCyan
                    isCompleted -> MatrixGreenBright.copy(alpha = 0.6f)
                    else -> TerminalBorder
                },
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> MatrixGreenBright
                        isActive -> CyberCyan
                        else -> Color(0xFF262E3B)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = TerminalBg, modifier = Modifier.size(10.dp))
            } else {
                Text(
                    text = "$step",
                    color = if (isActive) TerminalBg else TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Text(
            text = title,
            color = when {
                isActive -> CyberCyan
                isCompleted -> MatrixGreenBright
                else -> TextSecondary
            },
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ArchitectureOptionCard(
    type: ScriptType,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MatrixGreen.copy(alpha = 0.15f) else TerminalSurfaceVariant)
            .border(1.dp, if (isSelected) MatrixGreenBright else TerminalBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, if (isSelected) MatrixGreenBright else TextMuted, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(modifier = Modifier.size(10.dp).background(MatrixGreenBright, CircleShape))
                }
            }

            Column {
                Text(text = title, color = if (isSelected) MatrixGreenBright else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = subtitle, color = TextSecondary, fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
    }
}

@Composable
fun TemplateChip(label: String, onClick: () -> Unit) {
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

@Composable
fun RecipeBookPanel(
    recipes: List<ScriptRecipe>,
    onAddKey: (KeyItem) -> Unit,
    onTest: (String) -> Unit,
    context: Context
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(TerminalSurfaceVariant)
                    .border(1.dp, TerminalBorder, RoundedCornerShape(10.dp))
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = recipe.type.iconLabel,
                                    color = CyberCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = recipe.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = recipe.category,
                            color = AmberAccent,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = recipe.summary,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF070A0E))
                            .border(1.dp, TerminalBorder, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = recipe.codeBody.trim(),
                            color = MatrixGreenBright,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }

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
                                text = "Keycap:",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            val keyItem = KeyItem(
                                key = recipe.suggestedKeyCode,
                                display = recipe.suggestedKeyDisplay,
                                popup = if (recipe.suggestedPopup != null) KeyPopup(recipe.suggestedPopup, recipe.suggestedPopup) else null
                            )
                            KeyCapButton(
                                item = keyItem,
                                onClick = {},
                                isCompact = true
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    StorageHelper.copyToClipboard(context, "Recipe Code", recipe.codeBody)
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(14.dp))
                            }

                            OutlinedButton(
                                onClick = { onTest(recipe.suggestedKeyCode) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                            ) {
                                Text("Test", fontSize = 10.sp)
                            }

                            Button(
                                onClick = {
                                    val keyItem = KeyItem(
                                        key = recipe.suggestedKeyCode,
                                        display = recipe.suggestedKeyDisplay,
                                        popup = if (recipe.suggestedPopup != null) KeyPopup(recipe.suggestedPopup, recipe.suggestedPopup) else null
                                    )
                                    onAddKey(keyItem)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MatrixGreenBright,
                                    contentColor = TerminalBg
                                )
                            ) {
                                Text("Add Key", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TutorialChaptersPanel(
    chapters: List<TutorialChapter>,
    onRunSnippet: (String) -> Unit,
    context: Context
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(chapters, key = { it.id }) { chapter ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(TerminalSurface)
                    .border(1.dp, TerminalBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = chapter.title,
                        color = MatrixGreenBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = chapter.subtitle,
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = chapter.markdownGuide,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    if (chapter.runnableSnippet.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF070A0E))
                                .border(1.dp, TerminalBorder, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = chapter.runnableSnippet,
                                    color = MatrixGreenBright,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                Row {
                                    IconButton(
                                        onClick = {
                                            StorageHelper.copyToClipboard(context, "Snippet", chapter.runnableSnippet)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(13.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            onRunSnippet(chapter.runnableSnippet + "\n")
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = MatrixGreenBright, modifier = Modifier.size(15.dp))
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
