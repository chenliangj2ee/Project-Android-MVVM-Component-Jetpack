package com.fcyd.expert.visitor

import android.view.KeyEvent
import android.widget.TextView
import com.fcyd.expert.bean.BeanVisitor
import com.fcyd.expert.databinding.ActivityVisitorListBinding
import com.fcyd.expert.databinding.ItemVisitorBinding
import com.fcyd.expert.vm.VisitorViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.changed
import com.mtjk.utils.click
import com.mtjk.utils.goto

/*
* tag==来访者
* */
@MyClass(mToolbarTitle = "来访者")
class VisitorListActivity : MyBaseActivity<ActivityVisitorListBinding, VisitorViewModel>() {
    override fun initCreate() {
        mBinding.refresh.bindData<BeanVisitor>(::initItem)

        mBinding.refresh.loadData { loadData() }
    }

    private fun loadData() {
        var userName = mBinding.search?.text?.toString()?.trim()
        with(mBinding) {
            mViewModel.getVisitorList(userName!!, refresh.pageIndex, refresh.pageSize)
                .obs(this@VisitorListActivity) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
        }
    }

    override fun initClick() {
        super.initClick()
        mBinding.search?.changed {
            doSearch()
        }
        mBinding.search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return true
                }
                return false
            }
        })
    }

    private fun doSearch() {
        mBinding.refresh.resetPageIndex()
        loadData()
    }

    private fun initItem(bean: BeanVisitor) {
        with(bean.binding as ItemVisitorBinding) {
            data = bean
            root.click { toVisitorDetail(bean) }
        }
    }

    private fun toVisitorDetail(visitor: BeanVisitor?) {
        if (visitor != null) {
            goto(VisitorDetailActivity::class.java, "visitor", visitor)
        }
    }
}