package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.ui.components.CatalogPickerSheet
import com.example.ui.components.ExportBottomSheet
import com.example.ui.components.KeyInspectorDialog
import com.example.ui.components.KeypadEditorView
import com.example.ui.components.PresetsDialog
import com.example.ui.components.ReferenceHubView
import com.example.ui.components.TerminalSandboxView
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TermuxKeysViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TermuxKeysViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTab by remember { mutableIntStateOf(1) } // 0: Editor, 1: Terminal Sandbox, 2: Catalog, 3: Reference
    var showExportSheet by remember { mutableStateOf(false) }
    var showPresetsDialog by remember { mutableStateOf(false) }
    var showCatalogPicker by remember { mutableStateOf(false) }

    // Handle feedback toasts
    LaunchedEffect(uiState.feedbackMessage) {
        val msg = uiState.feedbackMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(TerminalBg),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = MatrixGreenBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "TermuxKeys Studio",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                actions = {
                    // Export Button
                    Button(
                        onClick = { showExportSheet = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenBright,
                            contentColor = TerminalBg
                        ),
                        modifier = Modifier.padding(end = 8.dp).testTag("top_bar_export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Export",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TerminalSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = TerminalSurface,
                contentColor = TextPrimary
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Keyboard, contentDescription = "Editor") },
                    label = { Text("Editor", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MatrixGreenBright,
                        selectedTextColor = MatrixGreenBright,
                        indicatorColor = MatrixGreen.copy(alpha = 0.25f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_editor")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = "Sandbox") },
                    label = { Text("Sandbox", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MatrixGreenBright,
                        selectedTextColor = MatrixGreenBright,
                        indicatorColor = MatrixGreen.copy(alpha = 0.25f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_sandbox")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Catalog") },
                    label = { Text("Catalog", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        selectedTextColor = CyberCyan,
                        indicatorColor = CyberCyan.copy(alpha = 0.25f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_catalog")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Book, contentDescription = "Reference") },
                    label = { Text("Reference", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        selectedTextColor = CyberCyan,
                        indicatorColor = CyberCyan.copy(alpha = 0.25f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_reference")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> {
                    // Visual Keypad Editor
                    KeypadEditorView(
                        rows = uiState.rows,
                        style = uiState.style,
                        selectedRow = uiState.selectedRowIndex,
                        selectedCol = uiState.selectedColIndex,
                        onSelectKey = { r, c -> viewModel.selectKeyForEdit(r, c) },
                        onAddKeyToRow = { rowIndex ->
                            viewModel.selectKeyForEdit(rowIndex, 0)
                            showCatalogPicker = true
                        },
                        onAddNewRow = { viewModel.addNewRow() },
                        onRemoveRow = { rowIndex -> viewModel.removeRow(rowIndex) },
                        onStyleChanged = { style -> viewModel.setStyle(style) },
                        onOpenPresets = { showPresetsDialog = true },
                        onOpenCatalog = { showCatalogPicker = true }
                    )
                }

                1 -> {
                    // Interactive Terminal Sandbox & Nano Simulator
                    TerminalSandboxView(
                        mode = uiState.terminalMode,
                        history = uiState.terminalHistory,
                        shellInput = uiState.shellInput,
                        shellCursor = uiState.shellCursor,
                        nanoFilename = uiState.nanoFilename,
                        nanoBuffer = uiState.nanoBuffer,
                        nanoLineIndex = uiState.nanoLineIndex,
                        nanoStatusMessage = uiState.nanoStatusMessage,
                        nanoIsModified = uiState.nanoIsModified,
                        keysLayout = uiState.rows,
                        onModeChanged = { mode -> viewModel.toggleTerminalMode(mode) },
                        onShellInputChange = { text -> viewModel.onShellInputChange(text) },
                        onExecuteCommand = { viewModel.executeShellInput() },
                        onKeyTapped = { keyItem, isPopup -> viewModel.onExtraKeyTapped(keyItem, isPopup) },
                        onClearTerminal = { viewModel.executeCommand("clear") }
                    )
                }

                2 -> {
                    // Exhaustive Shortcuts & Functions Catalog
                    com.example.ui.components.CatalogPickerSheet(
                        items = uiState.filteredCatalog,
                        searchQuery = uiState.catalogSearch,
                        selectedCategory = uiState.selectedCatalogCategory,
                        onSearchChanged = { viewModel.onCatalogSearchChanged(it) },
                        onCategorySelected = { viewModel.onCatalogCategorySelected(it) },
                        onInsertKey = { keyItem ->
                            viewModel.addKeyToActiveOrFirstRow(keyItem)
                            selectedTab = 0 // Switch back to editor
                        },
                        onDismiss = { selectedTab = 0 }
                    )
                }

                3 -> {
                    // Exhaustive Reference Material Hub
                    ReferenceHubView(
                        selectedTopicId = uiState.selectedReferenceTopicId,
                        onTopicSelected = { viewModel.onReferenceTopicSelected(it) },
                        onAddKeyToLayout = { keyItem ->
                            viewModel.addKeyToActiveOrFirstRow(keyItem)
                        }
                    )
                }
            }
        }
    }

    // Modal: Key Inspector Dialog (when key is tapped in editor)
    if (uiState.editingKey != null && uiState.selectedRowIndex != null && uiState.selectedColIndex != null) {
        val rIdx = uiState.selectedRowIndex!!
        val cIdx = uiState.selectedColIndex!!
        val row = uiState.rows.getOrNull(rIdx) ?: emptyList()

        KeyInspectorDialog(
            item = uiState.editingKey!!,
            rowIndex = rIdx,
            colIndex = cIdx,
            totalRows = uiState.rows.size,
            totalColsInRow = row.size,
            onSave = { updated -> viewModel.updateSelectedKey(updated) },
            onDelete = { viewModel.removeKey(rIdx, cIdx) },
            onMoveLeft = { viewModel.swapKeys(rIdx, cIdx, cIdx - 1) },
            onMoveRight = { viewModel.swapKeys(rIdx, cIdx, cIdx + 1) },
            onMoveUp = { viewModel.moveKeyAcrossRows(rIdx, cIdx, rIdx - 1) },
            onMoveDown = { viewModel.moveKeyAcrossRows(rIdx, cIdx, rIdx + 1) },
            onOpenCatalog = { showCatalogPicker = true },
            onDismiss = { viewModel.dismissKeyEdit() }
        )
    }

    // Modal: Catalog Picker (when adding a key from inspector)
    if (showCatalogPicker) {
        CatalogPickerSheet(
            items = uiState.filteredCatalog,
            searchQuery = uiState.catalogSearch,
            selectedCategory = uiState.selectedCatalogCategory,
            onSearchChanged = { viewModel.onCatalogSearchChanged(it) },
            onCategorySelected = { viewModel.onCatalogCategorySelected(it) },
            onInsertKey = { keyItem ->
                viewModel.addKeyToActiveOrFirstRow(keyItem)
                showCatalogPicker = false
            },
            onSetAsPopup = { popup ->
                val current = uiState.editingKey
                if (current != null) {
                    viewModel.updateSelectedKey(current.copy(popup = popup))
                }
                showCatalogPicker = false
            },
            onDismiss = { showCatalogPicker = false }
        )
    }

    // Modal: Presets Library
    if (showPresetsDialog) {
        PresetsDialog(
            presets = uiState.allPresets,
            activePresetName = uiState.activePresetName,
            onLoadPreset = { preset -> viewModel.loadPreset(preset) },
            onSaveCurrent = { name, cat, desc -> viewModel.saveCurrentLayoutAsPreset(name, cat, desc) },
            onDeleteCustomPreset = { presetId -> viewModel.deletePreset(presetId) },
            onDismiss = { showPresetsDialog = false }
        )
    }

    // Modal: Export to Termux Bottom Sheet
    if (showExportSheet) {
        ExportBottomSheet(
            rows = uiState.rows,
            style = uiState.style,
            onDismiss = { showExportSheet = false }
        )
    }
}
