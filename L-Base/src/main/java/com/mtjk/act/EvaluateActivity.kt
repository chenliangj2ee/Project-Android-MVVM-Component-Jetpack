package com.mtjk.act

import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.databinding.ActivityEvaluateBinding
import com.mtjk.base.databinding.ItemEvaluateBinding
import com.mtjk.base.obs
import com.mtjk.bean.BeanRecommend
import com.mtjk.vm.AccountViewModel

/**
 * author:chenliang
 * date:2021/12/22
 */
class EvaluateActivity : MyBaseActivity<ActivityEvaluateBinding, AccountViewModel>() {
    @MyField
    var title = ""

    @MyField
    var productId = ""

    @MyField
    var shopId = ""
    override fun initCreate() {
        setToolbarTitle("评价列表")
        if(title.isNullOrEmpty()){
            setToolbarTitle("评价列表")
        }else{
            setToolbarTitle(title)
        }


        mBinding.refresh.bindData<BeanRecommend>(::initItem)

        if (productId?.isNotEmpty()) {
            initProductId(productId)
        }
        if (shopId?.isNotEmpty()) {
            initShopId(shopId)
        }
    }

    private fun initProductId(id: String) {
        with(mBinding) {
            refresh.loadData {
                mViewModel.evaluateProductList(id, refresh.pageIndex, refresh.pageSize).obs(this@EvaluateActivity) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
            }
        }
    }

    private fun initShopId(id: String) {
        with(mBinding) {
            refresh.pageIndex = 1
            refresh.loadData {
                mViewModel.evaluateExpertList(id, refresh.pageIndex, refresh.pageSize).obs(this@EvaluateActivity) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
            }
        }
    }


    private fun initItem(it: BeanRecommend) {
        with(it.binding as ItemEvaluateBinding) {
            data = it
        }
    }
}
