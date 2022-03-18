package com.mentuojiankang.user.activity

import com.mentuojiankang.user.R
import com.mentuojiankang.user.databinding.ActivityFragmentBinding
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.goto

/**
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "咨询信息")
class FragmentActivity :
    MyBaseActivity<ActivityFragmentBinding, com.mtjk.vm.AccountViewModel>() {

    @MyField
    var fragment = "";

    @MyField
    var title = ""

    override fun initCreate() {
        setToolbarTitle(title)
        replace(R.id.frame, goto(fragment))
    }


}