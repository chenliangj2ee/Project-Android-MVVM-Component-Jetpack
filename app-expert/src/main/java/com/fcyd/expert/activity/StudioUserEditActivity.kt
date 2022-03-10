package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityStudioUserEditBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import kotlinx.android.synthetic.main.activity_personal_info_edit.header
import kotlinx.android.synthetic.main.activity_studio_user_edit.*

/**
 * tag==个人资料
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "个人资料")
class StudioUserEditActivity :
    MyBaseActivity<ActivityStudioUserEditBinding, com.mtjk.vm.AccountViewModel>() {
    var user = getBeanUser()
    override fun initCreate() {
        mBinding.data = user
//        mToolBar.showRight("帮助"){}
    }


    override fun initClick() {
        header.click { selectImage(false) }
        save.click {
            user?.sendSelf(BusCode.RESULT_EXPERT_STUDIO_USER_EDIT)
            finish()
        }
        editChanged(
            userNameItem.editText,
            expertArea.editText,
            expertEducation.editText,
            expertEertificate.editText,
            expertWorkingYears.editText,
            expertExpertise.editText
        ) {
            with(user!!) {
                save.isEnabled =
                    avatar.isNotEmpty()
                            && nickName.isNotEmpty()
                            && expertArea.isNotEmpty()
                            && expertEducation.isNotEmpty()
                            && expertEertificate.isNotEmpty()
                            && expertExpertise.isNotEmpty()
            }
        }
    }


    override fun resultSelectImage(array: ArrayList<String>) {
        if (array.isEmpty())
            return
        user?.avatar = "file://" + array?.get(0) ?: ""

        MyImage.uploadBusiness(array?.get(0)!!) {
            if (it.finish) {
                user?.avatar = it.path
            }
        }
        mBinding?.data?.notifyChange()
    }


}