package com.example.jewishcalendarwidget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.appwidget.AppWidgetManager
import android.content.ComponentName

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start the widget update service
        val serviceIntent = Intent(this, WidgetUpdateService::class.java)
        startService(serviceIntent)

        // Set up the add widget button
        val addWidgetButton = findViewById<Button>(R.id.addWidgetButton)
        addWidgetButton.setOnClickListener {
            openWidgetPicker()
        }
    }

    private fun openWidgetPicker() {
        try {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

            // Add our widget as an option
            val componentName = ComponentName(this, JewishCalendarWidget::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, arrayOf(componentName))

            startActivityForResult(intent, 1)
        } catch (_: Exception) {
            // Fallback: Show instructions
            Toast.makeText(this,
                "Please long press on your home screen and select Widgets to add the Jewish Calendar Widget",
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Widget added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
