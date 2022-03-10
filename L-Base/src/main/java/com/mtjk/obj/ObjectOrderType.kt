package com.mtjk.obj

object ObjectOrderType {

    //C客户充值
    const val TYPE_RECHARGE = 10

    //C客户消费, 需要计算店铺收入
    const val TYPE_CONSUMPTION = 20

    //user退款
    const val TYPE_REFUND = 30

    //店铺提现
    const val TYPE_SHOP_WITHDRAW = 40

    //shop充值
    const val TYPE_SHOP_RECHARGE = 50

    // shop消费, 需要计算店铺收入
    const val TYPE_SHOP_CONSUMPTION = 60

    // shop退款
    const val TYPE_SHOP_REFUND = 70

    // 咨询师充值
    const val TYPE_SHOP_EXPERT_RECHARGE = 80

    // 咨询师消费, 需要计算店铺收入
    const val TYPE_SHOP_EXPERT_CONSUMPTION = 90

    // 咨询师退款
    const val TYPE_SHOP_EXPERT_REFUND = 100

}