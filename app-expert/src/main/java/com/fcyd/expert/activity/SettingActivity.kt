package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivitySettingBinding
import com.mtjk.BaseInit
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.WebViewActivity
import com.mtjk.utils.click
import com.mtjk.utils.dialog
import com.mtjk.utils.goto
/**
 * tag==设置
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "设置")
class SettingActivity : MyBaseActivity<ActivitySettingBinding, DefaultViewModel>() {

    var userPrivate = ""


    override fun initCreate() {
        userPrivate = if (BaseInit.isUserApp) {
            "file:///android_asset/html/user_private.html"
        } else {
            "file:///android_asset/html/user_private_expert.html"
        }
    }

    override fun initClick() {
        super.initClick()
        with(mBinding) {
            accountSafe.click { goto(AccountSafeActivity::class.java) }
            exit.click { dialog("是否退出登录?").y { BaseInit.exit() }.show(this@SettingActivity) }
            upgrade.click { checkUpgrade(true) }
            about.goto(WebViewActivity::class.java, "url", "file:///android_asset/html/expert_about.html", "title", "关于吱吱")
            userXy.goto(WebViewActivity::class.java, "url", "file:///android_asset/html/agreement.html", "title", "用户协议")
            userYs.goto(WebViewActivity::class.java, "url",userPrivate, "title", "隐私协议")
        }
    }
}