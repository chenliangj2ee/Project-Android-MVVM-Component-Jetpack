package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanTrouble
import com.mentuojiankang.user.databinding.ActivityMyCollectionBinding
import com.mentuojiankang.user.fragment.HistoryConsultListFragment
import com.mentuojiankang.user.fragment.HistoryCourseListFragment
import com.mentuojiankang.user.fragment.HistoryEvaluationListFragment
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import kotlinx.android.synthetic.main.activity_my_collection.*

/**
 * tag==浏览足迹
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "浏览足迹")
class MyHistoryActivity : MyBaseActivity<ActivityMyCollectionBinding, DefaultViewModel>() {
    override fun initCreate() {
        viewpager.setTabLayout(
            tab,
//            "测评",
            "课程",
            "咨询"
        )
        viewpager.addFragments(
//            HistoryEvaluationListFragment(),
            HistoryCourseListFragment(),
            HistoryConsultListFragment()
        )
    }

    private fun initItem(beanTrouble: BeanTrouble) {

    }
}