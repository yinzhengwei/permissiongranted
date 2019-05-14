package com.yzw.permissiongranted

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * Create by yinzhengwei on 2018/11/27
 * @Function
 */

object PermissionUtils {

    var notify: PermissionGrantedCallback? = null

    fun permissionCheck(
        context: Activity,
        permission: Array<String>,
        permissionName: String,
        permissionGrantedCallback: PermissionGrantedCallback?
    ) {
        permissionCheck(context, permission, permissionName, true, permissionGrantedCallback)
    }

    /**
     * @param context 上下文
     * @param permissionName 权限集合名称（例如：相机）
     * @param permission 权限集合
     * @param isAllWaysRequest 是否强制
     * @param permissionGrantedCallback 权限申请的回调
     */

    fun permissionCheck(
        context: Activity,
        permission: Array<String>,
        permissionName: String,
        isAllWaysRequest: Boolean,
        permissionGrantedCallback: PermissionGrantedCallback?
    ) {
        notify = permissionGrantedCallback

        //如果传进来的权限都打开了就直接回调成功的结果，不需要跳转了
        if (isOpenPermisson(context, permission)) {
            notify?.granted()
        } else {
            context.startActivity(Intent(context, PermissionFetchUI::class.java).apply {
                putExtra("psn", permission)
                putExtra("permissionName", permissionName)
                putExtra("isAllWaysRequest", isAllWaysRequest)
            })
        }
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
         * 检查权限是否获取（android6.0及以上系统可能默认关闭权限，且没提示）
         *
         * 一般android6以下会在安装时自动获取权限,但在小米机上，可能通过用户权限管理更改权限,checkSelfPermission会始终是true，
         * targetSdkVersion<23时 即便运行在android6及以上设备 ContextWrapper.checkSelfPermission和Context.checkSelfPermission失效
         * 返回值始终为PERMISSION_GRANTED,此时必须使用PermissionChecker.checkSelfPermission
         */
        return if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED &&
            context.packageManager.checkPermission(permission, context.packageName) == PackageManager.PERMISSION_GRANTED) {
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
        val dialog = android.app.AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create()
        dialog.setCancelable(false)
        dialog.setTitle("提示")
        dialog.setMessage("需要手动开启${permissionName}权限才能使用")
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { _, _ ->
            dialog.dismiss()
            callback(true)
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确认") { _, _ ->
            dialog.dismiss()
            gotoPerMissionSetting(mContext, requestCode)
            callback(false)
        }
        dialog.show()
    }

    /**
     * 跳转到当前应用对应的设置页面
     * @param context
     */
    fun gotoPerMissionSetting(context: Activity, requestCode: Int) {
        var intent = Intent()
        try {
            /**
             * 获取手机厂商
             */
            when (android.os.Build.BRAND.toLowerCase()) {
                "vivo" -> intent = context.packageManager.getLaunchIntentForPackage("com.iqoo.secure") ?: (Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    intent.data = Uri.parse("package:${context.packageName}")
                })
                "oppo" -> intent = context.packageManager.getLaunchIntentForPackage("com.oppo.safe") ?: (Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    intent.data = Uri.parse("package:${context.packageName}")
                })
                "huawei" -> {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
                }
                "meizu" -> {
                    intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.putExtra("packageName", context.packageName)
                }
                "xiaomi" -> {
                    intent.action = "miui.intent.action.APP_PERM_EDITOR"
                    when (getMiuiVersion().toLowerCase()) {
                        "v6", "v7" -> {
                            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                            intent.putExtra("extra_pkgname", context.packageName)
                        }
                        "v8", "v9" -> {
                            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                            intent.putExtra("extra_pkgname", context.packageName)
                        }
                        else -> {
                            intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:${context.packageName}")
                        }
                    }
                }
                else -> {
                    intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${context.packageName}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
        }
        context.startActivityForResult(intent, requestCode)
    }

    private fun getMiuiVersion(): String {
        val propName = "ro.miui.ui.version.name"
        var line = ""
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        } finally {
            try {
                input!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return line
    }

}



