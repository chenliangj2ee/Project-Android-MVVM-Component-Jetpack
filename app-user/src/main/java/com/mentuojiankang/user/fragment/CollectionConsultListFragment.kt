package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.bean.BeanConsultant
import com.mentuojiankang.user.databinding.*
import com.mentuojiankang.user.vm.FavoriteViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.toast

/**
 * tag==收藏咨询/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class CollectionConsultListFragment : MyBaseFragment<FragmentCollectionConsultListBinding, FavoriteViewModel>() {
    override fun initOnCreateView() {

        with(mBinding) {

            refresh.bindData<BeanConsultant> {
                with(it.binding as ItemRecommendConsultantBinding) {
                        root.click { toast("我被点击了") }
                }

            }
            refresh.loadData {
                activity?.let {
                    mViewModel.getFavoriteConsults(refresh.pageIndex, refresh.pageSize).obs(it) {
                        it.y { refresh.addDatas(it.records ) }
                    }
                }
            }


        }

    }


}