package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notifyIntent: Intent
    private lateinit var resultPending: PendingIntent

    var URL: String = EMPTY_URL
    lateinit var btnSelected: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {

            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

            when (radioGroup.checkedRadioButtonId) {
                R.id.rbtn_glide -> {
                    URL = GLIDE_URL
                    btnSelected = this.getString(R.string.download_using_glide)
                }
                R.id.rbtn_loadapp -> {
                    URL = LOAD_APP_URL
                    btnSelected = this.getString(R.string.download_using_loadapp)
                }
                R.id.rbtn_retrofit -> {
                    URL = RETROFIT_URL
                    btnSelected = this.getString(R.string.download_using_refrofit)
                }
                else -> {
                    Toast.makeText(this, getString(R.string.rbtn_null_toast), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            custom_button.buttonState = ButtonState.Loading
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val query = DownloadManager.Query()
            query.setFilterById(id!!, 0)
            val manager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor: Cursor = manager.query(query)
            if (cursor.moveToFirst()) {
                if (cursor.count > 0) {
                    val downloadStatus: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    val statusFeedback :String = when(downloadStatus){
                        DownloadManager.STATUS_SUCCESSFUL -> "Download Finished"
                        else -> "Fail"
                    }
                    custom_button.buttonState = ButtonState.Completed
                    sendNotification(statusFeedback, btnSelected)
                }
            }

            notificationManager.notify(NOTIFICATION_ID, builder.build())


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(downloadStatus: String, downloadVia: String) {
        //Intent
        notifyIntent = Intent(applicationContext, DetailActivity::class.java)
        notifyIntent.putExtra("DOWNLOAD_VIA", downloadVia)
        notifyIntent.putExtra("DOWNLOAD_STATUS", downloadStatus)

        //Creating a PendingIntent
        resultPending = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // NotificationChannel
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "Resource Download Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Notify when download is finished"

        // Build Notification
        builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentIntent(resultPending)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_launcher_foreground, "Check Status", resultPending)

        // Set NotificationChannel into NotificationManager
        notificationManager = this.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun download() {

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
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/masterXXX.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val EMPTY_URL = " "

        const val CHANNEL_ID = "channelId"
        const val NOTIFICATION_ID = 0
    }

}
