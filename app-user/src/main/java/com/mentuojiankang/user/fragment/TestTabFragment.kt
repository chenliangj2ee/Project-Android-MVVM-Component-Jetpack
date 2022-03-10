package com.mentuojiankang.user.fragment

import android.graphics.Typeface
import android.widget.TextView
import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.R
import com.mentuojiankang.user.activity.MyTestingActivity
import com.mentuojiankang.user.bean.BeanType
import com.mentuojiankang.user.databinding.FragmentEvaluationBinding
import com.mentuojiankang.user.vm.AppViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.goto
import com.mtjk.utils.selected
import com.mtjk.utils.setBold

/**
 * tag==测评tab/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyRoute(mPath = "/user/test")
class TestTabFragment : MyBaseFragment<FragmentEvaluationBinding, AppViewModel>() {
    override fun initOnCreateView() {

        (activity as MyBaseActivity<*, *>).mToolBar.showRight("我的测评"){
            goto(MyTestingActivity::class.java)
        }
        mViewModel.getTestType().obs(this) {
            it.c { initViewPager(it) }
            it.y { initViewPager(it) }
        }

    }


    private fun initViewPager(arraylist: List<BeanType>) {
        with(mBinding) {
            testTab.selected {
                with(it?.customView?.findViewById<TextView>(R.id.title)) {
                    this?.isEnabled = it?.isSelected == false
                    this?.textSize = if (it?.isSelected == true) 14F else 12F
                    this?.setBold(it?.isSelected == true)
                }

            }
            testViewpager.clearFragments()
            var tabTitles = ArrayList<String>()
            arraylist.forEach {
                tabTitles.add(it.categoryName)
                testViewpager.addFragments(goto(TestListFragment()::class.java, "type", it.id))
            }

            testViewpager.setTabLayout(testTab, tabTitles, R.layout.item_home_tab) {
                it.customView?.findViewById<TextView>(R.id.title)?.text = it.text
            }


        }
    }
}