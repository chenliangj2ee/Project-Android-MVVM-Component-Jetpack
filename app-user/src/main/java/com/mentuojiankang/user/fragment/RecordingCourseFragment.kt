package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.databinding.ActivityMyCourseBinding
import com.mentuojiankang.user.databinding.FragmentRecordCourseBinding
import com.mentuojiankang.user.databinding.ItemMyCourseBinding
import com.mentuojiankang.user.vm.CourseViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.goto

/**
 * tag==录播课
 * author:chenliang
 * date:2021/11/3
 */
class RecordingCourseFragment : MyBaseFragment<FragmentRecordCourseBinding, CourseViewModel>() {


    override fun initOnCreateView() {
        with(mBinding) {
            refresh.bindData<BeanCourse>() {
                with(it.binding as ItemMyCourseBinding) {
                    data = it
                    root.goto(CourseInfoActivity::class.java, "courseId", it.id)
                }
            }
            refresh.loadData {
                mViewModel.getMyCourse(refresh.pageIndex, refresh.pageSize).obs(this@RecordingCourseFragment) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
            }
        }
    }


}