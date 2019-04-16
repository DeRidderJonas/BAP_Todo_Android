package be.nextapps.jonas.bap_todo_android

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import java.util.*

class NotificationService: IntentService("NotificationService"){

    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    private fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val context = this.applicationContext
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = "Todo alarm"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "be.nextapps.jonas.bap_todo_android.CHANNEL_ID"
        const val CHANNEL_NAME = "Alarm notification"
    }

    override fun onHandleIntent(intent: Intent?) {
        createChannel()

        var timestamp: Long = 0
        if(intent != null && intent.extras != null){
            timestamp = intent.extras!!.getLong("timestamp")
        }

        if(timestamp > 0){
            val context = this.applicationContext
            var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notifyIntent = Intent(this, MainActivity::class.java)

            val title = "Todo Alarm"
            val message = "Alarm is going off"

            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("message", message)
            notifyIntent.putExtra("notification", true)

            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val pendingIntent = PendingIntent.getActivity(context, 0,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val res = this.resources
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                mNotification = Notification.Builder(this, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(Notification.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .build()
            } else {
                mNotification = Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setStyle(Notification.BigTextStyle().bigText(message))
                    .setSound(uri)
                    .setContentText(message)
                    .build()
            }

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(mNotificationId, mNotification)
        }
    }

}