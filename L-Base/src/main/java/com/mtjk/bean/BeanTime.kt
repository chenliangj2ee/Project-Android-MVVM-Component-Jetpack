package com.mtjk.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.dateToLong
import java.text.SimpleDateFormat
import java.util.*

/**
 * author:chenliang
 * date:2021/12/6
 */
class BeanTime : MyBaseBean() {
    var text = ""//B端使用
    var state = 0
    var id = ""//B端时间段选择框使用
    var consultStartTime = ""//B端网络请求入参使用
    var consultEndTime = ""//B端网络请求入参使用


    var boo = true//ture-被占用
    var consultDay = ""
    var endTime = ""
    var price: Double = 0.0
    var salePrice = 0.0
    var serverType = 0
    var shopId = ""
    var startTime = ""
    var selected = false

    var serverId = ""

    var startHour = 0
    var startMinute = 0
    var endHour = 0
    var endMinute = 0


    fun initText() {
        if (consultStartTime.length > 4)
            text = consultStartTime.substring(0, 5)
    }

    fun initTime() {
        if(text.isNotEmpty()) {
            var format = SimpleDateFormat("HH:mm")
            var start = Calendar.getInstance()
            start.set(Calendar.HOUR_OF_DAY, text.split(":")[0].toInt())
            start.set(Calendar.MINUTE, 0)
            consultStartTime = format.format(start.time)
            start.add(Calendar.HOUR_OF_DAY, 1)
            consultEndTime = format.format(start.time)
        }

        if (consultStartTime.contains(":")) {
            startHour = consultStartTime.split(":")[0].toInt()
            startMinute = consultStartTime.split(":")[1].toInt()
        }
        if (consultEndTime.contains(":")) {
            endHour = consultEndTime.split(":")[0].toInt()
            endMinute = consultEndTime.split(":")[1].toInt()
        }
    }


    fun isPassTime(): Boolean {
        return "$consultDay $startTime".dateToLong("yyyy-MM-dd HH:mm:ss") <= System.currentTimeMillis()

    }
}