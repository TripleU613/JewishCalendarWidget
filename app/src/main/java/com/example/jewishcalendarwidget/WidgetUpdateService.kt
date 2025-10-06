package com.example.jewishcalendarwidget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class WidgetUpdateService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        private const val CHANNEL_ID = "widget_update_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        // Start as foreground service on Android 8.0+ (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Jewish Calendar Widget")
                .setContentText("Keeping widget updated")
                .setSmallIcon(android.R.drawable.ic_menu_day)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build()
            startForeground(NOTIFICATION_ID, notification)
        }

        // Acquire wake lock for system app
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "JewishCalendarWidget::UpdateLock"
        )
        wakeLock?.acquire()

        startPeriodicUpdates()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Widget Update Service",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Keeps Jewish Calendar Widget updated"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(restartIntent)
        } else {
            startService(restartIntent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
