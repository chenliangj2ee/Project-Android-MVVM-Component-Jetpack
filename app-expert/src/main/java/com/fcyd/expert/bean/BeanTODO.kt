package com.fcyd.expert.bean

import android.util.Log
import com.chenliang.annotation.ApiModel
import com.fcyd.expert.R
import com.mtjk.base.MyBaseBean
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.date
import com.mtjk.utils.dateT
import com.mtjk.utils.dateToLong
import com.mtjk.utils.log

/**
 * author:chenliang
 * date:2021/11/18
 */
class BeanTODO : MyBaseBean() {
    var id = ""
    var productType = 0
    var startTime = ""
    var endTime = ""
    var extend = BeanModel()

    @Transient
    var guide = BeanTodoGuide()


    fun productTypeImage(): Int {
        if (productType == ObjectProduct.TYPE_CONSULT) {
            return R.drawable.icon_todo_consult
        }
        if (productType == ObjectProduct.TYPE_LIVE) {
            return R.drawable.icon_todo_live
        }
        return R.drawable.icon_todo_consult
    }

    fun isVideo(): Int {
        if (productType == ObjectProduct.TYPE_LIVE) {
            return R.drawable.icon_todo_video
        }
        if (productType == ObjectProduct.TYPE_CONSULT) {
            if ("1" == extend.consultType) {
                return R.drawable.icon_todo_video
            }
            if ("2" == extend.consultType) {
                return R.drawable.icon_todo_audio
            }
        }


        return R.drawable.icon_todo_video
    }


    fun startDes(): String {
        if (productType == ObjectProduct.TYPE_LIVE) {
            return "开始直播"
        }
        if (productType == ObjectProduct.TYPE_CONSULT) {
            return "开始咨询"
        }
        return ""
    }

    fun datetimeDes(): String {
        if (startTime == null) startTime = ""
        if (endTime == null) endTime = ""
        if (startTime.isNotEmpty() && endTime.isNotBlank()) {
            return startTime.dateT("yyyy-MM-dd  ") + startTime.dateT("HH:mm-") + endTime.dateT("HH:mm")
        }
        if (startTime.isNotEmpty()) {
            return startTime.dateT("yyyy-MM-dd  ") + startTime.dateT("HH:mm")
        }
        if (endTime.isNotEmpty()) {
            return endTime.dateT("yyyy-MM-dd  ") + endTime.dateT("HH:mm")
        }
        return ""
    }

    fun userName(): String = run {
        if (extend.userList.isNotEmpty()) {
            return extend.userList[0].nickName
        }
        return ""
    }

    fun enable() =
        if (ApiModel.dev)
            true
        else System.currentTimeMillis() >= (startTime.dateT("yyyy-MM-dd HH:mm:ss")
            .dateToLong("yyyy-MM-dd HH:mm:ss") - 60 * 10 * 1000)
}