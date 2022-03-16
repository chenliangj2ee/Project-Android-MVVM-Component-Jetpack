package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanOrder
import com.fcyd.expert.databinding.ActivityVisitorUserInfoBinding
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs

/**
 * tag==来访者信息
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "来访者信息")
class VisitorUserInfoActivity : MyBaseActivity<ActivityVisitorUserInfoBinding, UserViewModel>() {

    @MyField
    lateinit var order: BeanOrder

    override fun initCreate() {

    }


    fun getVisitorInfo(){
        mViewModel.getVisitorInfo().obs(this){
            it.c {  }
            it.y {  }
        }
    }

}