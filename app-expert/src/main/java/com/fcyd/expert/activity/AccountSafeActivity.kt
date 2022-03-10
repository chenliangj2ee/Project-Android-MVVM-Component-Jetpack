package com.fcyd.expert.activity
import com.fcyd.expert.databinding.ActivityAccountSafeBinding
import com.fcyd.expert.activity.LogOffActivity
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.bean.BeanUser
import com.mtjk.utils.click
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
        super.initClick()
        with(mBinding) {
            mBinding.user= getBeanUser()
            logoff.click { goto(LogOffActivity::class.java) }
        }
    }
}