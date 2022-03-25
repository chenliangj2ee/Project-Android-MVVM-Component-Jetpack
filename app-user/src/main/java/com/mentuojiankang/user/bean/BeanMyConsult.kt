package com.mentuojiankang.user.bean

import com.chenliang.annotation.ApiModel
import com.mtjk.base.MyBaseBean
import com.mtjk.utils.dateT
import com.mtjk.utils.dateToLong

/**
 * 我的咨询
 * author:chenliang
 * date:2021/11/12
 */
class BeanMyConsult : MyBaseBean() {
    var consultType = 0
    var image = ""
    var orderBaseId = ""
    var roomId = 0
    var serverId = ""
    var title = ""
    var expertName = ""
    var expertId = ""
    var avatar = ""
    var shopId = ""
    var startTime = ""
    var endTime = ""
    var channelName = ""

    fun enable() =
        if (startTime.isNullOrEmpty())
            false
        else if (ApiModel.dev)
            true
        else System.currentTimeMillis() >= startTime.dateT("yyyy-MM-dd HH:mm:ss")
            .dateToLong("yyyy-MM-dd HH:mm:ss") - 60 * 10 * 1000
}