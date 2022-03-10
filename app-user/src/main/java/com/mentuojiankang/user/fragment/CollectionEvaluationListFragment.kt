package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.bean.BeanTest
import com.mentuojiankang.user.databinding.FragmentCollectionEvaluationListBinding
import com.mentuojiankang.user.databinding.ItemRecommendTestBinding
import com.mentuojiankang.user.vm.FavoriteViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.toast

/**
 * tag==收藏测评/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class CollectionEvaluationListFragment : MyBaseFragment<FragmentCollectionEvaluationListBinding, FavoriteViewModel>() {
    override fun initOnCreateView() {

        with(mBinding) {
            refresh.bindData<BeanTest> {
                with(it.binding as ItemRecommendTestBinding) {
                    data = it
                    root.click { toast("我被点击了") }
                }

            }

            refresh.loadData {
                activity?.let {
                    mViewModel.getFavoriteTests(refresh.pageIndex, refresh.pageSize).obs(it) {
                        it.y { refresh.addDatas(it.records ) }
                    }
                }
            }

        }

    }


}