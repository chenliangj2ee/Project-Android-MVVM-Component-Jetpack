package com.mentuojiankang.user.activity

import com.mentuojiankang.user.databinding.ActivityHelpBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.BusCode
import com.mtjk.utils.sendSelf
import com.tencent.qcloud.tuikit.tuiconversation.util.IM

/**
 * tag==帮助与反馈
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "帮助与反馈")
class HelpActivity : MyBaseActivity<ActivityHelpBinding, DefaultViewModel>() {
    override fun initCreate() {
        mToolBar.showRight("客服") {
            IM.gotoServiceChat()
        }
    }

    override fun initClick() {
        super.initClick()


    }
}