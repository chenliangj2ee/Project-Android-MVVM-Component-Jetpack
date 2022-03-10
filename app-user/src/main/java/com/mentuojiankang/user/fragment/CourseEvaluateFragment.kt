package com.mentuojiankang.user.fragment

import com.mtjk.bean.BeanRecommend
import com.mentuojiankang.user.databinding.FragmentCourseEvaluateBinding
import com.mentuojiankang.user.databinding.ItemCourseEvaluateBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment


/**
 * tag==评价/课程
 * author:chenliang
 * date:2021/11/4
 */
class CourseEvaluateFragment : MyBaseFragment<FragmentCourseEvaluateBinding, DefaultViewModel>() {
    override fun initOnCreateView() {
        with(mBinding) {
            videoRefresh.setEnableRefresh(false)
            videoRefresh.setEnableLoadMore(false)
            videoRefresh.bindData<BeanRecommend>(::initItem)
            videoRefresh.test(BeanRecommend::class.java, 10)
        }

    }

    private fun initItem(it: BeanRecommend) {
        with(it.binding as ItemCourseEvaluateBinding) {
            data = it
        }
    }

}