package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.TestInfoActivity
import com.mentuojiankang.user.bean.BeanTest
import com.mentuojiankang.user.databinding.FragmentEvaluationListBinding
import com.mentuojiankang.user.databinding.ItemRecommendTestBinding
import com.mentuojiankang.user.vm.TestViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto
import kotlinx.android.synthetic.main.fragment_evaluation_list.view.*

/**
 * tag==测评列表/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class TestListFragment : MyBaseFragment<FragmentEvaluationListBinding, TestViewModel>() {
    @MyField
    var type = ""


    override fun initOnCreateView() {
        mRootView.testRefresh.bindData<BeanTest>(::bindData).loadData(::loadData)
    }

    /**
     * 绑定Item
     */
    fun bindData(bean: BeanTest) {
        with(bean.binding as ItemRecommendTestBinding) {
            data = bean
            root.click { gotoInfo(bean) }
        }
    }

    /**
     * 加载数据
     */
    fun loadData() {
        with(mBinding.testRefresh) {
            mViewModel.getTestList(type, pageIndex, pageSize)
                .obs(this@TestListFragment) {
                    it.c { this.addCache(it.records) }
                    it.y { this.addDatas(it.records) }
                }
        }
    }


    /**
     * 进入详情
     */
    private fun gotoInfo(bean: BeanTest) {
        goto(TestInfoActivity::class.java, "test", bean)
    }

}