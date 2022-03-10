package com.tencent.qcloud.tuikit.tuiconversation

import android.util.Log
import com.chenliang.processor.LIMConversation.MyRoutePath
import com.mtjk.base.MyBaseApplication
import com.mtjk.BaseInit

class ConversationApplication : MyBaseApplication() {
    override fun onCreate() {
        super.onCreate()
        Log.i("ConversationApplication", "onCreate-------------------------------------")
        //java调用kotlin要加INSTANCE
        BaseInit.initMyRoute(MyRoutePath)
    }
}