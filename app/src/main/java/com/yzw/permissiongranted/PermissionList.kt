package com.yzw.permissiongranted

import android.Manifest

/**
 * Create by yinzhengwei on 2018/11/27
 * @Function 列出常用的权限列表，每种使用默认这里将相应的权限都打开了
 */

object PermissionList{

    /**
     *  sdcard读写权限
     */
    var permission_sdcardRW = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //  NETWORK(网络访问)
    var permission_NETWORK = arrayOf(
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.INTERNET
    )

    //  WIFI(wifi访问)
    var permission_WIFI = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE
    )

    //SMS（短信）
    var permission_SMS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECEIVE_MMS
    )
    //CONTACTS（联系人）
    var permission_CONTACTS = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.GET_ACCOUNTS
    )

    // PHONE（手机）
    var permission_PHONE = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.ADD_VOICEMAIL,
        Manifest.permission.USE_SIP,
        Manifest.permission.PROCESS_OUTGOING_CALLS
    )

    //    CALENDAR（日历）
    var permission_CALENDAR = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    //    CAMERA（相机）
    var permission_CAMERA = arrayOf(
        Manifest.permission.CAMERA
    )

    //    LOCATION（位置）
    var permission_LOCATION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
//        Manifest.permission.LOCATION_HARDWARE
    )

    //    MICROPHONE（麦克风）
    var permission_MICROPHONE = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    //  RECORD_AUDIO（录音）
    var permission_RECORD_AUDIO = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )


}


