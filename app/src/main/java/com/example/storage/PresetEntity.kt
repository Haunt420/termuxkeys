package com.example.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.model.KeyPreset
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "presets")
data class PresetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val rowsJson: String,
    val isCustom: Boolean = true
) {
    fun toPreset(): KeyPreset {
        val rows = mutableListOf<List<KeyItem>>()
        try {
            val rootArray = JSONArray(rowsJson)
            for (i in 0 until rootArray.length()) {
                val rowArray = rootArray.getJSONArray(i)
                val rowItems = mutableListOf<KeyItem>()
                for (j in 0 until rowArray.length()) {
                    val obj = rowArray.getJSONObject(j)
                    val id = obj.optString("id")
                    val key = obj.getString("key")
                    val display = obj.optString("display", "")
                    val popup = if (obj.has("popup")) {
                        val pObj = obj.getJSONObject("popup")
                        KeyPopup(
                            key = pObj.getString("key"),
                            display = pObj.optString("display", "")
                        )
                    } else null

                    rowItems.add(
                        KeyItem(
                            id = if (id.isNotBlank()) id else java.util.UUID.randomUUID().toString(),
                            key = key,
                            display = display,
                            popup = popup
                        )
                    )
                }
                rows.add(rowItems)
            }
        } catch (_: Exception) {
            // Fallback empty if parsing failed
        }
        return KeyPreset(
            id = id,
            name = name,
            category = category,
            description = description,
            rows = rows,
            isCustom = isCustom
        )
    }

    companion object {
        fun fromPreset(preset: KeyPreset): PresetEntity {
            val rootArray = JSONArray()
            for (row in preset.rows) {
                val rowArray = JSONArray()
                for (item in row) {
                    val obj = JSONObject()
                    obj.put("id", item.id)
                    obj.put("key", item.key)
                    obj.put("display", item.display)
                    if (item.popup != null) {
                        val pObj = JSONObject()
                        pObj.put("key", item.popup.key)
                        pObj.put("display", item.popup.display)
                        obj.put("popup", pObj)
                    }
                    rowArray.put(obj)
                }
                rootArray.put(rowArray)
            }
            return PresetEntity(
                id = preset.id,
                name = preset.name,
                category = preset.category,
                description = preset.description,
                rowsJson = rootArray.toString(),
                isCustom = preset.isCustom
            )
        }
    }
}
