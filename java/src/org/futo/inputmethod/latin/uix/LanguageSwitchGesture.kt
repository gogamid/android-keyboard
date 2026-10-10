package org.futo.inputmethod.latin.uix

import android.view.MotionEvent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

/** The original keyboard window owns the touch until the long-press finger is released. */
class LanguageSwitchGesture {
    val pointer = mutableStateOf<Offset?>(null)
    val highlightedSubtype = mutableStateOf<String?>(null)
    private val rows = mutableMapOf<String, Pair<Rect, () -> Unit>>()

    fun registerRow(subtype: String, bounds: Rect, select: () -> Unit) {
        rows[subtype] = bounds to select
    }

    fun removeRow(subtype: String) {
        rows.remove(subtype)
    }

    fun handle(action: Int, x: Int, y: Int): Boolean {
        if (action == MotionEvent.ACTION_CANCEL) {
            pointer.value = null
            highlightedSubtype.value = null
            return false
        }
        val position = Offset(x.toFloat(), y.toFloat())
        pointer.value = position
        val row = rows.entries.firstOrNull { it.value.first.contains(position) }
        highlightedSubtype.value = row?.key
        if (action == MotionEvent.ACTION_UP) {
            pointer.value = null
            row?.value?.second?.invoke()
            return row != null
        }
        return false
    }
}
