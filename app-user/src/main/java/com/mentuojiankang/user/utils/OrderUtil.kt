package com.mentuojiankang.user.utils

import android.content.Context
import com.mentuojiankang.user.activity.CourseEvaluateActivity
import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.activity.PaymentConfirmActivity
import com.mentuojiankang.user.bean.BeanOrder
import com.mentuojiankang.user.bean.BeanParams
import com.mentuojiankang.user.bean.BeanPayInfo
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.bean.BeanRecommend
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.date
import com.mtjk.utils.dateT
import com.mtjk.utils.goto

object OrderUtil {
    fun gotoCourseInfo(context: Context, beanOrder: BeanOrder) {
        context?.goto(CourseInfoActivity::class.java, "courseId", beanOrder.orderItems!!.productId)
    }

    fun gotoEvaluate(context: Context, beanOrder: BeanOrder) {
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
        context?.goto(CourseEvaluateActivity::class.java, "bean", bean)
    }

    /**
     * 继续支付
     */
    fun gotoPay(context: Context, bean: BeanOrder) {
        if (bean.orderServer == ObjectProduct.TYPE_COURSE) {
            var payInfo = BeanPayInfo()
            payInfo.paycoverImage = bean.orderItems!!.coverImage
            payInfo.paylessonTitle = bean.orderItems!!.title
            payInfo.payprice = bean.paidAmount
            payInfo.paysectionCount = 0
            payInfo.productId = bean.orderItems!!.productId
            context?.goto(
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

            context?.goto(
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
        if (bean.orderServer == ObjectProduct.TYPE_LIVE_COURSE) {
            var payInfo = BeanPayInfo()
            payInfo.paycoverImage = bean.orderItems!!.coverImage
            payInfo.paylessonTitle = bean.orderItems!!.title
            payInfo.payprice = bean.paidAmount
            payInfo.paysectionCount = 0
            payInfo.productId = bean.orderItems!!.productId
            context?.goto(
                PaymentConfirmActivity::class.java,
                "order",
                payInfo,
                "productType",
                ObjectProduct.TYPE_LIVE_COURSE,
                "payAgain",
                true,
                "orderId",
                bean.orderId
            )
            return;
        }
    }

    /**
     * 取消订单
     */
    fun cancelOrder(orderVM: OrderViewModel, orderId: String) {
        orderVM?.cancelOrder(orderId)
    }

    /**
     * 进入直播室t
     */
    fun intoLiveRoom(bean: BeanOrder) {
    }
}