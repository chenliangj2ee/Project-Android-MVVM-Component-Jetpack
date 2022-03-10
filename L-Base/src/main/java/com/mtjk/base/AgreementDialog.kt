package com.mtjk.base

import com.mtjk.BaseInit
import com.mtjk.base.databinding.BaseActivityAgreementBinding
import com.mtjk.utils.click
import com.mtjk.utils.goto

class AgreementDialog : MyBaseDialog<BaseActivityAgreementBinding>() {
    var userPrivate = ""
    var func: ((boo: Boolean) -> Unit)? = null
    override fun initCreate() {
        mBinding.cancel.click { func?.let { it1 -> it1(false) } }
        mBinding.confirm.click { func?.let { it1 -> it1(true) } }
        userPrivate = if (BaseInit.isUserApp) {
            "file:///android_asset/html/user_private.html"
        } else {
            "file:///android_asset/html/user_private_expert.html"
        }
    }


    fun click(func: (boo: Boolean) -> Unit) {
        this.func = func
    }

    override fun initClick() {
        mBinding.yhxy.goto(WebViewActivity::class.java, "url", "file:///android_asset/html/agreement.html", "title", "用户协议")
        mBinding.ysxy.goto(WebViewActivity::class.java, "url", userPrivate, "title", "隐私协议")
    }
}