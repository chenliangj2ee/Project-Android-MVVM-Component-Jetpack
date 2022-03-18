package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanOrder
import com.fcyd.expert.databinding.ActivityVisitorUserInfoBinding
import com.fcyd.expert.vm.OrderViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs

/**
 * tag==来访者信息
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "来访者信息", mRefresh = true)
class VisitorUserInfoActivity : MyBaseActivity<ActivityVisitorUserInfoBinding, OrderViewModel>() {
    @MyField
    lateinit var order: BeanOrder

    override fun initCreate() {
        refresh()
    }

    override fun refresh() {
        mViewModel.getVisitorInfo(order.orderId).obs(this) {
            it.c { mBinding.bean = it }
            it.y { mBinding.bean = it }
            stopRefresh()
        }
    }
}