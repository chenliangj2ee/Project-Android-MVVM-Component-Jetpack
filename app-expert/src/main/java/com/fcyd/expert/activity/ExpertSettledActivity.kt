package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityExpertSettledBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.BusCode
import com.mtjk.utils.goto
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_expert_settled.*

/**
 * tag==专家入驻/第一步
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "专家入驻")
class ExpertSettledActivity : MyBaseActivity<ActivityExpertSettledBinding, DefaultViewModel>() {
    override fun initCreate() {
    }

    override fun initClick() {
        super.initClick()
        start.goto(ExpertAuthActivity::class.java)
    }

    @Subscribe(code = BusCode.AUTH_FINISH)
    fun authFinish() {
        finish()
    }
}