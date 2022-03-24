package com.mentuojiankang.user.fragment

import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.activity.CourseEvaluateActivity
import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.activity.OrderInfoActivity
import com.mentuojiankang.user.activity.PaymentConfirmActivity
import com.mentuojiankang.user.bean.BeanOrder
import com.mentuojiankang.user.bean.BeanParams
import com.mentuojiankang.user.bean.BeanPayInfo
import com.mentuojiankang.user.databinding.*
import com.mentuojiankang.user.utils.OrderUtil
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.act.TextActivity
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.bean.BeanRecommend
import com.mtjk.obj.ObjectOrder
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.*
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.item_home_tab.view.*
import kotlinx.android.synthetic.main.item_my_order.view.*

/**
 * tag==订单列表/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyRoute(mPath = "/app/myorderlistfragment")
class MyOrderListFragment : MyBaseFragment<FragmentMyOrderListBinding, OrderViewModel>() {

    @MyField
    var status = 0
    override fun initOnCreateView() {
        with(mBinding) {
            refresh.bindData<BeanOrder>(::bindItem)
            refresh.loadData(::loadOrders)
        }
    }


    /**
     * 绑定列表Item
     */
    private fun bindItem(beanOrder: BeanOrder) {
        with(beanOrder.binding as ItemMyOrderBinding) {
            if (beanOrder.orderStatus == ObjectOrder.STATUS_CASHNO_PAY)
                beanOrder.orderStatus = ObjectOrder.STATUS_NO_PAY
            order = beanOrder
            root.evaluate.click { gotoEvaluate(beanOrder) }
            root.pay.click { gotoPay(beanOrder) }
            root.cancelOrder.click { cancelOrder(beanOrder.orderId) }
            root.chat.click { IM.gotoChat(beanOrder.expertAccountId, beanOrder.expertName) }
            root.startLive.click { intoLiveRoom(beanOrder) }
            root.look.goto(TextActivity::class.java, "text", beanOrder.expertPlan)
            root.startCourse.click { gotoCourseInfo(beanOrder) }

            root.click { goto(OrderInfoActivity::class.java, "order", beanOrder) }
        }
    }

    /**
     * 加载订单列表
     */
    private fun loadOrders() {
        with(mBinding.refresh) {
            mViewModel.orderList(status, pageIndex, pageSize)
                .obs(this@MyOrderListFragment) {
                    it.c { addCache(it.records) }
                    it.y { addDatas(it.records) }
                }
        }
    }

    /**
     * 去课程详情
     */
    private fun gotoCourseInfo(beanOrder: BeanOrder) {
        context?.let { OrderUtil.gotoCourseInfo(it, beanOrder) }
    }

    /**
     * 去评价
     */
    private fun gotoEvaluate(beanOrder: BeanOrder) {
        context?.let { OrderUtil.gotoEvaluate(it, beanOrder) }
    }


    /**
     * 继续支付
     */
    private fun gotoPay(bean: BeanOrder) {
        context?.let { OrderUtil.gotoPay(it, bean) }
    }

    /**
     * 支付成功后，刷新订单列表
     */
    @Subscribe(code = BusCode.PAYMENT_RESULT)
    fun refreshList() {
        orderRefresh()
    }

    /**
     * 刷新订单列表
     */
    @Subscribe(code = BusCode.ORDER_REFRESH)
    fun orderRefresh() {
        mBinding.refresh.refresh()
    }

    /**
     * 取消订单
     */
    private fun cancelOrder(orderId: String) {
        OrderUtil.cancelOrder(mViewModel, orderId)
    }

    /**
     * 进入直播室t
     */
    private fun intoLiveRoom(bean: BeanOrder) {
    }


}