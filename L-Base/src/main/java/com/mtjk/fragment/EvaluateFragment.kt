package com.mtjk.fragment

import com.chenliang.annotation.MyRoute
import com.mtjk.act.EvaluateActivity
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.databinding.BaseFragmentEvaluateBinding
import com.mtjk.base.databinding.ItemEvaluateBinding
import com.mtjk.base.obs
import com.mtjk.bean.BeanRecommend
import com.mtjk.utils.goto
import com.mtjk.vm.AccountViewModel
import kotlinx.android.synthetic.main.base_fragment_evaluate.*


/**
 * tag==评价
 * author:chenliang
 * date:2021/11/4
 */
@MyRoute(mPath = "/base/evaluate")
class EvaluateFragment : MyBaseFragment<BaseFragmentEvaluateBinding, AccountViewModel>() {

    var username = ""

    @MyField
    var productId: String? = null;

    @MyField
    var shopId: String? = null;
    override fun initOnCreateView() {
        with(mBinding) {
            refresh.disable()
            refresh.bindData<BeanRecommend>(::initItem)

            if (productId?.isNotEmpty() == true) {
                initProductId(productId!!)
                showAll.goto(EvaluateActivity::class.java, "productId", productId!!, "title", username)
            }
            if (shopId?.isNotEmpty() == true) {
                initShopId(shopId!!)
                showAll.goto(EvaluateActivity::class.java, "shopId", shopId!!, "title", username)
            }
        }


    }


    fun setUserName(name: String) {
        username = name
    }

    fun initProductId(id: String) {
        with(mBinding) {
            refresh.loadData {
                mViewModel.evaluateProductList(id, refresh.pageIndex, refresh.pageSize).obs(this@EvaluateFragment) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
            }
            showAll.goto(EvaluateActivity::class.java, "productId", id!!, "title", username+"")
        }
    }

    fun initShopId(id: String) {
        with(mBinding) {
            refresh.pageIndex = 1
            refresh.loadData {
                mViewModel.evaluateExpertList(id, refresh.pageIndex, refresh.pageSize).obs(this@EvaluateFragment) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
            }
            showAll.goto(EvaluateActivity::class.java, "shopId", id!!, "title", username+"")
        }
    }

    private fun initItem(it: BeanRecommend) {
        with(it.binding as ItemEvaluateBinding) {
            data = it
        }
    }

}