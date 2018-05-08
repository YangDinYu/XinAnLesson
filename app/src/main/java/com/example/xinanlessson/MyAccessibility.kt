package com.example.xinanlessson

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.io.DataOutputStream


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
            //不知道为什么出错了,猜测是该进程没有执行su命令？
/*            Runtime.getRuntime().exec("adb shell am force-stop com.eg.android.AlipayGphone");*/

/*            var process = Runtime.getRuntime().exec("su");
            var os = DataOutputStream(process.outputStream);
            os.writeBytes("adb shell am force-stop com.eg.android.AlipayGphone\n");
            os.flush();
            Log.i("test","打开了支付宝2")
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();*/

            Singleton.kill("com.eg.android.AlipayGphone")
            Log.i("test","打开了支付宝2")


/*            var process2 = Runtime.getRuntime().exec("su");
            var os2 = DataOutputStream(process.outputStream);
            Log.i("test","打开了支付宝3")
            os2.writeBytes("adb shell am force-stop com.eg.android.AlipayGphone\n");
            os2.flush();
            Log.i("test","打开了支付宝4")
            os2.writeBytes("exit\n");
            os2.flush();
            process2.waitFor();*/
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