package org.futo.inputmethod.latin.uix

import android.view.MotionEvent
import androidx.compose.ui.geometry.Rect
import org.junit.Assert.*
import org.junit.Test

class LanguageSwitchGestureTest {
    @Test
    fun slideHighlightsAndReleaseSelectsOnlyFinalRow() {
        val gesture = LanguageSwitchGesture()
        val selected = mutableListOf<String>()
        gesture.registerRow("en", Rect(0f, 0f, 100f, 50f)) { selected.add("en") }
        gesture.registerRow("fr", Rect(0f, 50f, 100f, 100f)) { selected.add("fr") }
        gesture.handle(MotionEvent.ACTION_MOVE, 25, 25)
        assertEquals("en", gesture.highlightedSubtype.value)
        gesture.handle(MotionEvent.ACTION_MOVE, 25, 75)
        assertEquals("fr", gesture.highlightedSubtype.value)
        assertTrue(selected.isEmpty())
        assertTrue(gesture.handle(MotionEvent.ACTION_UP, 25, 75))
        assertEquals(listOf("fr"), selected)
        assertNull(gesture.pointer.value)
    }

    @Test
    fun releaseOutsideRowsDoesNotSelect() {
        val gesture = LanguageSwitchGesture()
        gesture.registerRow("en", Rect(0f, 0f, 100f, 50f)) { fail("Unexpected selection") }
        gesture.handle(MotionEvent.ACTION_MOVE, 25, 25)
        assertFalse(gesture.handle(MotionEvent.ACTION_UP, 25, 150))
        assertNull(gesture.highlightedSubtype.value)
    }

    @Test
    fun cancelNeverSelects() {
        val gesture = LanguageSwitchGesture()
        gesture.registerRow("en", Rect(0f, 0f, 100f, 50f)) { fail("Unexpected selection") }
        gesture.handle(MotionEvent.ACTION_MOVE, 25, 25)
        assertFalse(gesture.handle(MotionEvent.ACTION_CANCEL, 25, 25))
        assertNull(gesture.pointer.value)
        assertNull(gesture.highlightedSubtype.value)
    }

    @Test
    fun disposedRowsCannotBeSelected() {
        val gesture = LanguageSwitchGesture()
        gesture.registerRow("en", Rect(0f, 0f, 100f, 50f)) { fail("Unexpected selection") }
        gesture.removeRow("en")
        assertFalse(gesture.handle(MotionEvent.ACTION_UP, 25, 25))
    }
}
