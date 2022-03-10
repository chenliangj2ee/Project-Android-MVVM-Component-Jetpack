package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

/**
 * 咨询服务
 * author:chenliang
 * date:2021/11/12
 */
class BeanConsultService : MyBaseBean() {
    var id = ""
    var shopId = ""
    var title = ""
    var coverImage = ""
    var salePrice:Double = 0.0
    var consultType = ""
    var consultNum = 0

    fun des(): String {
        return consultType.replace("1", "视频").replace("2", "语音").replace(",", "  ") + " / 50分钟"
    }

}