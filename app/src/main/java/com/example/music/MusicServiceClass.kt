package com.example.music

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.music.Music.ACTION_CLOSE
import com.example.music.Music.ACTION_START_PLAYER
import com.example.music.Music.ACTION_STOP_PLAYER
import com.example.music.Music.CHANNEL_ID
import com.example.music.Music.PAUSE
import com.example.music.Music.PLAY
import com.example.music.Music.checked
import com.example.music.Music.mutableLiveData


class MusicServicesClass : Service() {

    private lateinit var player: MediaPlayer
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var remoteView: RemoteViews
    private lateinit var channelId: String


    @SuppressLint("RemoteViewLayout")
    override fun onCreate() {
        super.onCreate()

channelId=resources.getString(R.string.app_name)

        player = MediaPlayer()
        player = MediaPlayer.create(this, R.raw.music)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        remoteView = RemoteViews(packageName, R.layout.custom_item)

        val intent = Intent(this, ServiceActivity::class.java)
        val intentFlagType: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, intentFlagType)

        //start and stop music
        val stopIntent = Intent(this, MusicServicesClass::class.java)
        stopIntent.action = ACTION_STOP_PLAYER
        val playPendingIntent = PendingIntent.getService(this, 0, stopIntent, intentFlagType)
        remoteView.setOnClickPendingIntent(R.id.btnPlay, playPendingIntent)


        //Remove Notification and Music
        val closeIntent = Intent(this, MusicServicesClass::class.java)
        closeIntent.action = ACTION_CLOSE
        val closePendingIntent = PendingIntent.getService(this, 0, closeIntent, intentFlagType)
        remoteView.setOnClickPendingIntent(R.id.btnClose, closePendingIntent)


        builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(resources.getString(R.string.music_start)).setChannelId(channelId)
            .setContentText(resources.getString(R.string.start_services))
            .setSmallIcon(R.drawable.music123).setContentIntent(pendingIntent)
            .setCustomContentView(remoteView).setCustomBigContentView(remoteView).setPriority(0)
            .setAutoCancel(true)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = resources.getString(R.string.app_name)

            notificationChannel = NotificationChannel(
                channelId, name, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

        }

        startForeground(CHANNEL_ID, builder.build())

    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        //Play event handel
        if (intent.action != null && intent.action.equals(ACTION_START_PLAYER)) {
            player.isLooping = true
            player.start()
            remoteView.setTextViewText(R.id.btnPlay, getString(R.string.pause))
            notificationManager.notify(CHANNEL_ID, builder.build())

        }

        // Play pause event handel
        if (intent.action != null && intent.action!! == ACTION_STOP_PLAYER) {
            if (player.isPlaying) {
                checked = true
                player.pause()
                remoteView.setTextViewText(R.id.btnPlay, getString(R.string.play))
                remoteView.setTextViewText(R.id.musicStart, getString(R.string.music_start))
                mutableLiveData.postValue(PLAY)
            } else {
                checked = false
                player.start()
                remoteView.setTextViewText(R.id.btnPlay, getString(R.string.pause))
                remoteView.setTextViewText(R.id.musicStart, getString(R.string.music_stop))
                mutableLiveData.postValue(PAUSE)
            }
            notificationManager.notify(CHANNEL_ID, builder.build())
        }

        //Close event handel
        if (intent.action != null && intent.action == ACTION_CLOSE) {
            checked = true
            player.pause()
            player.stop()
            stopForeground(false)
            stopSelf()
        }
        return START_STICKY

    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}