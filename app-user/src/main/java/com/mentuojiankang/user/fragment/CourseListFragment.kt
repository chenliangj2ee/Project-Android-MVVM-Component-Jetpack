package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.databinding.FragmentCourseListBinding
import com.mentuojiankang.user.databinding.ItemRecommendVideoBinding
import com.mentuojiankang.user.vm.CourseViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.goto

/**
 * tag==课程列表/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class CourseListFragment : MyBaseFragment<FragmentCourseListBinding, CourseViewModel>() {

    @MyField
    var type = ""
    override fun initOnCreateView() {

        with(mBinding) {

            refresh.bindData<BeanCourse> {
                it.resetDefault()
                with(it.binding as ItemRecommendVideoBinding) {
                    data = it
                    root.goto(CourseInfoActivity::class.java,"courseId",it.id)

                }

            }
            refresh.loadData {
                activity?.let {
                    mViewModel.getIndexCourseList(type, refresh.pageIndex, refresh.pageSize).obs(it) {
                        it.c { refresh.addCache(it.records) }
                        it.y { refresh.addDatas(it.records) }
                    }
                }
            }

        }

    }


}