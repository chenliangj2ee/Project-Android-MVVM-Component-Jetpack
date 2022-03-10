package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityExpertAuthSubmitSuccessBinding
import com.fcyd.expert.databinding.ActivityExpertIdUploadBinding
import com.mtjk.base.MyBaseActivity
import com.mtjk.vm.AccountViewModel
import kotlinx.android.synthetic.main.layout_header_expert_auth.*

/**
 * tag==专家认证/提交成功提示
 * author:chenliang
 * date:2021/11/3
 */
class ExpertAuthSubmitSuccessActivity : MyBaseActivity<ActivityExpertAuthSubmitSuccessBinding, AccountViewModel>() {
    override fun initCreate() {
        step1.isChecked = true
        step2.isChecked = true
        fullscreenTransparentBar(true)
    }

    override fun initClick() {


    }

    private fun updateUserInfo() {

    }

    private fun check() {
    }


}