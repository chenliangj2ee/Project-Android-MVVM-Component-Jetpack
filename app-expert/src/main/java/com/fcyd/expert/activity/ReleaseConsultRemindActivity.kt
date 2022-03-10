package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityReleaseConsultNotifyBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.gotoFinish
import kotlinx.android.synthetic.main.activity_release_consult_notify.*

/**
 * tag==工作室装修/发布未通过提醒
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "工作室装修")
class ReleaseConsultRemindActivity :
    MyBaseActivity<ActivityReleaseConsultNotifyBinding, com.mtjk.vm.AccountViewModel>() {
    override fun initCreate() {
//        mToolBar.showRight("帮助") {}
    }

    override fun initClick() {
        release.click { gotoFinish(ReleaseConsultActivity::class.java) }
    }

}