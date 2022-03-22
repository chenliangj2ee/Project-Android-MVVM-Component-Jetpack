package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanConsultType
import com.mentuojiankang.user.databinding.ActivityConsultInfoEditBinding
import com.mentuojiankang.user.vm.ConsultViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.obj.ObjectQuestionType
import com.mtjk.utils.*
import com.mtjk.view.DialogPicker
import kotlinx.android.synthetic.main.activity_consult_info_edit.*
import kotlinx.android.synthetic.main.activity_consult_info_edit.age
import kotlinx.android.synthetic.main.item_wallet_detail.view.*
import java.util.ArrayList

/**
 * tag==咨询信息
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "咨询信息", mScroll = true)
class ConsultInfoEditActivity :
    MyBaseActivity<ActivityConsultInfoEditBinding, ConsultViewModel>() {

    @MyField
    var orderItemId: String = ""

    private var sexValue = 1

    private var ageValue = 20

    //当前问题类型选项
    private val mTypes = intArrayOf(
        ObjectQuestionType.TYPE_LOVE, ObjectQuestionType.TYPE_MARRY,
        ObjectQuestionType.TYPE_CHILD, ObjectQuestionType.TYPE_SOCIETY, ObjectQuestionType.TYPE_JOB,
        ObjectQuestionType.TYPE_GROW, ObjectQuestionType.TYPE_OTHERS
    )

    override fun initCreate() {
        initView()
        loadData()
    }

    private fun initView() {
        initTypeView()
        initDescEdit()
    }

    private fun initTypeView() {
        typeSelector.enableRefresh = false
        typeSelector.bindData<BeanConsultType> { bean ->
            with(bean.binding as com.mentuojiankang.user.databinding.ItemConsultTypeBinding) {
                data = bean
                status.checked { isChecked ->
                    bean?.status = isChecked
                    enable()
                }
            }
        }
        updateTypeStatus(intArrayOf())
    }

    private fun updateTypeStatus(value: IntArray) {
        val list: MutableList<BeanConsultType> = ArrayList()
        var set: Set<Int> = value?.toSet()
        for (item in mTypes) {
            var bean = BeanConsultType()
            bean.type = item
            bean.status = if (set == null || !set?.contains(item)) false else true
            list.add(bean)
        }
        typeSelector.addDatas(list)
    }

    private fun initDescEdit() {
        desc_header_status.isChecked = true
        desc_content_layout.show(true)
        desc_header_status.checked { isChecked ->
            desc_content_layout.show(mBinding.descHeaderStatus.isChecked)
        }
    }

    private fun loadData() {
        mViewModel.getVisitorConsultDetail(orderItemId).obs(this@ConsultInfoEditActivity) {
            it.y {
                with(mBinding) {
                    if (!it.name.isNullOrEmpty()) name.setValue(it.name)
                    if (it.age != null) age.setValue(it.age.toString())
                    if (it.sex != null) sex.setValue(it.sexString())
                    updateTypeStatus(it.questionType)
                    desc_edit.edit.setText(it.description)
                    effect_edit.edit.setText(it.helperResult)
                }
            }
        }
    }

    private fun getTypeResult(): IntArray {
        var result = arrayListOf<Int>()
        var list = mBinding.typeSelector.getData<BeanConsultType>()
        for (item in list) {
            if (item?.status) {
                result.add(item.type)
            }
        }
        return result.toIntArray()
    }

    override fun initClick() {
        name.editText.changed { enable() }
        age.click { showAgeSelector() }
        sex.click { showSexSelector() }
        commit.click { commitClick() }
    }

    private fun showSexSelector() {
        var items = arrayListOf("男", "女")
        var dialog = DialogPicker()
        dialog.setItems(items)
        dialog.setTitle("选择性别")
        dialog.selected {
            when (it) {
                0 -> sexValue = 1
                1 -> sexValue = 2
            }
            sex.setValue(items[it])
            enable()
        }
        dialog.show(this)
    }

    private fun showAgeSelector() {
        var items = arrayListOf<String>()
        for (item in 16..80) {
            items.add("${item}")
        }
        var dialog = DialogPicker()
        dialog.setItems(items)
        dialog.setTitle("选择年龄")
        dialog.selected {
            age.setValue(items[it])
            ageValue = items[it].toInt()
            enable()
        }
        dialog.show(this)
    }

    private fun commitClick() {
        mViewModel.saveVisitorConsultDetail(
            with(mBinding) {
                mViewModel.body(
                    "name", name.editText.text.toString(),
                    "age", age.editText.text.toString().toInt(),
                    "sex", (if ("女".equals(sex.editText.text.toString())) 2 else 1),
                    "questionType", getTypeResult(),
                    "description", desc_edit.edit.text.toString(),
                    "wishResult", effect_edit.edit.text.toString(),
                    "orderItemId", orderItemId
                )
            }
        ).obs(this@ConsultInfoEditActivity) {
            it.y {
                toast("保存成功")
                finish()
                goto(MyOrderActivity::class.java)
            }
        }
    }

    private fun enable(){
        commit.isEnabled=name.editText.text.toString().isNotEmpty()&&age.editText.text.toString().isNotEmpty()&&sex.editText.text.toString().isNotEmpty()&& getTypeResult().isNotEmpty()
    }

}