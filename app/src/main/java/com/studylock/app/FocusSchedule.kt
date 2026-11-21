package com.studylock.app

object FocusSchedule {

    fun isWithinSchedule(
        currentMinutes: Int,
        startMinutes: Int,
        endMinutes: Int
    ): Boolean {

        if (startMinutes == endMinutes) return false

        return if (startMinutes < endMinutes) {
            currentMinutes >= startMinutes &&
                currentMinutes < endMinutes
        } else {
            currentMinutes >= startMinutes ||
                currentMinutes < endMinutes
        }
    }
}
