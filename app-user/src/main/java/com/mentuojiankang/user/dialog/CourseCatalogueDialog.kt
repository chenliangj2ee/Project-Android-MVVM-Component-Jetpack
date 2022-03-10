package com.mentuojiankang.user.dialog

import android.view.Gravity
import com.chenliang.processor.appuser.MySp
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.bean.BeanSection
import com.mentuojiankang.user.databinding.DialogCourseCatalogueBinding
import com.mentuojiankang.user.databinding.ItemCourseCatalogueListBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.utils.click

/**
 * tag==选集/课程
 * author:chenliang
 * date:2021/11/4
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class CourseCatalogueDialog(bean: BeanCourse) : MyBaseDialog<DialogCourseCatalogueBinding>() {
    var bean = bean
    private var clickFunc: ((Int) -> Unit?)? = null
    override fun initCreate() {
        with(mBinding) {
            mBinding.data = bean
            catalogue.disable()
            catalogue.bindData<BeanSection> {
                with(it.binding as ItemCourseCatalogueListBinding) {

                    this.position = MySp.getCourseSectionPosition(bean.id)
                    this.section = it
                    this.course = bean
                    root.click {
                        clickFunc?.let { it1 -> it1(this.section!!.itemPosition) }

                        if (this.section!!.itemPosition == 0 || bean.buy) {
                            MySp.setCourseSectionPosition(bean.id, this.section!!.itemPosition)
                        }


                    }
                }

            }
            catalogue.addDatas(bean.sectionList)
            catalogue.scrollToPosition(MySp.getCourseSectionPosition(bean.id))
            close.click { dismiss() }
        }

    }

    fun itemClick(func: (index: Int) -> Unit) {
        this.clickFunc = func
    }

}