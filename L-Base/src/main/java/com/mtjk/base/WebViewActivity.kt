package com.mtjk.base

import android.app.Dialog
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import com.chenliang.annotation.ApiModel
import com.mtjk.MyPath
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.databinding.BaseActivityWebviewBinding
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.mtjk.view.MyWebView
import kotlinx.android.synthetic.main.base_activity_webview.*

/**
 * author:chenliang
 * date:2021/12/9
 */
@MyClass(mToolbarTitle = " ", mRefresh = true)
class WebViewActivity : MyBaseActivity<BaseActivityWebviewBinding, DefaultViewModel>() {

    @MyField
    var url = ""

    @MyField
    var imgurl = ""

    @MyField
    var content = "";

    @MyField
    var title = ""

    @MyField
    var userId = ""

    @MyField
    var articleId = ""


    //测评
    @MyField
    var test = false

    @MyField
    var testId: String? = null

    //测评


    @MyField
    var testResultId: String? = null

    lateinit var loadDialog: Dialog
    override fun initCreate() {
        loadDialog = loading("加载中...")
        loadDialog.setCancelable(true)
        with(mBinding) {

            if (!title.isNullOrEmpty())
                mToolBar.setTitle(title)

            /**
             * 加载失败
             */
            webview.loadError {
                loadDialog.dismiss()
                error.show(true)
                mRefresh.setEnableRefresh(true)
                stopRefresh()
            }

            /**
             * 加载成功
             */
            webview.loadFinish {
                var user = getBeanUser()
                var serverUrl = ""

                if (ApiModel.dev) {
                    serverUrl = "http://172.11.200.3:9200"
                }
                if (ApiModel.test) {
                    serverUrl = "http://api.fangcunyuedong.cn"
                }
                if (ApiModel.release) {
                    serverUrl = "https://api.fangcunyuedong.com"
                }

                if (articleId.isNotEmpty()) {//文章
                    webview?.loadJs("bridge", serverUrl, user!!.token, articleId)
                } else if (test) {//测评
                    webview?.loadJs("signIn", serverUrl!!, user!!.token, testId!!)
                } else if (testResultId != null && testResultId != "") {//测评报告
                    webview?.loadJs("report", serverUrl!!, user!!.token, testResultId!!)
                } else {//咨询师详情
                    user?.let { webview?.loadJs("signIn", it.token, userId) }
                }

                loadDialog.dismiss()
                mRefresh.setEnableRefresh(false)
                stopRefresh()
                mBinding.error.show(false)
                return@loadFinish
            }


            if (imgurl.isNotEmpty()) {
                mRefresh.setEnableRefresh(false)
                webview.load(imgurl, MyWebView.Type.IMAGE)
            }

            if (content.isNotEmpty()) {
                mRefresh.setEnableRefresh(false)
                webview.load(content, MyWebView.Type.CONTENT)
            }

            if (url.isNotEmpty()) {
                webview.load(url, MyWebView.Type.PATH)
            }

        }
    }


    override fun onBackPressed() {
        if (test) {
            dialog("是否退出测评？").y { finish() }.show(this)
            return
        }

        super.onBackPressed()
    }

    override fun refresh() {
        initCreate()
    }


}

