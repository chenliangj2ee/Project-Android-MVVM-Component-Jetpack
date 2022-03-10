package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.databinding.FragmentCourseInfoRecommendBinding
import com.mentuojiankang.user.databinding.ItemRecommendVideoBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.toast

/**
 * tag==推荐/课程
 * author:chenliang
 * date:2021/11/4
 */
class CourseRecommendFragment : MyBaseFragment<FragmentCourseInfoRecommendBinding, DefaultViewModel>() {
    override fun initOnCreateView() {
        with(mBinding) {
            videoRefresh.setEnableRefresh(false)
            videoRefresh.setEnableLoadMore(false)
            videoRefresh.bindData<BeanCourse> {
                with(it.binding as ItemRecommendVideoBinding) {
                    data = it
                    root.click {
                        toast("我被点击了")
                        context?.goto(CourseInfoActivity::class.java)
                    }
                }

            }
            videoRefresh.test(BeanCourse::class.java,10)
        }

    }

}