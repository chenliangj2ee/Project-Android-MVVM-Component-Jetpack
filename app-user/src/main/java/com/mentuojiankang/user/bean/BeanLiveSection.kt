package com.mentuojiankang.user.bean

import com.mentuojiankang.user.R
import com.mtjk.base.MyBaseBean
import com.mtjk.utils.dateT
import com.mtjk.utils.dateToLong
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BeanLiveSection : MyBaseBean() {

    var accountId = ""
    var avatar = ""
    var channelName = ""
    var createBy = ""
    var createTime = ""
    var endTime = ""            //结束时间
    var expertName = ""
    var id = ""
    var liveCourseId = ""
    var liveCourseName = ""
    var liveCourseNum = 0
    var liveMode = ""           //1: live, 2: record
    var liveType = 0            //1: video, 2: voice
    var name = ""
    var playBack = 0            //1: 禁止 2: 永久 3: 限时
    var playBackEndTime = ""    //回放限时时间
    var shopId = ""
    var startTime = ""          //开始时间
    var state = 0
    var status = 0              //0: 上架 1: 下架
    var updateBy = ""
    var updateTime = ""

    fun typeDrawable() = when (liveType) {
        1 -> R.drawable.icon_todo_video
        2 -> R.drawable.icon_todo_audio
        else -> 0
    }

    /*
    * 0:直播中，1：未开始，2：已结束
    * */
    fun currentState() : Int{
        var startTimeStamp = if(startTime.isNullOrEmpty()) 0
            else startTime?.dateT("yyyy-MM-dd HH:mm:ss")?.dateToLong("yyyy-MM-dd HH:mm:ss")
        var endTimeStamp = if(endTime.isNullOrEmpty()) 0
            else endTime?.dateT("yyyy-MM-dd HH:mm:ss")?.dateToLong("yyyy-MM-dd HH:mm:ss")
        var currentTimeStamp = System.currentTimeMillis()
        if(currentTimeStamp < startTimeStamp) {
            return 1
        } else if(currentTimeStamp > endTimeStamp) {
            return 2
        }
        return 0
    }

    fun stateDesc() :String {
        var curState = currentState()
        if(curState == 1) {
            return "未开始"
        } else if(curState == 2) {
            return "已结束"
        }
        return ""

    }

    fun timeDesc() : String{
        if(startTime.isNullOrEmpty()) {
            return ""
        }
        var startDesc = startTime.dateT("yyyy.MM.dd HH:mm")
        if(endTime.isNullOrEmpty()) {
            return startDesc
        }

        var endDesc = endTime.dateT("yyyy.MM.dd HH:mm")
        var start = LocalDateTime.parse(startDesc, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
        var end = LocalDateTime.parse(endDesc, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))

        if(start.year != end.year) {
            endDesc = endTime.dateT("yyyy.MM.dd HH:mm")
        } else if(start.monthValue != end.monthValue || start.dayOfMonth != end.dayOfMonth){
            endDesc = endTime.dateT("MM.dd HH:mm")
        } else {
            endDesc = endTime.dateT("HH:mm")
        }
        return startDesc + "-" + endDesc
    }

}