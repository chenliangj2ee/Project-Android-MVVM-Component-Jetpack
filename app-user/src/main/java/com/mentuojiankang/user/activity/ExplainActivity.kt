package com.mentuojiankang.user.activity

import com.mentuojiankang.user.MainActivity
import com.mentuojiankang.user.databinding.ActivityExplainBinding
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.bean.BeanUser
import com.mtjk.utils.BusCode
import com.mtjk.utils.goto
import com.mtjk.utils.gotoFinish
import gorden.rxbus2.Subscribe
import gorden.rxbus2.ThreadMode
import kotlinx.android.synthetic.main.layout_next.*

/**
 * tag==吱吱心理
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "吱吱心理")
class ExplainActivity : MyBaseActivity<ActivityExplainBinding, DefaultViewModel>() {
    @MyField
    lateinit var user: BeanUser
    override fun initCreate() {
        mBinding.user=user
        next.isEnabled = true
        mToolBar.showRight("跳过") { gotoFinish(MainActivity::class.java) }
        next.goto(TroubleSelectActivity::class.java)
    }

    @Subscribe(code = BusCode.SAVE_LIST_TAGS_SUCCESS, threadMode = ThreadMode.MAIN)
    fun saveListTagsSuccess() {
        finish()
    }
}