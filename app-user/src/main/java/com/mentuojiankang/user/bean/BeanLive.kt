package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.dateT

class BeanLive : MyBaseBean() {
    var accountId = ""
    var avatar = ""
    //是否已购买, 1: 已预约，2：已购买
    var bought = 0
    var channelName = ""
    var coverImage = ""
    var detail = ""
    //1: 免费，2：收费
    var free = 0
    var liveCourseId = ""
    var liveCourseName = ""
    //课程开始时间
    var liveCourseStartAt = ""
    var lookNum = 0
    var nickname = ""
    var paid = false
    var shopId = ""
    var startTime = ""
    //直播状态，1-直播中，2-预约
    var status = 0

    fun startTimeDes() = liveCourseStartAt?.dateT("MM月dd日 HH:mm")


}