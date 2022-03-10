package com.fcyd.expert

import com.chenliang.processor.appexpert.MyRoutePath
import com.mtjk.BaseInit
import com.mtjk.base.MyBaseApplication

class ExpertApplication : MyBaseApplication() {
    override fun onCreate() {
        super.onCreate()
        BaseInit.registerApi(ApiService::class.java)
        BaseInit.initMyRoute(MyRoutePath)
    }


}