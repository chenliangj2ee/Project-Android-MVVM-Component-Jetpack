package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.obj.ObjectOrder
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.dateT

/**
 * author:chenliang
 * date:2021/11/18
 */
class BeanOrder : MyBaseBean() {
    var orderDate: String = ""//下单时间
    var orderId: String = ""
    var orderServer: Int = 0
    var orderStatus: Int = 0
    var paidAmount: Double = 0.0
    var orderItems: BeanProduct? = null
    var roomId = 0
    var userAccountId = ""
    var expertAccountId = ""
    var consultType = 0
    var commentState = -1
    var feedBackState = -1


    fun statusDes(): String {
        when (orderStatus) {
            ObjectOrder.STATUS_NO_PAY -> return "待支付"
            ObjectOrder.STATUS_DOING -> return "进行中"
            ObjectOrder.STATUS_WAIT_EVALUATE -> return "待评价"
            ObjectOrder.STATUS_WAIT_CONFIRM -> return "待确认"
            ObjectOrder.STATUS_FINISH -> return "已完成"
            ObjectOrder.STATUS_CANCEL -> return "已取消"
        }

        return ""
    }

    fun typeDes(): String {
        ObjectProduct.TYPE_COURSE
        when (consultType) {
            1 -> return "视频咨询"
            2 -> return "语音咨询"
        }
        return ""
    }

    fun orderDateDes()=orderDate.dateT("yyyy.MM.dd HH:mm:ss")
}