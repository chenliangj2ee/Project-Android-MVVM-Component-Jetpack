package com.fcyd.expert.activity

import android.view.View
import android.widget.TextView
import com.fcyd.expert.R
import com.fcyd.expert.bean.BeanWalletDetail
import com.fcyd.expert.bean.BeanWalletWithdraw
import com.fcyd.expert.databinding.ActivityMyWalletWithdrawListBinding
import com.fcyd.expert.databinding.ItemWalletWithdrawDetailBinding
import com.fcyd.expert.vm.UserViewModel
import com.gavin.com.library.PowerfulStickyDecoration
import com.gavin.com.library.listener.OnGroupClickListener
import com.gavin.com.library.listener.PowerGroupListener
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.DimenUtil
import com.mtjk.utils.goto
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
        mBinding.refresh.recyclerView?.addItemDecoration(
            PowerfulStickyDecoration.Builder.init(object: PowerGroupListener {
                override fun getGroupName(position: Int): String {
                    return mBinding.refresh.getData<BeanWalletWithdraw>()?.get(position)?.groupName()
                }

                override fun getGroupView(position: Int): View {
                    var view = layoutInflater.inflate(R.layout.wallet_item_decoration_content, null, false)
                    view.findViewById<TextView>(R.id.group_name).text =
                        mBinding.refresh.getData<BeanWalletWithdraw>()?.get(position)?.groupName()
                    return view;
                }

            }).setOnClickListener(
                object: OnGroupClickListener {
                    override fun onClick(position: Int, id: Int) {
                        //TODO 点击展开列表
                    }
                }
            ).setGroupHeight(DimenUtil.dp2Px(this@WalletWithdrawListActivity,30)).build()
        )
    }

    private fun toCustomerService() {
        IM.gotoServiceChat()
    }
}