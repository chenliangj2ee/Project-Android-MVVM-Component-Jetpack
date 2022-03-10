package com.mentuojiankang.user.activity

import com.mentuojiankang.user.MainActivity
import com.mentuojiankang.user.bean.BeanEmotionTag
import com.mentuojiankang.user.databinding.ActivityTroubleSelectedDegreeBinding
import com.mentuojiankang.user.databinding.ItemTroubleSelectDegreeBinding
import com.mentuojiankang.user.vm.UserViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe
import gorden.rxbus2.ThreadMode
import kotlinx.android.synthetic.main.activity_trouble_selected.*
import kotlinx.android.synthetic.main.layout_next.*

/**
 * tag==困扰选择程度
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "吱吱心理")
class TroubleSelectDegreeActivity :
    MyBaseActivity<ActivityTroubleSelectedDegreeBinding, UserViewModel>() {
    @MyField
    lateinit var data: ArrayList<BeanEmotionTag>
    override fun initCreate() {

        mToolBar.showRight("跳过") {
            gotoFinish(MainActivity::class.java)
            send(BusCode.SAVE_LIST_TAGS_SUCCESS)
        }
        recyclerView.setEnableLoadMore(false)
        recyclerView.setEnableRefresh(false)
        recyclerView.bindData<BeanEmotionTag>(::initItem)
        recyclerView.addDatas(data)
        next.isEnabled = true

    }

    private fun initItem(bean: BeanEmotionTag) {
        with(bean.binding as ItemTroubleSelectDegreeBinding) {
            data = bean
        }
    }

    override fun initClick() {
        next.click { saveListTags() }

    }

    private fun saveListTags() {
        mViewModel.saveListTag(data).obs(this) {
            it.y { goto(MainActivity::class.java) }
        }
    }

    @Subscribe(code = BusCode.SAVE_LIST_TAGS_SUCCESS, threadMode = ThreadMode.MAIN)
    fun saveListTagsSuccess() {
        finish()
    }
}