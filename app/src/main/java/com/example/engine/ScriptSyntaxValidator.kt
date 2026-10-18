package com.example.engine

import com.example.model.CustomShortcutDraft
import com.example.model.KeyItem
import com.example.model.ScriptType
import java.util.Stack

/**
 * Severity level of syntax and validation findings.
 */
enum class ValidationSeverity {
    ERROR,
    WARNING,
    INFO
}

/**
 * Individual issue found during script or shortcut syntax inspection.
 */
data class ValidationIssue(
    val severity: ValidationSeverity,
    val message: String,
    val line: Int? = null,
    val column: Int? = null,
    val suggestion: String? = null,
    val codeSnippet: String? = null
)

/**
 * Overall validation report containing validity flag, issues, and human-readable summary.
 */
data class ValidationResult(
    val isValid: Boolean,
    val issues: List<ValidationIssue> = emptyList(),
    val summary: String = ""
) {
    val errors: List<ValidationIssue> get() = issues.filter { it.severity == ValidationSeverity.ERROR }
    val warnings: List<ValidationIssue> get() = issues.filter { it.severity == ValidationSeverity.WARNING }
    val infos: List<ValidationIssue> get() = issues.filter { it.severity == ValidationSeverity.INFO }

    fun formattedFeedback(): String {
        if (isValid && issues.isEmpty()) {
            return "✓ $summary"
        }
        val sb = StringBuilder()
        if (!isValid) {
            sb.append("✕ Validation Failed: $summary\n")
        } else {
            sb.append("⚠ Valid with warnings: $summary\n")
        }
        for (issue in issues) {
            val prefix = when (issue.severity) {
                ValidationSeverity.ERROR -> "• [ERROR]"
                ValidationSeverity.WARNING -> "• [WARN]"
                ValidationSeverity.INFO -> "• [INFO]"
            }
            val loc = if (issue.line != null) " Line ${issue.line}:" else ""
            sb.append("$prefix$loc ${issue.message}\n")
            if (issue.suggestion != null) {
                sb.append("   → Tip: ${issue.suggestion}\n")
            }
        }
        return sb.toString().trimEnd()
    }
}

/**
 * Comprehensive utility class for performing Bash and Python syntax validation,
 * detecting common shell and script pitfalls, unbalanced quotes and brackets,
 * indentation anomalies, and providing actionable error feedback to the user before
 * shortcuts are committed to the Termux configuration.
 */
object ScriptSyntaxValidator {

    private val BASH_IDENTIFIER_REGEX = Regex("^[a-zA-Z_][a-zA-Z0-9_.-]*$")
    private val PYTHON_IDENTIFIER_REGEX = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")

    // Common bash construct keywords
    private val BASH_IF_REGEX = Regex("""\bif\b""")
    private val BASH_THEN_REGEX = Regex("""\bthen\b""")
    private val BASH_FI_REGEX = Regex("""\bfi\b""")
    private val BASH_FOR_WHILE_REGEX = Regex("""\b(for|while|until)\b""")
    private val BASH_DO_REGEX = Regex("""\bdo\b""")
    private val BASH_DONE_REGEX = Regex("""\bdone\b""")
    private val BASH_CASE_REGEX = Regex("""\bcase\b""")
    private val BASH_ESAC_REGEX = Regex("""\besac\b""")

    // Python statement header keywords requiring a colon
    private val PYTHON_BLOCK_HEADERS = listOf(
        "def", "class", "if", "elif", "else", "for", "while", "try", "except", "finally", "with", "match", "case"
    )

    /**
     * Dispatch validator for any supported ScriptType.
     */
    fun validateScript(
        code: String,
        type: ScriptType,
        name: String? = null
    ): ValidationResult {
        return when (type) {
            ScriptType.BASH_FUNCTION -> validateBashFunction(name ?: "", code)
            ScriptType.BASH_ALIAS -> validateBashAlias(name ?: "", code)
            ScriptType.PYTHON_SCRIPT -> validatePythonScript(code, name)
            ScriptType.ONE_LINER -> validateBashOneLiner(code, name)
        }
    }

