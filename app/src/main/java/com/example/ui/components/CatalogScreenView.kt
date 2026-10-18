package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.CatalogCategory
import com.example.model.CatalogItem
import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TerminalKeyBg
import com.example.ui.theme.TerminalKeyBorder
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CatalogScreenView(
    items: List<CatalogItem>,
    searchQuery: String,
    selectedCategory: CatalogCategory,
    totalRows: Int,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (CatalogCategory) -> Unit,
    onAddKeyToRow: (KeyItem, targetRow: Int) -> Unit,
    onTestKeyInTerminal: (String) -> Unit,
    onOpenBashWizard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var targetRowIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Search 60+ keys, macros, signals (e.g. nano, pipe, sigint)...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MatrixGreenBright)
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MatrixGreenBright,
                unfocusedBorderColor = TerminalBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth().testTag("catalog_screen_search")
        )

        // Custom Bash Function Wizard Quick-Action Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCyan.copy(alpha = 0.12f))
                .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .clickable { onOpenBashWizard() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("catalog_open_bash_wizard_banner")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Build Custom Bash Function",
                            color = MatrixGreenBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Stepwise wizard with syntax verification & direct row install",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Open Wizard",
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CatalogCategory.entries.forEach { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.displayName, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MatrixGreen.copy(alpha = 0.3f),
                        selectedLabelColor = MatrixGreenBright
                    )
                )
            }
        }

        // Target Row Selection Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TerminalSurface)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Target Row:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (r in 0 until totalRows.coerceAtLeast(1)) {
                    FilterChip(
                        selected = targetRowIndex == r,
                        onClick = { targetRowIndex = r },
                        label = { Text("Row ${r + 1}", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                            selectedLabelColor = CyberCyan
                        )
                    )
                }
            }

            Text(
                text = "${items.size} keys",
                color = MatrixGreenBright,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Items List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { item ->
                CatalogRowItemCard(
                    item = item,
                    targetRowIndex = targetRowIndex,
                    onAdd = {
                        val popup = if (!item.suggestedPopup.isNullOrBlank()) {
                            KeyPopup(key = item.suggestedPopup, display = item.suggestedPopup)
                        } else null
                        val keyItem = KeyItem(
                            key = item.keyString,
                            display = item.defaultDisplay,
                            popup = popup
                        )
                        onAddKeyToRow(keyItem, targetRowIndex)
                    },
                    onTest = {
                        onTestKeyInTerminal(item.keyString)
                    }
                )
            }
        }
    }
}

@Composable
fun CatalogRowItemCard(
    item: CatalogItem,
    targetRowIndex: Int,
    onAdd: () -> Unit,
    onTest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TerminalSurfaceVariant)
            .border(1.dp, TerminalBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TerminalKeyBg)
                            .border(1.dp, TerminalKeyBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.defaultDisplay.ifBlank { item.keyString },
                            color = MatrixGreenBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = item.name,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = item.category.name,
                    color = CyberCyan,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = item.description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onTest,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Test", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenBright,
                        contentColor = TerminalBg
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Row ${targetRowIndex + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
