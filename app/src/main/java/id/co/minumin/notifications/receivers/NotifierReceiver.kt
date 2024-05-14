package id.co.minumin.notifications.receivers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.notifications.helper.NotificationHelper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 15,February,2021
 */

@AndroidEntryPoint
class NotifierReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        /* Notify */
        GlobalScope.launch(Dispatchers.IO) {
            val nBuilder = notificationHelper.getNotificationReminder(true)
            notificationHelper.notify(2, nBuilder, false)
        }
    }
}