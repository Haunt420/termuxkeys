package com.example.model

import java.util.UUID

/**
 * Represents an individual keycap in the Termux extra-keys grid.
 */
data class KeyItem(
    val id: String = UUID.randomUUID().toString(),
    val key: String,                  // The Termux key identifier (e.g., "ESC", "CTRL", "TAB", "|", "UP")
    val display: String = "",         // Custom display label/symbol (e.g., "⎋", "^", "Pipe")
    val popup: KeyPopup? = null       // Optional swipe-up popup key configuration
) {
    /**
     * Resolves the visible label to render on the keycap.
     */
    val effectiveDisplay: String
        get() = if (display.isNotBlank()) display else key
}

/**
 * Represents a swipe-up/long-press popup action attached to a key.
 */
data class KeyPopup(
    val key: String,                  // Popup key code or macro (e.g., "F1", "~", "HOME")
    val display: String = ""          // Custom display label for the popup
) {
    val effectiveDisplay: String
        get() = if (display.isNotBlank()) display else key
}

/**
 * Preset layout containing multiple rows of extra keys.
 */
data class KeyPreset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,             // "Nano", "Bash & Piping", "Termux Essentials", "Custom", etc.
    val description: String,
    val rows: List<List<KeyItem>>,
    val isCustom: Boolean = false
)

/**
 * Categories for classifying shortcuts and function keys in the catalog.
 */
enum class CatalogCategory(val displayName: String, val iconDescription: String) {
    ALL("All Keys", "Entire shortcut and function collection"),
    MODIFIERS("Modifiers & Control", "Ctrl, Alt, Esc, Tab, Enter, Backspace"),
    NAVIGATION("Navigation & Arrows", "Directional arrows, Home, End, PgUp, PgDn"),
    FUNCTION_KEYS("Function Keys (F1-F12)", "VT terminal function keys F1 through F12"),
    TERMUX_ACTIONS("Termux Triggers", "Drawer, Keyboard toggle, Clipboard paste"),
    CONTROL_SIGNALS("Control Signals", "SIGINT (^C), EOF (^D), SIGTSTP (^Z), Clear (^L)"),
    NANO_COMMANDS("GNU Nano Shortcuts", "Save, Exit, Cut, Paste, Search, Replace, Justify"),
    BASH_READLINE("Bash & Zsh Readline", "History, sudo !!, line jumps, word deletes"),
    PIPES_OPERATORS("Pipes & Redirection", "Pipes, Redirection, grep, sed, awk, tee, 2>&1")
}

/**
 * An entry in the exhaustive key and function catalog.
 */
data class CatalogItem(
    val id: String,
    val name: String,
    val keyString: String,
    val category: CatalogCategory,
    val description: String,
    val defaultDisplay: String = "",
    val suggestedPopup: String? = null,
    val searchTags: List<String> = emptyList()
)

/**
 * A comprehensive documentation section in the Reference Material Hub.
 */
data class ReferenceTopic(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val sections: List<ReferenceSection>,
    val relatedKeys: List<String> = emptyList()
)

data class ReferenceSection(
    val heading: String,
    val content: String,
    val codeSnippet: String? = null
)
