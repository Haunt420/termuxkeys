package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.model.KeyPreset
import com.example.storage.PresetEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TermuxKeys Studio", appName)
    }

    @Test
    fun `test preset entity serialization with android json`() {
        val preset = KeyPreset(
            id = "test_1",
            name = "Test Setup",
            category = "Custom",
            description = "Custom test description",
            rows = listOf(
                listOf(
                    KeyItem(id = "k1", key = "ESC", display = "⎋"),
                    KeyItem(id = "k2", key = "CTRL", display = "^X", popup = KeyPopup(key = "Exit", display = "Quit"))
                )
            ),
            isCustom = true
        )

        val entity = PresetEntity.fromPreset(preset)
        assertEquals("test_1", entity.id)
        assertEquals("Test Setup", entity.name)

        val restored = entity.toPreset()
        assertEquals(preset.name, restored.name)
        assertEquals(preset.rows.size, restored.rows.size)
        assertEquals(2, restored.rows[0].size)
        assertEquals("ESC", restored.rows[0][0].key)
        assertEquals("⎋", restored.rows[0][0].display)
        assertEquals("^X", restored.rows[0][1].display)
        assertEquals("Exit", restored.rows[0][1].popup?.key)
        assertEquals("Quit", restored.rows[0][1].popup?.display)
    }
}
