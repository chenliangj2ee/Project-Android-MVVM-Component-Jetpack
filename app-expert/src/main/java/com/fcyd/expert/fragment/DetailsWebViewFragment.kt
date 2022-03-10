package com.fcyd.expert.fragment

import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.fcyd.expert.databinding.FragmentWebviewBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.bean.BeanUser
import com.mtjk.utils.getBeanUser

/**
 * tag==详情/课程
 * author:chenliang
 * date:2021/11/3
 */

@MyClass(mRefresh = true)
class DetailsWebViewFragment : MyBaseFragment<FragmentWebviewBinding, DefaultViewModel>() {
    override fun initOnCreateView() {
        with(mBinding) {
            webview.settings.javaScriptEnabled = true
            webview.settings.domStorageEnabled = true
            webview.webViewClient = wec()
            webview.webChromeClient = wecc()
            webview.loadUrl("http://172.25.20.177:8080/")

        }

    }

    override fun refresh() {
        with(mBinding) {
            webview.settings.javaScriptEnabled = true
            webview.webViewClient = wec()
            webview.webChromeClient = wecc()
            webview.loadUrl("https://detail.fangcunyuedong.com/")
        }
        stopRefresh()
    }
}


class wec() : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        var user =  getBeanUser()
        var method = "javascript:signIn('" + user?.token + "','" + "" + "')"
        view?.loadUrl(method)

    }

}

class wecc : WebChromeClient() {


}
