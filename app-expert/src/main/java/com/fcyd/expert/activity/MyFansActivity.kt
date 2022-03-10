package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanWork
import com.fcyd.expert.databinding.ActivityMyFansBinding
import com.fcyd.expert.databinding.ItemMyFansBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import kotlinx.android.synthetic.main.activity_my_fans.*

/**
 * tag==关注
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "关注")
class MyFansActivity : MyBaseActivity<ActivityMyFansBinding, DefaultViewModel>() {
    override fun initCreate() {
        recyclerView.bindData<BeanWork>() {
            with(it.binding as ItemMyFansBinding) { data = it }
        }
        recyclerView.loadData {

        }
    }

}