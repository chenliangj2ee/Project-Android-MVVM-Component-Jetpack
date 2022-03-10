package com.mtjk.act

import android.os.Bundle
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.databinding.ActivityTextBinding
import kotlinx.android.synthetic.main.activity_text.*

/**
 * tag==专家方案
 * author:chenliang
 * date:2021/12/22
 */
@MyClass(mToolbarTitle = "专家方案",mScroll = true)
class TextActivity : MyBaseActivity<ActivityTextBinding, DefaultViewModel>() {
    @MyField
    var text = ""
    override fun initCreate() {
        textview.text = text

    }
}
