package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.ConsultInfoActivity
import com.mentuojiankang.user.bean.BeanConsult
import com.mentuojiankang.user.databinding.FragmentConsultListBinding
import com.mentuojiankang.user.databinding.ItemRecommendConsultantBinding
import com.mentuojiankang.user.vm.ConsultViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto
import kotlinx.android.synthetic.main.item_recommend_consultant.view.*

/**
 * tag==咨询列表/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class ConsultListFragment : MyBaseFragment<FragmentConsultListBinding, ConsultViewModel>() {
    @MyField
    var type = ""
    override fun initOnCreateView() {
        mBinding.refresh.bindData<BeanConsult> {consult->
            consult.resetDefault()
            with(consult.binding as ItemRecommendConsultantBinding) {
                this.bean = consult
                this.root.click { consultInfoActivity(consult) }
                this.root.book.click {  consultInfoActivity(consult)  }
            }
        }.loadData { httpGetConsultList() }//.test(BeanConsult::class.java, 10)


    }

    private fun httpGetConsultList() {
        with(mBinding) {
            mViewModel.getIndexConsultList(type, refresh.pageIndex, refresh.pageSize)
                .obs(this@ConsultListFragment) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
        }
    }

    /**
     * 进入详情
     */
    fun consultInfoActivity(bean: BeanConsult) {
        goto(ConsultInfoActivity::class.java,"bean",bean)
    }

}