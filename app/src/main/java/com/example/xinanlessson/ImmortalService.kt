package com.example.xinanlessson

import android.app.ActivityManager
import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.widget.Toast
import android.content.Context.SENSOR_SERVICE
import android.content.IntentFilter
import android.hardware.SensorManager
import android.view.LayoutInflater
import android.view.WindowManager


/**
 * Created by 懵逼的杨定宇 on 2018/2/9.
 */
class ImmortalService:NotificationListenerService() {
    lateinit var mReceiver : BroadcastReceiver;
    override fun onCreate() {
        startForeground(1200, getNotification())
        var intentFilter = IntentFilter();




        //初始化Singleton
        Singleton.setmyContext(applicationContext);


        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_BACKGROUND);
        intentFilter.addAction(Intent.ACTION_USER_FOREGROUND);
        mReceiver = MyBroadcastReceiver();
        registerReceiver(mReceiver,intentFilter);
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        //return super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY;
    }

    override fun onListenerConnected() {
        Toast.makeText(applicationContext,"绑定Service",Toast.LENGTH_SHORT).show();
        super.onListenerConnected()
    }

    private fun getNotification(): Notification {
        val mBuilder = Notification.Builder(this@ImmortalService)
        mBuilder.setShowWhen(false)
        mBuilder.setAutoCancel(false)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round)

        mBuilder.setContentText("thisiscontent")
        mBuilder.setContentTitle("this is title")

        return mBuilder.build()

    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy()
    }
}