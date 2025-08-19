package com.example.fakegps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MockLocationService : Service() {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    private lateinit var mocker: LocationMocker

    override fun onCreate() {
        super.onCreate()
        mocker = LocationMocker(this)
        startForegroundWithNotification()
        job = scope.launch {
            while (isActive) {
                runCatching {
                    mocker.startMock(-6.9175, 107.6191, 5f)
                }
                delay(2000)
            }
        }
    }

    private fun startForegroundWithNotification() {
        val channelId = "mock_location_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Mock Location", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
        val notif: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Fake GPS berjalan")
            .setContentText("Menyuntik lokasi mock…")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()
        startForeground(1001, notif)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
