package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.model.KeyItem
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MatrixGreenBright
import kotlin.math.abs
import kotlin.math.roundToInt

data class DragDropTarget(
    val rowIndex: Int,
    val insertIndex: Int
)

class ExtraKeysDragController {
    var rootCoordinates by mutableStateOf<LayoutCoordinates?>(null)
    val rowBounds = mutableStateMapOf<Int, Rect>()
    val keyBoundsById = mutableStateMapOf<String, Rect>()

    var draggedItem by mutableStateOf<KeyItem?>(null)
        private set
    var sourceRow by mutableStateOf(-1)
        private set
    var sourceCol by mutableStateOf(-1)
        private set
    var pointerInRoot by mutableStateOf(Offset.Zero)
        private set
    var touchAnchor by mutableStateOf(Offset.Zero)
        private set
    var draggedWidthPx by mutableStateOf(0f)
        private set
    var dropTarget by mutableStateOf<DragDropTarget?>(null)
        private set

    val isDragging: Boolean
        get() = draggedItem != null

    fun isSourceItem(item: KeyItem): Boolean = draggedItem?.id == item.id

    fun isNoOpDrop(): Boolean {
        val target = dropTarget ?: return true
        return target.rowIndex == sourceRow &&
            (target.insertIndex == sourceCol || target.insertIndex == sourceCol + 1)
    }

