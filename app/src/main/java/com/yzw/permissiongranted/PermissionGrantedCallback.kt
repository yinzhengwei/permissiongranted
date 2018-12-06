package com.yzw.permissiongranted

/**
 * Create by yinzhengwei on 2018/12/6
 * @Function
 */
object PermissionGrantedCallback {
    var granted: (() -> Unit)? = null
    var denied: (() -> Unit)? = null
}