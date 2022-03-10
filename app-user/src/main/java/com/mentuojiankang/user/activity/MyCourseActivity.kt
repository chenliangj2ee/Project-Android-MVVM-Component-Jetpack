package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.databinding.ActivityMyCourseBinding
import com.mentuojiankang.user.databinding.ItemMyCourseBinding
import com.mentuojiankang.user.vm.CourseViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.goto
import kotlinx.android.synthetic.main.activity_my_evaluation.*

/**
 * tag==我的课程
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "我的课程")
class MyCourseActivity : MyBaseActivity<ActivityMyCourseBinding, CourseViewModel>() {
    override fun initCreate() {
//        mToolBar.showRight("管理") {}
        refresh.bindData<BeanCourse>() {
            with(it.binding as ItemMyCourseBinding) {
                data = it
                root.goto(CourseInfoActivity::class.java,"courseId",it.id)
            }
        }
        refresh.loadData {
            mViewModel.getMyCourse(refresh.pageIndex, refresh.pageSize).obs(this) {
                it.c { refresh.addCache(it.records) }
                it.y { refresh.addDatas(it.records) }
            }
        }
    }


}