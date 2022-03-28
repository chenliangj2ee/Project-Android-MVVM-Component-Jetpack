package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityHelpBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.tencent.qcloud.tuikit.tuiconversation.util.IM

/**
 * tag==帮助与反馈
 */
@MyClass("帮助与反馈")
class HelpAndActivity : MyBaseActivity<ActivityHelpBinding, DefaultViewModel>() {
    override fun initCreate() {
        mToolBar.showRight("客服") {
            IM.gotoServiceChat()
        }
    }

}