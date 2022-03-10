package com.mtjk.fragment

import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.databinding.FragmentImageBinding

/**
 * author:chenliang
 * date:2021/12/22
 */
class ImageFragment(url: String) : MyBaseFragment<FragmentImageBinding, DefaultViewModel>() {
    var url = url
    override fun initOnCreateView() {
        mBinding.path = url
    }
}