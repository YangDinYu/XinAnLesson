package com.example.xinanlessson

import android.app.AlertDialog
import android.content.Context
import android.gesture.Gesture
import android.gesture.GestureOverlayView
import android.graphics.Color
import android.util.Log
import android.view.*
import java.io.DataOutputStream
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.content.DialogInterface
import android.graphics.Bitmap
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.ArrayAdapter
import java.nio.file.Files.size
import android.R.attr.name
import android.app.admin.DevicePolicyManager
import android.gesture.Prediction
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.os.Environment.MEDIA_MOUNTED
import android.os.PowerManager
import java.io.File


/**
 * Created by Administrator on 2018/4/10.
 */
object Singleton {
    public var itsMeToLock = false;
    public var isShow  = false;
    public lateinit var mView: View;
    public lateinit var coverView: View;
    public lateinit var mWindowManager:WindowManager;
    public val params = WindowManager.LayoutParams()

    public lateinit var context:Context;

    //表示是否单击了左右两点，若是则进入手势识别模式
    private var left=false ;
    private var right=false ;
    private lateinit var mPowerManager: PowerManager
    private lateinit var mWakeLock: PowerManager.WakeLock
    private lateinit var policyManager: DevicePolicyManager

    private lateinit var gestureLib:GestureLibrary;

    init {

        Environment.getExternalStorageDirectory()
        Log.i("te","Singleton初始")
        // gestureLib = GestureLibraries.fromFile(mStoreFile)
        //"/mnt/sdcard/mygestures"在另一用户处读不到
        gestureLib = GestureLibraries.fromFile("/storage/emulated/0/mygestures")
        if (!gestureLib.load()) {
            Log.i("te","手势库加在失败")
        }else{
            Log.i("te","手势库加载成功")
            Log.i("te","gestureLib size = "+gestureLib.gestureEntries.size);
            Log.i("te","gestureLib size = "+gestureLib.getGestures("Love").size);
        }


    }


    public fun setmyContext(context2: Context){
        context = context2;

        //初始化需要用到的系统服务
        mPowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag")
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager;
        policyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        //初始化需要添加的悬浮窗的View，包括设置监听、手势等等
        initView();
    }

    public fun lightScreen(){

        mWakeLock.acquire()
        mWakeLock.release()
    }



    public fun lockScreen(){
        itsMeToLock = true;

        policyManager.lockNow()
    }

    public fun switchUser(userID:Int){
        lockScreen();

        var process = Runtime.getRuntime().exec("su");
        var os = DataOutputStream(process.outputStream);
        os.writeBytes("am switch-user "+userID+" \n");
        os.flush();
        os.writeBytes("exit\n");
        os.flush();
        process.waitFor();


        /**
         *尝试用overlay挡一下“正在切换”,但是此方案失败
         */
/*        var params2 = WindowManager.LayoutParams();

        params2.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params2.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or

                //WindowManager.LayoutParams.FLAG_SYSTEM_ERROR;不行，这个标致是无条件获得焦点，但是没有
                0x40000000;

        params2.width = WindowManager.LayoutParams.MATCH_PARENT;
        params2.height = WindowManager.LayoutParams.MATCH_PARENT;
        params2.gravity = Gravity.TOP;

        Thread.sleep(300);
        mWindowManager.addView(coverView, params2)*/


        removeScreenLock();
    }

    public fun setParams(){
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or

                //WindowManager.LayoutParams.FLAG_SYSTEM_ERROR;不行，这个标致是无条件获得焦点，但是没有
                0x40000000;

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP;
    }

    public fun initView(){
        mView = LayoutInflater.from(context).inflate(R.layout.lock_view,null);
        coverView = LayoutInflater.from(context).inflate(R.layout.cover_layout,null);
        var gestureOverlayView = mView.findViewById<GestureOverlayView>(R.id.myGestureOverlayView);
        gestureOverlayView.gestureColor = Color.BLUE;
        gestureOverlayView.gestureStrokeWidth = 5.0f;



        //监听完成手势时
        gestureOverlayView.addOnGesturePerformedListener(object : GestureOverlayView.OnGesturePerformedListener {


            override fun onGesturePerformed(p0: GestureOverlayView?, gesture: Gesture?) {
                Log.i("gesture","画完了？");



/*               gestureLib.addGesture("gestureName", gesture)
                gestureLib.save();*/



                //gestureLib.removeEntry("gestureName")
                val predictions = gestureLib.recognize(gesture)

                //遍历所有找到的Prediction对象
                for (pred in predictions) {
                    if (pred.score > -0.1) {

                        Log.i("ges","与手势【" + pred.name + "】相似度为" + pred.score);
                    }

                    if (pred.score > 8) {
                        gestureOverlayView.visibility = View.INVISIBLE;


                        switchUser(0);
                        Log.i("ges","与手势【" + pred.name + "】相似度为" + pred.score);
                    }
                }



            }


        })



        setParams();

        //设置锁屏界面的触屏事件


        mView.setOnTouchListener(object : View.OnTouchListener {
            //保存悬浮框最后位置的变量
            internal var lastX: Int = 0
            internal var lastY: Int = 0
            internal var paramX: Int = 0
            internal var paramY: Int = 0
            internal var downX: Int = 0
            internal var downY: Int = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX.toInt()

                        downX = event.rawX.toInt()
                        downY = event.rawY.toInt()

                        //lastY = event.rawY.toInt()
                        paramX = params.x
                        //paramY = params.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX.toInt() - lastX
                        params.x = paramX + dx
                        // 更新悬浮窗位置
                        mWindowManager.updateViewLayout(mView, params)

                        if (params.x < - 700){
                            //switchUser(0);
                        }

                        if (params.x > 700){

                            switchUser(11);

                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        //根据坐标判断是否是单击，以及单击的位置
                        var upX = event.rawX.toInt();
                        var upY = event.rawY.toInt();
                        if (downX==upX && downY==upY){
                            Log.i("单击事件","点击了："+downX+","+downY);

                            if(downX>253 && downX<383 && downY>720 && downY<790){
                                left = true;
                            }

                            if(downX>670 && downX<810 && downY>720 && downY<790){
                                right = true;
                            }

                            //左右两点都点击了，则将gestureOverlayView设为可见，开始手势识别
                            if(left&& right){
                                gestureOverlayView.visibility = View.VISIBLE;
                            }
                        }


                        //恢复锁屏界面回到初始位置
                        params.x = 0;
                        mWindowManager.updateViewLayout(mView,params);
                    }
                }
                return true
            }
        })

    }



    fun removeScreenLock(){
        if(isShow) {
            mWindowManager.removeViewImmediate(mView);
            isShow = false;
            //重置坐标，避免下一次重用参数时坐标未复原
            params.x = 0;
            left = false;
            right = false;

        }
    }
}