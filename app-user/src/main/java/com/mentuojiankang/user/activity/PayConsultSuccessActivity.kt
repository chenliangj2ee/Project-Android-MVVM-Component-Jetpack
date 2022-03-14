package com.mentuojiankang.user.activity

import com.mentuojiankang.user.databinding.ActivityPayConsultSuccessBinding
import com.mentuojiankang.user.vm.ConsultViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.click
import com.mtjk.utils.goto

@MyClass(mToolbarTitle = "")
class PayConsultSuccessActivity : MyBaseActivity<ActivityPayConsultSuccessBinding, ConsultViewModel>() {

    @MyField
    var orderItemId: String = ""

    override fun initCreate() {
        mBinding.write.click { toConsultEdit() }
    }

    private fun toConsultEdit() {
        goto(ConsultInfoEditActivity::class.java, "orderItemId", orderItemId)
        finish()
    }
}