package com.example

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import com.example.model.KeyItem
import com.example.ui.components.BashFunctionWizardDialog
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
        // Step 1 Next button is pinned and immediately displayed without scrolling
        composeRule.onNodeWithTag("wizard_dialog_next_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        // Step 2: verify syntax button and advance to Step 3
        composeRule.onNodeWithTag("wizard_verify_syntax_btn").performScrollTo().assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("wizard_dialog_next_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        // Step 3: keycap label input is shown and advances to Step 4
        composeRule.onNodeWithTag("wizard_key_label_input").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("wizard_dialog_next_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        // Step 4: Save to Row button deploys the shortcut
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

        // Remains on Step 1 and displays inline validation feedback
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
        // Sticky Next button is visible on Step 1 without needing to scroll past cards
        composeRule.onNodeWithTag("wizard_next_step_btn").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        // Should now be on Step 2 where syntax verification button is available
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
}
