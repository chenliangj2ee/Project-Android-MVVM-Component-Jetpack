package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.databinding.FragmentWebviewBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment

/**
 * tag==详情/课程
 * author:chenliang
 * date:2021/11/3
 */
class DetailsWebViewFragment(str: String, type: Int) :
    MyBaseFragment<FragmentWebviewBinding, DefaultViewModel>() {
    var str = str
    var type = type
    override fun initOnCreateView() {
        with(mBinding) {
            webview.load(str,type)
        }

    }
}