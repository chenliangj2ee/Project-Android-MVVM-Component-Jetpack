package com.mentuojiankang.user

import com.mentuojiankang.user.vm.AppViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.BaseSplashActivity
import com.mtjk.account.LoginActivity
import com.mtjk.base.databinding.ActivityBaseSplashBinding
import com.mtjk.base.obs

/**
 * tag==启动页
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mFullScreen = true)
class SplashActivity : BaseSplashActivity<ActivityBaseSplashBinding, AppViewModel>() {

    override fun initLogin() = LoginActivity::class.java

    override fun initMain() = MainActivity::class.java

}