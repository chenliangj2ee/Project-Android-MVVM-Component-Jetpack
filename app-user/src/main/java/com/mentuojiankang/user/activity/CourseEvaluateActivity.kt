package com.mentuojiankang.user.activity

import android.view.MotionEvent
import android.view.View
import com.mentuojiankang.user.databinding.ActivityCourseEvaluateBinding
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.bean.BeanRecommend
import com.mtjk.utils.changed
import com.mtjk.utils.click
import com.mtjk.utils.log
import com.mtjk.utils.toast
import kotlinx.android.synthetic.main.activity_course_evaluate.*

/**
 * tag==评价
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "评价")
class CourseEvaluateActivity : MyBaseActivity<ActivityCourseEvaluateBinding, OrderViewModel>() {
    @MyField
    var bean = BeanRecommend()
    override fun initCreate() {
        mBinding.data = bean
        with(mBinding) {
            data = bean
            comment.edit.changed {
                submit.isEnabled = it.isNotEmpty()
            }
            ratingbar.setOnRatingBarChangeListener { _, rating, _ ->
                bean.score = rating
                when (rating) {
                    in 0.0..1.0 -> {
                        good.text = "差"
                    }
                    in 2.0..3.0 -> {
                        good.text = "中"
                    }
                    in 3.0..5.0 -> {
                        good.text = "好"
                    }
                }
            }


        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {


//        postDelayed(200){
//            var rating = mBinding.ratingbar.rating
//            log("顶顶顶顶顶顶顶顶$rating")
//            if (rating in 1.0..2.0) {
//                good.text = "差"
//            } else if (rating in 2.0..3.0) {
//                good.text = "中"
//            } else if (rating in 3.0..5.0) {
//                good.text = "好"
//            }
//        }
        return super.dispatchTouchEvent(ev)
    }


    override fun initClick() {
        submit.click { httpSubmit() }
    }

    private fun httpSubmit() {
        bean.anonymous = if (checkbox.isChecked) 1 else 0

        if (bean.orderServer == 100) {
            mViewModel.orderCourseRecommend(bean).obs(this) {
                it.y {
                    toast("评论成功")
                    finish()
                }
            }
        } else {
            mViewModel.orderConsultRecommend(bean).obs(this) {
                it.y {
                    toast("评论成功")
                    finish()
                }
            }
        }

    }

}