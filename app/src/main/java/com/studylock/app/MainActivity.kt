package com.studylock.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TimePicker

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("studylock", MODE_PRIVATE)
    }

    private lateinit var startPicker: TimePicker
    private lateinit var endPicker: TimePicker

    private val selected = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selected.addAll(
            prefs.getStringSet("blocked", emptySet()) ?: emptySet()
        )

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
        }

        // Title
        root.addView(
            TextView(this).apply {
                text = "StudyLock"
                textSize = 30f
            }
        )

        // Description
        root.addView(
            TextView(this).apply {
                text = "Set a daily focus window and choose distracting apps."
                textSize = 16f
                setPadding(0, 10, 0, 20)
            }
        )

        // Focus start
        root.addView(
            TextView(this).apply {
                text = "Focus starts"
                textSize = 18f
            }
        )

        startPicker = TimePicker(this).apply {
            setIs24HourView(true)
            hour = prefs.getInt("startHour", 14)
            minute = prefs.getInt("startMinute", 0)
        }

        root.addView(startPicker)

        // Focus end
        root.addView(
            TextView(this).apply {
                text = "Focus ends"
                textSize = 18f
            }
        )

        endPicker = TimePicker(this).apply {
            setIs24HourView(true)
            hour = prefs.getInt("endHour", 16)
            minute = prefs.getInt("endMinute", 0)
        }

        root.addView(endPicker)

        // Choose apps
        root.addView(
            Button(this).apply {
                text = "Choose apps"

                setOnClickListener {
                    showAppPicker()
                }
            }
        )

        // Save schedule
        root.addView(
            Button(this).apply {
                text = "Save schedule"

                setOnClickListener {

                    prefs.edit()
                        .putInt("startHour", startPicker.hour)
                        .putInt("startMinute", startPicker.minute)
                        .putInt("endHour", endPicker.hour)
                        .putInt("endMinute", endPicker.minute)
                        .putStringSet("blocked", selected.toSet())
                        .apply()

                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("StudyLock")
                        .setMessage("Your focus schedule has been saved.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        )

        // Accessibility settings
        root.addView(
            Button(this).apply {
                text = "Accessibility settings"

                setOnClickListener {
                    this@MainActivity.startActivity(
                        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    )
                }
            }
        )

        // About StudyLock
        root.addView(
            TextView(this).apply {
                text = """

                About StudyLock

                StudyLock helps you follow your chosen study schedule.
                Accessibility must be enabled manually in Android Settings.
                You can disable it through Android Settings at any time.

            """.trimIndent()

                textSize = 14f
                setPadding(0, 18, 0, 0)
            }
        )

        // Scrollable layout
        val scrollView = ScrollView(this).apply {
            addView(root)
        }

        setContentView(scrollView)
    }

    private fun showAppPicker() {

        val pm = packageManager

        val launchIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = pm.queryIntentActivities(launchIntent, 0)
            .mapNotNull { ri ->

                val pkg = ri.activityInfo?.packageName
                    ?: return@mapNotNull null

                // Exclude StudyLock and Android Settings
                if (pkg == packageName ||
                    pkg == "com.android.settings"
                ) {
                    return@mapNotNull null
                }

                val label = ri.loadLabel(pm).toString()

                pkg to label
            }
            .distinctBy { it.first }
            .sortedBy { it.second.lowercase() }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 10, 20, 10)
        }

        val scroll = ScrollView(this).apply {
            addView(container)
        }

        apps.forEach { (pkg, label) ->

            container.addView(
                CheckBox(this).apply {
                    text = label
                    isChecked = selected.contains(pkg)

                    setOnCheckedChangeListener { _, checked ->
                        if (checked) {
                            selected.add(pkg)
                        } else {
                            selected.remove(pkg)
                        }
                    }
                }
            )
        }

        AlertDialog.Builder(this)
            .setTitle("Choose apps")
            .setView(scroll)
            .setPositiveButton("Done", null)
            .setNegativeButton("Cancel", null)
            .show()
    }
}
