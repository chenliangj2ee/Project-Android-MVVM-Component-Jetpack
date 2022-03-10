package com.mtjk.view

import android.view.Gravity
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.databinding.DialogPickerBinding
import com.mtjk.utils.click
import kotlinx.android.synthetic.main.dialog_picker.view.*


/**
 * author:chenliang
 * date:2021/11/11
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DialogPicker : MyBaseDialog<DialogPickerBinding>() {
    private var func: ((index: Int) -> Unit)? = null
    private var datas = ArrayList<String>()
    var titleText = ""
    override fun initCreate() {
        mRootView?.wheel?.setCyclic(false)
        mRootView?.no?.click { dismiss() }
        mRootView?.title?.text = titleText
        mRootView?.yes?.click {
            func?.let { it1 -> it1(mRootView?.wheel!!.currentItem) }
            dismiss()
        }
        mRootView?.wheel?.adapter = ArrayWheelAdapter<Any?>(datas as List<Any?>?)
    }

    fun setTitle(title: String) {
        this.titleText = title;
    }

    fun setItems(items: ArrayList<String>) {
        this.datas = items
    }

    fun selected(func: (index: Int) -> Unit) {
        this.func = func
    }
}