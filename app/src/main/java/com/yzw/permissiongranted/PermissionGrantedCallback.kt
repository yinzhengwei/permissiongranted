package com.yzw.permissiongranted

/**
 * Create by yinzhengwei on 2018/12/6
 * @Function
 */
interface PermissionGrantedCallback {
    //同意
    fun granted()
    //拒绝
    fun denied()
}
