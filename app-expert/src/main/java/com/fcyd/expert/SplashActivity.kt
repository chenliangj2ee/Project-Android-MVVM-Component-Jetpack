package com.fcyd.expert

import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.BaseSplashActivity
import com.mtjk.account.LoginActivity
import com.mtjk.base.databinding.ActivityBaseSplashBinding

/**
 * tag==启动页
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mFullScreen = true)
class SplashActivity : BaseSplashActivity<ActivityBaseSplashBinding, DefaultViewModel>() {


    override fun initLogin() = LoginActivity::class.java

    override fun initMain() = MainActivity::class.java

    override fun initClick() {
        super.initClick()
    }
}