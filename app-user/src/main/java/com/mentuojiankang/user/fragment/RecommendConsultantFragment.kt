package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.ConsultInfoActivity
import com.mentuojiankang.user.activity.FragmentActivity
import com.mentuojiankang.user.bean.BeanConsult
import com.mentuojiankang.user.databinding.FragmentRecommendConsultantBinding
import com.mentuojiankang.user.databinding.ItemRecommendConsultantBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.show
import kotlinx.android.synthetic.main.item_recommend_consultant.view.*
import kotlinx.android.synthetic.main.layout_recommend_module_title.view.*

/**
 * tag==首页推荐咨询/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class RecommendConsultantFragment :
    MyBaseFragment<FragmentRecommendConsultantBinding, DefaultViewModel>() {
    override fun initOnCreateView() {
        mRootView.show(false)
        with(mBinding) {
            root.more.click { goto(FragmentActivity::class.java, "fragment", "/user/consult", "title", "咨询") }
            mBinding.root.title.text = "咨询疏导"
            consultantRefresh.disable()
        }

    }

    fun addData(list: ArrayList<BeanConsult>) {

        if(list.isNullOrEmpty())
            return
        mRootView.show(true)
        with(mBinding) {
            consultantRefresh.bindData<BeanConsult> { consult ->
                consult.resetDefault()
                with(consult.binding as ItemRecommendConsultantBinding) {
                    bean = consult
                    this.root.click { consultInfoActivity(consult) }
                    this.root.book.click { consultInfoActivity(consult) }
                }


            }
            consultantRefresh.addDatas(list)
        }

    }

    /**
     * 进入详情
     */
    private fun consultInfoActivity(bean: BeanConsult) {
        goto(ConsultInfoActivity::class.java, "bean", bean)
    }
}