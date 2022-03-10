package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanTrouble
import com.mentuojiankang.user.databinding.ActivityMyCollectionBinding
import com.mentuojiankang.user.databinding.ActivityMyFollowBinding
import com.mentuojiankang.user.fragment.CollectionConsultListFragment
import com.mentuojiankang.user.fragment.CollectionCourseListFragment
import com.mentuojiankang.user.fragment.CollectionEvaluationListFragment
import com.mentuojiankang.user.vm.FavoriteViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import kotlinx.android.synthetic.main.activity_my_collection.*
import kotlinx.android.synthetic.main.activity_my_follow.*

/**
 * tag==收藏
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "收藏")
class MyCollectionActivity : MyBaseActivity<ActivityMyCollectionBinding, FavoriteViewModel>() {
    override fun initCreate() {
        viewpager.setTabLayout(
            tab,
//            "测评",
            "课程",
            "咨询"
        )
        viewpager.addFragments(
//            CollectionEvaluationListFragment(),
            CollectionCourseListFragment(),
            CollectionConsultListFragment()
        )
    }

    private fun initItem(beanTrouble: BeanTrouble) {

    }
}