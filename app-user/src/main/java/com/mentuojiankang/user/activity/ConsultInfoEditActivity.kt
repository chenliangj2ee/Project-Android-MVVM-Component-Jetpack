package com.mentuojiankang.user.activity

import com.mentuojiankang.user.databinding.ActivityConsultInfoEditBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.changed
import com.mtjk.utils.click
import com.mtjk.utils.toast
import com.mtjk.view.DialogPicker
import kotlinx.android.synthetic.main.activity_consult_info_edit.*
import kotlinx.android.synthetic.main.activity_personal_info_edit.sex

/**
 * tag==咨询信息
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "咨询信息",mScroll = true)
class ConsultInfoEditActivity :
    MyBaseActivity<ActivityConsultInfoEditBinding, com.mtjk.vm.AccountViewModel>() {

    override fun initCreate() {
    }


    override fun initClick() {
        sex.click { initSex() }
        edit1.changed { num1.text = "${edit1.text.toString().length}/200" }
        edit2.changed { num2.text = "${edit2.text.toString().length}/200"}
    }


    private fun initSex() {
        var items = arrayListOf("男", "女")
        var dialog = DialogPicker()
        dialog.setItems(items)
        dialog.setTitle("选择性别")
        dialog.selected {
            when (it) {
            }

            toast("选择：$it")
        }
        dialog.show(this)
    }


}