package com.yzw.permissiongranted

import java.io.Serializable

/**
 * Create by yinzhengwei on 2018/12/6
 * @Function
 */
interface PermissionGrantedCallback : Serializable {
    fun granted()
    fun denied()
}