package com.mentuojiankang.user.activity

import android.graphics.Color
import com.mentuojiankang.user.bean.BeanLiveSection
import com.mentuojiankang.user.databinding.ActivityLiveSectionListBinding
import com.mentuojiankang.user.databinding.ItemMyLiveSectionBinding
import com.mentuojiankang.user.vm.LiveViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.click

/**
 * tag==我的课程/直播课程/课节
 */
@MyClass(mToolbarTitle = " ")
class MyLiveSectionActivity : MyBaseActivity<ActivityLiveSectionListBinding, LiveViewModel>() {

    @MyField
    var courseId = ""

    @MyField
    var title = ""

    override fun initCreate() {
        setToolbarTitle(title)
        mBinding.refresh
            .bindData<BeanLiveSection> (::bindItem)
            .loadData { loadSection() }
    }

    private fun loadSection() {
        mViewModel.getLiveSection(courseId, mBinding.refresh.pageIndex, mBinding.refresh.pageSize).obs(this@MyLiveSectionActivity) {
            it.c { mBinding.refresh.addCache(it.records) }
            it.y { mBinding.refresh.addDatas(it.records) }
        }
    }

    private fun bindItem(bean: BeanLiveSection) {
        with(bean.binding as ItemMyLiveSectionBinding) {
            data = bean
            var drawable = bean.typeDrawable()
            if(drawable > 0) type.setImageResource(drawable)
            var color = Color.parseColor(if(bean.currentState()==0) "#008599" else "#1F2326")
            title.setTextColor(color)
            index.setTextColor(color)
            start.click { gotoLiveRoom(bean) }
        }
    }

    private fun gotoLiveRoom(bean: BeanLiveSection) {
        //TODO
    }
}