package com.example

import com.example.data.BuiltinCatalogData
import com.example.data.BuiltinPresetsLibrary
import com.example.data.BuiltinReferenceData
import com.example.engine.PropertiesFormatterEngine
import com.example.model.CatalogCategory
import com.example.model.KeyItem
import com.example.model.KeyPopup
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPropertiesFormatterEngine_formatsCorrectly() {
        val rows = listOf(
            listOf(
                KeyItem(key = "ESC", display = "ESC"),
                KeyItem(key = "TAB", display = "TAB"),
                KeyItem(key = "CTRL", display = "^O", popup = KeyPopup(key = "WriteOut", display = "Save"))
            ),
            listOf(
                KeyItem(key = "|", display = "|", popup = KeyPopup(key = " | grep ", display = "grep")),
                KeyItem(key = "ENTER", display = "↵")
            )
        )

        val properties = PropertiesFormatterEngine.formatTermuxProperties(rows, "default")
        assertTrue(properties.contains("extra-keys-style = default"))
        assertTrue(properties.contains("extra-keys = ["))
        assertTrue(properties.contains("'ESC'"))
        assertTrue(properties.contains("'TAB'"))
        assertTrue(properties.contains("{key: 'CTRL', display: '^O', popup: {macro: 'WriteOut', display: 'Save'}}"))
        assertTrue(properties.contains("{key: '|', popup: {macro: ' | grep ', display: 'grep'}}"))
        assertTrue(properties.contains("{key: 'ENTER', display: '↵'}"))
    }

    @Test
    fun testPropertiesFormatterEngine_generatesRunnableCommand() {
        val rows = listOf(
            listOf(
                KeyItem(key = "ESC"),
                KeyItem(key = "CTRL")
            )
        )
        val cmd = PropertiesFormatterEngine.generateRunnableBashCommand(rows)
        assertTrue(cmd.contains("mkdir -p ~/.termux"))
        assertTrue(cmd.contains("cat << 'EOF' > ~/.termux/termux.properties"))
        assertTrue(cmd.contains("termux-reload-settings"))
    }

    @Test
    fun testBuiltinPresets_allValid() {
        val presets = BuiltinPresetsLibrary.presets
        assertTrue("Presets must not be empty", presets.isNotEmpty())
        for (preset in presets) {
            assertTrue("Preset name must not be blank", preset.name.isNotBlank())
            assertTrue("Preset rows must not be empty", preset.rows.isNotEmpty())
            for (row in preset.rows) {
                assertTrue("Row must not be empty", row.isNotEmpty())
                for (key in row) {
                    assertTrue("Key identifier must not be blank", key.key.isNotBlank())
                }
            }
        }
    }

    @Test
    fun testBuiltinCatalog_coversAllCategories() {
        val catalog = BuiltinCatalogData.items
        assertTrue("Catalog must have extensive items", catalog.size >= 40)

        val categoriesInCatalog = catalog.map { it.category }.toSet()
        assertTrue(categoriesInCatalog.contains(CatalogCategory.MODIFIERS))
        assertTrue(categoriesInCatalog.contains(CatalogCategory.NAVIGATION))
        assertTrue(categoriesInCatalog.contains(CatalogCategory.FUNCTION_KEYS))
        assertTrue(categoriesInCatalog.contains(CatalogCategory.TERMUX_ACTIONS))
        assertTrue(categoriesInCatalog.contains(CatalogCategory.CONTROL_SIGNALS))
        assertTrue(categoriesInCatalog.contains(CatalogCategory.NANO_COMMANDS))
        assertTrue(categoriesInCatalog.contains(CatalogCategory.BASH_READLINE))
        assertTrue(categoriesInCatalog.contains(CatalogCategory.PIPES_OPERATORS))
    }

    @Test
    fun testBuiltinReference_hasCompleteGuides() {
        val topics = BuiltinReferenceData.topics
        assertTrue("Reference topics must have at least 5 topics", topics.size >= 5)
        for (topic in topics) {
            assertTrue("Topic title must be set", topic.title.isNotBlank())
            assertTrue("Topic sections must not be empty", topic.sections.isNotEmpty())
        }
    }
}
