package com.fcyd.expert.dialog

import android.view.Gravity
import com.fcyd.expert.bean.BeanConsult
import com.fcyd.expert.bean.BeanConsultService
import com.fcyd.expert.databinding.DialogConsultSubBinding
import com.fcyd.expert.databinding.ItemConsultSubDateBinding
import com.fcyd.expert.databinding.ItemConsultSubTimeBinding
import com.fcyd.expert.vm.ConsultViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.obs
import com.mtjk.bean.BeanDate
import com.mtjk.bean.BeanTime
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
            initDate()
            close.click { dismiss() }

            /**
             * 日期
             */
            dateRecyclerView.disable()
            dates.addAll(initDate())
            dateRecyclerView.bindData<BeanDate> { bindWeekItem(it) }
            dateRecyclerView.addDatas(dates)
            /**
             * 时间
             */
            timeRecyclerView.disable()
            timeRecyclerView.pageSize = 1000
            timeRecyclerView.bindData<BeanTime> { bindTimeItem(it) }
            timeRecyclerView.layoutParams.height = (54 * 3).px2dip()

            /**
             * 默认为视频，日期选中当前
             */
            isVideo = true
            selectDate = dates[0]
            mBinding.date = selectDate

            /**
             * 视频，语音，目前没作用
             */
            if (service.consultType.contains("1") && service.consultType.contains("2")) {
                mBinding.video.show(true)
                mBinding.voice.show(true)
            } else if (service.consultType.contains("1")) {
                mBinding.isVideo = true
            } else if (service.consultType.contains("2")) {
                mBinding.isVideo = false
            } else {

            }

        }

        getService()

    }

    /**
     * 绑定日期
     */
    private fun bindWeekItem(it: BeanDate) {
        with(it.binding as ItemConsultSubDateBinding) {
            this.itemDate = it
            root.click { _ ->
                mBinding.dateRecyclerView.selected(it.itemPosition)
                mBinding.date = it
                selectDate = it
                selectTime = null
                mBinding.time = null
                getService()

            }
        }
    }

    /**
     * 绑定时间
     */
    private fun bindTimeItem(it: BeanTime) {
        with(it.binding as ItemConsultSubTimeBinding) {
            data = it
            root.click { _ ->
                if (!it.boo && !it.isPassTime()) {
                    mBinding.timeRecyclerView.selected(it.itemPosition)
                    mBinding.time = it.startTime + "-" + it.endTime
                    selectTime = it
                }
            }
        }
    }

    override fun initClick() {

        /**
         * 目前没用到
         */
        mBinding.video.click {
            mBinding.isVideo = true
            getService()
        }
        /**
         * 目前没用到
         */
        mBinding.voice.click {
            mBinding.isVideo = false
            getService()
        }



        mBinding.startSub.click {

            toast("自己无法预约")

        }
    }

    private fun initDate(): ArrayList<BeanDate> {
        var date = Calendar.getInstance()
        var array = ArrayList<BeanDate>()
        for (i in 0..13) {
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
                it.c {
                    timeRecyclerView.addCache(it)
                    scrollToPosition(it)
                }
                it.y {
                    timeRecyclerView.addDatas(it)
                    scrollToPosition(it)
                }
                it.n { timeRecyclerView.clearData() }
            }
        }
    }

    /**
     * 跳转到可选时间段
     */
    private fun scrollToPosition(list: List<BeanTime>) {
        var index = 0;
        for (item in list) {
            if (!item.isPassTime() && !item.boo) {
                break
            } else {
                index++
            }
        }
        mBinding.timeRecyclerView.scrollToPosition(index)
    }

}