package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.ConsultInfoActivity
import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.activity.FragmentActivity
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.databinding.FragmentRecommendVideoBinding
import com.mentuojiankang.user.databinding.ItemRecommendVideoBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.utils.*
import kotlinx.android.synthetic.main.layout_recommend_module_title.view.*

/**
 * 首页推荐课程
 */
class RecommendVideoFragment : MyBaseFragment<FragmentRecommendVideoBinding, DefaultViewModel>() {
    override fun initOnCreateView() {
        mRootView.show(false)
        with(mBinding) {
            root.more.click { goto(FragmentActivity::class.java, "fragment", "/user/course", "title", "课程") }
            mBinding.root.title.text = "正念生活"
            videoRefresh.setEnableRefresh(false)
            videoRefresh.setEnableLoadMore(false)


        }

    }

    fun addData(list: ArrayList<BeanCourse>) {

        if(list.isNullOrEmpty())
            return

        mRootView.show(true)
        with(mBinding) {
            videoRefresh.bindData<BeanCourse> {
                it.resetDefault()
                with(it.binding as ItemRecommendVideoBinding) {
                    data = it
                    root.goto(CourseInfoActivity::class.java, "courseId", it.id)
                }

            }
            videoRefresh.addDatas(list)
        }
    }
}