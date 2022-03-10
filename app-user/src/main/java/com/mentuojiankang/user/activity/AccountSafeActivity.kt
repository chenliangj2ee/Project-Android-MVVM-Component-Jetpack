package com.mentuojiankang.user.activity

import com.mentuojiankang.user.databinding.ActivityAccountSafeBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.bean.BeanUser
import com.mtjk.utils.getBeanUser
import com.mtjk.utils.goto

/**
 * tag==账号与安全
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "账号与安全")
class AccountSafeActivity : MyBaseActivity<ActivityAccountSafeBinding, DefaultViewModel>() {
    override fun initCreate() {
    }

    override fun initClick() {
        mBinding.user = getBeanUser()
        mBinding.logoff.goto(LogOffActivity::class.java)
    }
}