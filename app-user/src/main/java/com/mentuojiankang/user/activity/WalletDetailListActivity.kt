package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanWalletDetail
import com.mentuojiankang.user.databinding.ActivityMyWalletDetailBinding
import com.mentuojiankang.user.databinding.ItemWalletDetailBinding
import com.mentuojiankang.user.vm.UserViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.load
import com.mtjk.view.WalletItemDecoration
import com.tencent.qcloud.tuikit.tuiconversation.util.IM

/**
 * tag==钱包明细
 * author:xujunsheng
 * date:2022/02/28
 */
@MyClass(mToolbarTitle = "明细")
class WalletDetailListActivity : MyBaseActivity<ActivityMyWalletDetailBinding, UserViewModel>(){
    override fun initCreate() {
        mToolBar.showRight("客服", { toCustomerService() })
        mBinding.refresh.bindData<BeanWalletDetail>(::initItem)
        updateRecyclerView()
        loadWalletDetailData()
    }

    private fun loadWalletDetailData() {
        with(mBinding) {
            refresh.loadData {
                mViewModel.getWalletDetailList(refresh.pageIndex, refresh.pageSize, "", "").obs(this@WalletDetailListActivity) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
            }
        }
    }

    private fun initItem(it: BeanWalletDetail) {
        with(it.binding as ItemWalletDetailBinding) {
            data = it
            var coverBg = it.coverDefaultImage()
            if(coverBg > 0) this.icon?.setImageResource(coverBg)
            var typeImage = it.productTypeImage()
            if(typeImage > 0) this.tag?.setImageResource(typeImage)
        }
    }

    private fun toCustomerService() {
        IM.gotoChat("1495673911154786306", "吱吱客服")
    }

    private fun updateRecyclerView() {
        with(mBinding) {
            refresh.recyclerView?.addItemDecoration(
                WalletItemDecoration(this@WalletDetailListActivity, object : WalletItemDecoration.OnGroupListener {
                    override fun getGroupName(position: Int): String? {
                        return refresh.getData<BeanWalletDetail>()?.get(position)?.groupName()
                    }
                })
            )
        }
    }
}