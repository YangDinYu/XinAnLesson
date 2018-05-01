package com.example.fakeqq

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_sendBroadcast.setOnClickListener({
            var intent = Intent("com.example.fakeqq.MY_BROADCAST");
            //intent.action = "com.example.fakeqq.MY_BROADCAST"
            sendBroadcast(intent);
        })
    }
}
