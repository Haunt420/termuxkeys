package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BuiltinCatalogData
import com.example.data.BuiltinPresetsLibrary
import com.example.data.BuiltinReferenceData
import com.example.engine.PropertiesFormatterEngine
import com.example.model.CatalogCategory
import com.example.model.CatalogItem
import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.model.KeyPreset
import com.example.model.ReferenceTopic
import com.example.storage.AppDatabase
import com.example.storage.PresetEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TerminalMode {
    SHELL,
    NANO
}

data class TerminalLine(
    val text: String,
    val isCommand: Boolean = false,
    val isError: Boolean = false,
    val isSystem: Boolean = false
)

data class TermuxKeysUiState(
    // Keypad Editor State
    val rows: List<List<KeyItem>> = emptyList(),
    val style: String = "default",
    val activePresetName: String = "Nano Editor Master",
    val selectedRowIndex: Int? = null,
    val selectedColIndex: Int? = null,
    val editingKey: KeyItem? = null,
    val allPresets: List<KeyPreset> = emptyList(),

    // Terminal Sandbox State
    val terminalMode: TerminalMode = TerminalMode.SHELL,
    val terminalHistory: List<TerminalLine> = emptyList(),
    val shellInput: String = "",
    val shellCursor: Int = 0,
    val commandHistory: List<String> = emptyList(),
    val historyIndex: Int = -1,

    // Nano Simulator State
    val nanoFilename: String = "termux.properties",
    val nanoBuffer: List<String> = emptyList(),
    val nanoLineIndex: Int = 0,
    val nanoColIndex: Int = 0,
    val nanoStatusMessage: String = "",
    val nanoIsModified: Boolean = false,

    // Catalog & Reference State
    val catalogSearch: String = "",
    val selectedCatalogCategory: CatalogCategory = CatalogCategory.ALL,
    val filteredCatalog: List<CatalogItem> = emptyList(),
    val referenceSearch: String = "",
    val selectedReferenceTopicId: String = "ref_termux_syntax",
    val feedbackMessage: String? = null
)

class TermuxKeysViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val presetDao = db.presetDao()

    private val _uiState = MutableStateFlow(TermuxKeysUiState())
    val uiState: StateFlow<TermuxKeysUiState> = _uiState.asStateFlow()

    init {
        // Load initial default preset (Nano Editor Master)
        val initialPreset = BuiltinPresetsLibrary.presets.first()
        _uiState.update { state ->
            state.copy(
                rows = initialPreset.rows,
                activePresetName = initialPreset.name,
                filteredCatalog = BuiltinCatalogData.items,
                terminalHistory = getInitialTerminalLines(),
                nanoBuffer = generateNanoInitialBuffer(initialPreset.rows)
            )
        }

        // Listen for database presets
        viewModelScope.launch {
            presetDao.getAllPresets().collect { customEntities ->
                val customPresets = customEntities.map { it.toPreset() }
                val merged = BuiltinPresetsLibrary.presets + customPresets
                _uiState.update { it.copy(allPresets = merged) }
            }
        }
    }

    private fun getInitialTerminalLines(): List<TerminalLine> {
        return listOf(
            TerminalLine("Welcome to Termux! (Preview Sandbox)", isSystem = true),
            TerminalLine("Working directory: /data/data/com.termux/files/home", isSystem = true),
            TerminalLine("Type commands or press the extra keys below to test.", isSystem = true),
            TerminalLine("Try typing 'nano' or press ^O / ^X to test Nano editor simulation.", isSystem = true)
        )
    }

    private fun generateNanoInitialBuffer(rows: List<List<KeyItem>>): List<String> {
        val properties = PropertiesFormatterEngine.formatTermuxProperties(rows)
        return properties.lines()
    }

    // ─── Keypad Layout Operations ─────────────────────────────────────────────

    fun loadPreset(preset: KeyPreset) {
        _uiState.update { state ->
            state.copy(
                rows = preset.rows,
                activePresetName = preset.name,
                selectedRowIndex = null,
                selectedColIndex = null,
                editingKey = null,
                nanoBuffer = generateNanoInitialBuffer(preset.rows),
                feedbackMessage = "Loaded '${preset.name}'"
            )
        }
    }

    fun setStyle(style: String) {
        _uiState.update { it.copy(style = style) }
    }

    fun selectKeyForEdit(rowIndex: Int, colIndex: Int) {
        val row = _uiState.value.rows.getOrNull(rowIndex) ?: return
        val item = row.getOrNull(colIndex) ?: return
        _uiState.update {
            it.copy(
                selectedRowIndex = rowIndex,
                selectedColIndex = colIndex,
                editingKey = item
            )
        }
    }

    fun dismissKeyEdit() {
        _uiState.update {
            it.copy(
                selectedRowIndex = null,
                selectedColIndex = null,
                editingKey = null
            )
        }
    }

    fun updateSelectedKey(updated: KeyItem) {
        val rIdx = _uiState.value.selectedRowIndex ?: return
        val cIdx = _uiState.value.selectedColIndex ?: return
        val currentRows = _uiState.value.rows.map { it.toMutableList() }.toMutableList()
        if (rIdx in currentRows.indices && cIdx in currentRows[rIdx].indices) {
            currentRows[rIdx][cIdx] = updated
            _uiState.update {
                it.copy(
                    rows = currentRows,
                    selectedRowIndex = null,
                    selectedColIndex = null,
                    editingKey = null,
                    nanoBuffer = generateNanoInitialBuffer(currentRows),
                    feedbackMessage = "Updated key '${updated.effectiveDisplay}'"
                )
            }
        }
    }

    fun addKeyToRow(rowIndex: Int, keyItem: KeyItem) {
        val currentRows = _uiState.value.rows.map { it.toMutableList() }.toMutableList()
        if (rowIndex in currentRows.indices) {
            currentRows[rowIndex].add(keyItem)
        } else if (currentRows.isEmpty()) {
            currentRows.add(mutableListOf(keyItem))
        }
        _uiState.update {
            it.copy(
                rows = currentRows,
                nanoBuffer = generateNanoInitialBuffer(currentRows),
                feedbackMessage = "Added '${keyItem.effectiveDisplay}'"
            )
        }
    }

    fun addKeyToActiveOrFirstRow(keyItem: KeyItem) {
        val targetRow = _uiState.value.selectedRowIndex ?: 0
        if (_uiState.value.rows.isEmpty()) {
            addNewRow(listOf(keyItem))
        } else {
            addKeyToRow(targetRow.coerceIn(0, _uiState.value.rows.size - 1), keyItem)
        }
    }

    fun removeKey(rowIndex: Int, colIndex: Int) {
        val currentRows = _uiState.value.rows.map { it.toMutableList() }.toMutableList()
        if (rowIndex in currentRows.indices && colIndex in currentRows[rowIndex].indices) {
            val removed = currentRows[rowIndex].removeAt(colIndex)
            _uiState.update {
                it.copy(
                    rows = currentRows,
                    selectedRowIndex = null,
                    selectedColIndex = null,
                    editingKey = null,
                    nanoBuffer = generateNanoInitialBuffer(currentRows),
                    feedbackMessage = "Removed '${removed.effectiveDisplay}'"
                )
            }
        }
    }

    fun swapKeys(rowIndex: Int, fromCol: Int, toCol: Int) {
        val currentRows = _uiState.value.rows.map { it.toMutableList() }.toMutableList()
        if (rowIndex in currentRows.indices) {
            val row = currentRows[rowIndex]
            if (fromCol in row.indices && toCol in row.indices) {
                val temp = row[fromCol]
                row[fromCol] = row[toCol]
                row[toCol] = temp
                _uiState.update {
                    it.copy(
                        rows = currentRows,
                        nanoBuffer = generateNanoInitialBuffer(currentRows)
                    )
                }
            }
        }
    }

    fun moveKeyAcrossRows(fromRow: Int, fromCol: Int, toRow: Int) {
        val currentRows = _uiState.value.rows.map { it.toMutableList() }.toMutableList()
        if (fromRow in currentRows.indices && toRow in currentRows.indices && fromCol in currentRows[fromRow].indices) {
            val item = currentRows[fromRow].removeAt(fromCol)
            currentRows[toRow].add(item)
            _uiState.update {
                it.copy(
                    rows = currentRows,
                    nanoBuffer = generateNanoInitialBuffer(currentRows)
                )
            }
        }
    }

    fun addNewRow(initialKeys: List<KeyItem> = emptyList()) {
        if (_uiState.value.rows.size >= 4) {
            _uiState.update { it.copy(feedbackMessage = "Termux supports up to 4 rows of extra keys") }
            return
        }
        val currentRows = _uiState.value.rows.map { it.toMutableList() }.toMutableList()
        val defaultKeys = if (initialKeys.isNotEmpty()) initialKeys else listOf(
            KeyItem(key = "ESC", display = "ESC"),
            KeyItem(key = "TAB", display = "TAB"),
            KeyItem(key = "CTRL", display = "CTRL"),
            KeyItem(key = "ALT", display = "ALT")
        )
        currentRows.add(defaultKeys.toMutableList())
        _uiState.update {
            it.copy(
                rows = currentRows,
                nanoBuffer = generateNanoInitialBuffer(currentRows),
                feedbackMessage = "Added new row (Total: ${currentRows.size})"
            )
        }
    }

    fun removeRow(rowIndex: Int) {
        if (_uiState.value.rows.size <= 1) {
            _uiState.update { it.copy(feedbackMessage = "At least one row is required") }
            return
        }
        val currentRows = _uiState.value.rows.map { it.toMutableList() }.toMutableList()
        if (rowIndex in currentRows.indices) {
            currentRows.removeAt(rowIndex)
            _uiState.update {
                it.copy(
                    rows = currentRows,
                    selectedRowIndex = null,
                    selectedColIndex = null,
                    editingKey = null,
                    nanoBuffer = generateNanoInitialBuffer(currentRows),
                    feedbackMessage = "Removed row"
                )
            }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    // ─── Preset Persistence ───────────────────────────────────────────────────

    fun saveCurrentLayoutAsPreset(name: String, category: String, description: String) {
        if (name.isBlank()) return
        val newPreset = KeyPreset(
            id = java.util.UUID.randomUUID().toString(),
            name = name.trim(),
            category = if (category.isNotBlank()) category.trim() else "Custom",
            description = if (description.isNotBlank()) description.trim() else "Custom extra keys configuration",
            rows = _uiState.value.rows,
            isCustom = true
        )
        viewModelScope.launch {
            presetDao.insertPreset(PresetEntity.fromPreset(newPreset))
            _uiState.update {
                it.copy(
                    activePresetName = newPreset.name,
                    feedbackMessage = "Saved preset '${newPreset.name}'"
                )
            }
        }
    }

    fun deletePreset(presetId: String) {
        viewModelScope.launch {
            presetDao.deletePresetById(presetId)
            _uiState.update { it.copy(feedbackMessage = "Preset deleted") }
        }
    }

    // ─── Catalog & Reference Search ───────────────────────────────────────────

    fun onCatalogSearchChanged(query: String) {
        _uiState.update { state ->
            val filtered = filterCatalog(query, state.selectedCatalogCategory)
            state.copy(catalogSearch = query, filteredCatalog = filtered)
        }
    }

    fun onCatalogCategorySelected(category: CatalogCategory) {
        _uiState.update { state ->
            val filtered = filterCatalog(state.catalogSearch, category)
            state.copy(selectedCatalogCategory = category, filteredCatalog = filtered)
        }
    }

    private fun filterCatalog(query: String, category: CatalogCategory): List<CatalogItem> {
        val base = if (category == CatalogCategory.ALL) {
            BuiltinCatalogData.items
        } else {
            BuiltinCatalogData.items.filter { it.category == category }
        }
        if (query.isBlank()) return base
        val q = query.trim().lowercase()
        return base.filter { item ->
            item.name.lowercase().contains(q) ||
            item.keyString.lowercase().contains(q) ||
            item.description.lowercase().contains(q) ||
            item.defaultDisplay.lowercase().contains(q) ||
            item.searchTags.any { it.lowercase().contains(q) }
        }
    }

    fun onReferenceTopicSelected(topicId: String) {
        _uiState.update { it.copy(selectedReferenceTopicId = topicId) }
    }

    // ─── Terminal Sandbox Engine ──────────────────────────────────────────────

    fun toggleTerminalMode(mode: TerminalMode) {
        _uiState.update { state ->
            if (mode == TerminalMode.NANO && state.nanoBuffer.isEmpty()) {
                state.copy(
                    terminalMode = mode,
                    nanoBuffer = generateNanoInitialBuffer(state.rows)
                )
            } else {
                state.copy(terminalMode = mode)
            }
        }
    }

    fun onShellInputChange(text: String) {
        _uiState.update { it.copy(shellInput = text, shellCursor = text.length) }
    }

    fun executeShellInput() {
        val input = _uiState.value.shellInput
        executeCommand(input)
    }

    fun executeCommand(rawCommand: String) {
        val cmd = rawCommand.trim()
        val history = _uiState.value.terminalHistory.toMutableList()
        val promptText = "u0_a123@termux:~$ $rawCommand"
        history.add(TerminalLine(promptText, isCommand = true))

        val cmdHistory = _uiState.value.commandHistory.toMutableList()
        if (cmd.isNotBlank()) {
            cmdHistory.add(cmd)
        }

        when {
            cmd.equals("clear", ignoreCase = true) || cmd.equals("cls", ignoreCase = true) -> {
                history.clear()
            }
            cmd.startsWith("nano", ignoreCase = true) -> {
                val filename = cmd.removePrefix("nano").trim().ifEmpty { "termux.properties" }
                _uiState.update {
                    it.copy(
                        terminalMode = TerminalMode.NANO,
                        nanoFilename = filename,
                        nanoStatusMessage = "Opened $filename in simulated GNU nano 7.2",
                        shellInput = "",
                        shellCursor = 0,
                        commandHistory = cmdHistory,
                        historyIndex = -1
                    )
                }
                return
            }
            cmd.equals("help", ignoreCase = true) -> {
                history.add(TerminalLine("TermuxKeys Studio Terminal Simulator Help:"))
                history.add(TerminalLine("• nano [file]  : Open interactive GNU nano editor simulation"))
                history.add(TerminalLine("• ls -la       : List files and .termux configuration"))
                history.add(TerminalLine("• cat <file>   : Print file contents to terminal"))
                history.add(TerminalLine("• termux-reload-settings : Reload and apply extra-keys configuration"))
                history.add(TerminalLine("• clear        : Clear terminal history buffer"))
                history.add(TerminalLine("• Tap any key below to type or send signals directly!"))
            }
            cmd.equals("ls", ignoreCase = true) || cmd.equals("ls -la", ignoreCase = true) -> {
                history.add(TerminalLine("drwx------  5 u0_a123 u0_a123 4096 Oct 17 17:30 ."))
                history.add(TerminalLine("drwx------ 11 u0_a123 u0_a123 4096 Oct 17 17:00 .."))
                history.add(TerminalLine("drwx------  2 u0_a123 u0_a123 4096 Oct 17 17:28 .termux"))
                history.add(TerminalLine("-rw-r--r--  1 u0_a123 u0_a123  128 Oct 17 17:20 .bashrc"))
                history.add(TerminalLine("-rw-r--r--  1 u0_a123 u0_a123  640 Oct 17 17:31 termux.properties"))
                history.add(TerminalLine("-rwxr-xr-x  1 u0_a123 u0_a123  256 Oct 17 17:25 test_keys.sh"))
                history.add(TerminalLine("drwxr-xr-x  2 u0_a123 u0_a123 4096 Oct 17 17:15 storage"))
            }
            cmd.equals("termux-reload-settings", ignoreCase = true) -> {
                history.add(TerminalLine("[OK] Termux settings reloaded successfully!", isSystem = true))
                history.add(TerminalLine("Extra keys bar refreshed with active configuration.", isSystem = true))
            }
            cmd.equals("pwd", ignoreCase = true) -> {
                history.add(TerminalLine("/data/data/com.termux/files/home"))
            }
            cmd.equals("whoami", ignoreCase = true) -> {
                history.add(TerminalLine("u0_a123"))
            }
            cmd.startsWith("echo ", ignoreCase = true) -> {
                history.add(TerminalLine(cmd.substring(5)))
            }
            cmd.startsWith("cat ", ignoreCase = true) -> {
                val filename = cmd.substring(4).trim()
                if (filename.contains("termux.properties")) {
                    val content = PropertiesFormatterEngine.formatTermuxProperties(_uiState.value.rows, _uiState.value.style)
                    content.lines().forEach { history.add(TerminalLine(it)) }
                } else {
                    history.add(TerminalLine("# Sample file content for $filename"))
                    history.add(TerminalLine("export TERM=xterm-256color"))
                }
            }
            cmd.isNotBlank() -> {
                history.add(TerminalLine("[Simulated Output] Executed: $cmd"))
            }
        }

        _uiState.update {
            it.copy(
                terminalHistory = history,
                shellInput = "",
                shellCursor = 0,
                commandHistory = cmdHistory,
                historyIndex = -1
            )
        }
    }

    /**
     * Handles live touch events when a key is tapped in the Extra Keys Bar.
     */
    fun onExtraKeyTapped(keyItem: KeyItem, isPopup: Boolean = false) {
        val effectiveKey = if (isPopup && keyItem.popup != null) keyItem.popup.key else keyItem.key
        val effectiveDisplay = if (isPopup && keyItem.popup != null) keyItem.popup.effectiveDisplay else keyItem.effectiveDisplay

        // If in Nano Mode, handle Nano-specific keyboard shortcuts
        if (_uiState.value.terminalMode == TerminalMode.NANO) {
            handleNanoKeyInput(effectiveKey, effectiveDisplay)
            return
        }

        // Handle Shell Mode
        handleShellKeyInput(effectiveKey, effectiveDisplay)
    }

    private fun handleShellKeyInput(key: String, display: String) {
        val state = _uiState.value
        val input = state.shellInput
        val cursor = state.shellCursor

        when (key) {
            "ENTER" -> {
                executeShellInput()
            }
            "BKSP" -> {
                if (cursor > 0) {
                    val newInput = input.substring(0, cursor - 1) + input.substring(cursor)
                    _uiState.update { it.copy(shellInput = newInput, shellCursor = cursor - 1) }
                }
            }
            "DEL" -> {
                if (cursor < input.length) {
                    val newInput = input.substring(0, cursor) + input.substring(cursor + 1)
                    _uiState.update { it.copy(shellInput = newInput) }
                }
            }
            "LEFT" -> {
                _uiState.update { it.copy(shellCursor = (cursor - 1).coerceAtLeast(0)) }
            }
            "RIGHT" -> {
                _uiState.update { it.copy(shellCursor = (cursor + 1).coerceAtMost(input.length)) }
            }
            "HOME" -> {
                _uiState.update { it.copy(shellCursor = 0) }
            }
            "END" -> {
                _uiState.update { it.copy(shellCursor = input.length) }
            }
            "UP" -> {
                // History Up
                val history = state.commandHistory
                if (history.isNotEmpty()) {
                    val newIdx = if (state.historyIndex == -1) history.size - 1 else (state.historyIndex - 1).coerceAtLeast(0)
                    val cmd = history[newIdx]
                    _uiState.update { it.copy(shellInput = cmd, shellCursor = cmd.length, historyIndex = newIdx) }
                }
            }
            "DOWN" -> {
                // History Down
                val history = state.commandHistory
                if (state.historyIndex != -1) {
                    val newIdx = state.historyIndex + 1
                    if (newIdx < history.size) {
                        val cmd = history[newIdx]
                        _uiState.update { it.copy(shellInput = cmd, shellCursor = cmd.length, historyIndex = newIdx) }
                    } else {
                        _uiState.update { it.copy(shellInput = "", shellCursor = 0, historyIndex = -1) }
                    }
                }
            }
            "TAB" -> {
                // Autocomplete
                val tokens = listOf("nano", "ls", "clear", "termux-reload-settings", "termux.properties", "help", "cat")
                val match = tokens.find { it.startsWith(input) && it != input }
                if (match != null) {
                    _uiState.update { it.copy(shellInput = "$match ", shellCursor = match.length + 1) }
                } else {
                    _uiState.update { it.copy(feedbackMessage = "TAB autocomplete") }
                }
            }
            "ESC" -> {
                _uiState.update { it.copy(feedbackMessage = "ESC sent") }
            }
            "CTRL" -> {
                when (display) {
                    "^C" -> {
                        // Cancel line
                        val history = state.terminalHistory.toMutableList()
                        history.add(TerminalLine("u0_a123@termux:~$ $input^C", isCommand = true))
                        _uiState.update { it.copy(terminalHistory = history, shellInput = "", shellCursor = 0, feedbackMessage = "SIGINT (^C)") }
                    }
                    "^L" -> {
                        // Clear
                        _uiState.update { it.copy(terminalHistory = emptyList(), feedbackMessage = "Clear (^L)") }
                    }
                    "^D" -> {
                        // Exit/EOF
                        val history = state.terminalHistory.toMutableList()
                        history.add(TerminalLine("u0_a123@termux:~$ exit", isCommand = true))
                        _uiState.update { it.copy(terminalHistory = history, shellInput = "", shellCursor = 0, feedbackMessage = "EOF (^D)") }
                    }
                    "^U" -> {
                        // Cut to start of line
                        val newInput = input.substring(cursor)
                        _uiState.update { it.copy(shellInput = newInput, shellCursor = 0) }
                    }
                    "^K" -> {
                        // Cut to end of line
                        val newInput = input.substring(0, cursor)
                        _uiState.update { it.copy(shellInput = newInput) }
                    }
                    "^W" -> {
                        // Delete word backward
                        val trimmed = input.substring(0, cursor).trimEnd()
                        val lastSpace = trimmed.lastIndexOf(' ')
                        val newStart = if (lastSpace == -1) "" else input.substring(0, lastSpace + 1)
                        val rest = input.substring(cursor)
                        val result = newStart + rest
                        _uiState.update { it.copy(shellInput = result, shellCursor = newStart.length) }
                    }
                    "^A" -> {
                        _uiState.update { it.copy(shellCursor = 0) }
                    }
                    "^E" -> {
                        _uiState.update { it.copy(shellCursor = input.length) }
                    }
                    "^O" -> {
                        // Launch nano directly
                        _uiState.update {
                            it.copy(
                                terminalMode = TerminalMode.NANO,
                                nanoStatusMessage = "Nano simulated mode opened with ^O"
                            )
                        }
                    }
                    "^X" -> {
                        _uiState.update {
                            it.copy(
                                terminalMode = TerminalMode.NANO,
                                nanoStatusMessage = "Nano simulated mode opened with ^X"
                            )
                        }
                    }
                    else -> {
                        _uiState.update { it.copy(feedbackMessage = "Modifier CTRL dispatched") }
                    }
                }
            }
            "ALT" -> {
                _uiState.update { it.copy(feedbackMessage = "Modifier ALT dispatched") }
            }
            "DRAWER" -> {
                _uiState.update { it.copy(feedbackMessage = "Termux Sidebar Drawer toggled [DRAWER]") }
            }
            "KEYBOARD" -> {
                _uiState.update { it.copy(feedbackMessage = "Virtual Keyboard toggled [KEYBOARD]") }
            }
            "PASTE" -> {
                val toPaste = "nano ~/.termux/termux.properties"
                val newInput = input.substring(0, cursor) + toPaste + input.substring(cursor)
                _uiState.update { it.copy(shellInput = newInput, shellCursor = cursor + toPaste.length, feedbackMessage = "Pasted sample clipboard text") }
            }
            else -> {
                // Text macro or symbol insertion
                val insert = key
                val newInput = input.substring(0, cursor) + insert + input.substring(cursor)
                _uiState.update { it.copy(shellInput = newInput, shellCursor = cursor + insert.length) }
            }
        }
    }

    private fun handleNanoKeyInput(key: String, display: String) {
        val state = _uiState.value
        val buffer = state.nanoBuffer.toMutableList()
        var lineIdx = state.nanoLineIndex.coerceIn(0, (buffer.size - 1).coerceAtLeast(0))

        when {
            display == "^X" || key == "^X" -> {
                // Exit Nano
                _uiState.update {
                    it.copy(
                        terminalMode = TerminalMode.SHELL,
                        feedbackMessage = "Exited GNU nano buffer",
                        nanoStatusMessage = ""
                    )
                }
            }
            display == "^O" || key == "^O" -> {
                // Save buffer
                _uiState.update {
                    it.copy(
                        nanoStatusMessage = "[ Wrote ${buffer.size} lines to ${it.nanoFilename} ]",
                        nanoIsModified = false,
                        feedbackMessage = "Saved file in Nano (^O)"
                    )
                }
            }
            display == "^K" || key == "^K" -> {
                // Cut line
                if (buffer.isNotEmpty() && lineIdx in buffer.indices) {
                    val cut = buffer.removeAt(lineIdx)
                    if (buffer.isEmpty()) buffer.add("")
                    lineIdx = lineIdx.coerceAtMost(buffer.size - 1)
                    _uiState.update {
                        it.copy(
                            nanoBuffer = buffer,
                            nanoLineIndex = lineIdx,
                            nanoIsModified = true,
                            nanoStatusMessage = "Cut line: \"${cut.take(20)}\"",
                            feedbackMessage = "Cut text in Nano (^K)"
                        )
                    }
                }
            }
            display == "^U" || key == "^U" -> {
                // Paste line
                buffer.add(lineIdx, "# Pasted line from cutbuffer")
                _uiState.update {
                    it.copy(
                        nanoBuffer = buffer,
                        nanoIsModified = true,
                        nanoStatusMessage = "Uncut 1 line from cutbuffer",
                        feedbackMessage = "Pasted text in Nano (^U)"
                    )
                }
            }
            display == "^C" || key == "^C" -> {
                // Show line info
                _uiState.update {
                    it.copy(
                        nanoStatusMessage = "line ${lineIdx + 1}/${buffer.size} (100%), col ${it.nanoColIndex + 1}/80",
                        feedbackMessage = "Cursor info (^C)"
                    )
                }
            }
            display == "^W" || key == "^W" -> {
                _uiState.update {
                    it.copy(
                        nanoStatusMessage = "Search: extra-keys [Found match on line 7]",
                        feedbackMessage = "Search forward (^W)"
                    )
                }
            }
            key == "UP" -> {
                val newL = (lineIdx - 1).coerceAtLeast(0)
                _uiState.update { it.copy(nanoLineIndex = newL) }
            }
            key == "DOWN" -> {
                val newL = (lineIdx + 1).coerceAtMost(buffer.size - 1)
                _uiState.update { it.copy(nanoLineIndex = newL) }
            }
            key == "LEFT" -> {
                _uiState.update { it.copy(nanoColIndex = (it.nanoColIndex - 1).coerceAtLeast(0)) }
            }
            key == "RIGHT" -> {
                _uiState.update { it.copy(nanoColIndex = it.nanoColIndex + 1) }
            }
            key == "ENTER" -> {
                buffer.add(lineIdx + 1, "")
                _uiState.update {
                    it.copy(
                        nanoBuffer = buffer,
                        nanoLineIndex = lineIdx + 1,
                        nanoColIndex = 0,
                        nanoIsModified = true
                    )
                }
            }
            key == "BKSP" -> {
                if (buffer.isNotEmpty() && lineIdx in buffer.indices) {
                    val line = buffer[lineIdx]
                    if (line.isNotEmpty()) {
                        buffer[lineIdx] = line.dropLast(1)
                        _uiState.update { it.copy(nanoBuffer = buffer, nanoIsModified = true) }
                    }
                }
            }
            else -> {
                // Insert character or text
                if (buffer.isNotEmpty() && lineIdx in buffer.indices) {
                    buffer[lineIdx] = buffer[lineIdx] + key
                    _uiState.update { it.copy(nanoBuffer = buffer, nanoIsModified = true) }
                }
            }
        }
    }
}
