package com.mtjk.obj

/**
 * 订单状态
 * author:chenliang
 * date:2021/11/30
 */
object ObjectOrder {

    //全部
    const val STATUS_ALL = 0

    //待支付
    const val STATUS_NO_PAY = 10

    //现金待支付
    const val STATUS_CASHNO_PAY = 20

    //待确认
    const val STATUS_WAIT_CONFIRM = 60

    //进行中
    const val STATUS_DOING = 70

    //待评价
    const val STATUS_WAIT_EVALUATE = 85

    //已完成
    const val STATUS_FINISH = 75

    //已取消
    const val STATUS_CANCEL = 80

    //待反馈
    const val STATUS_WAIT_FEEDBACK = 85

    //结束咨询
    const val STATE_STOP_CONSULT = STATUS_FINISH
}