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
import androidx.compose.ui.unit.dp
import com.example.model.KeyItem
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalSurface

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
            .background(TerminalSurface)
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (row in rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (item in row) {
                    KeyCapButton(
                        item = item,
                        onClick = { onKeyTapped(item, false) },
                        onPopupTriggered = { onKeyTapped(item, true) },
                        isCompact = isCompact
                    )
                }
            }
        }
    }
}
