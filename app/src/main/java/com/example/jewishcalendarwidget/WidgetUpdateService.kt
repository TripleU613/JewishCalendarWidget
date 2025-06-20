package com.example.jewishcalendarwidget

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.PowerManager

class WidgetUpdateService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()

        // Acquire wake lock for system app
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "JewishCalendarWidget::UpdateLock"
        )
        wakeLock?.acquire()

        startPeriodicUpdates()
    }

    private fun startPeriodicUpdates() {
        updateRunnable = object : Runnable {
            override fun run() {
                updateAllWidgets()
                handler.postDelayed(this, 60000) // Update every minute
            }
        }
        handler.post(updateRunnable!!)
    }

    private fun updateAllWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, JewishCalendarWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        if (appWidgetIds.isNotEmpty()) {
            val intent = Intent(this, JewishCalendarWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            sendBroadcast(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        updateRunnable?.let { handler.removeCallbacks(it) }
        wakeLock?.release()

        // Restart service
        val restartIntent = Intent(this, WidgetUpdateService::class.java)
        startService(restartIntent)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
