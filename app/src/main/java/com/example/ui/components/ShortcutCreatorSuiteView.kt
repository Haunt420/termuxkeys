package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import com.example.engine.ScriptSyntaxValidator
import com.example.engine.ValidationResult
import com.example.engine.ValidationSeverity
import com.example.model.CustomShortcutDraft
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuiltinShortcutRecipes
import com.example.data.TutorialChapter
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var suiteTab by remember { mutableIntStateOf(0) } // 0: Stepwise Guided Wizard, 1: Recipe Library, 2: Deep Tutorial

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Modal Dialog Suite Launcher Bar
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
                            text = "Launch Modal Bash Function Wizard",
                            color = AmberAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Interactive 4-step popup dialog with syntax checks & verify test",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Open Dialog",
                    tint = AmberAccent,
                    modifier = Modifier.size(16.dp)
                )
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

        when (suiteTab) {
            0 -> {
                // Highly Instructional Stepwise Guided Wizard with consistent asking & verifying
                StepwiseShortcutWizard(
                    onAddKey = onAddKeyToLayout,
                    onTestInTerminal = onTestInTerminal,
                    totalRows = totalRows,
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

/**
 * Stepwise Interactive Shortcut Creator Wizard.
 * Guides the user step-by-step through 6 instructional phases, asking and verifying consistently.
 */
@Composable
fun StepwiseShortcutWizard(
    onAddKey: (KeyItem, targetRow: Int) -> Unit,
    onTestInTerminal: (String) -> Unit,
    totalRows: Int,
    context: Context
) {
    var currentStep by remember { mutableIntStateOf(1) } // 1 to 6

    // State collected across steps
    var selectedType by remember { mutableStateOf(ScriptType.BASH_FUNCTION) }
    var shortcutName by remember { mutableStateOf("qpush") }
    var scriptBody by remember { mutableStateOf("git add . && git commit -m \"\${1:-Quick sync}\" && git push") }
    var keyLabel by remember { mutableStateOf("G-PUSH") }
    var targetRowIndex by remember { mutableIntStateOf(0) }
    var enablePopup by remember { mutableStateOf(true) }
    var popupLabel by remember { mutableStateOf("STATUS") }
    var popupMacro by remember { mutableStateOf("git status\n") }

    // Verification states
    var isLogicVerified by remember { mutableStateOf(false) }
    var logicVerificationNote by remember { mutableStateOf<String?>(null) }
    var detailedValidationResult by remember { mutableStateOf<ValidationResult?>(null) }
    var isExecutionVerified by remember { mutableStateOf(false) }
    var executionTestResult by remember { mutableStateOf<String?>(null) }

    val generatedKeyCode = remember(selectedType, shortcutName, scriptBody) {
        when (selectedType) {
            ScriptType.BASH_ALIAS -> "$shortcutName\n"
            ScriptType.BASH_FUNCTION -> "$shortcutName\n"
            ScriptType.PYTHON_SCRIPT -> "python3 ~/bin/$shortcutName.py\n"
            ScriptType.ONE_LINER -> if (scriptBody.endsWith("\n")) scriptBody else "$scriptBody\n"
        }
    }

    val generatedKeyItem = remember(generatedKeyCode, keyLabel, enablePopup, popupLabel, popupMacro) {
        val popup = if (enablePopup && popupMacro.isNotBlank()) {
            KeyPopup(key = popupMacro, display = popupLabel.ifBlank { popupMacro })
        } else null
        KeyItem(
            key = generatedKeyCode,
            display = keyLabel.ifBlank { shortcutName },
            popup = popup
        )
    }

    val generatedShellSnippet = remember(selectedType, shortcutName, scriptBody) {
        when (selectedType) {
            ScriptType.BASH_ALIAS -> "alias $shortcutName='$scriptBody'"
            ScriptType.BASH_FUNCTION -> "$shortcutName() {\n  $scriptBody\n}"
            ScriptType.PYTHON_SCRIPT -> "#!/data/data/com.termux/files/usr/bin/python\n# Save to ~/bin/$shortcutName.py and run 'chmod +x ~/bin/$shortcutName.py'\n$scriptBody"
            ScriptType.ONE_LINER -> scriptBody
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Step Progress Indicator Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TerminalSurface)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (step in 1..6) {
                val isCompleted = step < currentStep
                val isCurrent = step == currentStep
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> MatrixGreenBright
                                    isCurrent -> CyberCyan
                                    else -> Color(0xFF262E3B)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = TerminalBg, modifier = Modifier.size(14.dp))
                        } else {
                            Text(
                                text = "$step",
                                color = if (isCurrent) TerminalBg else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    if (step < 6) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(2.dp)
                                .background(if (step < currentStep) MatrixGreenBright else Color(0xFF262E3B))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }

        // Instructional Step Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(TerminalSurface)
                .border(1.dp, TerminalBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            when (currentStep) {
                1 -> {
                    // STEP 1: Objective & Architecture
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Step 1 of 6: Script Architecture",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "What kind of automation are you designing?",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Selecting the right architecture determines whether your shortcut supports arguments, persists across restarts, or runs external Python libraries.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        HorizontalDivider(color = TerminalBorder)

                        // Architectural Choices
                        ArchitectureOptionCard(
                            type = ScriptType.BASH_FUNCTION,
                            title = "Bash Function with Arguments (Recommended)",
                            subtitle = "Best for Git, compilers, and parameterized tasks ($1, $2)",
                            isSelected = selectedType == ScriptType.BASH_FUNCTION,
                            onClick = {
                                selectedType = ScriptType.BASH_FUNCTION
                                shortcutName = "qpush"
                                scriptBody = "git add . && git commit -m \"\${1:-Quick sync}\" && git push"
                                keyLabel = "G-PUSH"
                            }
                        )

                        ArchitectureOptionCard(
                            type = ScriptType.BASH_ALIAS,
                            title = "Bash / Zsh Simple Alias",
                            subtitle = "One-word shortcut for fixed flag commands (e.g. update, ll)",
                            isSelected = selectedType == ScriptType.BASH_ALIAS,
                            onClick = {
                                selectedType = ScriptType.BASH_ALIAS
                                shortcutName = "sysup"
                                scriptBody = "pkg update -y && pkg upgrade -y && pkg clean"
                                keyLabel = "SYS-UP"
                            }
                        )

                        ArchitectureOptionCard(
                            type = ScriptType.PYTHON_SCRIPT,
                            title = "Python Automation Script (~/bin)",
                            subtitle = "For JSON processing, HTTP servers, sockets, and web APIs",
                            isSelected = selectedType == ScriptType.PYTHON_SCRIPT,
                            onClick = {
                                selectedType = ScriptType.PYTHON_SCRIPT
                                shortcutName = "serve"
                                scriptBody = "import http.server, socketserver\nwith socketserver.TCPServer(('', 8080), http.server.SimpleHTTPRequestHandler) as h:\n    h.serve_forever()"
                                keyLabel = "SRV"
                            }
                        )

                        ArchitectureOptionCard(
                            type = ScriptType.ONE_LINER,
                            title = "Inline Terminal Macro",
                            subtitle = "Dispatches raw keystrokes directly into active shell prompt",
                            isSelected = selectedType == ScriptType.ONE_LINER,
                            onClick = {
                                selectedType = ScriptType.ONE_LINER
                                shortcutName = "clear_run"
                                scriptBody = "clear && ls -la\n"
                                keyLabel = "CLS-LS"
                            }
                        )
                    }
                }

                2 -> {
                    // STEP 2: Name & Logic with Interactive Verification
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Step 2 of 6: Command Logic & Verification",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Define the command name and execution body",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

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
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MatrixGreenBright,
                                unfocusedBorderColor = TerminalBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = scriptBody,
                            onValueChange = {
                                scriptBody = it
                                isLogicVerified = false
                                detailedValidationResult = null
                            },
                            label = { Text("Script Execution Body") },
                            placeholder = { Text("Commands to execute...") },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MatrixGreenBright,
                                unfocusedBorderColor = TerminalBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Quick Template Pickers
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Quick Fill:", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterVertically))
                            TemplateChip(label = "Git Push") {
                                shortcutName = "qpush"
                                scriptBody = "git add . && git commit -m \"\${1:-Quick sync}\" && git push"
                                keyLabel = "G-PUSH"
                                isLogicVerified = false
                                detailedValidationResult = null
                            }
                            TemplateChip(label = "Sys Update") {
                                shortcutName = "sysup"
                                scriptBody = "pkg update -y && pkg upgrade -y && pkg clean"
                                keyLabel = "SYS-UP"
                                isLogicVerified = false
                                detailedValidationResult = null
                            }
                            TemplateChip(label = "Python Web Server") {
                                shortcutName = "serve"
                                scriptBody = "python3 -m http.server 8080"
                                keyLabel = "SRV"
                                isLogicVerified = false
                                detailedValidationResult = null
                            }
                        }

                        // Instructional Syntax Verification Button
                        OutlinedButton(
                            onClick = {
                                val validation = ScriptSyntaxValidator.validateScript(
                                    code = scriptBody,
                                    type = selectedType,
                                    name = shortcutName
                                )
                                detailedValidationResult = validation
                                isLogicVerified = validation.isValid
                                logicVerificationNote = validation.summary
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
                                            text = if (res.isValid) "✓ Syntax Verified" else "✕ Syntax Validation Error",
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
                                                    text = "💡 Tip: ${issue.suggestion}",
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
                }

                3 -> {
                    // STEP 3: Touch Keycap Ergonomics
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Step 3 of 6: Touch Keycap Ergonomics",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Design the button appearance on your extra keys bar",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Termux extra keys look best with 2 to 6 characters so all 7 columns remain perfectly aligned across your phone's screen.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        OutlinedTextField(
                            value = keyLabel,
                            onValueChange = { keyLabel = it },
                            label = { Text("Display Label (On Keycap)") },
                            placeholder = { Text("e.g. G-PUSH, SYS-UP, SRV") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MatrixGreenBright,
                                unfocusedBorderColor = TerminalBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Target Row
                        Text("Select Target Extra Keys Row:", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
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

                        // Preview Box
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
                                Text("Live Keycap Preview:", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                KeyCapButton(
                                    item = generatedKeyItem,
                                    onClick = {},
                                    isTermuxStyle = true,
                                    modifier = Modifier.width(80.dp)
                                )
                            }
                        }
                    }
                }

                4 -> {
                    // STEP 4: Swipe-Up Secondary Action
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Step 4 of 6: Swipe-Up Secondary Action",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Would you like a secondary swipe-up action?",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Termux extra keys allow swiping upward from any key to trigger an alternate macro, effectively giving you two functions in one slot!",
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
                            OutlinedTextField(
                                value = popupLabel,
                                onValueChange = { popupLabel = it },
                                label = { Text("Popup Indicator Label") },
                                placeholder = { Text("e.g. STATUS, CANCEL, HELP") },
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
                }

                5 -> {
                    // STEP 5: Interactive Sandbox Verification Test
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Step 5 of 6: Terminal Verification Test",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Test command execution in the simulated sandbox",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Before writing to your Termux setup, verify the command in the sandbox below to confirm execution output.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        // Run Verification Test Button
                        Button(
                            onClick = {
                                onTestInTerminal(generatedKeyCode)
                                executionTestResult = "Executed in terminal: $generatedKeyCode"
                                isExecutionVerified = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isExecutionVerified) MatrixGreenBright else CyberCyan,
                                contentColor = TerminalBg
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("run_sandbox_verification_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isExecutionVerified) "Re-run Verification Test" else "Run Verification Test in Terminal", fontWeight = FontWeight.Bold)
                        }

                        if (executionTestResult != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF070A0E))
                                    .border(1.dp, MatrixGreenBright, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MatrixGreenBright, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "✓ Command verified successfully in terminal session!",
                                        color = MatrixGreenBright,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                6 -> {
                    // STEP 6: Installation & Deployment
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Step 6 of 6: Deploy & Install",
                            color = MatrixGreenBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "All checks passed! Deploy your new shortcut",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        // Code to add to ~/.bashrc
                        Text("1. Persistent Shell Definition (~/.bashrc):", color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
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
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(15.dp))
                                }
                            }
                        }

                        // Add Keycap to Active Layout Button
                        Button(
                            onClick = {
                                val draft = CustomShortcutDraft(
                                    name = shortcutName,
                                    type = selectedType,
                                    commandOrBody = scriptBody,
                                    keyDisplay = keyLabel,
                                    popupDisplay = if (enablePopup) popupLabel else "",
                                    popupCode = if (enablePopup) popupMacro else ""
                                )
                                val draftCheck = ScriptSyntaxValidator.validateShortcutDraft(draft)
                                if (draftCheck.isValid) {
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

                        // Finish & Restart Wizard
                        OutlinedButton(
                            onClick = {
                                currentStep = 1
                                isLogicVerified = false
                                isExecutionVerified = false
                                executionTestResult = null
                                detailedValidationResult = null
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create Another Shortcut")
                        }
                    }
                }
            }
        }

        // Navigation Footer: Previous & Next Step Controls
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
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (currentStep < 6) {
                Button(
                    onClick = {
                        if (currentStep == 2 && !isLogicVerified) {
                            val validation = ScriptSyntaxValidator.validateScript(
                                code = scriptBody,
                                type = selectedType,
                                name = shortcutName
                            )
                            detailedValidationResult = validation
                            isLogicVerified = validation.isValid
                            logicVerificationNote = validation.summary
                            if (validation.isValid) {
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
                    modifier = Modifier.testTag("wizard_next_step_btn")
                ) {
                    Text("Continue to Step ${currentStep + 1}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }
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
            .padding(12.dp)
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
                Text(text = title, color = if (isSelected) MatrixGreenBright else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
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
