package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.obj.ObjectOrder
import com.mtjk.obj.ObjectOrder.STATUS_CANCEL
import com.mtjk.obj.ObjectOrder.STATUS_NO_PAY
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.dateParse
import com.mtjk.utils.dateT

/**
 * author:chenliang
 * date:2021/11/18
 */
class BeanOrder : MyBaseBean() {
    var orderDate: String = ""  //下单时间
    var paidAt: String = ""     //支付时间
    var orderId: String = ""
    var orderServer: Int = 0
    var orderStatus: Int = 0
    var paidAmount: Double = 0.0
    var orderItems: BeanProduct? = null
    var roomId = 0
    var shopId = ""
    var expertAccountId = ""
    var expertPlan = ""
    var expertName = ""
    var consultType = 0
    var type = 0

    var commentState = -1
    var feedBackState = -1
    var device = 0

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
        when (orderServer) {
            ObjectProduct.TYPE_COURSE -> {
                return ""
            }
            ObjectProduct.TYPE_LIVE_COURSE -> {
                return "视频咨询"
            }
            ObjectProduct.TYPE_LIVE -> {
                return "视频咨询"
            }
            ObjectProduct.TYPE_CONSULT -> {
                when (consultType) {
                    1 -> return "视频咨询"
                    2 -> return "语音咨询"
                }
            }
        }

        return ""
    }

    fun orderDateDes() = if(orderDate.isNullOrEmpty()) "" else orderDate.dateT("yyyy.MM.dd HH:mm:ss")

    fun paidDateDes() = if(paidAt.isNullOrEmpty()) "" else paidAt.dateT("yyyy.MM.dd HH:mm:ss")

    fun isShowLookCourse(): Boolean {
        if (orderServer != ObjectProduct.TYPE_COURSE) {
            return false
        } else {
            when (orderStatus) {
                STATUS_NO_PAY -> return false
                STATUS_CANCEL -> return false
            }

            return true
        }
    }

    fun tipDesc() :String {
        if(orderStatus == 10) {
            return "超时未支付，订单将自动关闭"
        }
        return "如有疑问，请联系客服咨询"
    }
}