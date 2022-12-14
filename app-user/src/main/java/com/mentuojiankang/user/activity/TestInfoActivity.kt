package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanTest
import com.mentuojiankang.user.databinding.ActivityTestInfoBinding
import com.mtjk.MyPath
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.WebViewActivity
import com.mtjk.utils.*
import com.mtjk.view.MyWebView

/**
 * tag==测评详情
 * author:chenliang
 * date:2022/3/5
 */
class TestInfoActivity:MyBaseActivity<ActivityTestInfoBinding,DefaultViewModel>() {
    @MyField
    lateinit var test:BeanTest
    override fun initCreate() {
        mToolBar.setTitle(test.scaleName)
        mBinding.des.setHtmlText(test.briefIntro)
        mBinding.webview.loadError {
            log("加载失败.....")
        }
        mBinding.webview.loadFinish {
            log("加载完成.....")
        }
        mBinding.webview.load(test.briefIntro,MyWebView.Type.CONTENT)
        mBinding.start.click { gotoStart() }

    }

    private fun gotoStart(){
        gotoFinish(WebViewActivity::class.java,"url",MyPath.test,"title",test.scaleName,"test",true,"testId",test.id)
    }
}