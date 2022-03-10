package com.mtjk.utils

import android.app.Activity
import com.mtjk.account.LoginActivity
import gorden.rxbus2.RxBus
import gorden.rxbus2.Subscribe

/**
 * 退出
 */
class MyExitEvent() {

    var act:Activity?=null

    @Subscribe(code = BusCode.EXIT)
    fun exit() {
        if(act==null)
            return
        log("exit....................${act!!::class.java}")
        if (act!!::class.java != LoginActivity::class.java) {
            act?.finish()
        }
        act?.finish()
    }

    fun register(activity: Activity) {
//        log("register....................${activity::class.java}")
        act=activity
        RxBus.get().register(this)
    }

    fun unRegister() {
//        log("unRegister....................${act!!::class.java}")
        RxBus.get().unRegister(this)
    }
}