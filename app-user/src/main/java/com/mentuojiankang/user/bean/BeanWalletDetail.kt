package com.mentuojiankang.user.bean

import com.mentuojiankang.user.R
import com.mtjk.base.MyBaseBean
import com.mtjk.obj.ObjectOrderType
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.dateT

class BeanWalletDetail  : MyBaseBean(){

    //商品封面
    var coverImage = ""
    //订单类型：10：充值，20：咨询师收入/用户消费，30：提现，40，退款
    var orderType = 0
    //支付时间
    var paidAt = ""
    //订单价格
    var price = 0.0
    //商品名称
    var productTitle = ""
    //商品类型：咨询、直播、课程、督导
    var productType = 0

    fun titleDisplay(): String {
        if(orderType == ObjectOrderType.TYPE_SHOP_WITHDRAW) {
            return "余额提现"
        }
        if(orderType == ObjectOrderType.TYPE_RECHARGE) {
            return "充值"
        }
        if(orderType == ObjectOrderType.TYPE_REFUND) {
            return "退款"
        }
        return productTitle
    }

    fun coverDefaultImage(): Int {
        if(orderType == ObjectOrderType.TYPE_SHOP_WITHDRAW
            || orderType == ObjectOrderType.TYPE_RECHARGE
            || orderType == ObjectOrderType.TYPE_REFUND) {
            return R.drawable.item_wallet_withdraw_detail_icon
        }
        if (productType == ObjectProduct.TYPE_CONSULT) {
            return R.drawable.item_wallet_detail_icon_consult
        }
        if (productType == ObjectProduct.TYPE_LIVE) {
            return R.drawable.item_wallet_detail_icon_live
        }
        if(productType == ObjectProduct.TYPE_COURSE) {
            return R.drawable.item_wallet_detail_icon_course
        }
        return R.drawable.item_wallet_detail_icon_supervise
    }

    fun productTypeImage(): Int {
        if(orderType == ObjectOrderType.TYPE_SHOP_WITHDRAW
            || orderType == ObjectOrderType.TYPE_RECHARGE
            || orderType == ObjectOrderType.TYPE_REFUND) {
            return 0
        }

        if (productType == ObjectProduct.TYPE_CONSULT) {
            return R.drawable.item_wallet_detail_tag_consult
        }
        if (productType == ObjectProduct.TYPE_LIVE) {
            return R.drawable.item_wallet_detail_tag_live
        }
        if(productType == ObjectProduct.TYPE_COURSE) {
            return R.drawable.item_wallet_detail_tag_course
        }
        return R.drawable.item_wallet_detail_tag_supervise
    }

    fun paidTimeDes(): String {
        if (paidAt == null) paidAt = ""
        if (paidAt.isNotEmpty()) {
            return paidAt.dateT("yyyy.MM.dd  ") + paidAt.dateT("HH:mm:SS")
        }
        return ""
    }

    fun orderTypeDesc(): String {
        if(orderType == ObjectOrderType.TYPE_RECHARGE) {
            return "充值"
        }
        if(orderType == ObjectOrderType.TYPE_CONSUMPTION) {
            return "消费"
        }
        if(orderType == ObjectOrderType.TYPE_REFUND) {
            return "退款"
        }
        if(orderType == ObjectOrderType.TYPE_SHOP_WITHDRAW) {
            return "提现"
        }
        return ""
    }

    fun groupName(): String {
        if (!paidAt.isNullOrEmpty()) {
            var date = paidAt.dateT("yyyy-MM")
            return date.replace("-", "年") + "月"
        }
        return ""
    }
}