package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.databinding.FragmentCollectionCourseListBinding
import com.mentuojiankang.user.databinding.ItemRecommendVideoBinding
import com.mentuojiankang.user.vm.FavoriteViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.toast

/**
 * tag==收藏课程/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class CollectionCourseListFragment : MyBaseFragment<FragmentCollectionCourseListBinding, FavoriteViewModel>() {
    override fun initOnCreateView() {

        with(mBinding) {

            refresh.bindData<BeanCourse> {
                it.resetDefault()
                with(it.binding as ItemRecommendVideoBinding) {
                    data = it
                    root.goto(CourseInfoActivity::class.java, "courseId", it.id)

                }

            }
            refresh.loadData {
                activity?.let {
                    mViewModel.getFavoriteCourses(refresh.pageIndex, refresh.pageSize).obs(it) {
                        it.y { refresh.addDatas(it.records) }
                    }
                }
            }

        }

    }


}