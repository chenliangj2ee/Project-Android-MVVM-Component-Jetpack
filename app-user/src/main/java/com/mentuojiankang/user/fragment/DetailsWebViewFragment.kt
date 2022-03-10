package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.databinding.FragmentWebviewBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.WebViewActivity
import com.mtjk.utils.goto

/**
 * tag==详情/课程
 * author:chenliang
 * date:2021/11/3
 */
class DetailsWebViewFragment(htmlurl: String, imgurl: String) :
    MyBaseFragment<FragmentWebviewBinding, DefaultViewModel>() {
    var htmlurl = htmlurl
    var imgurl = imgurl
    override fun initOnCreateView() {
        with(mBinding) {
            if (imgurl.isNotEmpty()) {
                webview.loadDataWithBaseURL(
                    null,
                    "<body style='margin:0;padding:0'><img  src=$imgurl width='100%'></body>",
                    "text/html",
                    "charset=UTF-8",
                    null
                );
            }

            if (htmlurl.isNotEmpty()) {
                webview.loadUrl(htmlurl)
            }
            //                if (url.endsWith(".jpg")||url.endsWith(".jpg")){
//                    webview.loadDataWithBaseURL(null, "<img  src="+url+" width='100%'>", "text/html", "charset=UTF-8", null);
//                }else{
//                    webview.loadUrl(url)
//                }


        }

    }
}