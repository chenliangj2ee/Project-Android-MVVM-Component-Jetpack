package com.mtjk.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.chenliang.annotation.ApiModel
import com.chenliang.processor.LBase.MySp
import com.mtjk.BaseInit
import com.mtjk.bean.BeanUser
import com.mtjk.utils.BusCode
import com.mtjk.utils.getBeanUser
import com.mtjk.utils.goto
import com.mtjk.utils.sendSelf
import kotlinx.android.synthetic.main.activity_base_splash.*

abstract class BaseSplashActivity<BINDING : ViewDataBinding, VM : ViewModel> :
    MyBaseActivity<BINDING, VM>() {
    private var delayTime = 1000L;
    open abstract fun initLogin(): Class<*>
    open abstract fun initMain(): Class<*>

    override fun initCreate() {
        intent?.sendSelf(BusCode.SPLASH_IN)
        delayTime = if (BaseInit.icColdStart) 300 else delayTime



        if (ApiModel.dev) {
            des.text = "开发环境"
        }
        if (ApiModel.test) {
            des.text = "测试环境"
        }
        BaseInit.icColdStart = false

        if (!MySp.isAgree()) {
            var dialog = AgreementDialog()
            dialog.click {
                if (it) {
                    MySp.setAgree(true)
                    postDelayed(delayTime) { next() }
                } else {
                    finish()
                }
            }
            dialog.cancelable(false)
            dialog.show(this)

        } else {
            postDelayed(delayTime) { next() }
        }


    }

    fun next() {
        var user = getBeanUser()
        if (user?.isLogin == true) {
            goto(initMain())
        } else {
            goto(initLogin())
        }
        finish()
    }
}