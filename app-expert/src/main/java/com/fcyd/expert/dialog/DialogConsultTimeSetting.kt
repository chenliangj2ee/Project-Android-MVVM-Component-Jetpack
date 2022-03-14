package com.fcyd.expert.dialog

import android.content.Intent
import android.view.Gravity
import com.fcyd.expert.databinding.DialogConsultTimeSettingBinding
import com.fcyd.expert.databinding.ItemConsultTimeBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.bean.BeanTime
import com.mtjk.utils.*

/**
 * tag==咨询时间设置
 * author:chenliang
 * date:2022/3/14
 */

@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DialogConsultTimeSetting(selectedTimes: ArrayList<BeanTime>, selectedWeeks: ArrayList<Int>) :
    MyBaseDialog<DialogConsultTimeSettingBinding>() {

    var times = ArrayList<BeanTime>()
    var selectedTimes = selectedTimes
    private var weeks = arrayListOf<Int>()
    var selectedWeeks = selectedWeeks
    override fun initCreate() {
        for (i in 0..23) {
            var time = BeanTime()
            if (i < 10)
                time.text = "0$i:00"
            else
                time.text = "$i:00"

            selectedTimes.forEach {
                if (it.consultStartTime.length > 4 && it.consultStartTime.substring(
                        0,
                        5
                    ) == time.text
                ) {
                    time.itemSelected = true
                }
                if (it.text == time.text) {
                    time.itemSelected = true
                }
            }

            times.add(time)
        }



        mBinding.times.disable()
        mBinding.times.bindData<BeanTime>() { bindItem(it) }
        mBinding.times.addDatas(times)
    }

    private fun bindItem(bean: BeanTime) {
        with(bean.binding as ItemConsultTimeBinding) {
            time = bean
            this.time = bean;
            this.checkBox.click {
                bean.itemSelected = !bean.itemSelected
            }
        }
    }

    override fun initClick() {

        with(mBinding) {
            weeks.addAll(selectedWeeks)
            zhou1.isChecked = selectedWeeks.contains(1)
            zhou2.isChecked = selectedWeeks.contains(2)
            zhou3.isChecked = selectedWeeks.contains(3)
            zhou4.isChecked = selectedWeeks.contains(4)
            zhou5.isChecked = selectedWeeks.contains(5)
            zhou6.isChecked = selectedWeeks.contains(6)
            zhou7.isChecked = selectedWeeks.contains(0)

            zhou1.click { if (zhou1.isChecked) weeks.add(1) else weeks.remove(1) }
            zhou2.click { if (zhou2.isChecked) weeks.add(2) else weeks.remove(2) }
            zhou3.click { if (zhou3.isChecked) weeks.add(3) else weeks.remove(3) }
            zhou4.click { if (zhou4.isChecked) weeks.add(4) else weeks.remove(4) }
            zhou5.click { if (zhou5.isChecked) weeks.add(5) else weeks.remove(5) }
            zhou6.click { if (zhou6.isChecked) weeks.add(6) else weeks.remove(6) }
            zhou7.click { if (zhou7.isChecked) weeks.add(0) else weeks.remove(0) }

            finish.click { save() }
        }
    }

    private fun save() {

        if (times.none { it.itemSelected }) {
            toast("请选择服务时间")
            return
        }
        if (weeks.isEmpty()) {
            toast("请选择服务日")
            return
        }
        var intent = Intent()
        intent.putExtra("times", times.filter { it.itemSelected } as ArrayList<BeanTime>)
        intent.putExtra("weeks", weeks)
        intent.sendSelf(BusCode.RESULT_TIME_SETTINGS)
        dismiss()
    }
}