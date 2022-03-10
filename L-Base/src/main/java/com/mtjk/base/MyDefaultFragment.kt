package com.mtjk.base

import com.chenliang.annotation.MyRoute
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.databinding.BaseFragmentDefaultBinding

/**
 *
 * @Project: MVVM-Component
 * @Package: com.chenliang.baselibrary.base
 * @author: chenliang
 * @date: 2021/07/27
 */
@MyRoute(mPath="base/defautFragment")
class MyDefaultFragment : MyBaseFragment<BaseFragmentDefaultBinding, DefaultViewModel>() {
    override fun initOnCreateView() {
    }
}