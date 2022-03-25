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
import com.mtjk.utils.show

class VisitorConsultListFragment : MyBaseFragment<FragmentVisitorConsultListBinding, VisitorViewModel>() {

    @MyField
    lateinit var visitor: BeanVisitor

    override fun initOnCreateView() {
        bindView()
    }

    private fun bindView() {
        mBinding.refresh.bindData<BeanVisitorConsult> { bean->
            with(bean.binding as ItemVisitorConsultBinding) {
                data = bean
                if(bean.consultTypeDrawable() > 0) {
                    type.show(true)
                    type.setImageResource(bean.consultTypeDrawable())
                } else {
                    type.show(false)
                }
                root.click { toConsultDetail(bean) }
            }
        }.loadData {
            loadList()
        }
    }

    private fun loadList() {
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