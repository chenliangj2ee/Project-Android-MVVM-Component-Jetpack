package com.mtjk.account

import android.view.KeyEvent
import android.view.View
import com.chenliang.processor.LBase.MySp
import com.mtjk.BaseInit
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.WebViewActivity
import com.mtjk.base.databinding.FragmentPhoneCodeBinding
import com.mtjk.bean.MyLoginModel
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.mtjk.vm.AccountViewModel
import kotlinx.android.synthetic.main.fragment_phone_code.*
import kotlinx.android.synthetic.main.fragment_phone_code.view.*
import kotlinx.android.synthetic.main.layout_agreement.*
import kotlinx.android.synthetic.main.layout_agreement.view.*

/**
 * tag==验证码登录
 */
class MyCodeFragment : MyBaseFragment<FragmentPhoneCodeBinding, AccountViewModel>() {
    var userPrivate = ""
    var loginModel = MyLoginModel()
    var time = 60;

    override fun initOnCreateView() {
        var user = getBeanUser()

        user?.phone?.also { loginModel.phone = it }
        mBinding.model = loginModel;

//        user?.let { log("userSig:"+it.userSig) }
        userPrivate = if (BaseInit.isUserApp) {
            "file:///android_asset/html/user_private.html"
        } else {
            "file:///android_asset/html/user_private_expert.html"
        }
    }

    override fun initClick() {
        mBinding.getCode.click {
            if (!agreement.isChecked) {
                toast("请同意登录协议")
                return@click
            }
            getCode()
        }
        mBinding.phone.changed {
//            mBinding.phone.setText(it.insert(" ", 3, 7))
            mBinding.getCode.isEnabled =
                it.trim().length == 11
            if (it.trim().length == 11 && mRootView.agreement?.isChecked == false) {
                hideSoftInput(mBinding.getCode)
            }
        }
        mBinding.code.changed { codeLogin() }
        mBinding.time.click { getCode() }
        mRootView.agreement.click {
            mBinding.getCode.isEnabled = phone.text.length == 11 && agreement.isChecked
        }

        mRootView.noMessage.goto(NoMessageActivity::class.java)
//        mRootView.user_agreement.goto(TextActivity::class.java,"text",com.mtjk.base.R.string.text_user_xy,"title","用户使用协议")

        mRootView.yhxy.goto(
            WebViewActivity::class.java,
            "url",
            "file:///android_asset/html/agreement.html",
            "title",
            "用户协议"
        )
        mRootView.ysxy.goto(WebViewActivity::class.java, "url", userPrivate, "title", "隐私协议")


    }

    /**
     * 获取验证码
     */
    private fun getCode() {
        if (loginModel.phone.check(MyCheck.empty, "请输入手机号", MyCheck.mobilePhone, "手机号格式不正确"))
            return

        mViewModel.getPhoneCode(loginModel.phone.replace(" ", "")).obs(this) {
            it.y { showLogin(it) }
        }
    }


    /**
     * 展示验证码录入布局
     */
    private fun showLogin(key: String) {
        mBinding.model = loginModel;
        loginModel.key = key;
        timeTask()
        mBinding.codeView.show(true)
        mBinding.phoneView.show(false)
        mBinding.code.requestFocus()
    }


    /**
     * 验证码倒计时
     */
    private fun timeTask() {
        mBinding.time.isEnabled = time == 0
        if (time == 0) {
            mBinding.time.text = "重新发送"
            time = 60
        } else {
            mBinding.time.text = "${time--}秒后重发"
            postDelayed(1000) { timeTask() }
        }

    }


    /**
     * 验证码登录
     */
    private fun codeLogin() {
        if (loginModel.code.length < 6)
            return
        var phone = loginModel.phone.replace(" ", "")
        mViewModel.codeLogin(phone, loginModel.key, loginModel.code, MySp.getPushToken())
            .obs(this) { it ->
                it.y { user -> loginSuccess(user, it.token) }
                it.n { _ ->

                    postDelayed(500) {
                        if (it.code == 1) {
                            mBinding.code.setText("")
                        }
                    }

                }
            }
    }

    //登录成功，保存用户登录状态
    private fun loginSuccess(user: BeanUser, token: String) {
        log("登录userId:" + user.userId)
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


        activity?.finish()
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.codeView.visibility == View.VISIBLE) {
                mBinding.codeView.show(false)
                mBinding.phoneView.show(true)
                return true
            }
        }
        return false
    }

}