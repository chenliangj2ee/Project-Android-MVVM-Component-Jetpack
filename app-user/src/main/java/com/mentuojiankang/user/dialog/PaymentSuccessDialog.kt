package com.mentuojiankang.user.dialog

import com.mentuojiankang.user.R
import com.mentuojiankang.user.databinding.DialogPaymentSucessBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog

/**
 * tag==支付成功
 * author:chenliang
 * date:2021/11/4
 */
@MyClass(mDialogTransparent = true)
class PaymentSuccessDialog() : MyBaseDialog<DialogPaymentSucessBinding>() {
    var confirmSuccessOrFaile=false
    fun confire(successOrfaile: Boolean) {
        this.confirmSuccessOrFaile=successOrfaile
    }

    override fun initCreate() {
        with(mBinding) {
            if (confirmSuccessOrFaile){
                mBinding.confirmText.setText("支付成功")
            }else{
                mBinding.confirmText.setText("支付失败")
                mBinding.icon.setImageResource(R.drawable.icon_payment_error)
            }
        }
    }
}