package com.example.xinanlessson

import android.app.ActivityManager
import android.app.Notification
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.os.UserManager
import android.util.Log
import android.widget.Button

import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field

import java.io.*
import android.graphics.drawable.BitmapDrawable
import android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
import android.R.attr.y
import android.R.attr.x
import android.view.*
import android.view.View.OnTouchListener
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.widget.Toast
import android.widget.ArrayAdapter
import java.nio.file.Files.size
import android.R.attr.name
import android.app.AlertDialog
import android.gesture.*
import android.graphics.Color

import android.os.PowerManager
import android.content.ComponentName
import android.app.admin.DevicePolicyManager






class MainActivity : AppCompatActivity() {

    public lateinit var thisActivity :MainActivity;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED );

        thisActivity = this;



        Runtime.getRuntime().exec("su");
        var button1 = findViewById<Button>(R.id.buttion1) ;
        button1.setOnClickListener({



        })

        ImmortalServiceButoon.setOnClickListener({

            /**
             * 注意单例的初始化要放到ImmortalService中
             */

            var intent1 = Intent(this@MainActivity,ImmortalService::class.java);
            startService(intent1);
            Log.i("te","1")


        })

        newViewButoon.setOnClickListener({
            var mWindowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager;
            val params = WindowManager.LayoutParams()
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.TOP;

            var mView = LayoutInflater.from(applicationContext).inflate(R.layout.lock_view,null);
            mView.setOnTouchListener(object : View.OnTouchListener {
                //保存悬浮框最后位置的变量
                internal var lastX: Int = 0
                internal var lastY: Int = 0
                internal var paramX: Int = 0
                internal var paramY: Int = 0

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    Log.i("test","onTouch")
                    when (event.action) {

                        MotionEvent.ACTION_DOWN -> {
                            lastX = event.rawX.toInt()
                            //lastY = event.rawY.toInt()
                            paramX = params.x
                            //paramY = params.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.rawX.toInt() - lastX
                            //val dy = event.rawY.toInt() - lastY
                            params.x = paramX + dx
                            //params.y = paramY + dy
                            // 更新悬浮窗位置
                            windowManager.updateViewLayout(mView, params)
                        }
                        MotionEvent.ACTION_UP -> {
                            params.x = 0;
                            //params.y = 0;
                            windowManager.updateViewLayout(mView,params);
                        }
                    }
                    return true
                }
            })
            mWindowManager.addView(mView, params);
        })
    }




    // 读取输入流
    private fun read(process: Process) {
        try {
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String
            line = reader.readLine();
            while (line  != null) {
                Log.i("cmd",line)
                line = reader.readLine();

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
