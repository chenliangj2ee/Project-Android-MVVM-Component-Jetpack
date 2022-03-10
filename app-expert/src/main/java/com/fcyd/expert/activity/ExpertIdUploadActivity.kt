package com.fcyd.expert.activity

import android.app.Dialog
import com.fcyd.expert.R
import com.fcyd.expert.bean.BeanAuth
import com.fcyd.expert.databinding.ActivityExpertIdUploadBinding
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.*
import com.mtjk.view.DialogPicker
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_expert_id_upload.*
import kotlinx.android.synthetic.main.layout_header_expert_auth.*

/**
 * tag==专家认证/身份证上传
 * author:chenliang
 * date:2021/11/3
 */
class ExpertIdUploadActivity : MyBaseActivity<ActivityExpertIdUploadBinding, UserViewModel>() {
    var arrays = arrayListOf<String>("身份证", "护照", "港澳通行证", "台湾通行证")
    var imageType = 0//0,正面，1反面
    var errorCount = 0;

    var beanAuth = BeanAuth().get<BeanAuth>() ?: BeanAuth()

    override fun initCreate() {
        mBinding.bean = beanAuth
        mBinding.type = if (beanAuth.id_type == "身份证") 0 else 1
        step1.isChecked = true
        fullscreenTransparentBar(true)
        editChanged(idType.editText, idNum.editText) {
            enableNext()
        }
    }

    override fun initClick() {
        mBinding.idType.click { typeSelect() }
        mBinding.next.click { showSubmitDialog() }
        mBinding.camera1.click {
            imageType = 0
            selectImages = image1
            selectImageSingle(false)
        }
        mBinding.camera2.click {
            imageType = 1
            selectImages = image2
            selectImageSingle(false)
        }
    }

    private fun showSubmitDialog() {
        var dialog = dialog(
            """是否提交信息审核？
            |注意：审核中应用将无法操作
            |""".trimMargin()
        )
            .title("提示")
            .n("取消") {}
            .y("提交") { loadImages() }
        dialog.show(this)

    }

    lateinit var loading: Dialog
    var loadCount = 0
    var finishCount = 0
    var successCount = 0
    private fun loadImages() {
        loading = loading("上传中")
        loadCount = 0
        finishCount = 0
        successCount = 0
        //上传头像
        if (!beanAuth.header.contains("http") && beanAuth.header.isNotEmpty()) {
            loadCount++
            MyImage.uploadBusiness(beanAuth.header) {
                if (it.finish) {
                    beanAuth.header = it.path
                    finishCount++
                    if (it.success) {
                        successCount++
                    }
                    if (loadCount == finishCount) {
                        submit()
                    }
                }
            }
        }

        //上传教育背景
        if (!beanAuth.jybj_image.contains("http") && beanAuth.jybj_image.isNotEmpty()) {
            loadCount++
            MyImage.uploadBusiness(beanAuth.jybj_image) {
                if (it.finish) {
                    beanAuth.jybj_image = it.path
                    finishCount++
                    if (loadCount == finishCount) {
                        submit()
                    }
                }
            }
        }
        //上传教育背景
        beanAuth.zgzs.forEach {
            if (!it.certificateImageUrl.contains("http") && it.certificateImageUrl.isNotEmpty()) {
                loadCount++
                MyImage.uploadBusiness(it.certificateImageUrl) { load ->
                    if (load.finish) {
                        it.certificateImageUrl = load.path
                        finishCount++
                        if (load.success) {
                            successCount++
                        }
                        if (loadCount == finishCount) {
                            submit()
                        }
                    }
                }
            }
        }
        //上传个案经历
        beanAuth.gajl_images.forEach { path ->
            if (!path.contains("http") && path.isNotEmpty()) {
                loadCount++
                MyImage.uploadBusiness(path) {
                    if (it.finish) {
                        beanAuth.gajl_images[beanAuth.gajl_images.indexOf(path)] = it.path
                        finishCount++
                        if (it.success) {
                            successCount++
                        }
                        if (loadCount == finishCount) {
                            submit()
                        }
                    }
                }
            }
        }
        //上传个人体验经历
        beanAuth.grtyjl_images.forEach { path ->
            if (!path.contains("http") && path.isNotEmpty()) {
                loadCount++
                MyImage.uploadBusiness(path) {
                    if (it.finish) {
                        beanAuth.grtyjl_images[beanAuth.grtyjl_images.indexOf(path)] = it.path
                        finishCount++
                        if (it.success) {
                            successCount++
                        }
                        if (loadCount == finishCount) {
                            submit()
                        }
                    }
                }
            }
        }

        //上传个体督导经历
        beanAuth.gtddjl_images.forEach { path ->
            if (!path.contains("http") && path.isNotEmpty()) {
                loadCount++
                MyImage.uploadBusiness(path) {
                    if (it.finish) {
                        beanAuth.gtddjl_images[beanAuth.gtddjl_images.indexOf(path)] = it.path
                        finishCount++
                        if (it.success) {
                            successCount++
                        }
                        if (loadCount == finishCount) {
                            submit()
                        }
                    }
                }
            }
        }
        //上传团体督导经历
        beanAuth.ttddjl_images.forEach { path ->
            if (!path.contains("http") && path.isNotEmpty()) {
                loadCount++
                MyImage.uploadBusiness(path) {
                    if (it.finish) {
                        beanAuth.ttddjl_images[beanAuth.ttddjl_images.indexOf(path)] = it.path
                        finishCount++
                        if (it.success) {
                            successCount++
                        }
                        if (loadCount == finishCount) {
                            submit()
                        }
                    }
                }
            }
        }

        //上传短程受训经历
        beanAuth.dcsxjl_images.forEach { path ->
            if (!path.contains("http") && path.isNotEmpty()) {
                loadCount++
                MyImage.uploadBusiness(path) {
                    if (it.finish) {
                        beanAuth.dcsxjl_images[beanAuth.dcsxjl_images.indexOf(path)] = it.path
                        finishCount++
                        if (it.success) {
                            successCount++
                        }
                        if (loadCount == finishCount) {
                            submit()
                        }
                    }
                }
            }
        }

        //上传长程受训经历
        beanAuth.ccsxjl_images.forEach { path ->
            if (!path.contains("http") && path.isNotEmpty()) {
                loadCount++
                MyImage.uploadBusiness(path) {
                    if (it.finish) {
                        if (it.success) {
                            successCount++
                        }
                        beanAuth.ccsxjl_images[beanAuth.ccsxjl_images.indexOf(path)] = it.path
                        finishCount++
                        if (loadCount == finishCount) {
                            submit()
                        }
                    }
                }
            }
        }

        //上传身份证正面
        if (!beanAuth.id_image1.contains("http") && beanAuth.id_image1.isNotEmpty()) {
            loadCount++
            MyImage.uploadBusiness(beanAuth.id_image1) {
                if (it.finish) {
                    beanAuth.id_image1 = it.path
                    finishCount++
                    if (it.success) {
                        successCount++
                    }
                    if (loadCount == finishCount) {
                        submit()
                    }
                }
            }
        }
        //上传身份证反面
        if (!beanAuth.id_image2.contains("http") && beanAuth.id_image2.isNotEmpty()) {
            if (beanAuth.getIdTypeState() == 1) {
                loadCount++
                MyImage.uploadBusiness(beanAuth.id_image2) {
                    if (it.finish) {
                        beanAuth.id_image2 = it.path
                        finishCount++
                        if (it.success) {
                            successCount++
                        }
                        if (loadCount == finishCount) {
                            submit()
                        }
                    }
                }
            }
        }
        if (loadCount == 0)
            submit()
        log("上传图片总量:$loadCount")

    }


