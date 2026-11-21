package com.studylock.app

import android.accessibilityservice.AccessibilityService
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar

class FocusBarrierService : AccessibilityService() {

    private var barrier: android.view.View? = null

    private val prefs by lazy {
        getSharedPreferences("studylock", MODE_PRIVATE)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return

        // Do not block StudyLock itself
        if (pkg == packageName) {
            removeBarrier()
            return
        }

        // Always reevaluate the schedule and selected apps
        updateBarrier(pkg)
    }

    override fun onInterrupt() {
        removeBarrier()
    }

    private fun inScheduleNow(): Boolean {
    val now = Calendar.getInstance()

    val current =
        now.get(Calendar.HOUR_OF_DAY) * 60 +
        now.get(Calendar.MINUTE)

    val start =
        prefs.getInt("startHour", 14) * 60 +
        prefs.getInt("startMinute", 0)

    val end =
        prefs.getInt("endHour", 16) * 60 +
        prefs.getInt("endMinute", 0)

    return FocusSchedule.isWithinSchedule(
        currentMinutes = current,
        startMinutes = start,
        endMinutes = end
    )
    }

    private fun updateBarrier(pkg: String) {
        val blocked =
            prefs.getStringSet("blocked", emptySet()) ?: emptySet()

        if (inScheduleNow() && blocked.contains(pkg)) {
            showBarrier()
        } else {
            removeBarrier()
        }
    }

    private fun showBarrier() {
        if (barrier != null) return

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(36, 48, 36, 48)
            setBackgroundColor(Color.rgb(17, 17, 25))
        }

        content.addView(
            TextView(this).apply {
                text = "FOCUS SESSION"
                textSize = 14f
                setTextColor(Color.rgb(180, 150, 255))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 18)
            }
        )

        content.addView(
            TextView(this).apply {
                text = "You're scheduled to study right now."
                textSize = 26f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 12)
            }
        )

        content.addView(
            TextView(this).apply {
                text = "Put this distraction away. Your focus time is limited, and your future self will thank you."
                textSize = 17f
                setTextColor(Color.LTGRAY)
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 26)
            }
        )

        content.addView(
            Button(this).apply {
                text = "Return to home screen"
                setOnClickListener {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
            }
        )

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        try {
            wm.addView(content, params)
            barrier = content
        } catch (e: Exception) {
            barrier = null
        }
    }

    private fun removeBarrier() {
        val view = barrier ?: return

        try {
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.removeView(view)
        } catch (_: Exception) {
        }

        barrier = null
    }

    override fun onDestroy() {
        removeBarrier()
        super.onDestroy()
    }
}
