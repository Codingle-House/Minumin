package id.co.minumin.notifications.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import id.co.minumin.R
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.presentation.home.HomeActivity
import id.co.minumin.util.DateTimeUtil
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject


/**
 * Created by pertadima on 15,February,2021
 */

class NotificationHelper @Inject constructor(
    private val context: Context,
    private val userPreferenceManager: UserPreferenceManager
) {
    private var notificationManager: NotificationManager? = null

    private fun createChannels(enableVibration: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME,
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                enableLights(true)
                lightColor = Color.BLUE
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(enableVibration)
            }

            val notificationsNewMessageRingtone =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            notificationChannel.setSound(
                Uri.parse(notificationsNewMessageRingtone),
                audioAttributes
            )

            getManager()?.createNotificationChannel(notificationChannel)
        }
    }

    fun getNotificationReminder(enableVibration: Boolean): NotificationCompat.Builder? {
        createChannels(enableVibration)
        val notificationIntent = Intent(context, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val contentIntent =
            PendingIntent.getActivity(
                context,
                99,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


        val normalView = RemoteViews(context.packageName, R.layout.view_custom_reminder)
        val notification = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.notification_ic_logo)
            .setContent(normalView)
            .setShowWhen(true)
            .setAutoCancel(false)

        notification.setContentIntent(contentIntent)

        return notification
    }

    fun getNotificationCustom(enableVibration: Boolean): NotificationCompat.Builder? {
        createChannels(enableVibration)
        val notificationIntent = Intent(context, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val contentIntent =
            PendingIntent.getActivity(
                context,
                99,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


        val normalView = RemoteViews(context.packageName, R.layout.view_custom_notification)
        val notification = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.notification_ic_logo)
            .setContent(normalView)
            .setShowWhen(true)

        notification.setContentIntent(contentIntent)

        return notification
    }

    private suspend fun shallNotify(isNotify: (Boolean) -> Unit) {
        userPreferenceManager.getUserRegisterData().collect {
            val startTimeStamp =
                DateTimeUtil.convertDate(it.wakeUpTime, DateTimeUtil.DEFAULT_TIME)?.time ?: 0L
            val stopTimeStamp =
                DateTimeUtil.convertDate(it.sleepTime, DateTimeUtil.DEFAULT_TIME)?.time ?: 0L
            val totalIntake = it.waterNeeds

            if (startTimeStamp == 0L || stopTimeStamp == 0L || totalIntake == 0) {
                isNotify.invoke(false)
                return@collect
            }

            val now = Calendar.getInstance().time

            val start = Date(startTimeStamp)
            val stop = Date(stopTimeStamp)

            val passedSeconds = compareTimes(now, start)

            val doNotDisturbOff = passedSeconds >= 0 && compareTimes(now, stop) <= 0

            isNotify.invoke(doNotDisturbOff && it.isNotificationActive)
        }
    }

    /* Thanks to:
     * https://stackoverflow.com/questions/7676149/compare-only-the-time-portion-of-two-dates-ignoring-the-date-part
    */
    private fun compareTimes(currentTime: Date, timeToRun: Date): Long {
        val currentCal = Calendar.getInstance()
        currentCal.time = currentTime

        val runCal = Calendar.getInstance().apply {
            time = timeToRun
            set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH))
            set(Calendar.MONTH, currentCal.get(Calendar.MONTH))
            set(Calendar.YEAR, currentCal.get(Calendar.YEAR))

        }

        return currentCal.timeInMillis - runCal.timeInMillis
    }

    suspend fun notify(
        id: Long,
        notification: NotificationCompat.Builder?,
        isPermanent: Boolean = true
    ) {
        shallNotify {
            if (it) {
                val notif = notification?.build()?.apply {
                    if (isPermanent) flags = Notification.FLAG_NO_CLEAR
                }
                getManager()?.notify(id.toInt(), notif)
            }
        }
    }

    private fun getManager(): NotificationManager? {
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }

    companion object {
        private const val CHANNEL_ONE_ID = "id.co.minumin.CHANNELONE"
        private const val CHANNEL_ONE_NAME = "Channel One"
    }
}
