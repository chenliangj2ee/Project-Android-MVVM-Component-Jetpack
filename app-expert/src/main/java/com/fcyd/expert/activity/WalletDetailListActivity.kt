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
import com.mtjk.utils.goto
import com.mtjk.utils.load
import com.mtjk.utils.toast
import com.tencent.qcloud.tuikit.tuiconversation.util.IM

/**
 * tag==钱包明细
 * author:xujunsheng
 * date:2022/02/28
 */
@MyClass(mToolbarTitle = "明细")
class WalletDetailListActivity : MyBaseActivity<ActivityMyWalletDetailBinding, UserViewModel>() {
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
                        //TODO 点击展开列表
                    }
                }
            ).setGroupHeight(DimenUtil.dp2Px(this@WalletDetailListActivity,30)).build()
        )
    }
}