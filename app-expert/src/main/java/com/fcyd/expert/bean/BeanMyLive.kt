package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.dateT
import com.mtjk.utils.dateToLong

/**
 * author:chenliang
 * date:2021/11/18
 */
class BeanMyLive : MyBaseBean() {
    var id = ""
    var liveCourseSectionName = ""
    var startTime = ""
    var state = 0
    var channelName = ""
    var liveCourseCoverImage = ""

    fun startTimeDes() = "开始时间" + startTime.dateT("yyyy-MM-dd HH:mm")
    fun stateDes(): String {
        if (state == 1) {
            return "未开始"
        }
        if (state == 2) {
            return "已结束"
        }
        return ""
    }

    fun enable() =  System.currentTimeMillis() >= startTime.dateT("yyyy-MM-dd HH:mm:ss").dateToLong("yyyy-MM-dd HH:mm:ss") - 60 * 10*1000
}