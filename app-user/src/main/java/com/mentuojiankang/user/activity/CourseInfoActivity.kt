package com.mentuojiankang.user.activity

import android.graphics.Paint
import android.webkit.WebView
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.bean.BeanPayInfo
import com.mentuojiankang.user.databinding.ActivityCourseInfoBinding
import com.mentuojiankang.user.fragment.CourseInfoHeaderFragment
import com.mentuojiankang.user.fragment.CourseRecommendFragment
import com.mentuojiankang.user.fragment.DetailsWebViewFragment
import com.mentuojiankang.user.vm.CourseViewModel
import com.mentuojiankang.user.vm.FavoriteViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.fragment.EvaluateFragment
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.*
import com.mtjk.view.MyWebView
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_course_info.*

/**
 * tag==课程详情
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "课程", mRefresh = true)
class CourseInfoActivity : MyBaseActivity<ActivityCourseInfoBinding, CourseViewModel>() {
    lateinit var course: BeanCourse
    private lateinit var header: CourseInfoHeaderFragment
    lateinit var order: BeanPayInfo

    @MyField
    var courseId = ""
    override fun initCreate() {

        price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        header = headerFragment as CourseInfoHeaderFragment
        header.player?.initFullLayout(fullScreenLayout)
        viewpager.setTabLayout(tabLayout, "详情", "推荐", "评价")

        viewpager.onPageSelected { subscribe.show(it == 0) }
        subscribe.click {
            if (order != null)
                goto(
                    PaymentConfirmActivity::class.java,
                    "order",
                    order,
                    "productType",
                    ObjectProduct.TYPE_COURSE
                )
        }
        refresh()
    }


    @Subscribe(code = BusCode.PAYMENT_SUCCESS)
    override fun refresh() {
        if (courseId == null)
            return
        mViewModel.getCourseById(courseId).obs(this) {
            it.c { initData(it) }
            it.y { initData(it) }
        }
    }

    private fun initData(bean: BeanCourse) {
        bean.resetDefault()
        course = bean
        mBinding.data = course
//        initFavorite()
        stopRefresh()
        header.setData(course)
        mToolBar.setTitle(bean.title)
        order = BeanPayInfo()
        order.paycoverImage = course.coverImage
        order.paylessonTitle = course.title
        order.payprice = course.discountPrice
        order.paysectionCount = course.sectionList!!.size
        order.productId = course.id
        order.subTitle = "${course.sectionList!!.size}节"
        viewpager.clearFragments()
        viewpager.addFragments(
            DetailsWebViewFragment(bean.detailUrl, MyWebView.Type.IMAGE ),
            goto(CourseRecommendFragment::class.java, "courseId", bean.id),
            goto(EvaluateFragment::class.java, "productId", bean.id)
        )

    }


    /**
     * 初始化收藏按钮样式以及事件
     */
    private fun initFavorite() {
        mToolBar.showRightIcon(R.drawable.icon_favorite_default) {
            if (course.favorite) {
                initVM(FavoriteViewModel::class.java).cancelFavorite(courseId).obs(this) {
                    it.y {
                        mToolBar.showRightIcon(R.drawable.icon_favorite_default)
                        course.favorite = false
                    }
                }
            } else {
                initVM(FavoriteViewModel::class.java).favorite(0, courseId).obs(this) {
                    it.y {
                        mToolBar.showRightIcon(R.drawable.icon_favorite_checked)
                        course.favorite = true
                    }
                }
            }

        }

        if (course.favorite) {
            mToolBar.showRightIcon(R.drawable.icon_favorite_checked)
        } else {
            mToolBar.showRightIcon(R.drawable.icon_favorite_default)
        }
    }


}