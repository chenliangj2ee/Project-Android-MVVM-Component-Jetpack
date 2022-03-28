package com.fcyd.expert.visitor

import com.fcyd.expert.bean.BeanVisitor
import com.fcyd.expert.databinding.ActivityVisitorDetailBinding
import com.fcyd.expert.vm.VisitorViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.click
import com.mtjk.utils.goto

/*
* tag==来访者详情
* */
@MyClass(mToolbarTitle = "来访者详情")
class VisitorDetailActivity : MyBaseActivity<ActivityVisitorDetailBinding, VisitorViewModel>() {

    @MyField
    var visitor: BeanVisitor? = null

    override fun initCreate() {
        initTab()
        if(visitor != null) mBinding.data = visitor
    }

    override fun initClick() {
        super.initClick()
        mBinding.edit.click { toEdit() }
    }

    private fun initTab() {
        with(mBinding) {
            viewpager.setTabLayout(
                tab,
                "咨询记录",
                //TODO 量表暂时先不做
                //"量表测试"
            )
            viewpager.addFragments(
                goto(VisitorConsultListFragment::class.java, "visitor", visitor)
                //TODO 量表暂时先不做
                //goto(VisitorGaugeListFragment::class.java, "visitor", visitor)
            )
        }
    }

    private fun toEdit() {
        if(visitor != null) {
            goto(VisitorInfoEditActivity::class.java, "visitor", visitor)
        }
    }
}