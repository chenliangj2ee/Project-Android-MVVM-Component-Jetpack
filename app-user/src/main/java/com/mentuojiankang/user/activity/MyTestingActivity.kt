package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanTest
import com.mentuojiankang.user.databinding.ActivityMyEvaluationBinding
import com.mentuojiankang.user.databinding.ItemMyTestBinding
import com.mentuojiankang.user.vm.TestViewModel
import com.mtjk.MyPath
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.WebViewActivity
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto
import kotlinx.android.synthetic.main.activity_my_evaluation.*

/**
 * tag==我的测评
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "我的测评")
class MyTestingActivity : MyBaseActivity<ActivityMyEvaluationBinding, TestViewModel>() {
    override fun initCreate() {
        refresh.bindData<BeanTest>(::bindItem).loadData(::loadTests)
    }

    /**
     * 绑定列表item
     */
    private fun bindItem(test: BeanTest) {
        test.resetDefault()
        with(test.binding as ItemMyTestBinding) {
            this.data = test
            this.root.click { gotoInfo(test) }

        }
    }

    /**
     * 加载列表数据
     */
    private fun loadTests() {
        with(refresh) {
            mViewModel.getMyTest(pageIndex, pageSize).obs(this@MyTestingActivity) {
                it.c { addCache(it.records) }
                it.y { addDatas(it.records) }
            }
        }

    }

    /**
     * 进入测评报告
     */
    private fun gotoInfo(test: BeanTest) {
        goto(WebViewActivity::class.java, "url", MyPath.testResult, "title", test.scaleName,"testResultId",test.id)
    }

}