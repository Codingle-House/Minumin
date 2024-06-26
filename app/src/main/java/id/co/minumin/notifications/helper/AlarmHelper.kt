package id.co.minumin.notifications.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import id.co.minumin.notifications.receivers.BootReceiver
import id.co.minumin.notifications.receivers.NotifierReceiver
import java.util.concurrent.TimeUnit

/**
 * Created by pertadima on 15,February,2021
 */

class AlarmHelper {
    private var alarmManager: AlarmManager? = null

    fun setAlarm(context: Context, notificationFrequency: Long) {
        val notificationFrequencyMs = TimeUnit.MINUTES.toMillis(notificationFrequency)

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, NotifierReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        val pendingAlarmIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        Log.i("AlarmHelper", "Setting Alarm Interval to: $notificationFrequency minutes")

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            notificationFrequencyMs,
            pendingAlarmIntent
        )

        /* Restart if rebooted */
        val receiver = ComponentName(context, BootReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun cancelAlarm(context: Context) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, NotifierReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        val pendingAlarmIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pendingAlarmIntent)

        /* Alarm won't start again if device is rebooted */
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        Log.i("AlarmHelper", "Cancelling alarms")
    }

    fun checkAlarm(context: Context): Boolean {

        val alarmIntent = Intent(context, NotifierReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        return PendingIntent.getBroadcast(
            context, 0,
            alarmIntent,
            PendingIntent.FLAG_NO_CREATE or FLAG_IMMUTABLE
        ) != null
    }

    companion object {
        const val ACTION_BD_NOTIFICATION = "id.co.minumin.NOTIFICATION"
    }
}