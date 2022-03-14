package com.fcyd.expert.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.fcyd.expert.databinding.ActivityMyWalletWithdrawBinding
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.base.obs
import com.mtjk.utils.toast
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
        mToolBar.showRight("明细", { toDetail() })
        fullscreenTransparentBar(true)
        updateMoneyAvailable()
        initMoneyEdit()
    }

    override fun initClick() {
        super.initClick()
        mBinding.todetail.click { toDetail() }
        mBinding.withdraw.click { withdraw() }
    }

    private fun updateMoneyAvailable() {
        if(withdrawal >= 0.0) {
            mBinding.leftIncome.text = "可提现金额¥" + withdrawal
        }
    }

    private fun initMoneyEdit() {
        mBinding.withdrawMoney.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                var editStr = editable.toString().trim()
                var posDot = editStr.indexOf(".");
                //不允许输入3位小数,超过三位就删掉
                if (posDot < 0) {
                    return
                }
                if (editStr.length - posDot - 1 > 2) {
                    editable?.delete(posDot + 3, posDot + 4)
                }
            }
        })
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
            it.n {
                toast("提现申请失败，请检查网络")
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