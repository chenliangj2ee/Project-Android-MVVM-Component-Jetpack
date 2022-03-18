package com.fcyd.expert.visitor

import com.fcyd.expert.bean.BeanVisitor
import com.fcyd.expert.bean.BeanVisitorGauge
import com.fcyd.expert.databinding.FragmentVisitorGaugeListBinding
import com.fcyd.expert.databinding.ItemVisitorGaugeBinding
import com.fcyd.expert.vm.VisitorViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs

class VisitorGaugeListFragment : MyBaseFragment<FragmentVisitorGaugeListBinding, VisitorViewModel>() {

    @MyField
    lateinit var visitor: BeanVisitor

    override fun initOnCreateView() {
        mBinding.refresh.bindData<BeanVisitorGauge> { ::initItem }
            .loadData {
                loadList()
            }
    }

    private fun loadList() {
        with(mBinding) {
            mViewModel.getVisitorGauge(visitor.id, refresh.pageIndex, refresh.pageSize).obs(this@VisitorGaugeListFragment) {
                it.c { refresh.addCache(it.records) }
                it.y { refresh.addDatas(it.records)}
            }
        }
    }

    private fun initItem(it: BeanVisitorGauge) {
        with(it.binding as ItemVisitorGaugeBinding) {
            data = it
        }
    }
}