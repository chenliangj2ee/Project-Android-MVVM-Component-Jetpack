package com.mentuojiankang.user.dialog

import com.mentuojiankang.user.databinding.DialogOpenNotiBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.utils.click

/**
 * tag==打开通知
 * author:chenliang
 * date:2021/11/4
 */
@MyClass(mDialogTransparent = true)
class CourseOpenNotiDialog : MyBaseDialog<DialogOpenNotiBinding>() {
    override fun initCreate() {
        with(mBinding) {
            mBinding.close.click { dismiss() }
        }
    }
}