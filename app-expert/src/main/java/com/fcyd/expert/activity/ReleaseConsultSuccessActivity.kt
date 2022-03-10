package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityReleaseConsultSuccessBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.click
import com.mtjk.utils.gotoFinish
import kotlinx.android.synthetic.main.activity_release_consult.*
import kotlinx.android.synthetic.main.activity_release_consult_notify.*

/**
 * tag==发布咨询成功
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "  ")
class ReleaseConsultSuccessActivity :
    MyBaseActivity<ActivityReleaseConsultSuccessBinding, com.mtjk.vm.AccountViewModel>() {
    override fun initCreate() {
    }

    override fun initClick() {
        look.click { gotoFinish(ConsultManagerActivity::class.java) }
        release.click { gotoFinish(ReleaseConsultActivity::class.java) }
    }

}