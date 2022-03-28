package com.fcyd.expert.visitor

import com.fcyd.expert.bean.BeanVisitor
import com.fcyd.expert.databinding.ActivityVisitorInfoEditBinding
import com.fcyd.expert.vm.VisitorViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.body
import com.mtjk.utils.click
import com.mtjk.utils.toast
import kotlinx.android.synthetic.main.activity_visitor_info_edit.save

/*
* tag==来访者资料
* */
@MyClass(mToolbarTitle = "来访者资料")
class VisitorInfoEditActivity : MyBaseActivity<ActivityVisitorInfoEditBinding, VisitorViewModel>() {

    @MyField
    var visitor: BeanVisitor? = null

    override fun initCreate() {
        save.click { clickSave() }
        loadData()
    }

    private fun loadData() {
        mBinding.data = visitor
        mViewModel.getVisitorDetail(visitor?.id.toString()).obs(this@VisitorInfoEditActivity) {
            it.y {
                if(!it.note.isNullOrEmpty()) mBinding.remarkEdit.setText(it.note)
            }
        }
    }

    private fun clickSave() {
        var body = mViewModel.body(
            "id", visitor?.id.toString(),
            "userId", visitor?.id.toString(),
            "note", mBinding.remarkEdit.getText()
        )
        mViewModel.saveVisitorDetail(body).obs(this@VisitorInfoEditActivity) {
            it.y { toast("已保存备注信息") }
            it.n { toast("保存失败") }
        }
    }
}