    /**
     * 失败小于3次，则自动上传
     */
    private fun submit() {

        loading.dismiss()
        if (loadCount != successCount) {
            if (errorCount < 3) {
                errorCount++
                mBinding.next.performClick()
            } else {
                errorCount = 0
                toast("图片上传失败，请重试")
            }

            return
        }

        mViewModel.inApp(beanAuth).obs(this) {
            it.y { gotoFinish(ExpertAuthSubmitSuccessActivity::class.java) }
        }
    }

    var image1 = arrayListOf<String>()
    var image2 = arrayListOf<String>()
    override fun resultSelectImage(array: ArrayList<String>) {
        super.resultSelectImage(array)
        when (imageType) {
            0 -> {
                image1 = array
                mBinding.idBg.load(array[0], 15)
                beanAuth.id_image1 = array[0]
            }
            1 -> {
                image2 = array
                mBinding.idBg2.load(array[0], 15)
                beanAuth.id_image2 = array[0]
            }
        }
        enableNext()
    }

    private fun typeSelect() {
        var dialog = DialogPicker()
        dialog.setItems(arrays)
        dialog.selected {
            mBinding.type = it
            when (it) {
                0 -> {
                    if (image1.isEmpty())
                        mBinding.idBg.setImageResource(R.drawable.icon_id_bg_zheng)
                }
                1 -> {
                    if (image1.isEmpty())
                        mBinding.idBg.setImageResource(R.drawable.icon_id_bg_huzhao)
                }
                2 -> {
                    if (image1.isEmpty())
                        mBinding.idBg.setImageResource(R.drawable.icon_id_bg_gangao)
                }
                3 -> {
                    if (image1.isEmpty())
                        mBinding.idBg.setImageResource(R.drawable.icon_id_bg_taiwan)
                }
            }

            mBinding.idType.setValue(arrays[it])
        }
        dialog.setTitle("证件类型")
        dialog.show(this)
    }

    override fun onPause() {
        super.onPause()
        beanAuth?.save()
    }

    @Subscribe(code = BusCode.AUTH_FINISH)
    fun authFinish() {
        finish()
    }

    private fun enableNext() {
        if (beanAuth.id_type == "身份证") {
            next.isEnabled =
                beanAuth.id_Num.isNotEmpty() && beanAuth.id_type.isNotEmpty()
        } else {
            next.isEnabled = beanAuth.id_Num.isNotEmpty() && beanAuth.id_type.isNotEmpty()
        }

    }
}