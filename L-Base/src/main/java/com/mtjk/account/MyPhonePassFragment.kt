package com.mtjk.account

import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.account.LoginActivity
import com.mtjk.base.databinding.FragmentPhonePassBinding
import com.mtjk.utils.click

class MyPhonePassFragment : MyBaseFragment<FragmentPhonePassBinding, DefaultViewModel>() {
    override fun initOnCreateView() {

        mBinding.codeLogin.click { (activity as LoginActivity).codeLogin() }

    }
}