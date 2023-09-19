package com.example.foregrounservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.core.app.NotificationCompat
import com.example.foregrounservice.Model.song

class Myservice : Service() {
    private lateinit var media: MediaPlayer
    private val CHANNEL_ID = "channelid"
    private val CHANNEL_NAME = "channelname"
    private val NOTIFICATION_ID = 0
private var isLooping=true

    private val song = song("Tháng Tư", "Anh Tuấn", R.raw.file_music, R.drawable.ic_launcher_foreground)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        media = song.resound?.let { MediaPlayer.create(this, it) }!!
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        //sử dung when thay cho switch case
        when (intent?.action) {
            "ACTION_PAUSE" -> {
                media.pause()
                media.isLooping = false
                notification
            }
            "ACTION_RESUME" -> {

                media.start()
                media.isLooping = true
                notification
            }
            "ACTION_CLAER" -> {
                media.isLooping = false
                stopSelf()
                notification
            }
        }


        return START_STICKY
    }

    private fun createNotification(): Notification {
        val packageName = packageName
        val layoutId = R.layout.notification
        val stop = R.drawable.baseline_stop_24
        val play = R.drawable.baseline_play_arrow_24
        val pauseIntent = Intent(this, MainActivity::class.java)

        pauseIntent.action = "ACTION_PAUSE"
        val pendingPauseIntent = PendingIntent.getService(
            this,
            0,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        pauseIntent.action = "ACTION_RESUME"

        val pendingResumIntent = PendingIntent.getService(
            this,
            0,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        pauseIntent.action = "ACTION_CLAER"
        val pendingClearIntent = PendingIntent.getService(
            this,
            0,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val remoteViews = RemoteViews(packageName, layoutId).apply {
            setTextViewText(R.id.title_song, song.title)
            setTextViewText(R.id.single_song, song.sing)
//            song.img?.let { setImageViewResource(R.id.img_song, it) }
//            setImageViewResource(R.id.img_play, play)
         if (media.isLooping == true){
             setOnClickPendingIntent(R.id.img_play, pendingPauseIntent)
             setImageViewResource(R.id.img_play,stop)
         }else{
             setOnClickPendingIntent(R.id.img_play, pendingResumIntent)
             setImageViewResource(R.id.img_play,play)
         }
            setOnClickPendingIntent(R.id.img_cancel, pendingClearIntent)

        }

        val intent = Intent(this@Myservice, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setCustomContentView(remoteViews)
            .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(NOTIFICATION_ID,notification.build())
        }

        return notification.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        media.stop()
        media.release()
    }
}
