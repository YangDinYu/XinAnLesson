package com.example.fakewechat

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.telephony.TelephonyManager
import android.util.Log
import android.location.LocationManager
import android.widget.Toast
import java.util.*


/**
 * Created by ydy on 18-4-29.
 */
object MessageCollector {
    lateinit private var context :Context;
    private lateinit var myTelephonyManager:TelephonyManager;
    lateinit var locationManager:LocationManager;

    public fun setMyContext(context1: Context){
        context = context1;
        myTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
    }

    @SuppressLint("MissingPermission")
    fun getIccid(): String {
        var iccid = "N/A"
        iccid = myTelephonyManager.getSimSerialNumber()
        return iccid
    }

    @SuppressLint("MissingPermission")
//获取电话号码
    fun getNativePhoneNumber(): String {
        var nativePhoneNumber = "N/A"

        nativePhoneNumber = myTelephonyManager.getLine1Number()



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

        return providersName

    }

    @SuppressLint("MissingPermission")
    fun getLocation(): String{
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);



        Toast.makeText(context, getStreet(location),Toast.LENGTH_SHORT).show();
        //Log.i("location",getStreet(location));


        return "Time:"+location.time+";经度:"+location.longitude+";纬度:"+location.latitude+";海拔:"+location.altitude
    }


    fun getStreet(location: Location): String {
        val geocoder = Geocoder(context)
        val stringBuilder = StringBuilder();
        //根据经纬度获取地理位置信息
        var addresses:List<Address> = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

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
            stringBuilder.append(address.getThoroughfare()).append("_");//道路

            stringBuilder.append(address.getLatitude()).append("_");//经度
            stringBuilder.append(address.getLongitude());//维度


            stringBuilder.append(address.getFeatureName()).append("_");//周边地址

            stringBuilder.append(address.getSubAdminArea()).append("_");
            stringBuilder.append(address.getPostalCode()).append("_");
            stringBuilder.append(address.getCountryCode()).append("_");//国家编码
        }

        return stringBuilder.toString();

    }

}