package id.co.minumin.notifications.receivers

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.notifications.helper.AlarmHelper
import javax.inject.Inject

/**
 * Created by pertadima on 15,February,2021
 */

@AndroidEntryPoint
class BootReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var alarmHelper: AlarmHelper

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val notificationFrequency = 60
            alarmHelper.apply {
                cancelAlarm(context)
                setAlarm(context, notificationFrequency.toLong())
            }
        }
    }
}
