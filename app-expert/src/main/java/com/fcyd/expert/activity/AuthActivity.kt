package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityAuthBinding
import com.fcyd.expert.vm.StudioViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.BusCode
import com.mtjk.utils.goto
import com.mtjk.utils.initVM
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_auth.*

/**
 * tag==入驻认证
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "入驻认证")
class AuthActivity : MyBaseActivity<ActivityAuthBinding, DefaultViewModel>() {
    override fun initCreate() {

    }

    override fun initClick() {
        super.initClick()
        with(mBinding) {
            goEdit.goto(StudioMediaEditActivity::class.java)
        }

        initStudio()
    }

    private fun initStudio() {
        initVM(StudioViewModel::class.java).getStudioInfo().obs(this) {
            it.y {
                when (it.verify) {
                    0 -> {
                        studio_status.text = "请完成工作室信息填写"
                    }
                    1 -> {
                        studio_status.text = "待审核"
                    }
                    2 -> {
                        studio_status.text = "审核通过"
                    }
                    3 -> {
                        studio_status.text = "审核不通过"
                    }
                }
            }
        }
    }

    @Subscribe(code = BusCode.UPDATE_EXPERT_INFO)
    fun updateUserInfo(){
        finish()
    }
}