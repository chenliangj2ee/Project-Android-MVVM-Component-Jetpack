package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanConsultant
import com.mentuojiankang.user.databinding.ActivityMyFollowBinding
import com.mentuojiankang.user.databinding.ItemMyFollowBinding
import com.mentuojiankang.user.vm.FollowViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import kotlinx.android.synthetic.main.activity_my_follow.*

/**
 * tag==关注
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "关注")
class MyFollowActivity : MyBaseActivity<ActivityMyFollowBinding, FollowViewModel>() {
    override fun initCreate() {
        recyclerView.bindData<BeanConsultant>() {
            with(it.binding as ItemMyFollowBinding) { data = it }
        }
        recyclerView.loadData {
            mViewModel.getFollows(recyclerView.pageIndex, recyclerView.pageSize).obs(this) {
                it.c {
                    recyclerView.addCache(it.records)
                    mToolBar.setTitle("关注(${it.total})")
                }
                it.y {
                    recyclerView.addDatas(it.records)
                    mToolBar.setTitle("关注(${it.total})")
                }
            }
        }
    }

}