package com.mentuojiankang.user.activity

import android.graphics.Paint
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanOrder
import com.mentuojiankang.user.databinding.ActivityOrderInfoBinding
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.toast

/**
 * tag==订单详情
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "订单详情")
class OrderInfoActivity : MyBaseActivity<ActivityOrderInfoBinding, DefaultViewModel>() {

    @MyField
    lateinit var order: BeanOrder

    override fun initCreate() {
        mToolBar.showRightIcon(R.drawable.order_info_message) { toast("我被点击了") }
        with(mBinding) {
            mBinding.order=order
            evaluate.goto(CourseEvaluateActivity::class.java)
            totalPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG;

        }
    }


}