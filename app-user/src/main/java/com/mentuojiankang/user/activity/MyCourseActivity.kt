package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanTrouble
import com.mentuojiankang.user.databinding.ActivityMyCourseBinding
import com.mentuojiankang.user.fragment.MyLiveCourseFragment
import com.mentuojiankang.user.fragment.RecordingCourseFragment
import com.mentuojiankang.user.vm.CourseViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import kotlinx.android.synthetic.main.activity_my_course.*

/**
 * tag==我的课程
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "我的课程")
class MyCourseActivity : MyBaseActivity<ActivityMyCourseBinding, CourseViewModel>() {
    override fun initCreate() {
        viewpager.setTabLayout(
            tab,
            "直播课",
            "录播课"
        )
        viewpager.addFragments(
            MyLiveCourseFragment(),
            RecordingCourseFragment()
        )
    }

    private fun initItem(beanTrouble: BeanTrouble) {

    }

}