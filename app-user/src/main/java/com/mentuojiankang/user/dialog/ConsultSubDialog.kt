package com.mentuojiankang.user.dialog

import android.view.Gravity
import com.mentuojiankang.user.activity.PaymentConfirmActivity
import com.mentuojiankang.user.bean.BeanConsult
import com.mentuojiankang.user.bean.BeanConsultService
import com.mentuojiankang.user.bean.BeanParams
import com.mentuojiankang.user.bean.BeanPayInfo
import com.mentuojiankang.user.databinding.DialogConsultSubBinding
import com.mentuojiankang.user.databinding.ItemConsultSubDateBinding
import com.mentuojiankang.user.databinding.ItemConsultSubTimeBinding
import com.mentuojiankang.user.vm.ConsultViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.obs
import com.mtjk.bean.BeanDate
import com.mtjk.bean.BeanTime
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * tag==咨询方式与时间
 * author:chenliang
 * date:2021/11/30
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class ConsultSubDialog(service: BeanConsultService, bean: BeanConsult) :
    MyBaseDialog<DialogConsultSubBinding>() {
    var service = service
    private var consultType = "1"
    var dates = ArrayList<BeanDate>()
    var bean = bean
    var selectTime: BeanTime? = null
    override fun initCreate() {

        with(mBinding) {
            close.click { dismiss() }
            dateRecyclerView.disable()
            dates.addAll(initDate())
            dateRecyclerView.bindData<BeanDate> {
                with(it.binding as ItemConsultSubDateBinding) {
                    this.itemDate = it
                    root.click { _ ->
                        dateRecyclerView.selected(it.itemPosition)
                        mBinding.date = it
                        selectDate = it
                        selectTime = null
                        time = null
                        getService()

                    }
                }
            }
            dateRecyclerView.addDatas(dates)
            timeRecyclerView.disable()
            timeRecyclerView.pageSize=1000
            timeRecyclerView.bindData<BeanTime> {
                with(it.binding as ItemConsultSubTimeBinding) {
                    data = it
                    root.click { _ ->
                        if (!it.boo && !it.isPassTime()) {
                            timeRecyclerView.selected(it.itemPosition)
                            time = it.startTime + "-" + it.endTime
                            selectTime = it
                        }

                    }
                }
            }
            timeRecyclerView.layoutParams.height = (54 * 3).px2dip()
            isVideo = true
            selectDate = dates[0]
            mBinding.date = selectDate

            if (service.consultType.contains("1") && service.consultType.contains("2")) {
                mBinding.video.show(true)
                mBinding.voice.show(true)
            } else if (service.consultType.contains("1")) {
                mBinding.isVideo = true
            } else if (service.consultType.contains("2")) {
                mBinding.isVideo = false
            }

            getService()
        }


        initDate()
    }

    override fun initClick() {


        mBinding.video.click {
            mBinding.isVideo = true
            getService()
        }



        mBinding.voice.click {
            mBinding.isVideo = false
            getService()
        }



        mBinding.startSub.click {

            var payInfo = BeanPayInfo()
            payInfo.paycoverImage = service.coverImage
            payInfo.paylessonTitle = service.title + ""
            payInfo.payprice = service.salePrice
            payInfo.paysectionCount = 0
            payInfo.productId = selectTime!!.serverId
            payInfo.subTitle = "预约时间 "+selectDate.time.date("yyyy-MM-dd")+ " ${selectTime!!.startTime.substring(0,5)}-"+selectTime!!.endTime.subSequence(0,5)

            log("dddd:${selectTime!!.toJson()}")

            var params = BeanParams()
            params.shopId = bean.id
            params.day = selectDate.time.date("yyyy-MM-dd")
            selectTime!!.initTime()
            params.startTime = selectTime!!.startTime
            params.endTime = selectTime!!.endTime
            params.consultType = if (mBinding.isVideo == true) 1 else 2


            var productType = ObjectProduct.TYPE_CONSULT

            goto(
                PaymentConfirmActivity::class.java,
                "order",
                payInfo,
                "params",
                params,
                "productType",
                productType
            )
            dismiss()
        }
    }

    private fun initDate(): ArrayList<BeanDate> {
        var date = Calendar.getInstance()
        var array = ArrayList<BeanDate>()
        for (i in 0..15) {
            var bean = BeanDate()
            var w = date[Calendar.DAY_OF_WEEK] - 1
            when (w) {
                0 -> bean.week = "日"
                1 -> bean.week = "一"
                2 -> bean.week = "二"
                3 -> bean.week = "三"
                4 -> bean.week = "四"
                5 -> bean.week = "五"
                6 -> bean.week = "六"
            }
            bean.weekNum = w
            bean.selected = i == 0
            bean.time = date.timeInMillis
            array.add(bean)
            date.add(Calendar.DAY_OF_MONTH, 1)
        }
        array[0].itemSelected = true
        return array
    }

    lateinit var selectDate: BeanDate
    private fun getService() {
        consultType = if (mBinding.isVideo == true) "1" else "2"
        var day = SimpleDateFormat("yyyy-MM-dd").format(selectDate.time)
        with(mBinding) {
            initVM(ConsultViewModel::class.java).getServiceTime(
                consultType,
                day,
                selectDate?.weekNum.toString(),
                service.id + "",
                service.shopId
            ).obs(this@ConsultSubDialog) {
                it.c { timeRecyclerView.addCache(it) }
                it.y { timeRecyclerView.addDatas(it) }
                it.n { timeRecyclerView.clearData() }
            }
        }
    }

}