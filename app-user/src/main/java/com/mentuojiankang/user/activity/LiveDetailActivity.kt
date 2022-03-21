package com.mentuojiankang.user.activity

import android.graphics.Paint
import android.view.View
import com.mentuojiankang.user.bean.BeanLive
import com.mentuojiankang.user.bean.BeanPayInfo
import com.mentuojiankang.user.databinding.ActivityLiveDetailBinding
import com.mentuojiankang.user.vm.LiveViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.show
import com.mtjk.view.MyWebView

@MyClass(mToolbarTitle = "直播详情", mRefresh = true)
class LiveDetailActivity : MyBaseActivity<ActivityLiveDetailBinding, LiveViewModel>() {

    @MyField
    lateinit var live: BeanLive

    override fun initCreate() {
        initView()
    }

    private fun initView() {
        with(mBinding) {
            price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            webview.load("", MyWebView.Type.IMAGE)
        }
    }

    override fun refresh() {
        loadData()
        stopRefresh()

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        mViewModel.getLiveDetail(live.liveCourseId).obs(this@LiveDetailActivity) {
            it.y {
                with(mBinding) {
                    data = it
                    if (!it.detail.isNullOrEmpty()) {
                        webview.load(it.detail, MyWebView.Type.IMAGE)
                    }
                    subscribe.click { clickSubscribe() }
                }
            }
        }
    }

    private fun clickSubscribe() {
        var order = BeanPayInfo()
        order.paycoverImage = mBinding.data?.coverImage!!
        order.paylessonTitle = mBinding.data?.name!!
        order.payprice = mBinding.data?.discountPrice!!
        order.productId = mBinding.data?.id!!
        order.subTitle = mBinding.data?.name!!
        goto(
            PaymentConfirmActivity::class.java,
            "order",
            order,
            "productType",
            ObjectProduct.TYPE_LIVE_COURSE
        )
    }
}