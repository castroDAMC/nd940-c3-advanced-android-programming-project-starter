package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        configureNotification()

        custom_button.setOnClickListener {
            download()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun configureNotification() {
        // NotificationManager
        notificationManager = this.getSystemService(NotificationManager::class.java)

        // NotificationChannel
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "Resource Download Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Time for breakfast"

        builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        ).setContentTitle(applicationContext.getString(R.string.notification_title)).setContentText(
            getString(
                R.string.notification_description
            )
        ).setSmallIcon(R.drawable.ic_assistant_black_24dp)

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {


        try {
            notificationManager.notify(1, builder.build())
        } catch (e: Exception){
            Log.d("TEST", e.toString())
        }


        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