    /**
     * Validate a custom shortcut draft before saving it to extra keys layout.
     */
    fun validateShortcutDraft(draft: CustomShortcutDraft): ValidationResult {
        val issues = mutableListOf<ValidationIssue>()

        // 1. Validate shortcut identifier
        val trimmedName = draft.name.trim()
        if (trimmedName.isBlank()) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Shortcut command/function name cannot be empty.",
                    suggestion = "Choose a concise command name like 'qpush' or 'sysup'."
                )
            )
        }

        // 2. Validate key label ergonomics
        val trimmedLabel = draft.keyDisplay.trim()
        if (trimmedLabel.isBlank()) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Keycap display label cannot be empty.",
                    suggestion = "Provide a 2 to 6 character label to show on the touch button."
                )
            )
        } else if (trimmedLabel.length > 7) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.WARNING,
                    message = "Keycap label '${trimmedLabel}' is ${trimmedLabel.length} characters long.",
                    suggestion = "Termux extra-keys rows typically support 7 columns. Shorter labels (≤ 6 chars) prevent touch buttons from overflowing."
                )
            )
        }

        // 3. Validate popup macro if configured
        if (draft.popupDisplay.isNotBlank() || draft.popupCode.isNotBlank()) {
            if (draft.popupDisplay.isBlank()) {
                issues.add(
                    ValidationIssue(
                        severity = ValidationSeverity.WARNING,
                        message = "Popup macro has an execution command but no display label.",
                        suggestion = "Add a 2-4 letter popup label so you know what the swipe-up action does."
                    )
                )
            }
            if (draft.popupCode.isNotBlank()) {
                val popupResult = validateBashOneLiner(draft.popupCode, "Popup Macro")
                issues.addAll(popupResult.issues.map { it.copy(message = "Popup Macro: ${it.message}") })
            }
        }

        // 4. Validate script payload body
        val scriptResult = validateScript(draft.commandOrBody, draft.type, draft.name)
        issues.addAll(scriptResult.issues)

        val hasErrors = issues.any { it.severity == ValidationSeverity.ERROR }
        val summary = when {
            hasErrors -> "Draft has ${issues.count { it.severity == ValidationSeverity.ERROR }} error(s) to fix before saving."
            issues.isNotEmpty() -> "Ready to save with ${issues.size} warning/tip(s)."
            else -> "Shortcut syntax and ergonomics verified cleanly."
        }

        return ValidationResult(isValid = !hasErrors, issues = issues, summary = summary)
    }

    /**
     * Comprehensive Bash function validation.
     */
    fun validateBashFunction(name: String, body: String): ValidationResult {
        val issues = mutableListOf<ValidationIssue>()
        val trimmedName = name.trim()

        // Identifier checks
        if (trimmedName.isBlank()) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Function name cannot be blank.",
                    suggestion = "Enter an alphanumeric name like 'my_sync' or 'backup_db'."
                )
            )
        } else if (!trimmedName.matches(BASH_IDENTIFIER_REGEX)) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Invalid function name '$trimmedName'.",
                    suggestion = "Bash function names must start with a letter or underscore and contain only alphanumeric chars, underscores, or dashes."
                )
            )
        }

        // Body blank check
        val trimmedBody = body.trim()
        if (trimmedBody.isBlank()) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Function body cannot be empty.",
                    suggestion = "Add shell commands inside the function body (e.g., 'git status' or 'pkg update')."
                )
            )
            return ValidationResult(isValid = false, issues = issues, summary = "Function body is empty.")
        }

        // Run general bash lexical checks on body
        validateBashCode(trimmedBody, issues)

        val hasErrors = issues.any { it.severity == ValidationSeverity.ERROR }
        val summary = if (hasErrors) {
            "Found ${issues.count { it.severity == ValidationSeverity.ERROR }} error(s) in Bash function definition."
        } else if (issues.isNotEmpty()) {
            "Valid Bash function definition with ${issues.size} helpful tip(s)."
        } else {
            "Valid Bash function definition ready for ~/.bashrc."
        }

        return ValidationResult(isValid = !hasErrors, issues = issues, summary = summary)
    }

    /**
     * Validate Bash alias definition.
     */
    fun validateBashAlias(name: String, command: String): ValidationResult {
        val issues = mutableListOf<ValidationIssue>()
        val trimmedName = name.trim()

        if (trimmedName.isBlank()) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Alias name cannot be blank.",
                    suggestion = "Provide a short shortcut name (e.g. 'll', 'gs', 'up')."
                )
            )
        } else if (!trimmedName.matches(BASH_IDENTIFIER_REGEX)) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Invalid alias identifier '$trimmedName'.",
                    suggestion = "Alias names cannot contain spaces or special punctuation characters."
                )
            )
        }

        val trimmedCommand = command.trim()
        if (trimmedCommand.isBlank()) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Alias command target cannot be blank.",
                    suggestion = "Enter the command to run, e.g. 'ls -la --color=auto'."
                )
            )
        } else {
            validateBashCode(trimmedCommand, issues)
            if (trimmedCommand.contains("$1") || trimmedCommand.contains("$@")) {
                issues.add(
                    ValidationIssue(
                        severity = ValidationSeverity.WARNING,
                        message = "Aliases do not accept positional arguments like '\$1' or '\$@'.",
                        suggestion = "Switch type to 'Bash Function' if you need arguments or parameters."
                    )
                )
            }
        }

        val hasErrors = issues.any { it.severity == ValidationSeverity.ERROR }
        val summary = if (hasErrors) {
            "Alias has errors to address."
        } else {
            "Alias definition validated successfully."
        }

        return ValidationResult(isValid = !hasErrors, issues = issues, summary = summary)
    }

    /**
     * Validate a one-liner terminal command.
     */
    fun validateBashOneLiner(code: String, label: String? = null): ValidationResult {
        val issues = mutableListOf<ValidationIssue>()
        val trimmed = code.trim()

        if (trimmed.isBlank()) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "${label ?: "Command"} cannot be empty.",
                    suggestion = "Specify terminal command or executable string."
                )
            )
            return ValidationResult(isValid = false, issues = issues, summary = "Command is empty.")
        }

        validateBashCode(trimmed, issues)

        val hasErrors = issues.any { it.severity == ValidationSeverity.ERROR }
        val summary = if (hasErrors) {
            "Command syntax error detected."
        } else {
            "Command syntax verified clean."
        }

        return ValidationResult(isValid = !hasErrors, issues = issues, summary = summary)
    }

    /**
     * Comprehensive Python script syntax validator.
     */
    fun validatePythonScript(code: String, scriptName: String? = null): ValidationResult {
        val issues = mutableListOf<ValidationIssue>()
        val trimmed = code.trim()

        if (trimmed.isBlank()) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Python script cannot be empty.",
                    suggestion = "Write Python code or functions (e.g. print('Hello Termux'))."
                )
            )
            return ValidationResult(isValid = false, issues = issues, summary = "Python script is empty.")
        }

        val lines = code.lines()

        // 1. Triple-quotes and standard quotes parsing
        var inTripleSingle = false
        var inTripleDouble = false
        var tripleStartLine = 0

        // Bracket stack
        data class Bracket(val char: Char, val line: Int, val col: Int)
        val bracketStack = Stack<Bracket>()

        var hasTabs = false
        var hasSpaces = false
        var previousIndent = 0
        var expectIndentNext = false
        var previousHeaderLine = 0

        for ((index, rawLine) in lines.withIndex()) {
            val lineNum = index + 1
            val trimmedLine = rawLine.trim()

            // Skip empty lines or pure comment lines for block structure
            if (trimmedLine.startsWith("#") || trimmedLine.isEmpty()) {
                continue
            }

            // Check indentation characters
            val leadingWhitespace = rawLine.takeWhile { it.isWhitespace() }
            if (leadingWhitespace.contains('\t')) hasTabs = true
            if (leadingWhitespace.contains(' ')) hasSpaces = true

            val lineStartsInTripleQuote = inTripleSingle || inTripleDouble

            // Indentation check after statement header
            val currentIndent = leadingWhitespace.length
            if (expectIndentNext && !lineStartsInTripleQuote) {
                if (currentIndent <= previousIndent) {
                    issues.add(
                        ValidationIssue(
                            severity = ValidationSeverity.ERROR,
                            line = lineNum,
                            message = "Expected an indented block after statement on line $previousHeaderLine.",
                            suggestion = "Indent this line by 4 spaces to place it inside the block.",
                            codeSnippet = trimmedLine
                        )
                    )
                }
                expectIndentNext = false
            }

            // Check for statement headers ending without colon (only when not inside triple quotes)
            if (!lineStartsInTripleQuote) {
                for (header in PYTHON_BLOCK_HEADERS) {
                    val headerRegex = Regex("""^\s*${header}\b.*""")
                    if (rawLine.matches(headerRegex)) {
                        // Strip trailing comments
                        val codeWithoutComment = rawLine.split("#").first().trimEnd()
                        if (!codeWithoutComment.endsWith(":") && !codeWithoutComment.endsWith("\\")) {
                            // Might be a multi-line header if open bracket exists
                            if (bracketStack.isEmpty()) {
                                issues.add(
                                    ValidationIssue(
                                        severity = ValidationSeverity.ERROR,
                                        line = lineNum,
                                        message = "Missing colon ':' at end of '$header' header.",
                                        suggestion = "Append a colon ':' to end the statement header, e.g. '$codeWithoutComment:'",
                                        codeSnippet = rawLine.trim()
                                    )
                                )
                            }
                        } else if (codeWithoutComment.endsWith(":")) {
                            expectIndentNext = true
                            previousIndent = currentIndent
                            previousHeaderLine = lineNum
                        }
                        break
                    }
                }

                // Check common beginner Python syntax traps
                checkPythonCommonMistakes(rawLine, lineNum, issues)
            }

            // Character by character parsing for strings and brackets
            var i = 0
            while (i < rawLine.length) {
                val c = rawLine[i]

                // Check for triple quotes
                if (!inTripleSingle && !inTripleDouble && i + 2 < rawLine.length) {
                    val triplet = rawLine.substring(i, i + 3)
                    if (triplet == "\"\"\"") {
                        inTripleDouble = true
                        tripleStartLine = lineNum
                        i += 3
                        continue
                    } else if (triplet == "'''") {
                        inTripleSingle = true
                        tripleStartLine = lineNum
                        i += 3
                        continue
                    }
                } else if (inTripleDouble && i + 2 < rawLine.length && rawLine.substring(i, i + 3) == "\"\"\"") {
                    inTripleDouble = false
                    i += 3
                    continue
                } else if (inTripleSingle && i + 2 < rawLine.length && rawLine.substring(i, i + 3) == "'''") {
                    inTripleSingle = false
                    i += 3
                    continue
                }

                if (inTripleSingle || inTripleDouble) {
                    i++
                    continue
                }

                // If inside normal single/double quote
                if (c == '\'' || c == '"') {
                    val quoteChar = c
                    var escaped = false
                    var closed = false
                    val startCol = i + 1
                    i++ // skip opening quote
                    while (i < rawLine.length) {
                        val current = rawLine[i]
                        if (current == '\\') {
                            escaped = !escaped
                        } else if (current == quoteChar && !escaped) {
                            closed = true
                            break
                        } else {
                            escaped = false
                        }
                        i++
                    }
                    if (!closed && !rawLine.endsWith("\\")) {
                        issues.add(
                            ValidationIssue(
                                severity = ValidationSeverity.ERROR,
                                line = lineNum,
                                column = startCol,
                                message = "Unterminated string literal (missing closing quote $quoteChar).",
                                suggestion = "Close the string with $quoteChar or use triple quotes for multiline strings.",
                                codeSnippet = rawLine.trim()
                            )
                        )
                    }
                    i++
                    continue
                }

                // Comment line start
                if (c == '#') {
                    break // rest of line is comment
                }

                // Brackets tracking
                when (c) {
                    '(', '[', '{' -> bracketStack.push(Bracket(c, lineNum, i + 1))
                    ')' -> {
                        if (bracketStack.isEmpty() || bracketStack.peek().char != '(') {
                            issues.add(
                                ValidationIssue(
                                    severity = ValidationSeverity.ERROR,
                                    line = lineNum,
                                    column = i + 1,
                                    message = "Unmatched closing parenthesis ')'.",
                                    suggestion = "Check for an extra closing parenthesis or missing opening '('."
                                )
                            )
                        } else {
                            bracketStack.pop()
                        }
                    }
                    ']' -> {
                        if (bracketStack.isEmpty() || bracketStack.peek().char != '[') {
                            issues.add(
                                ValidationIssue(
                                    severity = ValidationSeverity.ERROR,
                                    line = lineNum,
                                    column = i + 1,
                                    message = "Unmatched closing bracket ']'.",
                                    suggestion = "Check for an extra closing bracket or missing opening '['."
                                )
                            )
                        } else {
                            bracketStack.pop()
                        }
                    }
                    '}' -> {
                        if (bracketStack.isEmpty() || bracketStack.peek().char != '{') {
                            issues.add(
                                ValidationIssue(
                                    severity = ValidationSeverity.ERROR,
                                    line = lineNum,
                                    column = i + 1,
                                    message = "Unmatched closing brace '}'.",
                                    suggestion = "Check for an extra closing brace or missing opening '{'."
                                )
                            )
                        } else {
                            bracketStack.pop()
                        }
                    }
                }

                i++
            }
        }

        // Unclosed triple quotes
        if (inTripleDouble || inTripleSingle) {
            val quoteStr = if (inTripleDouble) "\"\"\"" else "'''"
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    line = tripleStartLine,
                    message = "Unclosed triple-quoted string ($quoteStr) starting on line $tripleStartLine.",
                    suggestion = "Add closing $quoteStr at the end of the docstring/multiline block."
                )
            )
        }

        // Unclosed brackets
        while (bracketStack.isNotEmpty()) {
            val unclosed = bracketStack.pop()
            val expectedClose = when (unclosed.char) {
                '(' -> "')'"
                '[' -> "']'"
                '{' -> "'}'"
                else -> "closing bracket"
            }
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    line = unclosed.line,
                    column = unclosed.col,
                    message = "Unclosed '${unclosed.char}' on line ${unclosed.line} (missing matching $expectedClose).",
                    suggestion = "Add $expectedClose before closing the block or expression."
                )
            )
        }

        // Mixed tabs and spaces warning
        if (hasTabs && hasSpaces) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.WARNING,
                    message = "Inconsistent indentation: Script mixes tabs and spaces.",
                    suggestion = "Python PEP 8 strongly recommends using 4 spaces per indentation level and avoiding tabs."
                )
            )
        }

        // Shebang recommendation for Termux scripts
        if (!code.startsWith("#!") && code.contains("\n")) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.INFO,
                    message = "No shebang line found.",
                    suggestion = "Add '#!/usr/bin/env python3' at line 1 so Termux can execute the file directly without prefixing 'python3'."
                )
            )
        }

        val hasErrors = issues.any { it.severity == ValidationSeverity.ERROR }
        val summary = if (hasErrors) {
            "Found ${issues.count { it.severity == ValidationSeverity.ERROR }} Python syntax error(s)."
        } else if (issues.isNotEmpty()) {
            "Python script syntax verified with ${issues.size} tip(s)."
        } else {
            "✓ Valid Python syntax: Ready for Termux python3 execution."
        }

        return ValidationResult(isValid = !hasErrors, issues = issues, summary = summary)
    }

    /**
     * Internal validator for Bash code lines, control structures, and quotes.
     */
    private fun validateBashCode(code: String, issues: MutableList<ValidationIssue> = mutableListOf()): List<ValidationIssue> {
        val lines = code.lines()

        var ifCount = 0
        var thenCount = 0
        var fiCount = 0
        var forWhileCount = 0
        var doCount = 0
        var doneCount = 0
        var caseCount = 0
        var esacCount = 0

        var openSingleQuote = false
        var singleQuoteStartLine = 0
        var openDoubleQuote = false
        var doubleQuoteStartLine = 0

        var openCurly = 0
        var openParen = 0
        var openBracket = 0
        var backtickCount = 0

        for ((index, rawLine) in lines.withIndex()) {
            val lineNum = index + 1
            val trimmed = rawLine.trim()

            if (trimmed.startsWith("#") || trimmed.isEmpty()) {
                continue
            }

            // 1. Variable Assignment Spacing Pitfall (e.g. "FOO = bar" or "FOO= bar")
            val assignmentWithSpaces = Regex("""^\s*([a-zA-Z_][a-zA-Z0-9_]*)\s+=\s*(.*)""")
            val assignmentSpaceAfter = Regex("""^\s*([a-zA-Z_][a-zA-Z0-9_]*)=\s+(.*)""")
            if (assignmentWithSpaces.matches(rawLine) && !rawLine.contains("[") && !rawLine.contains("test")) {
                val varName = assignmentWithSpaces.find(rawLine)?.groupValues?.get(1) ?: "VAR"
                issues.add(
                    ValidationIssue(
                        severity = ValidationSeverity.ERROR,
                        line = lineNum,
                        message = "Illegal space before '=' in variable assignment.",
                        suggestion = "Bash does not allow spaces around '='. Use '$varName=value' instead of '$varName = value'.",
                        codeSnippet = trimmed
                    )
                )
            } else if (assignmentSpaceAfter.matches(rawLine) && !rawLine.contains("[") && !rawLine.contains("test")) {
                val varName = assignmentSpaceAfter.find(rawLine)?.groupValues?.get(1) ?: "VAR"
                issues.add(
                    ValidationIssue(
                        severity = ValidationSeverity.ERROR,
                        line = lineNum,
                        message = "Illegal space after '=' in variable assignment.",
                        suggestion = "Bash does not allow spaces around '='. Use '$varName=value' without spaces.",
                        codeSnippet = trimmed
                    )
                )
            }

            // 2. Test bracket spacing check (e.g. "[$a" or "foo]")
            if (trimmed.contains(Regex("""\[\S""")) && !trimmed.contains("[[") && !trimmed.contains("\$[")) {
                issues.add(
                    ValidationIssue(
                        severity = ValidationSeverity.WARNING,
                        line = lineNum,
                        message = "Missing space after opening bracket '[' in test expression.",
                        suggestion = "In Bash, '[' is a command and requires whitespace: e.g. '[ \"\$a\" = \"\$b\" ]'.",
                        codeSnippet = trimmed
                    )
                )
            }
            if (trimmed.contains(Regex("""\S\]""")) && !trimmed.contains("]]") && !trimmed.contains("[]") && !trimmed.contains("\$[")) {
                issues.add(
                    ValidationIssue(
                        severity = ValidationSeverity.WARNING,
                        line = lineNum,
                        message = "Missing space before closing bracket ']' in test expression.",
                        suggestion = "In Bash, ']' requires preceding whitespace: e.g. '[ \"\$a\" = \"\$b\" ]'.",
                        codeSnippet = trimmed
                    )
                )
            }

            // 3. Track Control flow keywords (when not inside comments or strings)
            // Strip quoted strings temporarily for keyword scanning
            val sanitized = sanitizeQuotesForKeywordScan(trimmed)
            if (BASH_IF_REGEX.containsMatchIn(sanitized)) ifCount++
            if (BASH_THEN_REGEX.containsMatchIn(sanitized)) thenCount++
            if (BASH_FI_REGEX.containsMatchIn(sanitized)) fiCount++

            if (BASH_FOR_WHILE_REGEX.containsMatchIn(sanitized)) forWhileCount++
            if (BASH_DO_REGEX.containsMatchIn(sanitized)) doCount++
            if (BASH_DONE_REGEX.containsMatchIn(sanitized)) doneCount++

            if (BASH_CASE_REGEX.containsMatchIn(sanitized)) caseCount++
            if (BASH_ESAC_REGEX.containsMatchIn(sanitized)) esacCount++

            // 4. Character-by-character quote and bracket counting
            var i = 0
            while (i < rawLine.length) {
                val c = rawLine[i]

                if (c == '\\' && i + 1 < rawLine.length) {
                    // Escaped character, skip next char unless inside single quotes
                    if (!openSingleQuote) {
                        i += 2
                        continue
                    }
                }

                if (c == '\'' && !openDoubleQuote) {
                    openSingleQuote = !openSingleQuote
                    if (openSingleQuote) singleQuoteStartLine = lineNum
                } else if (c == '"' && !openSingleQuote) {
                    openDoubleQuote = !openDoubleQuote
                    if (openDoubleQuote) doubleQuoteStartLine = lineNum
                } else if (!openSingleQuote) {
                    when (c) {
                        '`' -> backtickCount++
                        '{' -> openCurly++
                        '}' -> {
                            if (openCurly > 0) openCurly-- else {
                                issues.add(
                                    ValidationIssue(
                                        severity = ValidationSeverity.ERROR,
                                        line = lineNum,
                                        message = "Unmatched closing curly brace '}'.",
                                        suggestion = "Check for an unexpected '}' without a matching opening '{'."
                                    )
                                )
                            }
                        }
                        '(' -> openParen++
                        ')' -> {
                            if (openParen > 0) openParen-- else {
                                issues.add(
                                    ValidationIssue(
                                        severity = ValidationSeverity.ERROR,
                                        line = lineNum,
                                        message = "Unmatched closing parenthesis ')'.",
                                        suggestion = "Check for an unexpected ')' without a matching opening '('."
                                    )
                                )
                            }
                        }
                        '[' -> openBracket++
                        ']' -> {
                            if (openBracket > 0) openBracket-- else {
                                issues.add(
                                    ValidationIssue(
                                        severity = ValidationSeverity.ERROR,
                                        line = lineNum,
                                        message = "Unmatched closing bracket ']'.",
                                        suggestion = "Check for an unexpected ']' without a matching opening '['."
                                    )
                                )
                            }
                        }
                    }
                }
                i++
            }
        }

        // Unclosed quotes
        if (openSingleQuote) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    line = singleQuoteStartLine,
                    message = "Unbalanced single quote (') starting around line $singleQuoteStartLine.",
                    suggestion = "Add a closing single quote (') to terminate the string."
                )
            )
        }
        if (openDoubleQuote) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    line = doubleQuoteStartLine,
                    message = "Unbalanced double quote (\") starting around line $doubleQuoteStartLine.",
                    suggestion = "Add a closing double quote (\") to terminate the string."
                )
            )
        }

        // Unbalanced backticks
        if (backtickCount % 2 != 0) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Unbalanced backticks (`) detected in command.",
                    suggestion = "Use modern \$(command) syntax instead of backticks `command` for safer command substitution."
                )
            )
        } else if (backtickCount > 0) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.INFO,
                    message = "Backticks (`) detected for command substitution.",
                    suggestion = "Consider replacing `...` with \$(...) for nesting support and cleaner readability."
                )
            )
        }

        // Unclosed braces/parentheses
        if (openCurly > 0) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "$openCurly unclosed curly brace(s) '{'.",
                    suggestion = "Add closing '}' to balance your function body or parameter expansion."
                )
            )
        }
        if (openParen > 0) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "$openParen unclosed parenthesis '('.",
                    suggestion = "Add closing ')' to balance subshells or condition expressions."
                )
            )
        }
        if (openBracket > 0) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "$openBracket unclosed bracket(s) '['.",
                    suggestion = "Add closing ']' to balance test condition or array index."
                )
            )
        }

        // Control flow balance checks
        if (ifCount > fiCount) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Missing 'fi' for 'if' statement ($ifCount 'if' vs $fiCount 'fi').",
                    suggestion = "Close your conditional block with 'fi'."
                )
            )
        }
        if (forWhileCount > doneCount) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Missing 'done' for loop ($forWhileCount 'for/while' vs $doneCount 'done').",
                    suggestion = "Close your loop with 'done'."
                )
            )
        }
        if (caseCount > esacCount) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    message = "Missing 'esac' for 'case' statement.",
                    suggestion = "Close your case statement with 'esac'."
                )
            )
        }

        return issues
    }

    /**
     * Checks for common Python code mistakes often made by shell developers.
     */
    private fun checkPythonCommonMistakes(line: String, lineNum: Int, issues: MutableList<ValidationIssue>) {
        val trimmed = line.trim()

        // 1. Comparison assignment trap: e.g. "if x = 10:"
        val assignmentInIf = Regex("""^\s*(if|elif)\s+.*[^=!<>]=[^=].*:""")
        if (assignmentInIf.matches(trimmed) && !trimmed.contains(":=")) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    line = lineNum,
                    message = "Assignment '=' used inside conditional expression.",
                    suggestion = "Use '==' for equality comparison or ':=' for the walrus assignment operator.",
                    codeSnippet = trimmed
                )
            )
        }

        // 2. Shell logical operators in Python: "&&" or "||"
        if (trimmed.contains("&&") && !trimmed.contains("\"") && !trimmed.contains("'")) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    line = lineNum,
                    message = "Logical operator '&&' is not valid Python syntax.",
                    suggestion = "Replace '&&' with 'and' in Python.",
                    codeSnippet = trimmed
                )
            )
        }
        if (trimmed.contains("||") && !trimmed.contains("\"") && !trimmed.contains("'")) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    line = lineNum,
                    message = "Logical operator '||' is not valid Python syntax.",
                    suggestion = "Replace '||' with 'or' in Python.",
                    codeSnippet = trimmed
                )
            )
        }

        // 3. Lowercase booleans: true / false / null
        if (trimmed.contains(Regex("""\b(true|false|null)\b""")) && !trimmed.contains("\"") && !trimmed.contains("'")) {
            val match = Regex("""\b(true|false|null)\b""").find(trimmed)?.value
            val replacement = when (match) {
                "true" -> "True"
                "false" -> "False"
                "null" -> "None"
                else -> ""
            }
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.WARNING,
                    line = lineNum,
                    message = "Non-capitalized keyword '$match' found.",
                    suggestion = "In Python, use '$replacement' (capitalized).",
                    codeSnippet = trimmed
                )
            )
        }

        // 4. Shell echo or printf in Python
        if (trimmed.startsWith("echo ") || trimmed.startsWith("printf ")) {
            issues.add(
                ValidationIssue(
                    severity = ValidationSeverity.ERROR,
                    line = lineNum,
                    message = "Shell command '${trimmed.take(4).trim()}' used in Python script.",
                    suggestion = "Use Python's built-in print(...) function instead.",
                    codeSnippet = trimmed
                )
            )
        }
    }

    private fun sanitizeQuotesForKeywordScan(text: String): String {
        return text
            .replace(Regex("""'[^']*'"""), " ")
            .replace(Regex(""""[^"]*""""), " ")
    }
}
