package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.bean.BeanTest
import com.mentuojiankang.user.databinding.*
import com.mentuojiankang.user.vm.HistoryViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.toast

/**
 * tag==浏览足迹测评/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class HistoryEvaluationListFragment : MyBaseFragment<FragmentHistoryEvaluationListBinding, HistoryViewModel>() {
    override fun initOnCreateView() {

        with(mBinding) {
            refresh.bindData<BeanTest> {
                with(it.binding as ItemHistoryTestBinding) {
                    data = it
                    root.click { toast("我被点击了") }
                }

            }

            refresh.loadData {
                activity?.let {
                    mViewModel.getHistoryTests(refresh.pageIndex, refresh.pageSize).obs(it) {
                        it.y {
                            refresh.addDatas(it.records)
                        }
                    }
                }
            }

//            refresh.test(BeanTest::class.java,10)d
        }

    }


}