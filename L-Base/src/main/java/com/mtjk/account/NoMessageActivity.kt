package com.mtjk.account

import android.os.Bundle
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.databinding.ActivityNoMessageBinding
import com.mtjk.vm.AccountViewModel

@MyClass(mToolbarTitle = "收不到验证码")
class NoMessageActivity : MyBaseActivity<ActivityNoMessageBinding, AccountViewModel>() {
    override fun initCreate() {

    }


}