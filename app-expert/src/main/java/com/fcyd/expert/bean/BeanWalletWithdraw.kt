package com.fcyd.expert.bean

import com.fcyd.expert.R
import com.mtjk.base.MyBaseBean
import com.mtjk.obj.ObjectProduct
import com.mtjk.obj.ObjectWithdraw
import com.mtjk.utils.dateT

/**
 * 提现详情
 * author:xujunsheng
 * date:2022/02/28
 */
class BeanWalletWithdraw : MyBaseBean() {

    //商品封面
    var coverImage = ""
    //订单类型：20：收入，30：提现，40，退款
    var orderType = 0
    //支付时间
    var paidAt = ""
    //订单价格
    var price = 0.0
    //商品名称
    var productTitle = ""
    //商品类型：咨询、直播、课程、督导
    var productType = 0
    //提现状态
    var orderStatus = 0

    fun coverDefaultImage(): Int {
        if (productType == ObjectProduct.TYPE_CONSULT) {
            return R.drawable.item_wallet_detail_icon_consult
        }
        if (productType == ObjectProduct.TYPE_LIVE) {
            return R.drawable.item_wallet_detail_icon_live
        }
        if(productType == ObjectProduct.TYPE_COURSE) {
            return R.drawable.item_wallet_detail_icon_course
        }
        return 0
    }

    fun productTypeImage(): Int {
        if (productType == ObjectProduct.TYPE_CONSULT) {
            return R.drawable.item_wallet_detail_tag_consult
        }
        if (productType == ObjectProduct.TYPE_LIVE) {
            return R.drawable.item_wallet_detail_tag_live
        }
        if(productType == ObjectProduct.TYPE_COURSE) {
            return R.drawable.item_wallet_detail_tag_course
        }
        return 0
    }

    fun paidTimeDes(): String {
        if (paidAt == null) paidAt = ""
        if (paidAt.isNotEmpty()) {
            return paidAt.dateT("yyyy.MM.dd  ") + paidAt.dateT("HH:mm:SS")
        }
        return ""
    }

    fun groupName(): String {
        if (!paidAt.isNullOrEmpty()) {
            var date = paidAt.dateT("yyyy-MM")
            return date?.replace("-", "年") + "月"
        }
        return ""
    }

    fun orderStatusText(): String {
        if(orderStatus == ObjectWithdraw.STATUS_END_ORDER) {
            return "成功"
        }
        if(orderStatus == ObjectWithdraw.STATUS_VERIFYING) {
            return "审核中"
        }
        if(orderStatus == ObjectWithdraw.STATUS_CLOSE_ORDER) {
            return "失败"
        }
        if(orderStatus == ObjectWithdraw.STATUS_CANCEL_ORDER) {
            return "订单取消"
        }
        return ""
    }

}