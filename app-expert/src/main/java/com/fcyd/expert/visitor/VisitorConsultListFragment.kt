package com.fcyd.expert.visitor

import com.fcyd.expert.bean.BeanVisitor
import com.fcyd.expert.bean.BeanVisitorConsult
import com.fcyd.expert.databinding.FragmentVisitorConsultListBinding
import com.fcyd.expert.databinding.ItemVisitorConsultBinding
import com.fcyd.expert.vm.VisitorViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto

class VisitorConsultListFragment : MyBaseFragment<FragmentVisitorConsultListBinding, VisitorViewModel>() {

    @MyField
    lateinit var visitor: BeanVisitor

    override fun initOnCreateView() {
        bindView()
        loadData()
    }

    private fun bindView() {
        mBinding.refresh.bindData<BeanVisitorConsult> { bean->
            with(bean.binding as ItemVisitorConsultBinding) {
                data = bean
                title.setCompoundDrawables(if(bean.consultTypeDrawable() > 0) resources.getDrawable(bean.consultTypeDrawable()) else null, null, null, null)
                root.click { toConsultDetail(bean) }
            }
        }
    }

    private fun loadData() {
        with(mBinding) {
            mViewModel.getVisitorConsult(visitor.id, refresh.pageIndex, refresh.pageSize).obs(this@VisitorConsultListFragment) {
                it.c { refresh.addCache(it.records) }
                it.y { refresh.addDatas(it.records)}
            }
        }
    }

    fun toConsultDetail(consult: BeanVisitorConsult) {
        if(consult != null) {
            goto(VisitorConsultDetailActivity::class.java, "consult", consult)
        }
    }
}