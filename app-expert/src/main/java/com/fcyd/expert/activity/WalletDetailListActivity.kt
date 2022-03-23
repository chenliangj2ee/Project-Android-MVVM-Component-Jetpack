package com.fcyd.expert.activity

import android.view.View
import android.widget.TextView
import com.fcyd.expert.R
import com.fcyd.expert.bean.BeanWalletDetail
import com.fcyd.expert.databinding.ActivityMyWalletDetailBinding
import com.fcyd.expert.databinding.ItemWalletDetailBinding
import com.fcyd.expert.vm.UserViewModel
import com.gavin.com.library.PowerfulStickyDecoration
import com.gavin.com.library.listener.OnGroupClickListener
import com.gavin.com.library.listener.PowerGroupListener
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.DimenUtil
import com.mtjk.utils.click
import com.mtjk.utils.load
import com.mtjk.utils.show
import com.mtjk.view.DialogYearMonthPicker
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import java.time.LocalDateTime

/**
 * tag==钱包明细
 * author:xujunsheng
 * date:2022/02/28
 */
@MyClass(mToolbarTitle = "明细")
class WalletDetailListActivity : MyBaseActivity<ActivityMyWalletDetailBinding, UserViewModel>() {

    private var selectYear = 0

    private var selectMonth = 0

    override fun initCreate() {
        mToolBar.showRight("客服", { toCustomerService() })
        mBinding.refresh.bindData<BeanWalletDetail>(::initItem).loadData {
            loadWalletDetailData("", "")
        }
        updateRecyclerView()

    }

    private fun loadWalletDetailData(startTime: String, endTime: String) {
        with(mBinding) {
            refresh.loadData {
                mViewModel.getWalletDetailList(refresh.pageIndex, refresh.pageSize, startTime, endTime).obs(this@WalletDetailListActivity) {
                    it.c {
                        refresh.addCache(it.records)
                        checkEmptyList(it.records)
                    }
                    it.y {
                        refresh.addDatas(it.records)
                        checkEmptyList(it.records)
                    }
                }
            }
        }
    }

    override fun initClick() {
        super.initClick()
        mBinding.root.findViewById<View>(R.id.empty_group)?.click {
            dateDialog(
                if(selectYear > 0) selectYear else 0,
                if(selectMonth > 0) selectMonth else 0)
        }
    }

    private fun checkEmptyList(list: List<BeanWalletDetail>?) {
        if(selectYear <= 0 || selectMonth <= 0) {
            return
        }
        var groupView  = mBinding.root.findViewById<View>(R.id.empty_group)
        groupView?.show(list.isNullOrEmpty())
        var month = if(selectMonth > 10) selectMonth.toString() else ("0" + selectMonth.toString())
        groupView?.findViewById<TextView>(R.id.group_name)?.text = "${selectYear}年${month}月"
    }

    private fun initItem(it: BeanWalletDetail) {
        with(it.binding as ItemWalletDetailBinding) {
            data = it
            var bgRes = it.coverDefaultImage()
            if(bgRes > 0) this.icon?.setBackgroundResource(bgRes)
            var imageRes = it.productTypeImage()
            if(imageRes > 0) this.tag?.setImageResource(it.productTypeImage())
            if(!it.coverImage.isNullOrEmpty()) this.icon?.load(it.coverImage)
        }
    }

    private fun toCustomerService() {
        IM.gotoServiceChat()
    }

    private fun updateRecyclerView() {
        mBinding.refresh.recyclerView?.addItemDecoration(
            PowerfulStickyDecoration.Builder.init(object: PowerGroupListener {
                override fun getGroupName(position: Int): String {
                    return mBinding.refresh.getData<BeanWalletDetail>()?.get(position)?.groupName()
                }

                override fun getGroupView(position: Int): View {
                    var view = layoutInflater.inflate(R.layout.wallet_item_decoration_content, null, false)
                    view.findViewById<TextView>(R.id.group_name).text =
                        mBinding.refresh.getData<BeanWalletDetail>()?.get(position)?.groupName()
                    return view;
                }

            }).setOnClickListener(
                object: OnGroupClickListener {
                    override fun onClick(position: Int, id: Int) {
                        var detail = mBinding.refresh.getData<BeanWalletDetail>()?.get(position)
                        if(detail != null) {
                            dateDialog(detail.year(), detail.month())
                        }
                    }
                }
            ).setGroupHeight(DimenUtil.dp2Px(this@WalletDetailListActivity,30)).build()
        )
    }

    private fun dateDialog(initYear: Int, initMonth: Int) {
        DialogYearMonthPicker().showDialog(this, 2020, initYear, initMonth, "时间") { year, month ->
            var start = LocalDateTime.of(year, month, 1, 0, 0, 0).toString()
            var endYear = if (month == 12) (year + 1) else year
            var endMonth = if (month == 12) 1 else (month + 1)
            var end = LocalDateTime.of(endYear, endMonth, 1, 0, 0, 0).toString()
            selectYear = year
            selectMonth = month
            mBinding.refresh.clearData()
            loadWalletDetailData(start, end)
        }
    }
}