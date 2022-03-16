package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanWalletWithdraw
import com.fcyd.expert.databinding.ActivityMyWalletWithdrawListBinding
import com.fcyd.expert.databinding.ItemWalletWithdrawDetailBinding
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.goto
import com.mtjk.view.WalletItemDecoration
import com.tencent.qcloud.tuikit.tuiconversation.util.IM

/**
 * tag==提现明细
 * author:xujunsheng
 * date:2022/02/28
 */
@MyClass(mToolbarTitle = "提现明细")
class WalletWithdrawListActivity : MyBaseActivity<ActivityMyWalletWithdrawListBinding, UserViewModel>() {
    override fun initCreate() {
        mToolBar.showRight("客服") { toCustomerService() }
        mBinding.refresh.bindData<BeanWalletWithdraw>(::initItem)
        updateRecyclerView()
        loadWalletDetailData()
    }

    private fun initItem(it: BeanWalletWithdraw) {
        with(it.binding as ItemWalletWithdrawDetailBinding) {
            data = it
        }
    }

    private fun loadWalletDetailData() {
        with(mBinding) {
            refresh.loadData {
                mViewModel.getWalletWithdrawList(refresh.pageIndex, refresh.pageSize, "", "").obs(this@WalletWithdrawListActivity) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
            }
        }
    }

    private fun updateRecyclerView() {
        with(mBinding) {
            refresh.recyclerView?.addItemDecoration(
                WalletItemDecoration(this@WalletWithdrawListActivity, object : WalletItemDecoration.OnGroupListener {
                    override fun getGroupName(position: Int): String? {
                        return refresh.getData<BeanWalletWithdraw>()?.get(position)?.groupName()
                    }
                })
            )
        }
    }

    private fun toCustomerService() {
        IM.gotoChat("1495673911154786306", "吱吱客服")
    }
}