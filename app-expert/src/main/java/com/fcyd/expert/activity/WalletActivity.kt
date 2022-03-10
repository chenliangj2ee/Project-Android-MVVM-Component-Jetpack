package com.fcyd.expert.activity

import android.app.Activity
import android.content.Intent
import com.fcyd.expert.databinding.ActivityMyWalletBinding
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.WebViewActivity
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto

/**
 * tag==我的钱包
 * author:xujunsheng
 * date:2022/02/28
 */
@MyClass(mToolbarTitle = "我的钱包")
class WalletActivity : MyBaseActivity<ActivityMyWalletBinding, UserViewModel>() {

    val REQUEST_CODE_WITHDRAW = 10

    var withdrawNow = 0.0

    override fun initCreate() {
        mToolBar.showRight("帮助", { toHelp() })
        loadData()
    }

    fun loadData() {
        mViewModel.getWalletInfo().obs(this@WalletActivity) {
            it.y {
                mBinding.data = it
                withdrawNow = it.withdrawal
                updateWithdrawAvailble()
            }
        }
    }

    override fun initClick() {
        super.initClick()
        mBinding.withdraw.click { toWithDraw() }
        mBinding.detail.click { toDetail() }
    }

    private fun toHelp() {
        goto(WebViewActivity::class.java, "url", "file:///android_asset/html/user_about.html", "title", "钱包帮助")
    }

    /*
    * 跳转明细
    * */
    fun toDetail() {
        goto(WalletDetailListActivity::class.java)
    }

    private fun updateWithdrawAvailble() {
        mBinding.leftIncome.text = "可用余额" + withdrawNow + "元"
    }

    /*
    * 跳转提现
    * */
    fun toWithDraw() {
        val intent = Intent(this, WalletWithdrawActivity::class.java)
        intent.putExtra("withdrawal", withdrawNow)
        startActivityForResult(intent, REQUEST_CODE_WITHDRAW)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_WITHDRAW) {
            withdrawNow = data?.getDoubleExtra("withdrawal", 0.0)!!
            updateWithdrawAvailble()
        }
    }
}
