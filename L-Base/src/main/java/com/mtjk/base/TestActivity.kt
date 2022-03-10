package com.mtjk.base

import android.os.Bundle
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.databinding.ActivityTestBinding
import com.mtjk.utils.click
import com.mtjk.utils.toast

/**
 * author:chenliang
 * date:2021/12/9
 */
@MyClass(mToolbarTitle = "统一样式表",mScroll = true)
class TestActivity : MyBaseActivity<ActivityTestBinding, DefaultViewModel>() {
    override fun initCreate() {
        with(mBinding) {
            button1.click { toast("ddd") }
        }
    }


}