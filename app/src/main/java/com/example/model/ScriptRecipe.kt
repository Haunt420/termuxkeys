package com.example.model

import java.util.UUID

enum class ScriptType(val displayName: String, val extension: String, val iconLabel: String) {
    BASH_ALIAS("Bash / Zsh Alias", ".bashrc", "ALIAS"),
    BASH_FUNCTION("Bash Function with Args", ".sh", "FUNC"),
    PYTHON_SCRIPT("Python Automation Script", ".py", "PY"),
    ONE_LINER("Inline Terminal Command", "", "EXEC")
}

data class ScriptRecipe(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: ScriptType,
    val category: String,
    val summary: String,
    val explanation: String,
    val codeBody: String,
    val suggestedKeyDisplay: String,
    val suggestedKeyCode: String,
    val suggestedPopup: String? = null,
    val sampleUsage: String,
    val steps: List<String> = emptyList()
)

data class CustomShortcutDraft(
    val name: String = "",
    val type: ScriptType = ScriptType.BASH_ALIAS,
    val commandOrBody: String = "",
    val keyDisplay: String = "",
    val popupDisplay: String = "",
    val popupCode: String = "",
    val description: String = ""
)
