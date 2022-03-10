package com.fcyd.expert.dialog

import android.view.Gravity
import com.fcyd.expert.bean.BeanQualification
import com.fcyd.expert.databinding.DialogQualificationBinding
import com.fcyd.expert.databinding.ItemQualificationSpinnerBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.utils.click
import com.mtjk.utils.show

/**
 * author:chenliang
 * date:2021/12/6
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DialogQualificationSpinner : MyBaseDialog<DialogQualificationBinding>() {

    lateinit var yesFun: (name: String) -> Unit

    var arrays = arrayListOf<String>(
        "国家三级心理咨询师",
        "国家二级心理咨询师",
        "初级心理治疗师",
        "中级心理治疗师",
        "精神科医师",
        "注册系统注册心理师",
        "注册督导师",
        "其他"
    )
    var dataArrays = ArrayList<BeanQualification>()
    override fun initCreate() {
        for (item in arrays) {
            dataArrays.add(BeanQualification(item))
        }

        with(mBinding) {
            refresh.disable()
            refresh.bindData<BeanQualification> {

                with(it.binding as ItemQualificationSpinnerBinding) {
                    data = it
                    check.click { view ->
                        for (item in dataArrays) {
                            item.selected = false
                        }
                        dataArrays[it.itemPosition].selected = true

                        refresh.notifyDataSetChanged()
                        mBinding.editArea.show(it.name == "其他")
                        mBinding.editArea.edit.clearFocus()

                    }
                }
            }
            refresh.addDatas(dataArrays)

            no.click { dismiss() }
            yes.click {
                if (yesFun != null) {
                    dataArrays.forEach {
                        if (it.selected) {
                            if (it.name == "其他") {
                                yesFun(mBinding.editArea.getText().toString())
                            } else {
                                yesFun(it.name)
                            }

                        }
                    }

                }
                dismiss()
            }
        }

    }

    fun selected(f: (name: String) -> Unit) {
        this.yesFun = f
    }
}