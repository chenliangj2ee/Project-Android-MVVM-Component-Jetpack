package com.fcyd.expert.activity

import android.content.Intent
import android.widget.CheckBox
import com.fcyd.expert.R
import com.fcyd.expert.databinding.ActivityConsultTimeSettingBinding
import com.fcyd.expert.databinding.ItemConsultSubTimeBinding
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.bean.BeanTime
import com.mtjk.utils.*
import com.mtjk.view.DialogTimeRange
import kotlinx.android.synthetic.main.activity_consult_time_setting.*
import java.util.*

/**
 * tag==咨询时间设置
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "咨询时间设置")
class ConsultTimeSettingActivity :
    MyBaseActivity<ActivityConsultTimeSettingBinding, DefaultViewModel>() {

    @MyField
    var type: Int = 0
    var duration = 50

    @MyField
    var times = arrayListOf<BeanTime>()

    @MyField
    var weeks = arrayListOf<Int>()

    var nextTime: BeanTime? = null


    override fun initCreate() {
        nextTime = if (times.isNotEmpty()) times.last().nextTime(duration) else null
        mToolBar.showRight("保存") {
            save()
        }
        with(mBinding) {
            refresh.disable()
            refresh.bindData<BeanTime> {
                with(it.binding as ItemConsultSubTimeBinding) {
                    time = it
                    this.mType = type
                    delete.click { _ ->
                        times.remove(it)
                        refresh.remove(it.itemPosition)
                        refresh.notifyDataSetChanged()
                        nextTime = if (times.isNotEmpty()) times.last().nextTime(duration) else null
                        if (times.size < 5) {
                            mBinding.refresh.layoutParams.height = (54).dip2px() * times.size
                        }
                    }
                    edit.click { _ -> timeDialog(it) }
                }
            }
            resetRefreshHeight()
            refresh.addDatas(times)
            timeLong.changed {
                if (it.isNullOrEmpty())
                    return@changed
                duration = it.toInt()
                if (duration > 720) {
                    toast("时间不能超过720分钟")
                    timeLong.setText(it.substring(0, it.length - 1))
                }
            }

        }


        zhou1.isChecked = weeks.contains(1)
        zhou2.isChecked = weeks.contains(2)
        zhou3.isChecked = weeks.contains(3)
        zhou4.isChecked = weeks.contains(4)
        zhou5.isChecked = weeks.contains(5)
        zhou6.isChecked = weeks.contains(6)
        zhou7.isChecked = weeks.contains(0)


    }

    private fun save() {

        if (times.isEmpty()) {
            toast("请选择服务时间")
            return
        }
        if (weeks.isEmpty()) {
            toast("请选择服务日")
            return
        }
        var intent = Intent()
        intent.putExtra("times", times)
        intent.putExtra("weeks", weeks)
        intent.sendSelf(BusCode.RESULT_TIME_SETTINGS)
        finish()
    }

    override fun initClick() {
        with(mBinding) {
            addTime.click { timeDialog(nextTime) }
            time15.click { timeReset(time15) }
            time30.click { timeReset(time30) }
            time50.click { timeReset(time50) }
            timeCustom.click { timeReset(timeCustom) }
            zhou1.click { if (zhou1.isChecked) weeks.add(1) else weeks.remove(1) }
            zhou2.click { if (zhou2.isChecked) weeks.add(2) else weeks.remove(2) }
            zhou3.click { if (zhou3.isChecked) weeks.add(3) else weeks.remove(3) }
            zhou4.click { if (zhou4.isChecked) weeks.add(4) else weeks.remove(4) }
            zhou5.click { if (zhou5.isChecked) weeks.add(5) else weeks.remove(5) }
            zhou6.click { if (zhou6.isChecked) weeks.add(6) else weeks.remove(6) }
            zhou7.click { if (zhou7.isChecked) weeks.add(0) else weeks.remove(0) }
        }

    }


    private fun timeReset(time: CheckBox) {
        with(mBinding) {
            time15.isChecked = false
            time30.isChecked = false
            time50.isChecked = false
            timeCustom.isChecked = false
        }

        when (time.id) {
            R.id.time15 -> duration = 15
            R.id.time30 -> duration = 30
            R.id.time50 -> duration = 50
        }
        time.isChecked = true
    }

    /**
     * 选择时间段dialog
     */
    private fun timeDialog(editTime: BeanTime?) {
//        log("duration：${editTime?.toJson()}")
        if (duration <= 0) {
            toast("自定义时间不能为0")
            return
        }
        var dialog = DialogTimeRange(editTime)
        dialog.selected {

            var indexEditTime = times.indexOf(editTime)
            times.remove(editTime)

            var can = Calendar.getInstance()
            can.set(Calendar.HOUR_OF_DAY, it.startHour)
            can.set(Calendar.MINUTE, it.startMinute)
            can.add(Calendar.MINUTE, duration)

            it.endHour = can.get(Calendar.HOUR_OF_DAY)
            it.endMinute = can.get(Calendar.MINUTE)

            times.add(it)
            times.sortWith(compareBy({ it.startHour }, { it.startMinute }))


            for (item in times) {
                if (item != it) {
                    var can = Calendar.getInstance()

                    can.set(Calendar.HOUR_OF_DAY, item.startHour)
                    can.set(Calendar.MINUTE, item.startMinute)
                    var startOne = can.time
                    can.set(Calendar.HOUR_OF_DAY, item.endHour)
                    can.set(Calendar.MINUTE, item.endMinute)
                    var endOne = can.time

                    can.set(Calendar.HOUR_OF_DAY, it.startHour)
                    can.set(Calendar.MINUTE, it.startMinute)
                    var startTwo = can.time
                    can.set(Calendar.HOUR_OF_DAY, it.endHour)
                    can.set(Calendar.MINUTE, it.endMinute)
                    var endTwo = can.time


                    if (compareTwoTime(startOne, endOne, startTwo, endTwo)) {
                        times.remove(it)
//                        log(item.toJson() + ":" + it.toJson())
                        toast("时间重叠")
                        break
                    }


                }
            }

            if (it.endHour < it.startHour) {
                toast("时间不能超出当天")
                times.remove(it)
                return@selected
            }

            nextTime = it.nextTime(duration)
            if (!times.contains(it) && indexEditTime >= 0) {
                editTime?.let { it1 -> times.add(indexEditTime, it1) }
            }


            mBinding.refresh.clearData()
            mBinding.refresh.addDatas(times)
            resetRefreshHeight()
            mBinding.refresh.recyclerView?.scrollBy(0, 10000)

            notify.show(times.isEmpty())
        }
        dialog.show(this)
    }

    fun weekSelector() {
    }

    fun resetRefreshHeight(){
        if (times.size > 5) {
            mBinding.refresh.layoutParams.height = (54).dip2px() * 5
        } else {
            mBinding.refresh.layoutParams.height = (54).dip2px() * times.size
        }
    }

    private fun compareTwoTime(
        startOne: Date,
        endOne: Date,
        startTwo: Date,
        endTwo: Date
    ): Boolean {
        if (!startOne.after(endTwo) && !endOne.before(startTwo)) {
            //时间重叠
            return true
        }
        //时间不重叠
        return false
    }


}