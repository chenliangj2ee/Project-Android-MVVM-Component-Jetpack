package com.mtjk.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.date

/**
 * author:chenliang
 * date:2021/11/30
 */
class BeanDate : MyBaseBean() {

    var time: Long = 0
    var selected = false

    var week = ""
    var weekNum = 0
    fun timeDes(): String {
        return if (time.date("MM-dd") == System.currentTimeMillis().date("MM-dd")) {
            "今天"
        } else {
            time.date("MM-dd")
        }

    }

}