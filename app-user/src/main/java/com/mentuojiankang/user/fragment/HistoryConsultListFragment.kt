package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.bean.BeanConsultant
import com.mentuojiankang.user.databinding.FragmentHistoryConsultListBinding
import com.mentuojiankang.user.databinding.ItemHistoryConsultantBinding
import com.mentuojiankang.user.vm.HistoryViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.toast

/**
 * tag==浏览足迹咨询/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class HistoryConsultListFragment : MyBaseFragment<FragmentHistoryConsultListBinding, HistoryViewModel>() {
    override fun initOnCreateView() {

        with(mBinding) {

            refresh.bindData<BeanConsultant> {
                with(it.binding as ItemHistoryConsultantBinding) {
                    it.initDate()
                    data = it
                    root.click { toast("我被点击了") }
                }

            }
            refresh.loadData {

                activity?.let {
                    mViewModel.getHistoryConsults(refresh.pageIndex, refresh.pageSize).obs(it) {
                        it.c {
                            if (refresh.pageIndex == 1) {
                                BeanConsultant.date.clear()
                                refresh.addCache(it.records)
                            }

                        }
                        it.y {
                            if (refresh.pageIndex == 1) {
                                BeanConsultant.date.clear()
                            }
                            refresh.addDatas(it.records)
                        }
                    }
                }
            }


        }

    }


}