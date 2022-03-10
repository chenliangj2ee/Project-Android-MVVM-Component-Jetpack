package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.toTime

/**
 * 选集
 * author:chenliang
 * date:2021/11/16
 */
class BeanSection : MyBaseBean() {
    var id = ""
    var playCount: Int = 0//播放次数
    var sectionName: String = ""//标题
    var contentTime: Int = 0//时长
    var isPlay = false
    var fileUrl = ""

    fun sizeDes() = contentTime.toTime()

}