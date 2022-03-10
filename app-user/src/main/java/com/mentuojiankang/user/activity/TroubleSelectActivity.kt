package com.mentuojiankang.user.activity

import com.mentuojiankang.user.MainActivity
import com.mentuojiankang.user.bean.BeanEmotionTag
import com.mentuojiankang.user.databinding.ActivityTroubleSelectedBinding
import com.mentuojiankang.user.databinding.ItemTroubleSelectBinding
import com.mentuojiankang.user.vm.AppViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.BusCode
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.gotoFinish
import gorden.rxbus2.Subscribe
import gorden.rxbus2.ThreadMode
import kotlinx.android.synthetic.main.activity_trouble_selected.*
import kotlinx.android.synthetic.main.layout_next.*
import java.util.*

/**
 * tag==困扰选择
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "吱吱心理")
class TroubleSelectActivity : MyBaseActivity<ActivityTroubleSelectedBinding, AppViewModel>() {

    private var datas = ArrayList<BeanEmotionTag>()

    override fun initCreate() {

        mToolBar.showRight("跳过") { gotoFinish(MainActivity::class.java) }
        recyclerView.setEnableLoadMore(false)
        recyclerView.setEnableRefresh(false)
        recyclerView.bindData<BeanEmotionTag>(::initItem)
        recyclerView.loadData { }
        postDelayed(10) { getListTags() }
    }

    private fun initItem(bean: BeanEmotionTag) {

        with(bean.binding as ItemTroubleSelectBinding) {
            data = bean
            root.click {
                bean.isSelected = !bean.isSelected
                next.isEnabled = datas.any { it.isSelected }
                bean.notifyChange()
            }
        }
    }

    override fun initClick() {

        next.click {
            goto(TroubleSelectDegreeActivity::class.java, "data", datas.filter { it.isSelected })
        }
    }

    private fun getListTags() {
        mViewModel.getListTag().obs(this) {
            it.y {
                if (it.isNullOrEmpty()) {
                    gotoFinish(MainActivity::class.java)
                }else{
                    datas = it as ArrayList<BeanEmotionTag>
                    recyclerView.addDatas(it)
                }

            }
        }
    }

    @Subscribe(code = BusCode.SAVE_LIST_TAGS_SUCCESS, threadMode = ThreadMode.MAIN)
    fun saveListTagsSuccess() {
        finish()
    }

}