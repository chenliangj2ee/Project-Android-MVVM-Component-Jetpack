package com.mentuojiankang.user.activity

import android.graphics.Color
import android.widget.CompoundButton
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.bean.BeanCreateConfirmOrder
import com.mentuojiankang.user.bean.BeanParams
import com.mentuojiankang.user.bean.BeanRecharge
import com.mentuojiankang.user.databinding.ActivityRechargeBinding
import com.mentuojiankang.user.databinding.ItemMyRechargeBinding
import com.mentuojiankang.user.dialog.PaymentSuccessDialog
import com.mentuojiankang.user.utils.Ali_Pay
import com.mentuojiankang.user.utils.WX_Pay
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_my_evaluation.*
import kotlinx.android.synthetic.main.activity_payment_confirm.*
import kotlinx.android.synthetic.main.activity_recharge.*
import kotlinx.android.synthetic.main.activity_recharge.refresh
import kotlinx.android.synthetic.main.activity_vip_card.*

/**
 * tag==充值
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "充值")
class RechargeActivity : MyBaseActivity<ActivityRechargeBinding, OrderViewModel>(),
    CompoundButton.OnCheckedChangeListener {
    var productId = ""
    var params: BeanParams = BeanParams()
    var payType = 0
    var successorderid = ""
    var mposition = -1
    var rechargeprice = 0.0
    lateinit var beanCreateConfirmOrder: BeanCreateConfirmOrder

    @MyField
    var usermoney = 0.0
    override fun initCreate() {

        refresh.bindData<BeanRecharge> { rechargebean ->
            with(rechargebean.binding as ItemMyRechargeBinding) {
                usersmoney.setText(usermoney.toString() + "币")
                data = rechargebean
                root.click {
                    refresh.selected(rechargebean.itemPosition)
                    productId = rechargebean.id
                    rechargeprice = rechargebean.price
                }
            }
        }
//        refresh.test(BeanRecharge::class.java, 6)
        refresh.loadData {
            mViewModel.getOrderList().obs(this) {
                it.y {
                    refresh.addDatas(it)
                }
                it.n { toast(it) }
            }
        }
    }

    override fun initClick() {
        super.initClick()
        recharge_wx.setOnCheckedChangeListener(this)
        recharge_zfb.setOnCheckedChangeListener(this)
        nowRecharge.click {
            if (productId.isNotEmpty()) {
                if (payType == 0) {
                    toast("请选择支付方式")
                } else {
                    createOrder()
                }
            } else {
                toast("请选择充值额度")
            }

        }
    }

    private fun createOrder() {
        log("创建并确认订单")
        if (params == null)
            params = BeanParams()
        beanCreateConfirmOrder = BeanCreateConfirmOrder()
        beanCreateConfirmOrder.payAmount = rechargeprice
        beanCreateConfirmOrder.payType = payType
        initVM(OrderViewModel::class.java).createCourseConfirmOrderInfo(
            productId, 400, beanCreateConfirmOrder, params
        ).obs(this) {
            it.y {
                successorderid = it.orderId
                if (successorderid.isNotEmpty())
                    confirm()
            }
            it.n {
                confirmFaile()
            }
        }
    }

    /**
     * 支付订单，
     */
    private fun confirm() {
        log("选择支付方式")
        var beanUser =  getBeanUser()
        if (recharge_wx.isChecked) {
            log("确认订单orderId：successorderid====" + successorderid + " beanUser?.backWxZfb" + beanUser?.backWxZfb + "======用户token" + beanUser?.token)
            WX_Pay(this).jumpWX(beanUser?.token, successorderid)
        } else if (recharge_zfb.isChecked) {
            Ali_Pay(this).payV2()
        } else {
            toast("余额不足请选择支付方式")
        }

    }

    @Subscribe(code = BusCode.PAYMENT_RESULT)
    fun refreshList() {
        log("请求订单基础信息")
        mViewModel.getorderinfo(successorderid).obs(this) {
            it.y {
                log("支付状态："+it.orderStatus)
                if (it.orderStatus == 50 || it.orderStatus == 90) {
                    confirmSuccess()
                } else {
                    confirmFaile()
                }
            }
            it.n {
                toast(it)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.recharge_wx -> {
                if (isChecked) {
                    payType = 30
                    recharge_zfb.isChecked = false
                } else {
                    payType = 0
                }
            }
            R.id.recharge_zfb -> {
                if (isChecked) {
                    payType = 40
                    recharge_wx.isChecked = false
                } else {
                    payType = 0
                }
            }
            else -> {
                toast("支付宝微信都没选")
            }
        }
    }

    /**
     * 充值成功弹框
     */
    fun confirmSuccess() {
        postDelayed(1000, {
            var dialog = PaymentSuccessDialog()
            dialog.confire(true)
            dialog.show(this)
            postDelayed(3000) {
                dialog.dismiss()
                send(BusCode.RECHARGE_SUCCESS)
                finish()
            }
        })

    }

    /**
     * 充值失败弹窗
     */
    fun confirmFaile() {
        postDelayed(1000, {
            var dialog = PaymentSuccessDialog()
            dialog.confire(false)
            dialog.show(this)
            postDelayed(3000) {
                dialog.dismiss()
            }
        })
    }
}