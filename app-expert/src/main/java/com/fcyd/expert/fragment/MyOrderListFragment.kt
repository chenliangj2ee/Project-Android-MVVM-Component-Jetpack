package com.fcyd.expert.fragment

import com.fcyd.expert.activity.ConsultFeedbackActivity
import com.fcyd.expert.bean.BeanOrder
import com.fcyd.expert.databinding.FragmentMyOrderListBinding
import com.fcyd.expert.databinding.ItemMyOrderBinding
import com.fcyd.expert.vm.OrderViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.*
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_studio_media_edit.view.*
import kotlinx.android.synthetic.main.fragment_my_order_list.*
import kotlinx.android.synthetic.main.fragment_my_order_list.view.*
import kotlinx.android.synthetic.main.item_my_order.*

/**
 * tag==订单列表/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class MyOrderListFragment : MyBaseFragment<FragmentMyOrderListBinding, OrderViewModel>() {
    @MyField
    var status = 0
    override fun initOnCreateView() {
        mBinding.orderList.bindData<BeanOrder>(::bindItem).loadData(::loadOrders)
    }

    /**
     * 绑定列表item
     */
    private fun bindItem(bean: BeanOrder) {
        with(bean.binding as ItemMyOrderBinding) {
            this.order = bean
            this.chat.click { IM.gotoChat(bean.userAccountId, "") }
            this.stop.click { stopConsult(bean) }
            this.startLive.click { intoLiveRoom(bean) }
            this.confirm.click { confirm(bean) }
            this.feedback.goto(ConsultFeedbackActivity::class.java, "order", bean)
        }
    }

    /**
     * 加载订单列表数据
     */
    private fun loadOrders() {
        with(mBinding.orderList) {
            mViewModel.orderList(status, pageIndex, pageSize)
                .obs(this@MyOrderListFragment) {
                    it.c { this.addCache(it.records) }
                    it.y { this.addDatas(it.records) }
                }
        }
    }


    /**
     * 进入直播室
     */
    private fun intoLiveRoom(bean: BeanOrder) {

        if (bean.consultType == 1) {

        } else {
            if (bean.userAccountId.isNullOrEmpty()) {
                toast("用户Id为空")
            } else {
                toast("去聊天")
            }
        }


    }

    /**
     * 接单
     */
    private fun confirm(bean: BeanOrder) {
        mViewModel.orderConfirms(bean.orderId).obs(this@MyOrderListFragment) { }
    }

    /**
     * 结束咨询
     */
    private fun stopConsult(bean: BeanOrder) {
        dialog("是否结束本次咨询服务？").y { mViewModel.stopConsult(bean.orderId).obs(this) { } }.show(this)
    }

    /**
     * 刷新列表
     */
    @Subscribe(code = BusCode.ORDER_REFRESH)
    fun orderRefresh() {
        mBinding.orderList.refresh()
    }

}