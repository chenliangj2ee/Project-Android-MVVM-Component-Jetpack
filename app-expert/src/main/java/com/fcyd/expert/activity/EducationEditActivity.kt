package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanAuth
import com.fcyd.expert.databinding.ActivityEducationEditBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.*
import com.mtjk.view.DialogDateRange
import com.mtjk.view.DialogPicker
import com.mtjk.vm.AccountViewModel
import kotlinx.android.synthetic.main.activity_education_edit.*
import kotlinx.android.synthetic.main.activity_release_consult.delete

/**
 * tag==教育背景
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "教育背景")
class EducationEditActivity : MyBaseActivity<ActivityEducationEditBinding, AccountViewModel>() {
    //不要用MyField注解
    var bean = BeanAuth().get<BeanAuth>() ?: BeanAuth()
    var cynxArrays = arrayListOf<String>("大专", "本科", "研究生", "博士")
    override fun initCreate() {
        mBinding.bean = bean
        if (bean!!.jybj_starttime.isNotEmpty() && bean!!.jybj_endtime.isNotEmpty())
            dateRange.setValue(timeToData(bean!!.jybj_starttime) + "-" + timeToData(bean!!.jybj_endtime))
    }

    override fun initClick() {

        with(mBinding) {
            xl.click { xlDialog() }
            addIcon.click { selectImage(false) }
            delete.click {
                image.setImageBitmap(null)
                delete.show(false)
                addIconText.show(true)
                bean?.jybj_image = ""
                enableSave()
            }

            dateRange.click { dateDialog() }
            save.click {
                bean?.save()
                send(BusCode.REFRESH_EXPERT_EDIT)
                finish()
            }

            editChanged(name.editText, zy.editText) {
                enableSave()
            }
        }

    }

    /**
     * 学历
     */
    private fun xlDialog() {
        var dialog = DialogPicker()
        dialog.setItems(cynxArrays)
        dialog.setTitle("学历")
        dialog.selected {
            bean?.jybj_xl = cynxArrays[it]
            mBinding.bean?.notifyChange()
            enableSave()
        }
        dialog.show(this)
    }

    private fun dateDialog() {
        var dialog = DialogDateRange()
        dialog.setTitle("时间")
        dialog.selected { start, end ->
            bean?.jybj_time = "$start - $end"
            dateRange.setValue("$start - $end")
            bean?.jybj_starttime = start.toString()
            bean.jybj_endtime = end.toString()
//            mBinding.bean = bean
            enableSave()
        }
        dialog.show(this)
    }

    override fun resultSelectImage(array: ArrayList<String>) {
        super.resultSelectImage(array)

        if (array.isNotEmpty()) {
//            image.load(array[0], 10)
            bean?.jybj_image = array[0]
            mBinding.bean?.notifyChange()
            delete.show(true)
        }
        enableSave()
    }

    override fun onPause() {
        super.onPause()

//        bean?.save()
    }


    private fun enableSave() {
        log(bean.toJson())
        mBinding.save.isEnabled =
            bean.jybj_xx.isNotEmpty()
//                    && bean.jybj_image.isNotEmpty()
                    && bean.jybj_time.isNotEmpty()
                    && bean.jybj_xl.isNotEmpty()
                    && bean.jybj_zy.isNotEmpty()
    }

    private fun timeToData(time: String): String {
        var endtime = ""
        if (time.isNotEmpty())
            endtime = time.substring(0, 4)
        return endtime
    }

}