package com.yzw.permissiongranted

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker

/**
 * Create by yinzhengwei on 2018/11/27
 * @Function
 */

class PermissionUtils {

    companion object {

        fun permissionCheck(
            context: Activity,
            permission: Array<String>,
            permissionName: String,
            successfulCallback: () -> Unit,
            failCallback: () -> Unit
        ) {
            permissionCheck(context, permission, permissionName, true, successfulCallback, failCallback)
        }

        /**
         * @param context 上下文
         * @param permissionName 权限集合名称（例如：相机）
         * @param permission 权限集合
         * @param isAllWaysRequest 是否强制
         * @param successfulCallback 权限申请成功的回调
         * @param failCallback 权限申请失败的回调
         */
        fun permissionCheck(
            context: Activity,
            permission: Array<String>,
            permissionName: String,
            isAllWaysRequest: Boolean,
            sufCallback: () -> Unit,
            failCallback: () -> Unit
        ) {
            context.startActivity(Intent(context, PermissionFetchUI(sufCallback, failCallback)::class.java).apply {
                putExtra("psn", permission)
                putExtra("permissionName", permissionName)
                putExtra("isAllWaysRequest", isAllWaysRequest)
            })
        }

        //打开某个权限
        fun openPermission(context: Activity, permission: String, requestCode: Int) {
            ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
        }

        //打开某组权限
        fun openPermission(context: Activity, permission: Array<String>, requestCode: Int) {
            ActivityCompat.requestPermissions(context, permission, requestCode)
        }

        //判断某个权限是否已打开
        fun isOpenPermisson(context: Context, permission: String): Boolean {
            /**
             * 一般android6以下会在安装时自动获取权限,但在小米机上，可能通过用户权限管理更改权限,checkSelfPermission会始终是true，
             * targetSdkVersion<23时 即便运行在android6及以上设备 ContextWrapper.checkSelfPermission和Context.checkSelfPermission失效
             * 返回值始终为PERMISSION_GRANTED,此时必须使用PermissionChecker.checkSelfPermission
             */
            return if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                PermissionChecker.checkPermission(
                    context,
                    permission,
                    Binder.getCallingPid(),
                    Binder.getCallingUid(),
                    context.packageName
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                false
            }
        }

        /**
         * 判断权限集合
         * permissions 权限数组
         * return true-表示全部打开了  false-表示有没有打开的
         */
        fun isOpenPermisson(context: Context, permission: Array<String>): Boolean {
            if (permission.isEmpty()) {
                return false
            }
            permission.forEach {
                if (!isOpenPermisson(context, it)) {
                    return false
                }
            }
            return true
        }

        fun isOpenPermisson(permission: IntArray): Boolean {
            if (permission.isEmpty()) {
                return false
            }
            permission.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }

        //权限设置提示框
        fun showPermissions(mContext: Activity, permissionName: String, requestCode: Int, callback: (Boolean) -> Unit) {
            val dialog = android.app.AlertDialog.Builder(mContext).create()
            dialog.setCancelable(false)
            dialog.setTitle("提示")
            dialog.setMessage("需要手动开启${permissionName}权限才能使用")
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { _, _ ->
                dialog.dismiss()
                callback(true)
            }
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确认") { _, _ ->
                dialog.dismiss()
                callback(false)
                gotoPerMissionSetting(mContext, requestCode)
            }
            dialog.show()
        }

        /**
         * 跳转到当前应用对应的设置页面
         * @param context
         */
        fun gotoPerMissionSetting(context: Activity, requestCode: Int) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).run {
                data = Uri.parse("package:${context.packageName}")
                context.startActivityForResult(this, requestCode)
            }
        }

    }

}



