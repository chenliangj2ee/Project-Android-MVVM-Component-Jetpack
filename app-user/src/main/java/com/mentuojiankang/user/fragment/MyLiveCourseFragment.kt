package com.mentuojiankang.user.fragment

import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.bean.BeanLiveCourse
import com.mentuojiankang.user.databinding.FragmentMyLiveCourseListBinding
import com.mentuojiankang.user.databinding.ItemMyLiveCourseBinding
import com.mentuojiankang.user.vm.LiveViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.load

/**
 * tag==直播课程/Fragment
 */
@MyRoute(mPath = "/app/mylivecoursefragment")
class MyLiveCourseFragment : MyBaseFragment<FragmentMyLiveCourseListBinding, LiveViewModel>() {

    override fun initOnCreateView() {
        with(mBinding) {
            refresh.bindData<BeanLiveCourse>(::bindItem)
            refresh.loadData(::loadLiveCourse)
        }
    }

    private fun bindItem(course: BeanLiveCourse) {
        with(course.binding as ItemMyLiveCourseBinding) {
            data = course
            this.cover.load(course.coverImage)
        }
    }

    private fun loadLiveCourse() {
        with(mBinding.refresh) {
            mViewModel.getLiveCourse(pageIndex, pageSize).obs(this@MyLiveCourseFragment) {
                it.c { addCache(it.records) }
                it.y { addDatas(it.records) }
            }
        }
    }
}