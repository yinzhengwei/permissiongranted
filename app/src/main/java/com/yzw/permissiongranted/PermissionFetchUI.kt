package com.yzw.permissiongranted

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.yzw.permissiongranted.PermissionUtils.Companion.isOpenPermisson
import com.yzw.permissiongranted.PermissionUtils.Companion.openPermission
import com.yzw.permissiongranted.PermissionUtils.Companion.showPermissions

/**
 * Create by yinzhengwei on 2018/11/28
 * @Function
 */
class PermissionFetchUI : Activity() {

    val REQUESTCODE: Int = 100
    var psn = arrayOf<String>()
    var permissionName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        psn = intent.getStringArrayExtra("psn")
        permissionName = intent.getStringExtra("permissionName")

        checkPermission()
    }

    private fun checkPermission() {
        psn.forEach {
            if (!isOpenPermisson(this, it)) {
                // 缺少权限时, 进入权限配置页面
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    openPermission(this, it, REQUESTCODE)
                }
                return
            }
        }
        Toast.makeText(this, "${permissionName}权限已全部获取成功", Toast.LENGTH_SHORT).show()
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
            showPermissions(this,permissionName, REQUESTCODE) {
                if (!it) return@showPermissions

                //如果点击了取消，则再次打开权限
                checkPermission()
            }
        }
    }

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