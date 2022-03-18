package com.fcyd.expert.vm

import com.chenliang.processor.appexpert.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.obj.ObjectOrder
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.body

/**
 * author:chenliang
 * date:2021/11/29
 */
class OrderViewModel : MyBaseViewModel() {
    //创建课程咖啡
    fun createCourseOrderInfo(productId: String) = go {
        API.createOrderInfo(body("productId", productId, "type", ObjectProduct.TYPE_COURSE))
    }

    fun paymentConfirm(
        orderId: String,
        cardIds: ArrayList<String>,
        payType: Int,
        account: Boolean
    ) = go {
        API.paymentConfirm(
            body(
                "orderId", orderId,
                "cardIds", cardIds,
                "payType", payType,
                "account", account
            )
        )
    }

    /**
     * 订单列表
     */
    fun orderList(status: Int, pageNo: Int, pageSize: Int) =
        go { API.orderList(status, pageNo, pageSize) }

    /**
     * 接单
     */
    fun orderConfirms(orderId: String ) = go { API.orderConfirm(body("orderId", orderId )) }

    /**
     * 结束咨询服务
     */
    fun stopConsult(orderId: String) = go { API.stopConsult(body("orderId", orderId, "orderState", ObjectOrder.STATE_STOP_CONSULT)) }


    /**
     * 写咨询方案
     */
    fun feedback(orderId: String, orderPlan: String) = go { API.feedback(body("orderId", orderId, "diagnosisText", orderPlan)) }

    /**
     * 获取来访者详情
     */
    fun getVisitorInfo(orderId: String) = go {
        API.getVisitorInfo(orderId)
    }
}