package com.mentuojiankang.user.activity

import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.databinding.ActivityUserInfoEditBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.mtjk.view.DialogPicker
import com.mtjk.vm.AccountViewModel
import kotlinx.android.synthetic.main.activity_user_info_edit.*

/**
 * tag==信息编辑
 * author:chenliang
 * date:2021/11/3
 */
@MyRoute(mPath = "/app/userInfoEdit")
@MyClass(mToolbarTitle = "信息编辑")
class UserInfoEditActivity : MyBaseActivity<ActivityUserInfoEditBinding, AccountViewModel>() {
    var array = arrayListOf<String>(
        "互联网/IT/电子/通信",
        "交通/物流/贸易/零售",
        "广告/传媒/文化体育",
        "房地产/建筑",
        "教育培训",
        "金融",
        "汽车",
        "消费品",
        "服务业",
        "制药/医疗",
        "机械/制造",
        "能源/环保/化工",
        "其他"
    )
    var user = getBeanUser()
    var isFinish = false
    override fun initCreate() {
        mBinding.user = user
        updateUserInfo()
        mToolBar.showRight("跳过") {
            gotoFinish(ExplainActivity::class.java, "user", user!!)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun initClick() {

        name.changed { check() }

        header.click { selectImage(false) }
        next.click {
            if (user?.nickName!!.length < 2) {
                toast("昵称最少2个字符")
                return@click
            }
            isFinish = true
            updateUserInfo()
        }
        nan.checked {
            if (it) user?.gender = 1
            check()
            hideSoftInput(name)
        }
        nv.checked {
            if (it) user?.gender = 2
            check()
            hideSoftInput(name)
        }
        hy.click { selectorHy() }
    }

    private fun updateUserInfo() {
        if (user?.nickName.isNullOrEmpty()) {
            user?.nickName = "吱友" + user?.account?.substring(7)
        }

        if (user?.avatar.isNullOrEmpty()) {
            user?.avatar =
                "https://fcyd-website.obs.cn-north-4.myhuaweicloud.com/user_defaultAvatar.png"
        }
        user?.save()

        mViewModel.updateUserAccount(user!!).obs(this) {
            it.y {
                user!!.save()
                if (isFinish) {
                    gotoFinish(ExplainActivity::class.java, "user", user!!)
                }

            }
        }
    }

    private fun check() {
        next.isEnabled = !(user!!.nickName.isNullOrBlank() || user!!.gender == 0)
    }

    override fun resultSelectImage(array: ArrayList<String>) {
        if (array.isEmpty())
            return
        user?.avatar = "file://" + array?.get(0) ?: ""

        MyImage.uploadApp(array?.get(0)!!) {
            if (it.finish) {
                user?.avatar = it.path
                user!!.save()
            }
        }
        mBinding?.user?.notifyChange()
    }


    private fun selectorHy() {

        var dialog = DialogPicker()
        dialog.setItems(array)
        dialog.setTitle("所在行业")
        dialog.selected {
            hy.text = array[it]
            user!!.industry = array[it]
        }
        dialog.show(this)
    }

}