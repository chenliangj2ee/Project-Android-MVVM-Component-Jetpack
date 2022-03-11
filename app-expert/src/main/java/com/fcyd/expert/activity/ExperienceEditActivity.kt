package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanAuth
import com.fcyd.expert.bean.BeanImageSelected
import com.fcyd.expert.databinding.ActivityExperienceEditBinding
import com.fcyd.expert.databinding.ItemSelectorImageBinding
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.player.view.PlayActivity
import com.mtjk.utils.*
import kotlinx.android.synthetic.main.activity_experience_edit.*
import kotlinx.android.synthetic.main.item_studio_media.view.*

/**
 * tag==经历编辑
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "经历编辑")
class ExperienceEditActivity : MyBaseActivity<ActivityExperienceEditBinding, DefaultViewModel>() {

    @MyField(mKey = "toolbarTitle")
    var tbTitle = ""

    @MyField
    var title = ""

    @MyField
    var hint = ""

    var arrays = ArrayList<BeanImageSelected>()

    override fun initCreate() {
        mBinding.title = title
        mToolBar.setTitle(tbTitle)
        if (hint != null)
            editLayout.edit.hint = hint
        mBinding.radio1.click {
            mBinding.editLayout.show(false)
            mBinding.refresh.show(true)
            mBinding.sczm.show(true)
            mBinding.save.isEnabled =
                mBinding.time.text.isNotEmpty()
        }
        mBinding.radio2.click {
            mBinding.editLayout.show(true)
            mBinding.sczm.show(false)
            mBinding.refresh.show(false)
            mBinding.save.isEnabled =
                mBinding.time.text.isNotEmpty() && mBinding.editLayout.getText().isNotEmpty()
        }

        with(mBinding) {
            refresh.disable()
                .bindData<BeanImageSelected> { bean ->
                    with(bean.binding as ItemSelectorImageBinding) {
                        image = bean
                        root.click { _ ->
                            if (bean.add) {
                                selectImages(false)
                            }
                        }

                        root.delete.click {
                            refresh.removeData(bean)
                             arrays.removeAt(bean.itemPosition)
                            for (item in selectImages) {
                                if (item == bean.url) {
                                    selectImages.remove(item)
                                    break
                                }
                            }
                        }

                        root.play.goto(PlayActivity::class.java, "url", bean.url)
                    }
                }
            arrays.add(BeanImageSelected("", true))
            refresh.addDatas(arrays)
        }

        initView()
    }


    override fun initClick() {
        with(mBinding) {
            editChanged(time, editLayout.edit) {
                if (radio1.isChecked) {
                    save.isEnabled =
                        time.text.isNotEmpty()
                }
                if (radio2.isChecked) {
                    save.isEnabled =
                        time.text.isNotEmpty() && editLayout.edit.text.isNotEmpty()
                }

            }

            save.click { save() }
        }

    }


    override fun resultSelectImage(array: ArrayList<String>) {
        super.resultSelectImage(array)
        var loadArrays = arrays.filter { it.url.startsWith("http") }
        arrays.clear()
        arrays.addAll(loadArrays)
        array.filter { it.endsWith(".mp4") }.forEach {
            arrays.add(BeanImageSelected(it, false))
        }

        array.filter { !it.endsWith(".mp4") }.forEach {
            arrays.add(BeanImageSelected(it, false))
        }

        arrays.add(BeanImageSelected("", true))
        mBinding.refresh.clearData()
        mBinding.refresh.addDatas(arrays)
        mBinding.refresh.scrollToPosition(arrays.size)

        with(mBinding) {
            save.isEnabled =
                time.text.isNotEmpty() || editLayout.edit.text.isNotEmpty()
        }
    }

    private fun initView() {
        arrays.clear()
        var bean = BeanAuth().get<BeanAuth>()
        with(mBinding) {
            when (tbTitle) {
                "个案经历" -> {
                    time.setText(bean?.gajl_sc)
                    radio1.isChecked = bean?.gajl_zmzl == 0
                    radio2.isChecked = bean?.gajl_zmzl == 1
                    refresh.show(bean?.gajl_zmzl == 0)
                    sczm.show(bean?.grtyjl_zmzl == 0)
                    editLayout.show(bean?.gajl_zmzl == 1)
                    editLayout.setText(bean?.gajl_text!!)



                    bean?.gajl_images?.forEach {
                        arrays.add(BeanImageSelected(it, false))
                    }

                    arrays.add(BeanImageSelected("", true))
                    refresh.addDatas(arrays)
                    if (bean?.gajl_images != null)
                        selectImages = bean?.gajl_images

                }
                "个人体验经历" -> {

                    time.setText(bean?.grtyjl_sc)
                    radio1.isChecked = bean?.grtyjl_zmzl == 0
                    radio2.isChecked = bean?.grtyjl_zmzl == 1
                    refresh.show(bean?.grtyjl_zmzl == 0)
                    sczm.show(bean?.grtyjl_zmzl == 0)
                    editLayout.show(bean?.grtyjl_zmzl == 1)
                    editLayout.edit.setText(bean?.grtyjl_text)

                    bean?.grtyjl_images?.forEach {
                        arrays.add(BeanImageSelected(it, false))
                    }

                    arrays.add(BeanImageSelected("", true))
                    refresh.addDatas(arrays)
                    if (bean?.grtyjl_images != null)
                        selectImages = bean?.grtyjl_images
                }
                "个人督导经历" -> {

                    time.setText(bean?.gtddjl_sc)
                    radio1.isChecked = bean?.gtddjl_zmzl == 0
                    radio2.isChecked = bean?.gtddjl_zmzl == 1
                    refresh.show(bean?.gtddjl_zmzl == 0)
                    sczm.show(bean?.grtyjl_zmzl == 0)
                    editLayout.show(bean?.gtddjl_zmzl == 1)
                    editLayout.edit.setText(bean?.gtddjl_text)

                    bean?.gtddjl_images?.forEach {
                        arrays.add(BeanImageSelected(it, false))
                    }

                    arrays.add(BeanImageSelected("", true))
                    refresh.addDatas(arrays)
                    if (bean?.gtddjl_images != null)
                        selectImages = bean?.gtddjl_images
                }
                "团体督导经历" -> {
                    ziliao.show(false)
                    time.setText(bean?.ttddjl_sc)
                    radio1.isChecked = bean?.ttddjl_zmzl == 0
                    radio2.isChecked = bean?.ttddjl_zmzl == 1
                    refresh.show(bean?.ttddjl_zmzl == 0)
                    sczm.show(bean?.grtyjl_zmzl == 0)
                    editLayout.show(bean?.ttddjl_zmzl == 1)
                    editLayout.edit.setText(bean?.ttddjl_text)

                    bean?.ttddjl_images?.forEach {
                        arrays.add(BeanImageSelected(it, false))
                    }

                    arrays.add(BeanImageSelected("", true))
                    refresh.addDatas(arrays)
                    if (bean?.ttddjl_images != null)
                        selectImages = bean?.ttddjl_images
                }
                "短程受训经历" -> {
                    ziliao.show(false)
                    time.setText(bean?.dcsxjl_sc)
                    radio1.isChecked = bean?.dcsxjl_zmzl == 0
                    radio2.isChecked = bean?.dcsxjl_zmzl == 1
                    refresh.show(bean?.dcsxjl_zmzl == 0)
                    sczm.show(bean?.grtyjl_zmzl == 0)
                    editLayout.show(bean?.dcsxjl_zmzl == 1)
                    editLayout.edit.setText(bean?.dcsxjl_text)

                    bean?.dcsxjl_images?.forEach {
                        arrays.add(BeanImageSelected(it, false))
                    }

                    arrays.add(BeanImageSelected("", true))
                    refresh.addDatas(arrays)
                    if (bean?.dcsxjl_images != null)
                        selectImages = bean?.dcsxjl_images
                }
                "长程受训经历" -> {
                    ziliao.show(false)
                    time.setText(bean?.ccsxjl_sc)
                    radio1.isChecked = bean?.ccsxjl_zmzl == 0
                    radio2.isChecked = bean?.ccsxjl_zmzl == 1
                    refresh.show(bean?.ccsxjl_zmzl == 0)
                    sczm.show(bean?.grtyjl_zmzl == 0)
                    editLayout.show(bean?.ccsxjl_zmzl == 1)
                    editLayout.edit.setText(bean?.ccsxjl_text)

                    bean?.ccsxjl_images?.forEach {
                        arrays.add(BeanImageSelected(it, false))
                    }

                    arrays.add(BeanImageSelected("", true))
                    refresh.addDatas(arrays)
                    if (bean?.ccsxjl_images != null)
                        selectImages = bean?.ccsxjl_images
                }
                else -> {
                }
            }
        }

    }

    fun save() {
        var bean = BeanAuth().get<BeanAuth>()
        with(mBinding) {
            when (tbTitle) {
                "个案经历" -> {
                    bean?.gajl_sc = time.text.toString()
                    bean?.gajl_zmzl = if (radio1.isChecked) 0 else 1
                    bean?.gajl_text = editLayout.edit.text.toString()
                    bean?.gajl_images = selectImages

                }
                "个人体验经历" -> {
                    bean?.grtyjl_sc = time.text.toString()
                    bean?.grtyjl_zmzl = if (radio1.isChecked) 0 else 1
                    bean?.grtyjl_text = editLayout.edit.text.toString()
                    bean?.grtyjl_images = selectImages
                }
                "个人督导经历" -> {
                    bean?.gtddjl_sc = time.text.toString()
                    bean?.gtddjl_zmzl = if (radio1.isChecked) 0 else 1
                    bean?.gtddjl_text = editLayout.edit.text.toString()
                    bean?.gtddjl_images = selectImages
                }
                "团体督导经历" -> {
                    bean?.ttddjl_sc = time.text.toString()
                    bean?.ttddjl_zmzl = if (radio1.isChecked) 0 else 1
                    bean?.ttddjl_text = editLayout.edit.text.toString()
                    bean?.ttddjl_images = selectImages
                }
                "短程受训经历" -> {
                    bean?.dcsxjl_sc = time.text.toString()
                    bean?.dcsxjl_zmzl = if (radio1.isChecked) 0 else 1
                    bean?.dcsxjl_text = editLayout.edit.text.toString()
                    bean?.dcsxjl_images = selectImages
                }
                "长程受训经历" -> {
                    bean?.ccsxjl_sc = time.text.toString()
                    bean?.ccsxjl_zmzl = if (radio1.isChecked) 0 else 1
                    bean?.ccsxjl_text = editLayout.edit.text.toString()
                    bean?.ccsxjl_images = selectImages
                }
            }
        }
        bean?.save()
        finish()

    }
}