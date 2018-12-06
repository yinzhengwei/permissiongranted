package com.yzw.permissiongranted

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.yzw.permissiongranted.PermissionUtils.Companion.isOpenPermisson
import com.yzw.permissiongranted.PermissionUtils.Companion.openPermission
import com.yzw.permissiongranted.PermissionUtils.Companion.showPermissions

/**
 * Create by yinzhengwei on 2018/11/28
 * @Function 权限名称、是否强制打开、弹窗提示内容(callback)
 */
class PermissionFetchUI : Activity() {

    val REQUESTCODE: Int = 100
    var psn = arrayOf<String>()
    var permissionName = ""
    var isAllWaysRequest = true
    var callback: PermissionGrantedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callback = intent.getSerializableExtra("callback") as PermissionGrantedCallback?
        psn = intent.getStringArrayExtra("psn")
        permissionName = intent.getStringExtra("permissionName")
        isAllWaysRequest = intent.getBooleanExtra("isAllWaysRequest", true)

        checkPermission()
    }

    private fun checkPermission() {
        psn.forEach {
            //判断此权限是否已打开
            if (!isOpenPermisson(this, it)) {
                // 缺少权限时, 申请权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    openPermission(this, it, REQUESTCODE)
                }
                return
            }
        }
        finishAtty(true)
    }

    private fun finishAtty(isSucful: Boolean) {
        if (isSucful) {
            callback?.granted()
        } else {
            callback?.denied()
        }
        finish()
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    override fun onRequestPermissionsResult(rc: Int, permissions: Array<String>, grantResults: IntArray) {
        //部分厂商手机系统返回授权成功时，厂商会在此弹出授权框，有可能拒绝权限，所以要用PermissionChecker二次判断，例如红米手机（MIUI9.6）
        if (rc == REQUESTCODE && isOpenPermisson(grantResults) && isOpenPermisson(this, permissions)) {
            checkPermission()
        } else {
            //如果不是强制的权限，则用户从系统权限申请框回来后不作处理
            if (isAllWaysRequest) {
                //勾选了对话框中”Don’t ask again”的选项, 返回false,则走自定义弹窗；否则不走自定义弹窗，去请求系统弹窗
                for (deniedPermission in permissions) {
                    val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        shouldShowRequestPermissionRationale(deniedPermission)
                    } else {
                        true
                    }
                    if (!flag) {
                        //拒绝授权
                        showPermissions(this, permissionName, REQUESTCODE) {
                            if (!it) return@showPermissions

                            //如果点击了取消，则再次打开权限
                            checkPermission()
                        }
                        return
                    }
                }
                //如果点击了取消，则再次打开权限
                checkPermission()
            } else {
                finishAtty(false)
            }
        }
    }

    //从系统应用详情页回来后再次判断
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE) {
            checkPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "onDestroy")
    }

}