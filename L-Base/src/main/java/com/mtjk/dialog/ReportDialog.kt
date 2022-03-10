package com.mtjk.dialog

import android.view.Gravity
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.databinding.DialogReportBinding
import com.mtjk.base.databinding.ItemReportBinding
import com.mtjk.base.obs
import com.mtjk.bean.BeanString
import com.mtjk.utils.*
import com.mtjk.vm.AppModelModel
import kotlinx.android.synthetic.main.dialog_report.*

/**
 * tag==投诉
 * author:chenliang
 * date:2021/11/30
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class ReportDialog(roomId: Int) : MyBaseDialog<DialogReportBinding>() {
    var roomId = roomId
    var arrays =
        arrayListOf<String>("侮辱谩骂", "行为举止不当", "引导私下交易", "色情低俗", "出售违禁品", "涉嫌违法犯罪", "侵犯知识产权", "其他")


    var content = ""

    var select: BeanString? = null

    override fun initCreate() {
        mBinding.refresh.disable()
        mBinding.refresh.bindData<BeanString> {
            with(it.binding as ItemReportBinding) {
                data = it
                root.click { _ ->
                    mBinding.refresh.selected(it.itemPosition)
                    select = it
                    content = it.str
                    mBinding.editarea.show(it.str.contains("其他"))
                    checkEnable()
                }
            }
        }
        var datas = arrayListOf<BeanString>()
        arrays.forEach {
            var bean = BeanString()
            bean.str = it
            datas.add(bean)
        }
        mBinding.refresh.addDatas(datas)
        mBinding.close.click { dismiss() }
        mBinding.editarea.edit.changed {
            content = it
            checkEnable()
        }
        mBinding.submit.click {
            if (select != null) {
                content = select?.str.toString()
            }

            initVM(AppModelModel::class.java).report(roomId, content).obs(this) {
                it.y { toast("投诉成功") }
                it.n { toast("投诉成功") }
                dismiss()
            }
        }
    }


    private fun checkEnable() {
        submit.isEnabled = false
        if (select?.str != "其他") {
            submit.isEnabled = true
            select = null
        }
        if (select?.str == "其他" && content.isNotEmpty()) {
            submit.isEnabled = true
        }
    }

}