package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.databinding.*
import com.mentuojiankang.user.vm.HistoryViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.toast

/**
 * tag==浏览足迹课程/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class HistoryCourseListFragment : MyBaseFragment<FragmentHistoryCourseListBinding, HistoryViewModel>() {
    override fun initOnCreateView() {

        with(mBinding) {

            refresh.bindData<BeanCourse> {
                with(it.binding as ItemHistoryVideoBinding) {
                    data = it
                    root.click {
                        toast("我被点击了")
                        context?.goto(CourseInfoActivity::class.java)
                    }

                }

            }
            refresh.loadData {
                activity?.let {
                    mViewModel.getHistoryCourses(refresh.pageIndex, refresh.pageSize).obs(it) {
                        it.y {
                            refresh.addDatas(it.records)
                        }
                    }
                }
            }

//            refresh.test(BeanCourse::class.java, 10)
        }

    }


}