package com.mtjk.view

import android.os.Build
import android.view.Gravity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.databinding.DialogYearMonthPickerBinding
import com.mtjk.utils.click
import kotlinx.android.synthetic.main.dialog_year_month_picker.view.*
import com.mtjk.utils.log
import java.util.*
import kotlin.collections.ArrayList

@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DialogYearMonthPicker : MyBaseDialog<DialogYearMonthPickerBinding>() {
    private var func: ((start: Int, end: Int) -> Unit)? = null
    private var yearArrays = ArrayList<String>()
    private var monthArrays = ArrayList<String>()
    var titleText = ""
    var startYear = 1949

    var initialYear = 0
    var initialMonth = 0

    override fun initCreate() {
        mRootView?.year?.setCyclic(false)
        mRootView?.month?.setCyclic(false)
        mRootView?.title?.text = titleText

        var year = Calendar.getInstance().get(Calendar.YEAR)
        for (i in startYear..year) {
            yearArrays.add("$i" + "年")
        }
        for (i in 1..12) {
            monthArrays.add("$i" + "月")
        }
        mRootView?.year?.adapter = ArrayWheelAdapter<Any?>(yearArrays as List<Any?>?)
        mRootView?.month?.adapter = ArrayWheelAdapter<Any?>(monthArrays as List<Any?>?)

        mRootView?.no?.click { dismiss() }
        mRootView?.yes?.click {
            var selectYear = yearArrays[mRootView?.year!!.currentItem].replace("年", "").toInt()
            var selectMonth = monthArrays[mRootView?.month!!.currentItem].replace("月", "").toInt()
            func?.let { it1 -> it1(selectYear, selectMonth) }
            dismiss()
        }

        mRootView?.year?.setOnItemSelectedListener {
            var selectYear = yearArrays[it].replace("年", "").toInt()
            log("选择了$selectYear")
        }
        mRootView?.month?.setOnItemSelectedListener {
            var selectmonth = monthArrays[it].replace("月", "").toInt()
            log("选择了$selectmonth")
        }
        setInitPosition()
    }

    private fun setInitPosition() {
        if(initialYear > 0) {
            for ((index, value) in yearArrays.withIndex()) {
                if(value?.startsWith(initialYear.toString())) {
                    mRootView?.year?.currentItem = index
                    break
                }
            }
        }
        if(initialMonth > 0) {
            for ((index, value) in monthArrays.withIndex()) {
                if(value?.startsWith(initialMonth.toString())) {
                    mRootView?.month?.currentItem = index
                    break
                }
            }
        }
    }

    fun setTitle(title: String) {
        this.titleText = title;
    }

    fun selected(func: (start: Int, end: Int) -> Unit) {
        this.func = func
    }

    private fun setBeginYear(year: Int) {
        this.startYear = year
    }

    private fun setInitYear(year: Int) {
        this.initialYear = year
    }

    private fun setInitMonth(month: Int) {
        this.initialMonth = month
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDialog(activity: AppCompatActivity, startYear: Int, initYear: Int, initMonth: Int, title: String, listener: (Int, Int) -> Unit) {
        setTitle(title)
        setBeginYear(startYear)
        setInitYear(initYear)
        setInitMonth(initMonth)
        selected { year, month ->
            listener(year, month)
        }
        show(activity)
    }

}