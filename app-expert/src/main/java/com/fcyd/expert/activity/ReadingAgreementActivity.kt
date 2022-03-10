package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityReadingAgreementBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.BusCode
import com.mtjk.utils.click
import com.mtjk.utils.goto
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_reading_agreement.*

/**
 * tag==阅读协议
 * author:chenliang
 * date:2021/11/3
 */

class ReadingAgreementActivity :
    MyBaseActivity<ActivityReadingAgreementBinding, DefaultViewModel>() {
    override fun initCreate() {
        webview.loadUrl("file:///android_asset/html/expert_inapp.html")
        fullscreenTransparentBar(true)
    }

    override fun initClick() {
        super.initClick()
        no.click { finish() }
        yes.goto(ExpertAuthActivity::class.java)
    }
    @Subscribe(code = BusCode.AUTH_FINISH)
    fun authFinish() {
        finish()
    }
}