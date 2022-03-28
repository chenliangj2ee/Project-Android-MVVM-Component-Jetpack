package com.fcyd.expert.bean

import com.fcyd.expert.R
import com.mtjk.base.MyBaseBean
import com.mtjk.utils.dateT

class BeanVisitorConsult : MyBaseBean() {
    var visitorCaseId = ""
    var consultEndTime = ""
    var consultStartTime = ""
    var title = ""
    var consultType = 0 //1视频 2 语音

    fun consultTypeDrawable() : Int{
        if(consultType == 1) {
            return R.drawable.visitor_consult_type_video_tag
        } else if(consultType == 2) {
            return R.drawable.visitor_consult_type_audio_tag
        }
        return 0
    }

    fun consultStartTimeDesc(): String {
        if (!consultStartTime.isNullOrEmpty()) {
            return consultStartTime.dateT("yyyy.MM.dd  ") + consultStartTime.dateT("HH:mm:SS")
        }
        return ""
    }
}