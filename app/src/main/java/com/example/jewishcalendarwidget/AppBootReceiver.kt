package com.example.jewishcalendarwidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AppBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON" -> {

                // Start service immediately (use startForegroundService on Android 8.0+)
                val serviceIntent = Intent(context, WidgetUpdateService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}
