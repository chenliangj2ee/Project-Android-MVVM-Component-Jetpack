package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.date
import com.mtjk.utils.handleDate
import com.mtjk.utils.timestamp

/**
 * 咨询师
 * author:chenliang
 * date:2021/11/11
 */
class BeanConsultant : MyBaseBean() {

    var createBy = "";
    var createTime = "";
    var expertId = "";
    var id = "";
    var state = 0
    var updateB = "";
    var updateTime = "";
    var userId = "";

    companion object Date {
        val date = HashMap<String, String>()
    }

    var dateShow = false
    fun initDate() {
        if (dateShow)
            return
        var date = createTime.timestamp().date("yyyy-MM-dd")
        if (BeanConsultant.date[date] == null) {
            BeanConsultant.date[date] = date
            dateShow = true;
        } else {
            dateShow = false;
        }
    }

    fun dateDes(): String {
        return createTime.timestamp().handleDate()
    }


}