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
import kotlinx.android.synthetic.main.base_activity_webview.*

/**
 * author:chenliang
 * date:2021/12/9
 */
@MyClass(mToolbarTitle = " ", mRefresh = false)
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

    lateinit var loadDialog:Dialog
    override fun initCreate() {
        loadDialog=loading("加载中...")
        with(mBinding) {

            if (!title.isNullOrEmpty())
                mToolBar.setTitle(title)

            webview.settings.javaScriptEnabled = true
            webview.settings.domStorageEnabled = true

//            webview.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK;
            webview.webViewClient = wec(userId, this@WebViewActivity)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webview.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webview.webChromeClient = wecc()

            if (imgurl.isNotEmpty()) {
                webview.loadDataWithBaseURL(
                    null,
                    "<body style='margin:0;padding:0'><img  src=$imgurl width='100%'></body>",
                    "text/html",
                    "charset=UTF-8",
                    null
                );
            }

            if (content.isNotEmpty()) {
                webview.loadDataWithBaseURL(null, content, "text/html", "charset=UTF-8", null);
            }

            if (url.isNotEmpty()) {
                log("webview url:$url")
                webview.loadUrl(url)
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


class wec(var userId: String, webview: WebViewActivity) : WebViewClient() {

    var webview = webview

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
    }


    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
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

        if (webview.articleId.isNotEmpty()) {//文章
            view?.loadJs("bridge", serverUrl, user!!.token, webview.articleId)
        } else if (webview.test) {//测评
            view?.loadJs("signIn", serverUrl!!, user!!.token, webview!!.testId!!)
        } else if (webview.testResultId != null && webview.testResultId != "") {//测评报告
            view?.loadJs("report", serverUrl!!, user!!.token, webview!!.testResultId!!)
        } else {//咨询师详情
            user?.let { view?.loadJs("signIn", it.token, userId) }
        }

        webview.loadDialog.dismiss()
        webview.mRefresh.setEnableRefresh(false)
        webview.stopRefresh()
        webview.error.show(false)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        webview.loadDialog.dismiss()
        webview.error.show(true)
        webview.mRefresh.setEnableRefresh(true)
        webview.stopRefresh()
        super.onReceivedError(view, request, error)

    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
        super.onReceivedSslError(view, handler, error)
    }


}


class wecc : WebChromeClient() {


}