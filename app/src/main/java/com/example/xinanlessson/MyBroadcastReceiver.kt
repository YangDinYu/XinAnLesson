package com.example.xinanlessson

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Config

import android.util.Log
import android.graphics.PixelFormat
import android.app.KeyguardManager
import android.view.*
import java.io.DataOutputStream


/**
 * Created by 懵逼的杨定宇 on 2018/2/9.
 */
class MyBroadcastReceiver :BroadcastReceiver() {




    override fun onReceive(p0: Context, p1: Intent) {


        if(p1.action.equals(Intent.ACTION_USER_PRESENT)){
            Log.i("test","解锁")
            var intent = Intent(p0,MainActivity::class.java);
            //p0?.startActivity(intent);
        }

        if(p1.action.equals(Intent.ACTION_SCREEN_ON)){
            Log.i("test","开屏幕")
            var intent = Intent(p0,MainActivity::class.java);
            p0?.startActivity(intent);
        }



        if(p1.action.equals(Intent.ACTION_SCREEN_OFF)){
            initScreenLock(p0);
        }



        //切换到蜜罐系统
        if(p1.action.equals(Intent.ACTION_USER_BACKGROUND)){
            Singleton.lightScreen();
            Log.i("change","切换到蜜罐系统");
            Singleton.removeScreenLock();
        }

        //切换到主系统
        if(p1.action.equals(Intent.ACTION_USER_FOREGROUND)){
            Singleton.lightScreen();
            Log.i("change","切换到主系统");
            Singleton.removeScreenLock();
        }

    }

    fun initScreenLock(p0:Context){





        //设置锁屏界面的触屏事件


        if (!Singleton.isShow && !Singleton.itsMeToLock) {
            Singleton.mWindowManager.addView(Singleton.mView, Singleton.params);

            Singleton.isShow = true;
        }

        Singleton.itsMeToLock = false;
    }





}