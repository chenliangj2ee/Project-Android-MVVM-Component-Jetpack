package com.fcyd.expert.activity

import com.mtjk.bean.BeanCertificate
import com.fcyd.expert.databinding.ActivityQualificationEditBinding
import com.fcyd.expert.dialog.DialogQualificationSpinner
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.*
import com.mtjk.vm.AccountViewModel

/**
 * tag==添加资格证书
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "添加资格证书")
class QualificationEditActivity :
    MyBaseActivity<ActivityQualificationEditBinding, AccountViewModel>() {

    @MyField
    var bean: BeanCertificate? = null

    override fun initCreate() {

        if (bean == null) {
            bean = BeanCertificate()
            bean?.myId = System.currentTimeMillis().toString()
        } else {
            mToolBar.setTitle("编辑资格证书")
            mToolBar.showRight("删除") {
                bean?.sendSelf(BusCode.RESULT_DELETE_ZGZS)
                finish()
            }
        }
        mBinding.bean = bean

    }

    override fun initClick() {

        with(mBinding) {
            zsmc.click { zsmcDialog() }
            addIcon.click { selectImage(video = false, crop = false)}
            delete.click {
                bean?.certificateImageUrl = ""
                mBinding.bean?.notifyChange()
                image.setImageBitmap(null)
                delete.show(false)
            }

            save.click {
                bean?.sendSelf(BusCode.RESULT_ADD_ZGZS)
                finish()
            }
        }


    }

    private fun zsmcDialog() {
        var dialog = DialogQualificationSpinner()
        dialog.selected {
            bean?.certificateName = it
            mBinding.bean?.notifyChange()

        }
        dialog.show(this)
    }


    override fun resultSelectImage(array: ArrayList<String>) {
        super.resultSelectImage(array)

        if (array.isNotEmpty()) {
            bean?.certificateImageUrl = array[0]
            mBinding.bean?.notifyChange()
            mBinding.delete.show(true)
        }

    }

    override fun onBackPressed() {

        if (!bean?.certificateName.isNullOrEmpty() && !bean?.certificateImageUrl.isNullOrEmpty()) {
            super.onBackPressed()
        } else if (!bean?.certificateName.isNullOrEmpty() || !bean?.certificateImageUrl.isNullOrEmpty()) {
            dialog("信息马上填完，确认退出吗？")
                .n("下次再说") { super.onBackPressed() }
                .y("继续填写") { }
                .show(this)
        } else {
            super.onBackPressed()
        }

    }


}