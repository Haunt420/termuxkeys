package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.KeyItem
import com.example.ui.components.BashFunctionWizardDialog
import com.example.ui.components.CatalogPickerSheet
import com.example.ui.components.CatalogScreenView
import com.example.ui.components.ExportBottomSheet
import com.example.ui.components.KeyInspectorDialog
import com.example.ui.components.KeypadEditorView
import com.example.ui.components.PresetsDialog
import com.example.ui.components.ReferenceHubView
import com.example.ui.components.ShortcutCreatorSuiteView
import com.example.ui.components.TerminalSandboxView
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TermuxKeysViewModel

private data class NavDestination(
    val index: Int,
    val label: String,
    val icon: ImageVector,
    val accent: Color,
    val tag: String
)

@Composable
fun MainScreen(viewModel: TermuxKeysViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0

    // Tab 0: Sandbox, Tab 1: Editor, Tab 2: Catalog, Tab 3: Creator Suite, Tab 4: Reference
    var selectedTab by remember { mutableIntStateOf(0) }
    var showExportSheet by remember { mutableStateOf(false) }
    var showPresetsDialog by remember { mutableStateOf(false) }
    var showCatalogPicker by remember { mutableStateOf(false) }
    var showBashWizardDialog by remember { mutableStateOf(false) }

    // Return to Sandbox when pressing back on secondary tabs
    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    // Handle feedback toasts
    LaunchedEffect(uiState.feedbackMessage) {
        val msg = uiState.feedbackMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    val destinations = remember {
        listOf(
            NavDestination(0, "Sandbox", Icons.Default.Terminal, MatrixGreenBright, "nav_tab_sandbox"),
            NavDestination(1, "Editor", Icons.Default.Keyboard, MatrixGreenBright, "nav_tab_editor"),
            NavDestination(2, "Catalog", Icons.AutoMirrored.Filled.List, CyberCyan, "nav_tab_catalog"),
            NavDestination(3, "Creator", Icons.Default.Build, AmberAccent, "nav_tab_creator"),
            NavDestination(4, "Reference", Icons.Default.Book, CyberCyan, "nav_tab_reference")
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
    ) {
        val isWideOrLandscape = maxWidth >= 600.dp || (maxWidth > maxHeight && maxHeight < 500.dp)
        val isCompactHeight = maxHeight < 500.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Adaptive Top Header Bar (respects top & horizontal cutout/status insets)
            Surface(
                color = TerminalSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                        )
                        .height(if (isCompactHeight) 44.dp else 56.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = MatrixGreenBright,
                            modifier = Modifier.size(if (isCompactHeight) 18.dp else 20.dp)
                        )
                        Text(
                            text = "TermuxKeys Studio",
                            color = TextPrimary,
                            fontSize = if (isCompactHeight) 14.sp else 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = { showExportSheet = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenBright,
                            contentColor = TerminalBg
                        ),
                        contentPadding = if (isCompactHeight) {
                            PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        } else {
                            PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        },
                        modifier = Modifier
                            .height(if (isCompactHeight) 32.dp else 38.dp)
                            .testTag("top_bar_export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export",
                            modifier = Modifier.size(if (isCompactHeight) 14.dp else 16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Export",
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompactHeight) 11.sp else 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            HorizontalDivider(color = TerminalBorder, thickness = 0.5.dp)

            if (isWideOrLandscape) {
                // ─── LANDSCAPE / TABLET LAYOUT: Side NavigationRail + Full-Height Content ───
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                        )
                        .imePadding()
                ) {
                    NavigationRail(
                        containerColor = TerminalSurface,
                        contentColor = TextPrimary,
                        modifier = Modifier
                            .width(72.dp)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            destinations.forEach { dest ->
                                NavigationRailItem(
                                    selected = selectedTab == dest.index,
                                    onClick = { selectedTab = dest.index },
                                    icon = {
                                        Icon(
                                            imageVector = dest.icon,
                                            contentDescription = dest.label,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = dest.label,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = dest.accent,
                                        selectedTextColor = dest.accent,
                                        indicatorColor = dest.accent.copy(alpha = 0.22f),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag(dest.tag)
                                )
                            }
                        }
                    }

                    VerticalDivider(color = TerminalBorder, thickness = 0.5.dp)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        MainTabContent(
                            selectedTab = selectedTab,
                            uiState = uiState,
                            viewModel = viewModel,
                            onSelectTab = { selectedTab = it },
                            onShowCatalogPicker = { showCatalogPicker = true },
                            onShowPresetsDialog = { showPresetsDialog = true },
                            onShowBashWizard = { showBashWizardDialog = true }
                        )
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            } else {
                // ─── PORTRAIT LAYOUT: Content + IME-Aware Adaptive Bottom NavigationBar ───
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                        )
                        .let { mod ->
                            // When IME is open, NavigationBar hides and imePadding() shrinks content cleanly above the keyboard!
                            if (isImeVisible) mod.imePadding() else mod
                        }
                ) {
                    MainTabContent(
                        selectedTab = selectedTab,
                        uiState = uiState,
                        viewModel = viewModel,
                        onSelectTab = { selectedTab = it },
                        onShowCatalogPicker = { showCatalogPicker = true },
                        onShowPresetsDialog = { showPresetsDialog = true },
                        onShowBashWizard = { showBashWizardDialog = true }
                    )
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Hide bottom NavigationBar while the system keyboard is open so bottom of layout stays visible
                AnimatedVisibility(visible = !isImeVisible) {
                    NavigationBar(
                        containerColor = TerminalSurface,
                        contentColor = TextPrimary
                    ) {
                        destinations.forEach { dest ->
                            NavigationBarItem(
                                selected = selectedTab == dest.index,
                                onClick = { selectedTab = dest.index },
                                icon = { Icon(dest.icon, contentDescription = dest.label) },
                                label = {
                                    Text(
                                        text = dest.label,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = dest.accent,
                                    selectedTextColor = dest.accent,
                                    indicatorColor = dest.accent.copy(alpha = 0.25f),
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag(dest.tag)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Key Inspector Dialog
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

    // Modal: Quick Catalog Picker
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

    // Modal: Multi-Step Function Wizard Dialog Suite
    if (showBashWizardDialog) {
        BashFunctionWizardDialog(
            totalRows = uiState.rows.size,
            onSaveShortcut = { keyItem, targetRow, fnName, fnBody ->
                viewModel.registerCustomFunction(fnName, fnBody)
                viewModel.addKeyToRow(targetRow, keyItem)
                showBashWizardDialog = false
            },
            onTestInTerminal = { command, fnName, fnBody ->
                viewModel.registerCustomFunction(fnName, fnBody)
                viewModel.executeCommand(command.trimEnd('\n', '\r'))
                showBashWizardDialog = false
                selectedTab = 0
            },
            onDismiss = { showBashWizardDialog = false }
        )
    }
}

@Composable
private fun MainTabContent(
    selectedTab: Int,
    uiState: com.example.viewmodel.TermuxKeysUiState,
    viewModel: TermuxKeysViewModel,
    onSelectTab: (Int) -> Unit,
    onShowCatalogPicker: () -> Unit,
    onShowPresetsDialog: () -> Unit,
    onShowBashWizard: () -> Unit
) {
    when (selectedTab) {
        0 -> {
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
                onClearTerminal = { viewModel.executeCommand("clear") },
                onMoveKey = { fromRow, fromCol, toRow, insertIndex ->
                    viewModel.moveKey(fromRow, fromCol, toRow, insertIndex)
                }
            )
        }

        1 -> {
            KeypadEditorView(
                rows = uiState.rows,
                style = uiState.style,
                selectedRow = uiState.selectedRowIndex,
                selectedCol = uiState.selectedColIndex,
                onSelectKey = { r, c -> viewModel.selectKeyForEdit(r, c) },
                onAddKeyToRow = { rowIndex ->
                    viewModel.selectKeyForEdit(rowIndex, 0)
                    onShowCatalogPicker()
                },
                onAddNewRow = { viewModel.addNewRow() },
                onRemoveRow = { rowIndex -> viewModel.removeRow(rowIndex) },
                onStyleChanged = { style -> viewModel.setStyle(style) },
                onOpenPresets = onShowPresetsDialog,
                onOpenCatalog = { onSelectTab(2) },
                onOpenBashWizard = onShowBashWizard,
                onMoveKey = { fromRow, fromCol, toRow, insertIndex ->
                    viewModel.moveKey(fromRow, fromCol, toRow, insertIndex)
                }
            )
        }

        2 -> {
            CatalogScreenView(
                items = uiState.filteredCatalog,
                searchQuery = uiState.catalogSearch,
                selectedCategory = uiState.selectedCatalogCategory,
                totalRows = uiState.rows.size,
                onSearchChanged = { viewModel.onCatalogSearchChanged(it) },
                onCategorySelected = { viewModel.onCatalogCategorySelected(it) },
                onAddKeyToRow = { keyItem, targetRow ->
                    viewModel.addKeyToRow(targetRow, keyItem)
                },
                onTestKeyInTerminal = { keyString ->
                    viewModel.onExtraKeyTapped(KeyItem(key = keyString, display = keyString))
                    onSelectTab(0)
                },
                onOpenBashWizard = onShowBashWizard
            )
        }

        3 -> {
            ShortcutCreatorSuiteView(
                onAddKeyToLayout = { keyItem, targetRow ->
                    viewModel.addKeyToRow(targetRow, keyItem)
                },
                onTestInTerminal = { command ->
                    if (command.endsWith("\n")) {
                        viewModel.executeCommand(command.trimEnd('\n', '\r'))
                    } else {
                        viewModel.onExtraKeyTapped(KeyItem(key = command, display = command))
                    }
                    onSelectTab(0)
                },
                totalRows = uiState.rows.size,
                onOpenBashWizardDialog = onShowBashWizard,
                onRegisterCustomFunction = { name, body ->
                    viewModel.registerCustomFunction(name, body)
                }
            )
        }

        4 -> {
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
