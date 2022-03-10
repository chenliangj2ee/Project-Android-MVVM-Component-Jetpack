package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.bean.BeanTest
import com.mentuojiankang.user.databinding.FragmentRecommendEvaluationBinding
import com.mentuojiankang.user.databinding.ItemRecommendTestBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.utils.click
import com.mtjk.utils.toast

/**
 * tag==推荐测评/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class RecommendEvaluationFragment : MyBaseFragment<FragmentRecommendEvaluationBinding, DefaultViewModel>() {
    override fun initOnCreateView() {
        with(mBinding) {
            recommendTestRefresh.setEnableRefresh(false)
            recommendTestRefresh.setEnableLoadMore(false)
            recommendTestRefresh.bindData<BeanTest> {
                with(it.binding as ItemRecommendTestBinding) {
                    data=it
                    root.click { toast("我被点击了") }
                }

            }

            recommendTestRefresh.test(BeanTest::class.java,4)
        }

    }

}