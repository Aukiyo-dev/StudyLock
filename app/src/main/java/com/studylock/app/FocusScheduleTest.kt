package com.studylock.app

import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class FocusScheduleTest {

    @Test
    fun normalSchedule_insideRange_returnsTrue() {
        assertTrue(
            FocusSchedule.isWithinSchedule(
                currentMinutes = 15 * 60,
                startMinutes = 14 * 60,
                endMinutes = 16 * 60
            )
        )
    }

    @Test
    fun normalSchedule_outsideRange_returnsFalse() {
        assertFalse(
            FocusSchedule.isWithinSchedule(
                currentMinutes = 13 * 60,
                startMinutes = 14 * 60,
                endMinutes = 16 * 60
            )
        )
    }

    @Test
    fun normalSchedule_atEnd_returnsFalse() {
        assertFalse(
            FocusSchedule.isWithinSchedule(
                currentMinutes = 16 * 60,
                startMinutes = 14 * 60,
                endMinutes = 16 * 60
            )
        )
    }

    @Test
    fun overnightSchedule_afterStart_returnsTrue() {
        assertTrue(
            FocusSchedule.isWithinSchedule(
                currentMinutes = 23 * 60,
                startMinutes = 22 * 60,
                endMinutes = 6 * 60
            )
        )
    }

    @Test
    fun overnightSchedule_beforeEnd_returnsTrue() {
        assertTrue(
            FocusSchedule.isWithinSchedule(
                currentMinutes = 3 * 60,
                startMinutes = 22 * 60,
                endMinutes = 6 * 60
            )
        )
    }

    @Test
    fun overnightSchedule_outsideRange_returnsFalse() {
        assertFalse(
            FocusSchedule.isWithinSchedule(
                currentMinutes = 12 * 60,
                startMinutes = 22 * 60,
                endMinutes = 6 * 60
            )
        )
    }

    @Test
    fun sameStartAndEnd_returnsFalse() {
        assertFalse(
            FocusSchedule.isWithinSchedule(
                currentMinutes = 14 * 60,
                startMinutes = 14 * 60,
                endMinutes = 14 * 60
            )
        )
    }
}
