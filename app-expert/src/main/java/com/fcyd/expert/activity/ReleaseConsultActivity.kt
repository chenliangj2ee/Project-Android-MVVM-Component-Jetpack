package com.fcyd.expert.activity

import android.content.Intent
import com.fcyd.expert.bean.BeanConsult
import com.fcyd.expert.databinding.ActivityReleaseConsultBinding
import com.fcyd.expert.vm.ConsultViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.bean.BeanTime
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_release_consult.*

/**
 * tag==发布咨询/选择类型，方式，咨询时间
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "发布咨询")
class ReleaseConsultActivity : MyBaseActivity<ActivityReleaseConsultBinding, ConsultViewModel>() {

    @MyField
    var id = ""
    var bean = BeanConsult()

    override fun initCreate() {
        mBinding.data = bean
        getConsultInfo()
//        mToolBar.showRight("帮助") { }
        editChanged(editarea.edit, price) { check() }
    }


    override fun initClick() {
        addIcon.click { selectImage(false) }
        delete.click {
            image.setImageBitmap(null)
            delete.show(false)
        }
        type1.click {
            bean.serverType = 1
            time.isEnabled = true
            check()
        }
        type2.click {
            bean.serverType = 2
            time.isEnabled = true
            check()
        }
        mode1.click {
            if (mode1.isChecked)
                bean.consultType.add(1)
            else
                bean.consultType.remove(1)
            check()
        }
        mode2.click {
            if (mode2.isChecked)
                bean.consultType.add(2)
            else
                bean.consultType.remove(2)
            check()
        }
        time.click {
            if (bean.serverType == 0) {
                toast("请选择咨询类型")
                return@click
            }
            bean.consultTime.timeIntervals.forEach { it.initHM() }
            goto(
                ConsultTimeSettingActivity::class.java,
                "type",
                bean.serverType,
                "times",
                bean.consultTime.timeIntervals,
                "weeks",
                bean.consultTime.week
            )
        }

        submitConsult.click { submitConsult() }
    }

    /**
     * 发布
     */
    private fun submitConsult() {
        bean.salePrice = mBinding.price.text.toString().toFloat()
        bean.price = mBinding.price.text.toString().toFloat()
        bean.consultTime.timeIntervals.forEach { it.initTime() }


        log("发布咨询：${bean.toJson()}")

        mViewModel.releaseConsult(bean).obs(this) {
            it.y { gotoFinish(ReleaseConsultSuccessActivity::class.java) }

        }
    }

    /**
     * 咨询时间选择后回调
     */
    @Subscribe(code = BusCode.RESULT_TIME_SETTINGS)
    fun resultTimes(intent: Intent) {
        bean.consultTime.timeIntervals.clear()
        bean.consultTime.week.clear()
        bean.consultTime.timeIntervals.addAll(intent.getSerializableExtra("times") as ArrayList<BeanTime>)
        bean.consultTime.week.addAll(intent.getIntegerArrayListExtra("weeks") as ArrayList<Int>)
        time.text = "修改"
        check()
    }

    /**
     * 图片选择后回调》上传
     */
    override fun resultSelectImage(array: ArrayList<String>) {
        super.resultSelectImage(array)

        if (array.isNotEmpty()) {
            var url = array[0]
            image.load(url, 10)
            delete.show(true)

            MyImage.uploadBusiness(url) {
                if (it.finish) {
                    bean.coverImage = it.path
                    check()
                }
            }
        }

    }

    /**
     * 检查是否启用【发布】按钮
     */
    private fun check() {
        with(mBinding) {
            submitConsult.isEnabled =
                editarea.getText().isNotEmpty()
                        && bean.serverType != -1
                        && bean.consultType.size != 0
                        && price.text.isNotEmpty()
                        && bean.coverImage != null
                        && bean.consultTime.timeIntervals.size > 0
                        && bean.coverImage.isNotEmpty()
        }
    }

    /**
     * 网络加载数据
     */
    private fun getConsultInfo() {
        if (id.isNullOrEmpty())
            return
        mViewModel.getConsultInfo(id).obs(this) {

            it.c { getConsultInfoSuccess(it) }
            it.y { getConsultInfoSuccess(it) }
            it.n { finish() }
        }
    }

    /**
     * 网络数据同步到view
     */
    private fun getConsultInfoSuccess(bean: BeanConsult) {
        if (bean == null) {
            finish()
        }
        bean.consultTime.timeIntervals.forEach { it.initHM() }
        this.bean = bean
        mBinding.data = bean
        var price = bean.price.toString()
        if (price.endsWith(".0"))
            price = price.replace(".0", "")
        mBinding.price.setText(if (bean.price > 0) price else "")
        check()
    }

}