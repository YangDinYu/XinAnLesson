package com.example.xinanlessson

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Config

import android.util.Log
import android.graphics.PixelFormat
import android.app.KeyguardManager
import android.app.admin.DeviceAdminReceiver
import android.view.*
import java.io.DataOutputStream
import android.widget.Toast




/**
 * Created by 懵逼的杨定宇 on 2018/2/9.
 */
class MyScreenOffAdminReceiver :DeviceAdminReceiver() {

    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onEnabled(context: Context, intent: Intent) {
        showToast(context,
                "设备管理器使能")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        showToast(context,
                "设备管理器没有使能")
    }

}