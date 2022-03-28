package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

class BeanVisitorConsultDetail : MyBaseBean() {
    var name = ""
    var age = 0
    var sex = 0
    var questionType = intArrayOf()
    var description = ""
    var helperResult = ""
    var helperMethod = ""
    var mainQuestion = ""
    var orderItemId = ""
    var shopId = ""
    var summary = ""
    var userId = ""
    var wishResult = ""
    
    fun sexString() :String{
        return if(sex == 2) "女" else "男"
    }
}