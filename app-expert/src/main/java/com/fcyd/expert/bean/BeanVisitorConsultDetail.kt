package com.fcyd.expert.bean

import com.fcyd.expert.R
import com.mtjk.base.MyBaseBean
import com.mtjk.obj.ObjectQuestionType
import com.mtjk.utils.dateT

class BeanVisitorConsultDetail : MyBaseBean() {

    var createBy = ""
    var createTime = ""
    var description = ""
    var helperMethod = ""
    var helperResult = ""
    var id = ""
    var mainQuestion = ""
    var orderItemId = ""
    var questionType = intArrayOf()
    var shopId = ""
    var state = 0
    var summary = ""
    var updateBy = ""
    var updateTime = ""
    var userId = ""
    var wishResult = ""

    fun consultTimeDesc(): String {
        if (!updateTime.isNullOrEmpty()) {
            return updateTime.dateT("yyyy.MM.dd  ") + updateTime.dateT("HH:mm:SS")
        }
        return ""
    }

    fun questionTypeDesc(): String {
        var result = ""
        if(questionType?.size > 0) {
            for(item in questionType) {
                var typeName = ObjectQuestionType.getTypeString(item)
                result = if(result.isNullOrEmpty()) typeName else (result + "," + typeName)
            }
        }
        return result
    }
}