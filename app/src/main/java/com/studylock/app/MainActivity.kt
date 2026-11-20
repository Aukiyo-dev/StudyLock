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
    private val prefs by lazy { getSharedPreferences("studylock", MODE_PRIVATE) }
    private lateinit var startPicker: TimePicker
    private lateinit var endPicker: TimePicker
    private val selected = mutableSetOf<String>()
    private val names = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selected.addAll(prefs.getStringSet("blocked", emptySet()) ?: emptySet())

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
        }

        root.addView(TextView(this).apply {
            text = "StudyLock"
            textSize = 30f
        })

        root.addView(TextView(this).apply {
            text = "Set a daily focus window and choose distracting apps."
            textSize = 16f
            setPadding(0, 10, 0, 20)
        })

        root.addView(TextView(this).apply {
            text = "Focus starts"
        })

        startPicker = TimePicker(this).apply {
            setIs24HourView(true)
            hour = prefs.getInt("startHour", 14)
            minute = prefs.getInt("startMinute", 0)
        }
        root.addView(startPicker)

        root.addView(TextView(this).apply {
            text = "Focus ends"
        })

        endPicker = TimePicker(this).apply {
            setIs24HourView(true)
            hour = prefs.getInt("endHour", 16)
            minute = prefs.getInt("endMinute", 0)
        }
        root.addView(endPicker)

        root.addView(Button(this).apply {
            text = "Choose blocked apps"
            setOnClickListener {
                showAppPicker()
            }
        })

        root.addView(Button(this).apply {
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
                    .setMessage("Schedule saved. The blocker runs only while Accessibility is enabled.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        })

        root.addView(Button(this).apply {
            text = "Enable overlay permission"
            setOnClickListener {
                startActivity(
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                )
            }
        })

        root.addView(Button(this).apply {
            text = "Enable Accessibility blocker"
            setOnClickListener {
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                )
            }
        })

        root.addView(TextView(this).apply {
            text = "This prototype is transparent and can be disabled in Android Settings. It is not tamper-proof."
            setPadding(0, 18, 0, 0)
        })

        setContentView(ScrollView(this).apply {
            addView(root)
        })
    }

    private fun showAppPicker() {
        val pm = packageManager

        val launchIntent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)

        val apps = pm.queryIntentActivities(launchIntent, 0)
            .mapNotNull { ri ->
                val pkg = ri.activityInfo?.packageName
                    ?: return@mapNotNull null

                if (pkg == packageName || pkg == "com.android.settings") {
                    return@mapNotNull null
                }

                val label = ri.loadLabel(pm).toString()
                names[pkg] = label
                pkg to label
            }
            .distinctBy { it.first }
            .sortedBy { it.second.lowercase() }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            addView(container)
        }

        apps.forEach { (pkg, label) ->
            container.addView(CheckBox(this).apply {
                text = label
                isChecked = selected.contains(pkg)

                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        selected.add(pkg)
                    } else {
                        selected.remove(pkg)
                    }
                }
            })
        }

        AlertDialog.Builder(this@MainActivity)
            .setTitle("Apps to block")
            .setView(scroll)
            .setPositiveButton("Done", null)
            .show()
    }
}
