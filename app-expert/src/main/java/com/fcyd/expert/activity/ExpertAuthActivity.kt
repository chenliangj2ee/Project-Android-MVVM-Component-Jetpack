package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanAuth
import com.fcyd.expert.databinding.ActivityUserInfoEditBinding
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.*
import com.mtjk.view.DialogPicker
import com.mtjk.vm.AccountViewModel
import com.zaaach.citypicker.CityPicker
import com.zaaach.citypicker.adapter.OnPickListener
import com.zaaach.citypicker.model.City
import com.zaaach.citypicker.model.HotCity
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_user_info_edit.*
import kotlinx.android.synthetic.main.layout_header_expert_auth.*

/**
 * tag==专家认证
 * author:chenliang
 * date:2021/11/3
 */
class ExpertAuthActivity : MyBaseActivity<ActivityUserInfoEditBinding, AccountViewModel>() {

    var cynxArrays = arrayListOf<String>()
    var beanAuth = BeanAuth().get<BeanAuth>() ?: BeanAuth()

    override fun initCreate() {
        mBinding.bean = beanAuth
        step1.isChecked = true
        for (i in 1..50) {
            cynxArrays.add("${i}年")
        }
        fullscreenTransparentBar(true)
    }

    override fun initClick() {

        with(mBinding) {
            header.click { selectImage(false) }
            nan.click { beanAuth?.sex = 1 }
            nv.click { beanAuth?.sex = 2 }
            cynx.click { cynxDialog() }
            address.click { initCity() }
            jybj.goto(EducationEditActivity::class.java, "bean", beanAuth)
            zgzs.goto(QualificationListActivity::class.java)

            gajl.goto(
                ExperienceEditActivity::class.java,
                "toolbarTitle",
                "个案经历",
                "title",
                "累计个案时长",
                "hint",
                "如果您无法上传个案证明，可在此处描述您 的个案经历。例如：20016年1月至2021年8月，总计100小时，认知流派等描述。"
            )
            grtyjl.goto(
                ExperienceEditActivity::class.java,
                "toolbarTitle",
                "个人体验经历",
                "title",
                "累计个人体验时长",
                "hint",
                "如果您无法上传个人体验证明，可在此处描述您的个人体验经历。例如：2016年1月至2021年8月，总计100小时，您自己的咨询取向等描述。"
            )
            gtddjl.goto(
                ExperienceEditActivity::class.java,
                "toolbarTitle",
                "个人督导经历",
                "title",
                "累计个人督导时长",
                "hint",
                "如果您无法上传个人督导证明，可在此处描述您的个人督导经历。例如：2016年1月至2021年8月，总计100小时，督导姓名、督导取向等描述。"
            )
            ttddjl.goto(
                ExperienceEditActivity::class.java,
                "toolbarTitle",
                "团体督导经历",
                "title",
                "累计团体督导时长",
                "hint",
                ""
            )
            dcsxjl.goto(
                ExperienceEditActivity::class.java,
                "toolbarTitle",
                "短程受训经历",
                "title",
                "累计短程受训时长",
                "hint",
                ""
            )
            ccsxjl.goto(
                ExperienceEditActivity::class.java,
                "toolbarTitle",
                "长程受训经历",
                "title",
                "累计长程受训时长",
                "hint",
                ""
            )
            next.goto(ExpertIdUploadActivity::class.java)
        }

        name.editText.changed {
            nextIsEnabled()
        }

        areaSCLY.edit.changed {
            nextIsEnabled()
        }

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
                    beanAuth?.address = data!!.name
                    mBinding.bean?.notifyChange()
                }

                override fun onLocate() {
                }

                override fun onCancel() {
                }
            })
            .show();
    }

    override fun onResume() {
        super.onResume()
        beanAuth = BeanAuth().get<BeanAuth>() ?: BeanAuth()
        mBinding.bean = beanAuth
        nextIsEnabled()

    }

    private fun nextIsEnabled() {
        mBinding.next.isEnabled = beanAuth.name.isNotEmpty() &&
//                beanAuth.header.isNotEmpty() &&
                beanAuth.sex > 0 &&
                beanAuth.address.isNotEmpty() &&
                beanAuth.cynx.isNotEmpty() &&
                beanAuth.jybj_xx.isNotEmpty() &&
                beanAuth.zgzs.size > 0 &&
                beanAuth.gajl_sc?.isNotEmpty()!! &&
                beanAuth.grtyjl_sc?.isNotEmpty()!! &&
                beanAuth.gtddjl_sc?.isNotEmpty()!! &&
                beanAuth.ttddjl_sc?.isNotEmpty()!! &&
                beanAuth.dcsxjl_sc?.isNotEmpty()!! &&
                beanAuth.ccsxjl_sc?.isNotEmpty()!! &&
                beanAuth.scly.isNotEmpty()

    }

    override fun onPause() {
        super.onPause()
        beanAuth?.save()
    }

    /**
     * 从业年限
     */

    private fun cynxDialog() {
        var dialog = DialogPicker()
        dialog.setItems(cynxArrays)
        dialog.setTitle("从业年限")
        dialog.selected {
            beanAuth?.cynx = cynxArrays[it]
            mBinding.bean?.notifyChange()
        }
        dialog.show(this)
    }

    override fun resultSelectImage(array: ArrayList<String>) {
        super.resultSelectImage(array)
        beanAuth.header = array[0]
        mBinding.bean = beanAuth
        beanAuth.save()
    }

    @Subscribe(code = BusCode.AUTH_FINISH)
    fun authFinish() {
        finish()
    }

}