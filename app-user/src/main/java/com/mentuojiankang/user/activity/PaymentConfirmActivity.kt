package com.mentuojiankang.user.activity

import android.widget.CompoundButton
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.*
import com.mentuojiankang.user.databinding.ActivityPaymentConfirmBinding
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
import kotlinx.android.synthetic.main.activity_payment_confirm.*
import java.math.BigDecimal
import kotlin.math.abs

/**
 * tag==支付
 * tag==确认支付
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "确认支付")
class PaymentConfirmActivity : MyBaseActivity<ActivityPaymentConfirmBinding, OrderViewModel>(),
    CompoundButton.OnCheckedChangeListener {
    var cardIds = ArrayList<String>()
    var payType = 0//差额支付方式30-微信，40-支付宝
    var account = false
    var differenceblance: Double = 0.0
    lateinit var beanmoney: BeanMoney
    var blanceMoney = 0.0
    var beancards = ArrayList<BeanCard>()
    lateinit var beanCreateConfirmOrder: BeanCreateConfirmOrder
    var cardposition = 0
    var userMoney = 0.0
    var leftAmount = 0.0
    var selectVipCard = true

    //订单创建并确认后产生的orderId
    var successorderid = ""

    //暂定余额
    var yue = 100
    var endPay = 0.0

    //标记第一次进来只算第一张会员卡
    var firsttime = false

    @MyField
    var params: BeanParams = BeanParams()

    @MyField
    lateinit var order: BeanPayInfo

    @MyField
    var productType = 0

    @MyField
    var payAgain = false

    @MyField
    var orderId = ""

    override fun initCreate() {
        mBinding.order = order
        if (order.paysectionCount > 0)
            selection_num.show(true)
        beanmoney = BeanMoney()
        mViewModel.getblance().obs(this) {
            it.y {
                mBinding.beanmoney = it
                userMoney = it.userMoney
                beancards = it.listWorker
                noblance.show(userMoney <= 0)
                initData()
                if (beancards.size > 0 && selectVipCard) {
                    leftAmount = beancards[0].leftAmount
                    vipCardCanUse.show(true)
                    if (order.payprice > leftAmount) {
                        vipCardCanUse.text = "会员卡减-$leftAmount"
                    } else {
                        vipCardCanUse.text = "会员卡减-" + order.payprice
                    }

                }
            }
        }
        vipCardCanUse.text = beancards?.size.toString()
    }

    override fun initClick() {
        vipCard.goto(VPICardActivity::class.java, "enterway", true)
        payment_wx.setOnCheckedChangeListener(this)
        payment_zfb.setOnCheckedChangeListener(this)
        payment_ye.setOnCheckedChangeListener(this)
        submit.click {
            dialog("确认提交订单吗？").y {
                endPay = payNum.text.toString().toDouble()
                if (endPay > 0 && payType == 0) {
                    if (account) {
                        toast("余额不足请选择微信支付")
                    } else {
                        toast("请选择支付方式")
                    }
                } else {
                    if (payAgain) {
                        payAgain()
                    } else {
                        createOrder()
                    }

                }
            }.show(this)
        }

    }


    //一开始只默认选中一张会员卡，应付=会员卡-课程价
    private fun initData() {
        var cardMoney = 0.0
        var accountMoney = 0.0
        val size = beancards.size
        if (size > 0 && selectVipCard) {//选择会员卡
            cardMoney = beancards[cardposition].leftAmount
            if (cardIds.size > 0) {
                cardIds.clear()
                cardIds.add(beancards[cardposition].id)
            } else {
                cardIds.add(beancards[cardposition].id)
            }
        } else {
            cardIds.clear()
        }

        if (account) {//选择账户余额
            accountMoney = userMoney
        }

//        blanceMoney = (cardMoney + accountMoney) - order.payprice
//        blanceMoney =   differenceblance// BigDecimal(differenceblance).setScale(2, BigDecimal.ROUND_UP).toDouble()
        blanceMoney = BigDecimal(cardMoney.toString()).add(BigDecimal(accountMoney.toString()))
            .subtract(BigDecimal(order.payprice.toString())).setScale(2, BigDecimal.ROUND_UP)
            .toDouble()
        if (blanceMoney > 0) {//余额够用
            payNum.text = ("0.0")
        } else {
            payNum.text = abs(blanceMoney).toString()
        }
    }

    /*
    * 创建订单,并确认
    * body("payAmount", differenceblance, "account", account, "paytype", payType)
    */
    private fun createOrder() {
        endPay = payNum.text.toString().toDouble()
        if (params == null)
            params = BeanParams()
        beanCreateConfirmOrder = BeanCreateConfirmOrder()
        beanCreateConfirmOrder.account = account
        beanCreateConfirmOrder.payType = payType
        beanCreateConfirmOrder.payAmount = endPay
        beanCreateConfirmOrder.cardIds = cardIds
        log("account$account==============payType$payType===========endPay$endPay=========cardIds$cardIds=======productType$productType")
        initVM(OrderViewModel::class.java).createCourseConfirmOrderInfo(
            order.productId, productType, beanCreateConfirmOrder, params
        ).obs(this) {
            it.y {
                successorderid = it.orderId
                //如果余额不够就进行拉起小程序
                if (endPay > 0) {
                    confirm()
                } else {
                    confirmSuccess()
                }
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
        var beanUser = getBeanUser()
        if (payment_wx.isChecked) {
            log("确认订单orderId：successorderid====" + successorderid + " beanUser?.backWxZfb" + beanUser?.backWxZfb + "======用户token" + beanUser?.token + "========usid" + beanUser?.userId)
            WX_Pay(this).jumpWX(beanUser?.token, successorderid)
        } else if (payment_zfb.isChecked) {
            Ali_Pay(this).payV2()
        } else {
            toast("余额不足请选择支付方式")
        }

    }

    private fun payAgain() {
        endPay = payNum.text.toString().toDouble()
        if (params == null)
            params = BeanParams()
        beanCreateConfirmOrder = BeanCreateConfirmOrder()
        beanCreateConfirmOrder.account = account
        beanCreateConfirmOrder.payType = payType
        beanCreateConfirmOrder.payAmount = endPay
        beanCreateConfirmOrder.cardIds = cardIds


        mViewModel.paymentConfirm(orderId, cardIds, payType, account, endPay.toFloat()).obs(this) {
            it.y {
                successorderid = orderId
                //如果余额不够就进行拉起小程序
                if (endPay > 0) {
                    confirm()
                } else {
                    confirmSuccess()
                }
            }
            it.n {
                confirmFaile()
            }
        }
    }

    /**
     * 支付确认成功弹框
     */
    private fun confirmSuccess() {
        var dialog = PaymentSuccessDialog()
        dialog.confire(true)
        dialog.show(this)
        postDelayed(3000) {
            dialog.dismiss()
            if (productType == 100) {
                send(BusCode.PAYMENT_SUCCESS)
            } else {
                goto(MyOrderActivity::class.java)
            }
            finish()
        }

    }

    /**
     * 支付确认失败弹框,留在当前页
     */
    private fun confirmFaile() {
        var dialog = PaymentSuccessDialog()
        dialog.confire(false)
        dialog.show(this)
        postDelayed(3000) {
            dialog.dismiss()
            goto(MyOrderActivity::class.java)
            finish()
        }

    }

    //"订单状态;10:待付款,20:三方付款中,60:已付款,45:待审核,70:专家同意,80:订单取消,85:待评价,100:交易关闭
    @Subscribe(code = BusCode.PAYMENT_RESULT)
    fun confirmOrderState() {
        log("请求订单基础信息")
        mViewModel.getorderinfo(successorderid).obs(this) {
            it.y {
                if (it.orderStatus == 50 || it.orderStatus == 90) {
                    confirmSuccess()
                } else {
                    confirmFaile()
                }
            }

        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.payment_zfb -> {
                if (isChecked) {
                    payType = 40
                    payment_wx.isChecked = false
                } else {
                    payType = 0
                }
            }
            R.id.payment_wx -> {
                if (isChecked) {
                    payType = 30
                    payment_zfb.isChecked = false
                } else {
                    payType = 0
                }
                initData()
            }
            R.id.payment_ye -> {
                account = isChecked
                initData()
            }
            else -> {
                toast("支付宝微信都没选")
            }
        }
    }

    @Subscribe(code = BusCode.VIP_CARD_USEORCANLE)
    fun setVipCardUseOrCanle(vipcards: String) {
        cardposition = vipcards.toInt()

        if ("-1".equals(vipcards)) {//为选择会员卡
            selectVipCard = false
            vipCardCanUse.show(false)
            cards_nums.show(true)
            cards_nums.setText(cardIds.size.toString() + "张可用")
        } else {
            selectVipCard = true
            cards_nums.show(false)
            vipCardCanUse.show(true)
            leftAmount = beancards[cardposition].leftAmount
            if (order.payprice > leftAmount) {
                vipCardCanUse.setText("会员卡减-" + leftAmount)
            } else {
                vipCardCanUse.setText("会员卡减-" + order.payprice)
            }
        }
        initData()
    }


}