package com.example.jewishcalendarwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter
import android.provider.AlarmClock
import android.app.PendingIntent
import android.content.ComponentName

import java.util.*

class JewishCalendarWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        val serviceIntent = Intent(context, WidgetUpdateService::class.java)
        context.startService(serviceIntent)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val serviceIntent = Intent(context, WidgetUpdateService::class.java)
        context.startService(serviceIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            Intent.ACTION_TIME_TICK,
            Intent.ACTION_TIME_CHANGED,  // This covers manual time changes
            "android.intent.action.TIME_SET",
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName =
                    ComponentName(context, JewishCalendarWidget::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.jewish_calendar_widget)

        val calendar = Calendar.getInstance()
        val jewishCalendar = JewishCalendar(calendar)

        val hebrewFormatter = HebrewDateFormatter().apply {
            isHebrewFormat = true
            isUseLongHebrewYears = false
            isUseGershGershayim = true
        }

        val hebrewDayOfMonth = hebrewFormatter.formatHebrewNumber(jewishCalendar.jewishDayOfMonth)
        val hebrewMonth = hebrewFormatter.formatMonth(jewishCalendar)
        val hebrewYear = hebrewFormatter.formatHebrewNumber(jewishCalendar.jewishYear)

        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
        val gregorianDay = calendar.get(Calendar.DAY_OF_MONTH)
        val gregorianDateStr = "$dayOfWeek $monthName $gregorianDay"

        views.setTextViewText(R.id.gregorian_date, gregorianDateStr)
        views.setTextViewText(
            R.id.hebrew_date_combined,
            "$hebrewDayOfMonth $hebrewMonth $hebrewYear"
        )

        // Add click action to launch Alarms app
        val alarmIntent = createAlarmIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set click listener for the entire widget
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun createAlarmIntent(context: Context): Intent {
        // Try different alarm app intents
        val alarmIntents = listOf(
            // Default Clock/Alarms app
            Intent(AlarmClock.ACTION_SHOW_ALARMS),
            // Generic alarm intent
            Intent("android.intent.action.SHOW_ALARMS"),
            // Clock app
            Intent().apply {
                component =
                    ComponentName("com.android.deskclock", "com.android.deskclock.AlarmClock")
            },
            // Samsung Clock
            Intent().apply {
                component = ComponentName(
                    "com.sec.android.app.clockpackage",
                    "com.sec.android.app.clockpackage.alarm.activity.AlarmMainActivity"
                )
            },
            // Fallback to any available clock app
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage("com.android.deskclock")
            }
        )

        // Find the first working intent
        val packageManager = context.packageManager
        for (intent in alarmIntents) {
            if (intent.resolveActivity(packageManager) != null) {
                return intent
            }
        }

        // Ultimate fallback - open settings
        return Intent(android.provider.Settings.ACTION_DATE_SETTINGS)
    }

}

