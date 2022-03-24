package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.databinding.FragmentCourseInfoRecommendBinding
import com.mentuojiankang.user.databinding.ItemRecommendVideoBinding
import com.mentuojiankang.user.vm.CourseViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto

/**
 * tag==推荐/课程
 * author:chenliang
 * date:2021/11/4
 */
class CourseRecommendFragment : MyBaseFragment<FragmentCourseInfoRecommendBinding, CourseViewModel>() {

    @MyField
    var courseId: String = ""

    override fun initOnCreateView() {
        with(mBinding) {
            refresh.disable()
            refresh.bindData<BeanCourse> {
                with(it.binding as ItemRecommendVideoBinding) {
                    data = it
                    root.click {
                        goto(CourseInfoActivity::class.java, "courseId", data?.id!!)
                        activity?.finish()
                    }
                }
            }.loadData {
                loadRecommendData()
            }
        }
    }

    private fun loadRecommendData() {
        with(mBinding) {
            mViewModel.getCourseRecommendList(courseId).obs(this@CourseRecommendFragment) {
                it.c { refresh.addCache(it) }
                it.y { refresh.addDatas(it) }
            }
        }
    }
}