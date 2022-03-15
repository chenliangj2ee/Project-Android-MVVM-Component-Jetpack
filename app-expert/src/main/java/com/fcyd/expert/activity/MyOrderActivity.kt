package com.fcyd.expert.activity

import android.graphics.Color
import android.widget.TextView
import com.fcyd.expert.R
import com.fcyd.expert.databinding.ActivityMyOrderBinding
import com.fcyd.expert.fragment.MyOrderListFragment
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.obj.ObjectOrder
import com.mtjk.utils.goto
import com.mtjk.utils.selected
import com.mtjk.utils.setTextSizeDip

/**
 * tag==我的订单
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "我的订单")
class MyOrderActivity : MyBaseActivity<ActivityMyOrderBinding, DefaultViewModel>() {

    override fun initCreate() {


        with(mBinding) {
            var datas = ArrayList<String>()
            datas.add("全部")
            datas.add("待支付")
            datas.add("待确认")
            datas.add("进行中")
            datas.add("待反馈")
            tab.selected {
                with(it?.customView?.findViewById<TextView>(R.id.title)) {
                    this?.setTextColor(Color.parseColor(if (it?.isSelected == true) "#008599" else "#A9ADB2"))
                    this?.setTextSizeDip(if (it?.isSelected == true) 15 else 15)
                }

            }
            viewpager.setTabLayout(tab, datas, R.layout.item_my_consult_tab) {
                with(it.customView?.findViewById<TextView>(R.id.title)) { this?.text = it.text }
            }
            viewpager.offscreenPageLimit=5
            viewpager.addFragments(goto(MyOrderListFragment::class.java,"status", ObjectOrder.STATUS_ALL))
            viewpager.addFragments(goto(MyOrderListFragment::class.java,"status", ObjectOrder.STATUS_NO_PAY))
            viewpager.addFragments(goto(MyOrderListFragment::class.java,"status", ObjectOrder.STATUS_WAIT_CONFIRM))
            viewpager.addFragments(goto(MyOrderListFragment::class.java,"status", ObjectOrder.STATUS_DOING))
            viewpager.addFragments(goto(MyOrderListFragment::class.java,"status", ObjectOrder.STATUS_WAIT_FEEDBACK))

        }
    }


}