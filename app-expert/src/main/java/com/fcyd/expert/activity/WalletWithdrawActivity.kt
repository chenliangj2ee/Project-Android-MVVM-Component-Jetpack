package com.fcyd.expert.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.fcyd.expert.databinding.ActivityMyWalletWithdrawBinding
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.base.obs
import com.mtjk.utils.toast
import com.mtjk.view.CashierInputFilter
import java.math.BigDecimal

/**
 * tag==余额提现
 * author:xujunsheng
 * date:2022/02/28
 */
class WalletWithdrawActivity : MyBaseActivity<ActivityMyWalletWithdrawBinding, UserViewModel>() {

    @MyField
    var withdrawal = 0.0

    override fun initCreate() {
        mToolBar.showRight("明细") { toDetail() }
        fullscreenTransparentBar(true)
        initWithdrawEdit()
        updateMoneyAvailable()
    }

    override fun initClick() {
        super.initClick()
        mBinding.todetail.click { toDetail() }
        mBinding.withdraw.click { withdraw() }
    }

    private fun initWithdrawEdit() {
        mBinding.withdrawMoney.filters = arrayOf(CashierInputFilter())
    }

    private fun updateMoneyAvailable() {
        if(withdrawal >= 0.0) {
            mBinding.leftIncome.text = "可提现金额¥$withdrawal"
        }
    }


    private fun toDetail() {
        goto(WalletWithdrawListActivity::class.java)
    }

    private fun withdraw() {
        var moneyStr = mBinding.withdrawMoney.text?.toString()?.trim()
        if(moneyStr.isNullOrEmpty()) {
            toast("请输入提现金额")
            return
        }
        var amount = moneyStr.toFloat()
        if(amount > withdrawal) {
            toast("超出可提现金额")
            return
        } else if(amount <= 0) {
            toast("请输入提现金额")
            return
        }
        //提现操作
        mViewModel.WalletWithdraw(amount).obs(this@WalletWithdrawActivity) {
            it.y {
                withdrawal -= amount
                withdrawal = if(withdrawal >= 0.0) withdrawal else 0.0
                withdrawal = withdrawal.roundTo2DecimalPlaces()
                afterWithdraw()
            }

        }
    }

    private fun setWithdrawResult() {
        var intent = Intent()
        intent.putExtra("withdrawal", withdrawal)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun afterWithdraw() {
        mBinding.withdraw.visibility = View.GONE
        mBinding.withdrawAmountLayout.visibility = View.GONE
        mBinding.empty.visibility = View.VISIBLE
        mBinding.step2.isChecked = true
        mBinding.withdraw.isClickable = false
        updateMoneyAvailable()
        setWithdrawResult()
    }

    fun Double.roundTo2DecimalPlaces() =
        BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()

}