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
import com.mtjk.utils.click
import com.mtjk.utils.goto

/*
* tag==来访者
* */
@MyClass(mToolbarTitle = "来访者")
class VisitorListActivity : MyBaseActivity<ActivityVisitorListBinding, VisitorViewModel>() {
    override fun initCreate() {
        mBinding.refresh.bindData<BeanVisitor>(::initItem)
        loadData("")
    }

    private fun loadData(userName: String) {
        with(mBinding) {
            mViewModel.getVisitorList(userName, refresh.pageIndex, refresh.pageSize).obs(this@VisitorListActivity) {
                refresh.clearData()
                it.c { refresh.addCache(it.records) }
                it.y { refresh.addDatas(it.records) }
            }
        }
    }

    override fun initClick() {
        super.initClick()
        mBinding.searchButton.click {
            doSearch()
        }
        mBinding.search.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event?.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return true
                }
                return false
            }
        })
    }

    private fun doSearch() {
        var searchStr = mBinding.search?.text?.toString()?.trim()
        if(!searchStr.isNullOrEmpty()) {
            loadData(searchStr)
        }
    }

    private fun initItem(bean: BeanVisitor) {
        with(bean.binding as ItemVisitorBinding) {
            data = bean
            root.click { toVisitorDetail(bean) }
        }
    }

    private fun toVisitorDetail(visitor: BeanVisitor?) {
        if(visitor != null) {
            goto(VisitorDetailActivity::class.java, "visitor", visitor)
        }
    }
}