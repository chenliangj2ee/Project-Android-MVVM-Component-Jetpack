package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.obj.ObjectQuestionType

class BeanConsultType : MyBaseBean() {
    var type = 0

    var status = false

    fun getTypeString(): String {
        return ObjectQuestionType.getTypeString(type)
    }
}