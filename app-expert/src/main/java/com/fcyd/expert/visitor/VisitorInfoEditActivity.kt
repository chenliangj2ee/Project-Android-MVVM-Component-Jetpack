package com.fcyd.expert.visitor

import com.fcyd.expert.databinding.ActivityVisitorInfoEditBinding
import com.fcyd.expert.vm.VisitorViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.changed
import com.mtjk.utils.click
import com.mtjk.utils.toast
import kotlinx.android.synthetic.main.activity_visitor_info_edit.*

/*
* tag==来访者资料
* */
@MyClass(mToolbarTitle = "来访者资料")
class VisitorInfoEditActivity : MyBaseActivity<ActivityVisitorInfoEditBinding, VisitorViewModel>() {

    override fun initCreate() {
        save.click { clickSave() }
        initView()
    }

    private fun initView() {
        remark_edit.changed { remark_count.text = "${remark_edit.text.toString().length}" }
    }

    private fun clickSave() {
        //TODO
        toast("需要保存信息接口")
    }
}