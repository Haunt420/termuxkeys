package com.example

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.core.app.ApplicationProvider
import com.example.model.KeyItem
import com.example.ui.components.BashFunctionWizardDialog
import com.example.ui.components.KeypadEditorView
import com.example.ui.components.StepwiseShortcutWizard
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TermuxKeysViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WizardAndViewModelTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `bash function wizard advances past step 1 through step 4 and saves`() {
        var savedItem: KeyItem? = null
        var savedRow = -1
        var savedFnName = ""

        composeRule.setContent {
            MyApplicationTheme {
                BashFunctionWizardDialog(
                    totalRows = 2,
                    onSaveShortcut = { item, row, fnName, _ ->
                        savedItem = item
                        savedRow = row
                        savedFnName = fnName
                    },
                    onTestInTerminal = { _, _, _ -> },
                    onDismiss = {}
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("wizard_dialog_next_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("wizard_verify_syntax_btn").performScrollTo().assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("wizard_dialog_next_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("wizard_key_label_input").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("wizard_dialog_next_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("wizard_save_to_layout_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        assertEquals(0, savedRow)
        assertEquals("gpush", savedFnName)
        assertEquals("G-PUSH", savedItem?.display)
    }

    @Test
    fun `bash function wizard blocks invalid identifier on step 1 with clear error`() {
        composeRule.setContent {
            MyApplicationTheme {
                BashFunctionWizardDialog(
                    totalRows = 2,
                    onSaveShortcut = { _, _, _, _ -> },
                    onTestInTerminal = { _, _, _ -> },
                    onDismiss = {}
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("wizard_fn_name_input").performTextReplacement("bad name!")
        composeRule.onNodeWithTag("wizard_dialog_next_btn").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Step 1 of 4", substring = true).assertIsDisplayed()
    }

    @Test
    fun `stepwise shortcut wizard sticky next button advances past step 1`() {
        val app = ApplicationProvider.getApplicationContext<Application>()

        composeRule.setContent {
            MyApplicationTheme {
                StepwiseShortcutWizard(
                    onAddKey = { _, _ -> },
                    onTestInTerminal = {},
                    totalRows = 2,
                    context = app
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("wizard_next_step_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("verify_command_logic_btn").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `viewModel executes registered custom function and newline macro keys`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = TermuxKeysViewModel(app)

        vm.registerCustomFunction(
            name = "greet",
            body = "echo \"Hello \$1\""
        )
        vm.onExtraKeyTapped(KeyItem(key = "greet Termux\n", display = "GREET"))

        val historyText = vm.uiState.value.terminalHistory.joinToString("\n") { it.text }
        assertTrue(historyText.contains("Running custom function 'greet'"))
        assertTrue(historyText.contains("Hello Termux"))
    }

    @Test
    fun `viewModel moveKey reorders within row and across rows accurately`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = TermuxKeysViewModel(app)

        val row0Before = vm.uiState.value.rows[0]
        val row1Before = vm.uiState.value.rows[1]
        val firstKey = row0Before[0]
        val secondKey = row0Before[1]
        val thirdKey = row0Before[2]

        // Move first key to after third key (insertIndex = 3 in Row 0)
        vm.moveKey(fromRow = 0, fromCol = 0, toRow = 0, insertIndex = 3)
        val row0AfterIntra = vm.uiState.value.rows[0]
        assertEquals(secondKey.id, row0AfterIntra[0].id)
        assertEquals(thirdKey.id, row0AfterIntra[1].id)
        assertEquals(firstKey.id, row0AfterIntra[2].id)

        // Move it across to Row 1 at index 1
        vm.moveKey(fromRow = 0, fromCol = 2, toRow = 1, insertIndex = 1)
        val row0AfterCross = vm.uiState.value.rows[0]
        val row1AfterCross = vm.uiState.value.rows[1]
        assertEquals(row0Before.size - 1, row0AfterCross.size)
        assertEquals(row1Before.size + 1, row1AfterCross.size)
        assertEquals(firstKey.id, row1AfterCross[1].id)
    }

    @Test
    fun `keypadEditor arrange mode supports drag and drop reordering`() {
        val row0 = listOf(
            KeyItem(id = "k1", key = "ESC", display = "ESC"),
            KeyItem(id = "k2", key = "TAB", display = "TAB"),
            KeyItem(id = "k3", key = "CTRL", display = "CTRL")
        )
        var movedFromCol = -1
        var movedToCol = -1

        composeRule.setContent {
            MyApplicationTheme {
                KeypadEditorView(
                    rows = listOf(row0),
                    style = "default",
                    selectedRow = null,
                    selectedCol = null,
                    onSelectKey = { _, _ -> },
                    onAddKeyToRow = {},
                    onAddNewRow = {},
                    onRemoveRow = {},
                    onStyleChanged = {},
                    onOpenPresets = {},
                    onOpenCatalog = {},
                    onMoveKey = { _, fromCol, _, insertIndex ->
                        movedFromCol = fromCol
                        movedToCol = insertIndex
                    }
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("toggle_arrange_mode_btn").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("keycap_ESC").performTouchInput {
            swipeRight(startX = center.x, endX = center.x + 220f, durationMillis = 300)
        }
        composeRule.waitForIdle()

        assertEquals(0, movedFromCol)
        assertTrue(movedToCol >= 2)
    }
}
