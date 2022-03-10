package com.mentuojiankang.user.fragment

import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.activity.CourseEvaluateActivity
import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.activity.PaymentConfirmActivity
import com.mentuojiankang.user.bean.BeanOrder
import com.mentuojiankang.user.bean.BeanParams
import com.mentuojiankang.user.bean.BeanPayInfo
import com.mentuojiankang.user.databinding.*
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.act.TextActivity
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.bean.BeanRecommend
import com.mtjk.obj.ObjectOrder
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.*
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.item_home_tab.view.*
import kotlinx.android.synthetic.main.item_my_order.view.*

/**
 * tag==订单列表/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyRoute(mPath = "/app/myorderlistfragment")
class MyOrderListFragment : MyBaseFragment<FragmentMyOrderListBinding, OrderViewModel>() {

    @MyField
    var status = 0
    override fun initOnCreateView() {
        with(mBinding) {
            refresh.bindData<BeanOrder>(::bindItem)
            refresh.loadData(::loadOrders)
        }
    }


    /**
     * 绑定列表Item
     */
    private fun bindItem(beanOrder: BeanOrder) {
        with(beanOrder.binding as ItemMyOrderBinding) {
            if (beanOrder.orderStatus == ObjectOrder.STATUS_CASHNO_PAY)
                beanOrder.orderStatus = ObjectOrder.STATUS_NO_PAY
            order = beanOrder
            root.evaluate.click { gotoEvaluate(beanOrder) }
            root.pay.click { gotoPay(beanOrder) }
            root.cancelOrder.click { cancelOrder(beanOrder.orderId) }
            root.chat.click { IM.gotoChat(beanOrder.expertAccountId, beanOrder.expertName) }
            root.startLive.click { intoLiveRoom(beanOrder) }
            root.look.goto(TextActivity::class.java, "text", beanOrder.expertPlan)
            root.startCourse.click { gotoCourseInfo(beanOrder) }
        }
    }

    /**
     * 加载订单列表
     */
    private fun loadOrders() {
        with(mBinding.refresh) {
            mViewModel.orderList(status, pageIndex, pageSize)
                .obs(this@MyOrderListFragment) {
                    it.c { addCache(it.records) }
                    it.y { addDatas(it.records) }
                }
        }
    }

    /**
     * 去课程详情
     */
    private fun gotoCourseInfo(beanOrder: BeanOrder) {
        goto(CourseInfoActivity::class.java, "courseId", beanOrder.orderItems!!.productId)
    }

    /**
     * 去评价
     */
    private fun gotoEvaluate(beanOrder: BeanOrder) {
        var bean = BeanRecommend()
        bean.title = beanOrder.orderItems!!.title
        bean.url = beanOrder.orderItems!!.coverImage
        bean.orderDate = beanOrder.orderDate

        bean.productId = beanOrder.orderItems!!.productId
        bean.orderId = beanOrder.orderId
        bean.orderServer = beanOrder.orderServer
        if (beanOrder.orderServer != 100) {
            bean.expertShopId = beanOrder.shopId
        }
        goto(CourseEvaluateActivity::class.java, "bean", bean)
    }


    /**
     * 继续支付
     */
    private fun gotoPay(bean: BeanOrder) {
        if (bean.orderServer == ObjectProduct.TYPE_COURSE) {
            var payInfo = BeanPayInfo()
            payInfo.paycoverImage = bean.orderItems!!.coverImage
            payInfo.paylessonTitle = bean.orderItems!!.title
            payInfo.payprice = bean.paidAmount
            payInfo.paysectionCount = 0
            payInfo.productId = bean.orderItems!!.productId
            goto(
                PaymentConfirmActivity::class.java,
                "order",
                payInfo,
                "productType",
                ObjectProduct.TYPE_COURSE,
                "payAgain",
                true,
                "orderId",
                bean.orderId
            )
            return;
        }
        if (bean.orderServer == ObjectProduct.TYPE_CONSULT) {
            var payInfo = BeanPayInfo()
            payInfo.paycoverImage = bean.orderItems!!.coverImage
            payInfo.paylessonTitle = bean.orderItems!!.title
            payInfo.payprice = bean.paidAmount
            payInfo.paysectionCount = 0
            payInfo.productId = bean.orderItems!!.productId
            payInfo.subTitle = "预约时间 " + bean.orderItems!!.consultStartTime.date("yyyy-MM-dd") +
                    " ${bean.orderItems!!.consultStartTime.dateT("HH:mm")}-" + bean.orderItems!!.consultEndTime.dateT(
                "HH:mm"
            )

            var params = BeanParams()
            params.shopId = bean.shopId
            params.day = bean.orderItems!!.consultDay

            params.startTime = bean.orderItems!!.consultStartTime.dateT("HH:mm")
            params.endTime = bean.orderItems!!.consultEndTime.dateT("HH:mm")
            params.consultType = bean.consultType


            var productType = ObjectProduct.TYPE_CONSULT

            goto(
                PaymentConfirmActivity::class.java,
                "order",
                payInfo,
                "params",
                params,
                "productType",
                productType,
                "payAgain",
                true,
                "orderId",
                bean.orderId
            )

            return;
        }


    }

    /**
     * 支付成功后，刷新订单列表
     */
    @Subscribe(code = BusCode.PAYMENT_RESULT)
    fun refreshList() {
        orderRefresh()
    }

    /**
     * 刷新订单列表
     */
    @Subscribe(code = BusCode.ORDER_REFRESH)
    fun orderRefresh() {
        mBinding.refresh.refresh()
    }

    /**
     * 取消订单
     */
    private fun cancelOrder(orderId: String) {
        mViewModel.cancelOrder(orderId).obs(this) {}
    }

    /**
     * 进入直播室t
     */
    private fun intoLiveRoom(bean: BeanOrder) {
    }


}