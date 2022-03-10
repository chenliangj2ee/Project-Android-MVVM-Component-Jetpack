package com.mtjk.obj

object ObjectWithdraw {
    //待付款
    const val STATUS_PENDING_PAY = 10

    //三方付款中
    @Deprecated("")
    const val STATUS_PENDING = 20

    //已付款
    const val STATUS_PAID = 50

    //待专家审核/退款、提现流程中后台审核
    const val STATUS_VERIFYING = 60

    //专家同意
    const val STATUS_AGREE_SERVICE = 70

    //专家（服务完成）已关闭
    const val STATUS_END_SERVER = 75

    //订单取消
    const val STATUS_CANCEL_ORDER = 80

    //待评价,待评价不应该是交易流程中的一个状态
    @Deprecated("")
    const val STATUS_EVALUATE = 85

    //交易完成
    const val STATUS_END_ORDER = 90

    //交易关闭
    const val STATUS_CLOSE_ORDER = 100
}