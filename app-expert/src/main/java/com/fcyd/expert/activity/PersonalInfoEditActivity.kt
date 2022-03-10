package com.fcyd.expert.activity

import android.app.Dialog
import android.os.Handler
import com.fcyd.expert.bean.BeanAuth
import com.fcyd.expert.databinding.ActivityPersonalInfoEditBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.mtjk.view.DialogPicker
import com.mtjk.vm.AccountViewModel
import com.zaaach.citypicker.CityPicker
import com.zaaach.citypicker.adapter.OnPickListener
import com.zaaach.citypicker.model.City
import com.zaaach.citypicker.model.HotCity
import gorden.rxbus2.RxBus
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_personal_info_edit.*

/**
 * tag==个人资料
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "个人资料")
class PersonalInfoEditActivity :
    MyBaseActivity<ActivityPersonalInfoEditBinding, AccountViewModel>() {
    var beanAuth = BeanAuth()
    var user = getBeanUser()
    var educationModel =  getBeanUser()?.educationExperienceModel
    var token = user?.token
    var cynxArrays = arrayListOf<String>()
    var intyear = 0
    override fun initCreate() {

        mViewModel.getUserInfo().obs(this) {
            it.y {
                mBinding.bean = it
                if (it.educationExperienceModel != null) {
                    beanAuth.jybj_endtime = it.educationExperienceModel.endTime
                    beanAuth.jybj_starttime = it.educationExperienceModel.startTime
                    beanAuth.jybj_time = beanAuth.jybj_starttime + " - " + beanAuth.jybj_endtime
                    beanAuth.jybj_image = it.educationExperienceModel.certificate + ""
                    beanAuth.jybj_xl = it.educationExperienceModel.education
                    beanAuth.jybj_zy = it.educationExperienceModel.major
                    beanAuth.jybj_xx = it.educationExperienceModel.school
                }
                beanAuth.zgzs = it.qualificationCertificateModels
                beanAuth.save()
                user?.gender = it.gender
                user = it
                user?.token = token!!

            }
            it.c { mBinding.bean = it }
            it.n { log(it) }
        }

        expertareaSCLY.edit.isEnabled=false
        expertareaXXJS.edit.isEnabled=false

    }

    override fun onResume() {
        super.onResume()
        hideSoftInput(expertname.editText)
    }


    override fun initClick() {
//        header.click { selectImage(false) }
//        exportnext.click { dialog("是否提交").n("取消") {  }.y("确认"){uploadImage() } }
//        expertloction.click { initCity() }
//        expertman.click { user?.gender = 1 }
//        expertwoman.click { user?.gender = 2 }
//        expertcynx.click { initWorkyear() }
//        //教育背景
//        experteducation.click { goto(EducationEditActivity::class.java, "bean", beanAuth) }
//        //资格证书
//        expertcertificate.click { goto(QualificationListActivity::class.java, "beanAuth", beanAuth) }
//
//        with(mBinding) {
//            expertareaSCLY.edit.changed {
//                exportnextclick()
//            }
//            expertname.editText.changed {
//                exportnextclick()
//            }
//        }

    }


    lateinit var loading: Dialog
    var loadCount = 0
    var finishCount = 0
    var successCount = 0
    fun uploadImage() {
        loading = loading("上传中")
        loadCount = 0
        finishCount = 0
        successCount = 0

        //上传头像
        if (!user!!.avatar.contains("http")) {
            log("上传头像")
            loadCount++
            MyImage.uploadBusiness(beanAuth.header) {
                if (it.finish) {
                    beanAuth.header = it.path
                    beanAuth.save()
                    finishCount++
                    if (it.success) {
                        successCount++
                    }
                    if (loadCount == finishCount) {
                        expertInfor()
                    }
                }
            }
        }

        //上传教育背景
        if (!beanAuth.jybj_image.contains("http")) {
            log("上传教育背景")
            loadCount++
            MyImage.uploadBusiness(beanAuth.jybj_image) {
                if (it.finish) {
                    beanAuth.jybj_image = it.path
                    beanAuth.save()
                    finishCount++
                    if (loadCount == finishCount) {
                        expertInfor()
                    }
                }
            }
        }

        //上传资格证书
        beanAuth.zgzs.forEach {
            log("上传资格证书")
            if (!it.certificateImageUrl.contains("http") && it.certificateImageUrl.isNotEmpty()) {
                loadCount++
                MyImage.uploadBusiness(it.certificateImageUrl) { load ->
                    if (load.finish) {
                        it.certificateImageUrl = load.path
                        beanAuth.save()
                        finishCount++
                        if (load.success) {
                            successCount++
                        }
                        if (loadCount == finishCount) {
                            expertInfor()
                        }
                    }
                }
            }
        }

        if (loadCount == 0)
            expertInfor()
        log("上传图片总量:$loadCount")
    }

    fun expertInfor() {
        loading.dismiss()
        addEducationAbout()
        user?.educationExperienceModel = educationModel!!
        user?.qualificationCertificateModels?.clear()
        user?.qualificationCertificateModels?.addAll(beanAuth?.zgzs)
        user?.realName = expertname.editText.text.toString()
        user?.personalIntroduce = expertareaXXJS.edit.text.toString()
        user?.introduction = expertareaSCLY.edit.text.toString()
        intyear = timeToData(expertcynx.editText.text.toString())
        user?.years = intyear
        user?.location = expertloction.editText.text.toString()
        mViewModel.updateUserAccount(user!!).obs(this) {
            it.y {
                user!!.save()
                postDelayed(200) {
                    super.onBackPressed()
                }
                send(BusCode.REFRESH_MINE_USER_INFO)

            }
            it.n {
                log("上传" + it)
                finish()
            }
        }
    }

    fun addEducationAbout() {
        educationModel =  getBeanUser()?.educationExperienceModel
        beanAuth = BeanAuth().get<BeanAuth>() ?: BeanAuth()
        educationModel?.education = beanAuth?.jybj_xl
        educationModel?.certificate = beanAuth?.jybj_image
        educationModel?.school = beanAuth?.jybj_xx
        educationModel?.major = beanAuth?.jybj_zy
        if (beanAuth.jybj_starttime.isNotEmpty() && beanAuth.jybj_endtime.isNotEmpty()) {
            educationModel?.startTime = beanAuth.jybj_starttime
            educationModel?.endTime = beanAuth.jybj_endtime
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun resultSelectImage(array: ArrayList<String>) {
        if (array.isEmpty())
            return
        user?.avatar = "file://" + array?.get(0) ?: ""

        MyImage.uploadApp(array?.get(0)!!) {
            if (it.finish) {
                user?.avatar = it.path
                user!!.save()
            }
        }
        mBinding?.bean = user
    }

    private fun initCity() {
        var hotCities = ArrayList<HotCity>()
        hotCities.add(HotCity("北京", "北京", "101010100"))
        hotCities.add(HotCity("上海", "上海", "101020100"))
        hotCities.add(HotCity("广州", "广东", "101280101"))
        hotCities.add(HotCity("深圳", "广东", "101280601"))
        hotCities.add(HotCity("杭州", "浙江", "101210101"))

        CityPicker.from(this) //activity或者fragment
            .enableAnimation(true)
            .setHotCities(hotCities)    //指定热门城市
            .setOnPickListener(object : OnPickListener {
                override fun onPick(position: Int, data: City?) {
                    expertloction.setValue(data!!.name)
                    user?.location = data?.name
                    mBinding.bean = user
                }

                override fun onLocate() {
                }

                override fun onCancel() {
                }
            }).show();
    }

    private fun initWorkyear() {
        for (i in 1..50) {
            cynxArrays.add("${i}年")
        }
        var dialog = DialogPicker()
        dialog.setItems(cynxArrays)
        dialog.setTitle("从业年限")
        dialog.selected {
            expertcynx.setValue(cynxArrays[it])
            intyear = timeToData(cynxArrays[it])
            user?.years = intyear
            mBinding.bean = user
        }
        dialog.show(this)
    }


    @Subscribe(code = BusCode.REFRESH_EXPERT_EDIT)
    fun onRefresh() {
        beanAuth = BeanAuth().get<BeanAuth>() ?: BeanAuth()
        if (beanAuth!!.jybj_xx != null) {
            experteducation.setValue(beanAuth!!.jybj_xx)
        }
        if (beanAuth!!.zgzs.isNotEmpty()) {
            expertcertificate.setValue(beanAuth.zgzs[0].certificateName)
            user!!.qualificationCertificateModels = beanAuth!!.zgzs
        }
        postDelayed(100) {
            exportnextclick()
        }
    }

    fun timeToData(time: String): Int {
        var endtime = 0
        var index: Int = time.indexOf("年")
        if (time.length > 0)
            endtime = time.substring(0, index).toInt()
        return endtime
    }

    fun exportnextclick() {
        with(mBinding) {
            exportnext.isEnabled =
                user?.avatar!!.isNotEmpty() &&
                        mBinding.expertname.editText.text.isNotEmpty() &&
                        user?.gender!! > 0 &&
                        user?.location!!.isNotEmpty() &&
                        user?.years!! > 0 &&
                        mBinding.experteducation.editText.text.isNotEmpty() &&
                        mBinding.expertareaSCLY.edit.text.isNotEmpty() &&
                        mBinding.expertcertificate.editText.text.isNotEmpty()
        }
    }

}