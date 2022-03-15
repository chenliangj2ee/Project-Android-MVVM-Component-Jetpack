package com.mentuojiankang.user.fragment

import android.util.TypedValue
import android.widget.TextView
import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanType
import com.mentuojiankang.user.databinding.FragmentCourseBinding
import com.mentuojiankang.user.vm.AppViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.*

/**
 * tag==课程tab/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyRoute(mPath = "/user/course")
class CourseTabFragment : MyBaseFragment<FragmentCourseBinding, AppViewModel>() {
    override fun initOnCreateView() {
        mViewModel.getType2().obs(this) {
            it.c { initViewPager(it) }
            it.y { initViewPager(it) }
        }
    }

    private fun initViewPager(arraylist: List<BeanType>) {
        with(mBinding) {
            courseTab.selected {
                with(it?.customView?.findViewById<TextView>(R.id.title)) {
                    this?.isEnabled = it?.isSelected == false
                    this?.setTextSizeDip(if (it?.isSelected == true) 15 else 15)
                    this?.setBold(it?.isSelected == true)
                }
            }
            courseViewpager.clearFragments()
            var tabTitles = ArrayList<String>()
            arraylist.forEach {
                tabTitles.add(it.name)
                courseViewpager.addFragments(goto(CourseListFragment()::class.java, "type", it.id))
            }

            courseViewpager.setTabLayout(courseTab, tabTitles, R.layout.item_home_tab) {
                it.customView?.findViewById<TextView>(R.id.title)?.text = it.text
            }


        }
    }
}