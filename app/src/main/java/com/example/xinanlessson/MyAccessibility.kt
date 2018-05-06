package com.example.xinanlessson

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo



/**
 * Created by ydy on 18-5-5.
 */
class MyAccessibility: AccessibilityService() {
    var state = 1;
    override fun onInterrupt() {


    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

            Log.i("test","接收到事件了,包名：" +p0?.packageName+"，类型："+p0?.eventType)

        if(p0?.eventType == 32){
            Log.i("test","打开了支付宝")
            sendBroadcast(Intent("com.example.fakeqq.MY_BROADCAST"));
            //不知道为什么出错了
/*            Runtime.getRuntime().exec("adb shell am force-stop com.eg.android.AlipayGphone");*/
        }


        //sendBroadcast(Intent("com.example.fakeqq.MY_BROADCAST"));



    }

    override fun onServiceConnected() {
        Log.i("test","服务连接")
        super.onServiceConnected()
    }

    var TAG = "test";
    fun recycle(info: AccessibilityNodeInfo) {
        if (info.childCount == 0) {
            Log.i(TAG, "child widget----------------------------" + info.className)
            Log.i(TAG, "showDialog:" + info.canOpenPopup())
            Log.i(TAG, "Text：" + info.text)
            Log.i(TAG, "windowId:" + info.windowId)
        } else {
            for (i in 0 until info.childCount) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i))
                }
            }
        }
    }
}