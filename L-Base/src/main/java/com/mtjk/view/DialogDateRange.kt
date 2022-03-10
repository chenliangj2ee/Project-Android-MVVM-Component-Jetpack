package com.mtjk.view

import android.view.Gravity
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.databinding.DialogDateRangeBinding
import com.mtjk.utils.click
import com.mtjk.utils.log
import kotlinx.android.synthetic.main.dialog_date_range.view.*
import kotlinx.android.synthetic.main.dialog_picker.view.*
import kotlinx.android.synthetic.main.dialog_picker.view.no
import kotlinx.android.synthetic.main.dialog_picker.view.title
import kotlinx.android.synthetic.main.dialog_picker.view.yes
import okhttp3.internal.addHeaderLenient
import okhttp3.internal.notifyAll
import java.util.*
import kotlin.collections.ArrayList


/**
 * author:chenliang
 * date:2021/11/11
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DialogDateRange : MyBaseDialog<DialogDateRangeBinding>() {
    private var func: ((start: Int, end: Int) -> Unit)? = null
    private var startArrays = ArrayList<String>()
    private var endArrays = ArrayList<String>()
    var titleText = ""
    override fun initCreate() {
        mRootView?.start?.setCyclic(false)
        mRootView?.end?.setCyclic(false)
        mRootView?.no?.click { dismiss() }

        var year = Calendar.getInstance().get(Calendar.YEAR)

        for (i in 1949..year) {
            startArrays.add("$i" + "年")
            endArrays.add("$i" + "年")
        }

        mRootView?.title?.text = titleText
        mRootView?.yes?.click {
            var startYear = startArrays[mRootView?.start!!.currentItem].replace("年", "").toInt()
            var endYear = endArrays[mRootView?.end!!.currentItem].replace("年", "").toInt()
            func?.let { it1 -> it1(startYear, endYear) }
            dismiss()
        }
        mRootView?.start?.adapter = ArrayWheelAdapter<Any?>(startArrays as List<Any?>?)
        mRootView?.end?.adapter = ArrayWheelAdapter<Any?>(endArrays as List<Any?>?)

        mRootView?.start?.setOnItemSelectedListener {
            var startYear = startArrays[it].replace("年", "").toInt()
            log("选择了$startYear")
            endArrays.clear()
            for (i in startYear..year) {
                endArrays.add("$i" + "年")
            }
            mRootView?.end?.adapter = ArrayWheelAdapter<Any?>(endArrays as List<Any?>?)

        }
    }

    fun setTitle(title: String) {
        this.titleText = title;
    }


    fun selected(func: (start: Int, end: Int) -> Unit) {
        this.func = func
    }
}