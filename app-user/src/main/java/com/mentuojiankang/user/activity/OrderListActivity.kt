package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanCard
import com.mentuojiankang.user.databinding.ActivityOrderListBinding
import com.mentuojiankang.user.databinding.ItemOrderListBinding
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import kotlinx.android.synthetic.main.activity_order_list.*
@MyClass(mToolbarTitle = "账单记录")
class OrderListActivity : MyBaseActivity<ActivityOrderListBinding,OrderViewModel>() {
    override fun initCreate() {
       initdata()
    }

    fun initdata(){
        orderrefresh.bindData<BeanCard> { orderlist->
            with(orderlist.binding as ItemOrderListBinding){
                data=orderlist

            }
        }

        orderrefresh.loadData {
            mViewModel.getvipcardslist().obs(this){
                orderrefresh.addData(it)
            }

        }
    }
}