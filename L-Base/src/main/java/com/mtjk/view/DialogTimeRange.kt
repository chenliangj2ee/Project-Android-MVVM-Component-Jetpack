package com.mtjk.view

import android.view.Gravity
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.databinding.DialogTimeRangeBinding
import com.mtjk.bean.BeanTime
import com.mtjk.utils.click
import java.util.*
import kotlin.collections.ArrayList


/**
 * author:chenliang
 * date:2021/11/11
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DialogTimeRange(beanTime: BeanTime?) : MyBaseDialog<DialogTimeRangeBinding>() {
    var bean = beanTime
    private var func: ((bean: BeanTime) -> Unit)? = null
    private var hArrays = ArrayList<String>()
    private var mArrays = ArrayList<String>()
    var titleText = ""
    override fun initCreate() {
        with(mBinding) {
            hWheel?.setCyclic(false)
            mWheel?.setCyclic(false)
            no?.click { dismiss() }


            var year = Calendar.getInstance().get(Calendar.YEAR)

            for (i in 0..23) {
                if (i < 10) {
                    hArrays.add("0$i:00")
                } else {
                    hArrays.add("$i:00")
                }

            }
            for (i in 0..59) {
                if (i < 10)
                    mArrays.add("0$i" + "分")
                else
                    mArrays.add("$i" + "分")
            }

            yes?.click {

                var time = BeanTime()
                time.startHour = hArrays[hWheel.currentItem].replace(":00", "").toInt()
                time.startMinute = mArrays[mWheel.currentItem].replace("分", "").toInt()
                func?.let { it1 -> it1(time) }
                dismiss()
            }
            hWheel.adapter = ArrayWheelAdapter<Any?>(hArrays as List<Any?>?)
            mWheel.adapter = ArrayWheelAdapter<Any?>(mArrays as List<Any?>?)

            hWheel

            bean?.let {
                if (it.startHour < 10) {

                } else {
                }
                var h =
                    if (it.startHour < 10) "0${it.startHour}:00" else "${it.startHour}:00"
                var m =
                    if (it.startMinute < 10) "0${it.startMinute}分" else it.startMinute.toString() + "分"


                for (i in 0..23) {
                    if (hArrays[i] == h) {
                        hWheel.currentItem = i
                    }
                }
                for (i in 0..59) {
                    if (mArrays[i] == m) {
                        mWheel.currentItem = i
                    }
                }
            }
        }


    }

    fun setTitle(title: String) {
        this.titleText = title;
    }


    fun selected(func: (bean: BeanTime) -> Unit) {
        this.func = func
    }
}