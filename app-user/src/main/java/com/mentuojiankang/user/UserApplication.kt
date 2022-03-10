package com.mentuojiankang.user

import com.chenliang.processor.appuser.MyRoutePath
import com.mtjk.BaseInit
import com.mtjk.base.MyBaseApplication
import com.mtjk.utils.log


class UserApplication : MyBaseApplication() {

    override fun onCreate() {
        super.onCreate()
        log("用户端 UserApplication onCreate..... ")
        BaseInit.registerApi(ApiService::class.java)
        BaseInit.initMyRoute(MyRoutePath)
    }


}