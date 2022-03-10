package com.mtjk.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.dateToLong
import com.mtjk.utils.log
import com.mtjk.utils.toJson
import java.text.SimpleDateFormat
import java.util.*

/**
 * author:chenliang
 * date:2021/12/6
 */
class BeanTime : MyBaseBean() {
    var startHour = 0//B端时间段选择框使用
    var startMinute = 0//B端时间段选择框使用
    var endHour = 0//B端时间段选择框使用
    var endMinute = 0//B端时间段选择框使用
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

    fun timeDes(): String {
        var start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, startHour)
        start.set(Calendar.MINUTE, startMinute)

        var end = Calendar.getInstance()
        end.set(Calendar.HOUR_OF_DAY, endHour)
        end.set(Calendar.MINUTE, endMinute)
        var format = SimpleDateFormat("HH:mm")
        return format.format(start.timeInMillis) + " - " + format.format(end.timeInMillis)
    }


    fun nextTime(duration: Int): BeanTime {
        var start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, startHour)
        start.set(Calendar.MINUTE, startMinute)
        start.add(Calendar.MINUTE, 10 + duration)
        log("现在：${this.toJson()}")
        var next = BeanTime()
        next.startHour = start.get(Calendar.HOUR_OF_DAY)
        next.startMinute = start.get(Calendar.MINUTE)
        log("加后：${next.toJson()}")
        return next
    }


    fun initHM() {
        if (consultStartTime.contains(":")) {
            startHour = consultStartTime.split(":")[0].toInt()
            startMinute = consultStartTime.split(":")[1].toInt()
        }
        if (consultEndTime.contains(":")) {
            endHour = consultEndTime.split(":")[0].toInt()
            endMinute = consultEndTime.split(":")[1].toInt()
        }
    }

    fun initTime() {

        var format = SimpleDateFormat("HH:mm")

        var start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, startHour)
        start.set(Calendar.MINUTE, startMinute)
        consultStartTime = format.format(start.timeInMillis)

        var end = Calendar.getInstance()
        end.set(Calendar.HOUR_OF_DAY, endHour)
        end.set(Calendar.MINUTE, endMinute)
        consultEndTime = format.format(end.timeInMillis)
    }


    fun isPassTime(): Boolean {
        return "$consultDay $startTime".dateToLong("yyyy-MM-dd HH:mm:ss") <= System.currentTimeMillis()

    }
}