package com.mentuojiankang.user.activity

import android.os.Handler
import android.os.Looper
import com.mentuojiankang.user.bean.BeanOrder
import com.mentuojiankang.user.bean.ObjectOrderStatus
import com.mentuojiankang.user.databinding.ActivityOrderInfoBinding
import com.mentuojiankang.user.utils.OrderUtil
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.act.TextActivity
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.*
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import kotlinx.android.synthetic.main.activity_order_info.*

/**
 * tag==订单详情
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "订单详情")
class OrderInfoActivity : MyBaseActivity<ActivityOrderInfoBinding, OrderViewModel>() {

    @MyField
    lateinit var order: BeanOrder

    override fun initCreate() {
        mBinding.order = order
        setToolbarTitle(order.statusDes())
    }

    override fun initClick() {
        super.initClick()
        modify_info.click { goto(TextActivity::class.java, "text", order.expertPlan) }
        cancelOrder.click { OrderUtil.cancelOrder(mViewModel, order.orderId) }
        pay.click { OrderUtil.gotoPay(this, order) }
        delete_order.click {  }
        consult_again.click {  }
        chat.click { IM.gotoChat(order.expertAccountId, order.expertName) }
        remind.click {  }
        evaluate.click { OrderUtil.gotoEvaluate(this, order) }
        start_consult.click { OrderUtil.intoLiveRoom(order) }
        refund.click { }
        service.click {  }
        startCourse.click { OrderUtil.gotoCourseInfo(this, order) }
    }


//以下为倒计时功能，暂时不实现支付倒计时功能
//    private val ORDER_PAID_DURATION = 15 * 60 * 1000

//    private var closeAtLong: Long = 0

//    private var mHandler: Handler = Handler(Looper.getMainLooper())
//
//    private val mRunnable: Runnable = object : Runnable {
//        override fun run() {
//            updatePayTime()
//        }
//    }

//    override fun onDestroy() {
//        mHandler.removeCallbacks(mRunnable)
//        super.onDestroy()
//    }

//    private fun loadOrderInfo() {
//        var createTime = mBinding.order?.orderDate?.dateT("yyyy-MM-dd HH:mm:ss")?.dateToLong("yyyy-MM-dd HH:mm:ss")
//        closeAtLong = if(createTime != null) (createTime + ORDER_PAID_DURATION) else 0
//        mHandler.post(mRunnable)
//    }

//    private fun updatePayTime() {
//        var leftTime = closeAtLong - System.currentTimeMillis()
//        if(mBinding.order?.orderStatus == ObjectOrderStatus.noPayment && leftTime > 0 && leftTime < ORDER_PAID_DURATION) {
//            tip_layout.show(true)
//            var timeStr = timeFromMillis(leftTime)
//            tip_title.text = "剩余${timeStr}，请尽快支付"
//            mHandler.postDelayed(mRunnable, 1_000L)
//        } else {
//            mHandler.removeCallbacks(mRunnable)
//            tip_layout.show(false)
//        }
//    }

//    private fun timeFromMillis(value: Long) : String{
//        var allTime = value / 1000
//        if(allTime < 10) {
//            return "00:0${allTime}"
//        } else if(allTime < 60) {
//            return "00:${allTime}"
//        } else if(allTime < 3600) {
//            var minute = allTime / 60
//            var minStr = if(minute < 10) "0${minute}" else "${minute}"
//            var secondLeft = allTime % 60
//            var secStr = if(secondLeft < 10) "0${secondLeft}" else "${secondLeft}"
//            return "${minStr}:${secStr}"
//        }
//        return ""
//    }

}