    fun startDrag(
        item: KeyItem,
        rowIndex: Int,
        colIndex: Int,
        slotCoordinates: LayoutCoordinates?,
        localTouchOffset: Offset,
        rows: List<List<KeyItem>>,
        haptic: HapticFeedback?
    ) {
        val root = rootCoordinates
        if (root == null || !root.isAttached || slotCoordinates == null || !slotCoordinates.isAttached) return
        val slotBox = root.localBoundingBoxOf(slotCoordinates, clipBounds = false)
        val startPointer = root.localPositionOf(slotCoordinates, localTouchOffset)

        draggedItem = item
        sourceRow = rowIndex
        sourceCol = colIndex
        pointerInRoot = startPointer
        touchAnchor = startPointer - slotBox.topLeft
        draggedWidthPx = slotBox.width
        dropTarget = computeDropTarget(startPointer, rows)
        haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun updateDrag(
        dragAmount: Offset,
        rows: List<List<KeyItem>>,
        haptic: HapticFeedback?
    ) {
        if (!isDragging) return
        pointerInRoot += dragAmount
        val next = computeDropTarget(pointerInRoot, rows)
        if (next != dropTarget) {
            dropTarget = next
            haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun endDrag(onMoveKey: (fromRow: Int, fromCol: Int, toRow: Int, insertIndex: Int) -> Unit) {
        val target = dropTarget
        val fromR = sourceRow
        val fromC = sourceCol
        val active = isDragging
        cancelDrag()
        if (active && target != null) {
            onMoveKey(fromR, fromC, target.rowIndex, target.insertIndex)
        }
    }

    fun cancelDrag() {
        draggedItem = null
        sourceRow = -1
        sourceCol = -1
        pointerInRoot = Offset.Zero
        touchAnchor = Offset.Zero
        draggedWidthPx = 0f
        dropTarget = null
    }

    fun updateRowBounds(rowIndex: Int, coords: LayoutCoordinates) {
        val root = rootCoordinates
        if (root != null && root.isAttached && coords.isAttached) {
            rowBounds[rowIndex] = root.localBoundingBoxOf(coords, clipBounds = false)
        }
    }

    fun updateKeyBounds(itemId: String, coords: LayoutCoordinates) {
        val root = rootCoordinates
        if (root != null && root.isAttached && coords.isAttached) {
            keyBoundsById[itemId] = root.localBoundingBoxOf(coords, clipBounds = false)
        }
    }

    private fun computeDropTarget(point: Offset, rows: List<List<KeyItem>>): DragDropTarget? {
        if (rows.isEmpty()) return null

        val validRowIndices = rows.indices.filter { rowBounds[it] != null }
        if (validRowIndices.isEmpty()) return null

        // 1. Prefer row whose vertical band (with small forgiveness margin) contains pointer Y
        val targetRow = validRowIndices.firstOrNull { r ->
            val rect = rowBounds[r]!!
            point.y >= rect.top - 12f && point.y <= rect.bottom + 12f
        } ?: validRowIndices.minByOrNull { r ->
            val rect = rowBounds[r]!!
            abs(point.y - rect.center.y)
        } ?: return null

        // 2. Determine horizontal insertion index inside targetRow
        val rowItems = rows[targetRow]
        if (rowItems.isEmpty()) {
            return DragDropTarget(targetRow, 0)
        }

        var insertIndex = rowItems.size
        for (c in rowItems.indices) {
            val rect = keyBoundsById[rowItems[c].id] ?: continue
            if (point.x < rect.center.x) {
                insertIndex = c
                break
            }
        }
        return DragDropTarget(targetRow, insertIndex)
    }
}

@Composable
fun rememberExtraKeysDragController(): ExtraKeysDragController = remember { ExtraKeysDragController() }

/**
 * Modifier to make any keycap slot track its root bounds and respond to drag-and-drop gestures.
 */
@Composable
fun Modifier.draggableKeySlot(
    controller: ExtraKeysDragController,
    item: KeyItem,
    rowIndex: Int,
    colIndex: Int,
    rows: List<List<KeyItem>>,
    immediateDrag: Boolean,
    haptic: HapticFeedback,
    onMoveKey: (fromRow: Int, fromCol: Int, toRow: Int, insertIndex: Int) -> Unit
): Modifier {
    var slotCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    return this
        .onGloballyPositioned { coords ->
            slotCoords = coords
            controller.updateKeyBounds(item.id, coords)
        }
        .pointerInput(item.id, rowIndex, colIndex, rows.size, immediateDrag) {
            if (immediateDrag) {
                detectDragGestures(
                    onDragStart = { offset ->
                        controller.startDrag(item, rowIndex, colIndex, slotCoords, offset, rows, haptic)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        controller.updateDrag(dragAmount, rows, haptic)
                    },
                    onDragEnd = {
                        controller.endDrag(onMoveKey)
                    },
                    onDragCancel = {
                        controller.cancelDrag()
                    }
                )
            } else {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        controller.startDrag(item, rowIndex, colIndex, slotCoords, offset, rows, haptic)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        controller.updateDrag(dragAmount, rows, haptic)
                    },
                    onDragEnd = {
                        controller.endDrag(onMoveKey)
                    },
                    onDragCancel = {
                        controller.cancelDrag()
                    }
                )
            }
        }
}

/**
 * Overlay rendered inside the root drag container: draws the glowing insertion beam at the
 * exact drop slot and floats the dragged keycap smoothly under the user's finger.
 */
@Composable
fun ExtraKeysDragOverlay(
    controller: ExtraKeysDragController,
    rows: List<List<KeyItem>>,
    isCompact: Boolean = false,
    isTermuxStyle: Boolean = true
) {
    val dragged = controller.draggedItem ?: return
    val target = controller.dropTarget
    val density = LocalDensity.current
    val beamWidthPx = with(density) { 3.dp.toPx() }
    val ghostWidthDp: Dp = with(density) {
        controller.draggedWidthPx.coerceAtLeast(with(density) { 46.dp.toPx() }).toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(50f)
    ) {
        // Draw glowing insertion indicator at the computed target slot
        if (target != null && !controller.isNoOpDrop()) {
            val rowRect = controller.rowBounds[target.rowIndex]
            val targetRowItems = rows.getOrNull(target.rowIndex).orEmpty()

            Canvas(modifier = Modifier.fillMaxSize()) {
                val (beamX, beamTop, beamBottom) = if (targetRowItems.isEmpty() && rowRect != null) {
                    Triple(rowRect.left + 12f, rowRect.top + 4f, rowRect.bottom - 4f)
                } else {
                    val firstRect = targetRowItems.firstOrNull()?.let { controller.keyBoundsById[it.id] }
                    val lastRect = targetRowItems.lastOrNull()?.let { controller.keyBoundsById[it.id] }
                    val top = firstRect?.top ?: rowRect?.top ?: 0f
                    val bottom = firstRect?.bottom ?: rowRect?.bottom ?: 0f

                    val x = when {
                        target.insertIndex <= 0 && firstRect != null -> firstRect.left - beamWidthPx
                        target.insertIndex >= targetRowItems.size && lastRect != null -> lastRect.right + beamWidthPx / 2f
                        else -> {
                            val leftItemRect = targetRowItems.getOrNull(target.insertIndex - 1)?.let { controller.keyBoundsById[it.id] }
                            val rightItemRect = targetRowItems.getOrNull(target.insertIndex)?.let { controller.keyBoundsById[it.id] }
                            when {
                                leftItemRect != null && rightItemRect != null -> (leftItemRect.right + rightItemRect.left) / 2f
                                rightItemRect != null -> rightItemRect.left
                                leftItemRect != null -> leftItemRect.right
                                else -> rowRect?.left ?: 0f
                            }
                        }
                    }
                    Triple(x, top, bottom)
                }

                if (beamBottom > beamTop) {
                    // Outer subtle glow
                    drawRoundRect(
                        color = CyberCyan.copy(alpha = 0.35f),
                        topLeft = Offset(beamX - beamWidthPx * 1.2f, beamTop - 2f),
                        size = Size(beamWidthPx * 2.4f, (beamBottom - beamTop) + 4f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    // Sharp core beam
                    drawRoundRect(
                        color = MatrixGreenBright,
                        topLeft = Offset(beamX - beamWidthPx / 2f, beamTop),
                        size = Size(beamWidthPx, beamBottom - beamTop),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
            }
        }

        // Floating dragged keycap ghost
        val ghostPos = controller.pointerInRoot - controller.touchAnchor
        Box(
            modifier = Modifier
                .offset { IntOffset(ghostPos.x.roundToInt(), ghostPos.y.roundToInt()) }
                .width(ghostWidthDp)
                .graphicsLayer {
                    scaleX = 1.1f
                    scaleY = 1.1f
                    alpha = 0.94f
                }
                .shadow(10.dp, RoundedCornerShape(6.dp))
                .background(Color.Transparent, RoundedCornerShape(6.dp))
                .border(1.5.dp, MatrixGreenBright, RoundedCornerShape(6.dp))
        ) {
            KeyCapButton(
                item = dragged,
                onClick = {},
                isSelected = true,
                isEditMode = true,
                isCompact = isCompact,
                isTermuxStyle = isTermuxStyle,
                modifier = Modifier.width(ghostWidthDp)
            )
        }
    }
}
