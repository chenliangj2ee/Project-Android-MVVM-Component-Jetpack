package com.mentuojiankang.user.activity

import com.mentuojiankang.user.databinding.ActivityMyWalletBinding
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.WebViewActivity
import com.mtjk.base.obs
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe

/**
 * tag==我的钱包
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "我的钱包")
class MyWalletActivity : MyBaseActivity<ActivityMyWalletBinding, OrderViewModel>() {
    var usermoney = 0.0
    override fun initCreate() {
        mToolBar.showRight("账单记录") { goto(WalletDetailListActivity::class.java) }
        refreshmoney()
    }

    override fun initClick() {
        super.initClick()
        with(mBinding) {
            recharge.click { goto(RechargeActivity::class.java, "usermoney", usermoney) }
            usualquestion.click { goto(WebViewActivity::class.java, "url", "file:///android_asset/html/usualquestion.html", "title", "常见问题") }
        }
    }

    @Subscribe(code = BusCode.RECHARGE_SUCCESS)
    fun refreshmoney() {
        mViewModel.getblance().obs(this) {
            it.y {
                mBinding.data = it
                usermoney = it.userMoney
            }
        }
    }
}