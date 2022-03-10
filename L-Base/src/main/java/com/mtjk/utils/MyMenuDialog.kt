package com.mtjk.utils

import android.view.Gravity
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.databinding.BaseMenuDialogBinding
import com.mtjk.base.databinding.ItemBaseMenuDialogBinding
import com.mtjk.bean.BeanMenu
import kotlinx.android.synthetic.main.base_menu_dialog.view.*

/**
 * author:chenliang
 * date:2021/11/22
 */
@MyClass(mDialogGravity = Gravity.BOTTOM,mDialogTransparent = true,mDialogAnimation = true)
class MyMenuDialog : MyBaseDialog<BaseMenuDialogBinding>() {
    var arrayList = arrayListOf<BeanMenu>()
    lateinit var clickFun: (index: Int) -> Unit
    override fun initCreate() {
        mRootView?.recyclerView?.setEnableLoadMore(false)
        mRootView?.recyclerView?.setEnableRefresh(false)
        mRootView?.recyclerView?.bindData<BeanMenu> {
            var bean = it
            with(it.binding as ItemBaseMenuDialogBinding) {
                data = it
                itemView.click {
                    if (clickFun != null) {
                        clickFun(arrayList.indexOf(bean))
                    }
                    dismiss()
                }
            }
        }

        mRootView?.recyclerView?.addDatas(arrayList)
        mRootView?.cancel?.click { dismiss() }

    }

    fun itemClick(func: (index: Int) -> Unit) {
        this.clickFun = func
    }

    fun addMenu(resource: Int, title: String) {
        var menu = BeanMenu()
        menu.icon = resource
        menu.title = title
        arrayList.add(menu)
    }


}