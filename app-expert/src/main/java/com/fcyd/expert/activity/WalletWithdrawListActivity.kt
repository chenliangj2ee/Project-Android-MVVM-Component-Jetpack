package com.fcyd.expert.activity

import android.view.View
import android.widget.TextView
import com.fcyd.expert.R
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
import com.mtjk.view.DialogYearMonthPicker
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import java.time.LocalDateTime

/**
 * tag==提现明细
 * author:xujunsheng
 * date:2022/02/28
 */
@MyClass(mToolbarTitle = "提现明细")
class WalletWithdrawListActivity : MyBaseActivity<ActivityMyWalletWithdrawListBinding, UserViewModel>() {
    override fun initCreate() {
        mToolBar.showRight("客服") { toCustomerService() }
        mBinding.refresh.bindData<BeanWalletWithdraw>(::initItem).loadData {
            loadWalletDetailData("", "")
        }
        updateRecyclerView()
    }

    private fun initItem(it: BeanWalletWithdraw) {
        with(it.binding as ItemWalletWithdrawDetailBinding) {
            data = it
        }
    }

    private fun loadWalletDetailData(startTime: String, endTime: String) {
        with(mBinding) {
            refresh.loadData {
                mViewModel.getWalletWithdrawList(refresh.pageIndex, refresh.pageSize, startTime, endTime).obs(this@WalletWithdrawListActivity) {
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
                        var detail = mBinding.refresh.getData<BeanWalletWithdraw>()?.get(position)
                        if(detail != null) {
                            dateDialog(detail.year(), detail.month())
                        }
                    }
                }
            ).setGroupHeight(DimenUtil.dp2Px(this@WalletWithdrawListActivity,30)).build()
        )
    }

    private fun dateDialog(initYear: Int, initMonth: Int) {
        DialogYearMonthPicker().showDialog(this, 2020, initYear, initMonth, "时间") { year, month ->
            var start = LocalDateTime.of(year, month, 1, 0, 0, 0).toString()
            var endYear = if (month == 12) (year + 1) else year
            var endMonth = if (month == 12) 1 else (month + 1)
            var end = LocalDateTime.of(endYear, endMonth, 1, 0, 0, 0).toString()
            mBinding.refresh.clearData()
            loadWalletDetailData(start, end)
        }
    }

    private fun toCustomerService() {
        IM.gotoServiceChat()
    }
}