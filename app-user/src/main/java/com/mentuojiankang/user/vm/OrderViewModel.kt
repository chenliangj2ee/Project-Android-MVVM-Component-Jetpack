package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mentuojiankang.user.bean.BeanCreateConfirmOrder
import com.mentuojiankang.user.bean.BeanOrder
import com.mentuojiankang.user.bean.BeanParams
import com.mtjk.bean.BeanRecommend
import com.mtjk.base.MyBaseViewModel
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.body

/**
 * 用户订单相关
 * author:chenliang
 * date:2021/11/10
 */
class OrderViewModel : MyBaseViewModel() {

    //创建课程咖啡
    fun createCourseOrderInfo(productId: String) = go {
        API.createOrderInfo(body("productId", productId, "type", ObjectProduct.TYPE_COURSE))
    }

    //创建课程订单并支付
    fun createCourseConfirmOrderInfo(
        productId: String,
        productType: Int,
        createConfirmOrder: BeanCreateConfirmOrder,
        params: BeanParams
    ) = go {
        API.createConfirmOrderInfo(
            body(
                "productId",
                productId,
                "type",
                productType,
                "confirmOrderEntity",
                createConfirmOrder,
                "params",
                params
            )
        )
    }


    fun paymentConfirm(
        orderId: String,
        cardIds: ArrayList<String>,
        payType: Int,
        account: Boolean,
        payAmount: Float
    ) = go {
        API.paymentConfirm(
            body(
                "orderId", orderId,
                "cardIds", cardIds,
                "payType", payType,
                "account", account,
                "payAmount", payAmount
            )
        )
    }

    fun orderList(status: Int, pageNo: Int, pageSize: Int) =
        go { API.orderList(status, 0, pageNo, pageSize) }

    fun getblance() = go { API.getBlance() }

    fun getvipcardslist() = go { API.getVipCardList() }

    fun cancelOrder(orderId: String) =
        go { API.cancelOrder(orderId) }

    /**
     * 评论课程
     */
    fun orderCourseRecommend(comment: BeanRecommend) =
        go { API.orderCourseRecommend(comment) }

    /**
     * 评论服务
     */
    fun orderConsultRecommend(comment: BeanRecommend) =
        go { API.orderConsultRecommend(comment) }


    /**
     * 获取充值商品列表
     */
    fun getOrderList() = go { API.getOrderLists(body("device", ObjectProduct.TYPE_DEVICE)) }
}