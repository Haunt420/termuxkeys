package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.KeyItem

@Composable
fun LiveExtraKeysBar(
    rows: List<List<KeyItem>>,
    onKeyTapped: (KeyItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF000000))
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (row in rows) {
            val shouldFit = row.size in 1..8
            if (shouldFit) {
                // Distributed evenly across screen width (Pixel clone of Termux)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (item in row) {
                        KeyCapButton(
                            item = item,
                            onClick = { onKeyTapped(item, false) },
                            onPopupTriggered = { onKeyTapped(item, true) },
                            isCompact = isCompact,
                            isTermuxStyle = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Scrollable for very wide rows
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    for (item in row) {
                        KeyCapButton(
                            item = item,
                            onClick = { onKeyTapped(item, false) },
                            onPopupTriggered = { onKeyTapped(item, true) },
                            isCompact = isCompact,
                            isTermuxStyle = true
                        )
                    }
                }
            }
        }
    }
}
