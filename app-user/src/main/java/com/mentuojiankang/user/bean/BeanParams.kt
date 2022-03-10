package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

class BeanParams : MyBaseBean() {
    var day = ""
    var endTime = ""
    var startTime = ""
    var shopId: String? = null
    var consultType = 1 //1视频，2语音
}