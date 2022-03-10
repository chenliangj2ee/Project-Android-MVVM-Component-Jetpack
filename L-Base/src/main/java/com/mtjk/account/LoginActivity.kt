package com.mtjk.account

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.chenliang.annotation.MyRoute
import com.chenliang.processor.LBase.MySp
import com.mtjk.BaseInit
import com.mtjk.base.*
import com.mtjk.base.databinding.ActivityBaseLoginBinding
import com.mtjk.bean.BeanUser
import com.mtjk.obj.ObjectBusinessId
import com.mtjk.utils.*
import com.mtjk.vm.AccountViewModel
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.helper.UnifyUiConfig
import com.netease.nis.quicklogin.listener.ActivityLifecycleCallbacks
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener
import com.netease.nis.quicklogin.ui.YDQuickLoginActivity
import java.lang.Exception

/**
 * tag==登录
 */
@MyRoute("base/login")
class LoginActivity : MyBaseActivity<ActivityBaseLoginBinding, AccountViewModel>() {

    var userPrivate = ""


    private var toLogin = false
    private val codeFragment = MyCodeFragment()
    private val passFragment = MyPhonePassFragment()
    lateinit var quickLogin: QuickLogin
    override fun initCreate() {
        userPrivate = if (BaseInit.isUserApp) {
            "file:///android_asset/html/user_private.html"
        } else {
            "file:///android_asset/html/user_private_expert.html"
        }
        MySp.setAgree(true)
        codeLogin()
        quickLogin()



    }

    fun codeLogin() {
        replace(R.id.frame_layout, codeFragment)
    }

    fun passLogin() {
        replace(R.id.frame_layout, passFragment)
    }


    /**
     * 一键登录
     */
    private fun quickLogin() {
        quickLogin = QuickLogin.getInstance(
            this,
            if (BaseInit.isUserApp) ObjectBusinessId.C else ObjectBusinessId.B
        )
        quickLogin.setUnifyUiConfig(initUnifyUiConfig())
        quickLogin.prefetchMobileNumber(object : QuickLoginPreMobileListener() {
            override fun onGetMobileNumberSuccess(YDToken: String, mobileNumber: String) {
                quickLogin.onePass(object : QuickLoginTokenListener() {
                    override fun onGetTokenSuccess(YDToken: String?, accessCode: String?) {
                        log("一键登录：onGetTokenSuccess YDToken:${YDToken?.toString()}   accessCode:${accessCode}")
                        if (YDToken != null && accessCode != null) {
                            mViewModel.quickLogin(accessCode, YDToken, MySp.getPushToken())
                                .obs(this@LoginActivity) {
                                    it.y { user -> loginSuccess(user, it.token) }
                                }
                        }

                    }

                    override fun onGetTokenError(YDToken: String?, msg: String?) {
                        quickLogin.quitActivity()
                        log("一键登录：onGetTokenError YDToken:${YDToken?.toString()}  msg:${msg}")
                    }
                })
            }

            override fun onGetMobileNumberError(YDToken: String?, msg: String?) {
                log("一键登录：onGetMobileNumberError YDToken:${YDToken?.toString()}  msg:${msg}")
                quickLogin.quitActivity()
            }
        })
    }

    //登录成功，保存用户登录状态
    private fun loginSuccess(user: BeanUser, token: String) {
        user.token = token
        user.isLogin = true
        user.save()
        if (BaseInit.isUserApp) {
            if (user.nickName.isNullOrEmpty())
                goto("/app/userInfoEdit")
            else
                goto("/app/main")
        } else {
            goto("/app/main")
        }

        postDelayed(1000) {
            quickLogin.quitActivity()
            this@LoginActivity.finish()
        }
    }

    private fun initUnifyUiConfig(): UnifyUiConfig {
        val builder = UnifyUiConfig.Builder()
        builder.setLogoIconName("login_logo")
        builder.setLogoTopYOffset(0)
        builder.setHideNavigation(true)
        builder.setStatusBarDarkColor(true)
        builder.setStatusBarColor(Color.WHITE)
        builder.setMaskNumberSize(21)
        builder.setMaskNumberTopYOffset(0)
        builder.setSloganSize(12)
        builder.setSloganColor(Color.parseColor("#A9ADB2"))
        builder.setSloganTopYOffset(0)
        builder.setLoginBtnTextSize(14)
        builder.setLoginBtnTextColor(R.color.white)
        builder.setLoginBtnWidth(300)
        builder.setLoginBtnTopYOffset(0)
        builder.setLoginBtnText("使用本机号码一键登录")
        builder.setLoginBtnBackgroundRes("base_selector_button_dark")
        builder.setLoginBtnTextColor(Color.WHITE)
        builder.setLoginBtnHeight(44)
        var button = Button(this)
        button.text = "其它手机号登录"
        button.setBackgroundColor(Color.TRANSPARENT)
        button.setTextColor(Color.parseColor("#133C53"))
        builder.addCustomView(button, null, UnifyUiConfig.POSITION_IN_BODY) { _, _ ->
            quickLogin.quitActivity()
            toLogin = true;
        }
        var p = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        p.topMargin = (270).dip2px()
        button.layoutParams = p
        builder.setPrivacyTextStart("同意吱吱心理")
        builder.setPrivacyState(false)
        builder.setProtocolText("《用户协议》")
        builder.setProtocol2Text("《隐私政策》")
        builder.setProtocolLink("file:///android_asset/html/agreement.html")
        builder.setProtocol2Link(userPrivate)
//        R.drawable.base_selector_checkbox_bg
        builder.setPrivacyProtocolColor(R.color.text_default)
        builder.setPrivacyTextMarginLeft(10)
        builder.setPrivacyTextColor(Color.BLACK)
        builder.setCheckedImageName("key_login_checkbox_selected")
        builder.setUnCheckedImageName("key_login_checkbox_default")
        builder.setPrivacyMarginLeft(50)
        builder.setPrivacyMarginRight(50)
        builder.setActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onCreate(p0: Activity?) {
                mHttpEvent = MyHttpEvent(p0!!)
                try {
                    BaseInit.act = p0 as AppCompatActivity?
                }catch (e:Exception){}


            }

            override fun onResume(p0: Activity?) {}

            override fun onStart(p0: Activity?) {}
            override fun onPause(p0: Activity?) {
            }


            override fun onStop(p0: Activity?) {
            }

            override fun onDestroy(p0: Activity?) {
                mHttpEvent.onDestroy()
                if (!toLogin) {
                    this@LoginActivity.finish()
                }
            }
        })
        return builder.build(this)
    }

    //before 2.0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && codeFragment.onKeyDown(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}