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

                // Start service as regular background service (system app doesn't need foreground)
                val serviceIntent = Intent(context, WidgetUpdateService::class.java)
                context.startService(serviceIntent)
            }
        }
    }
}
