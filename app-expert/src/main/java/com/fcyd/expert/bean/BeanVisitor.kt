package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.dateT

class BeanVisitor : MyBaseBean() {
    var account = ""    //来访者账号
    var age = 18
    var avatar = ""
    var birthday = ""
    var consultNumber = 0 //咨询量
    var cumulative = 0.0  //累计消费
    var delFlag = 0     //删除状态
    var id = ""         //userId
    var industry = ""   //行业
    var lastConsultTime = ""
    var location = ""   //长居地
    var maritalStatus = 0 //婚姻状态,0-保密，1-已婚，2-未婚
    var nickname = ""
    var occupation = "" //职业
    var realname = ""   //真实姓名
    var sex = 0         //性别：0，默认，1男，2女
    var status = 0
    var vipLevel = 0

    fun sexDesc(): String {
        return if(sex == 2) "女" else "男"
    }

    fun maritalDesc(): String {
        return if(maritalStatus == 1) "已婚" else if(maritalStatus == 2) "未婚" else "保密"
    }

    fun industryDesc(): String {
        return if(industry.isNullOrEmpty()) "未知" else industry
    }

    fun occupationDesc(): String {
        return if(occupation.isNullOrEmpty()) "未知" else occupation
    }

    fun locationDesc(): String {
        return if(location.isNullOrEmpty()) "未知" else location
    }

    fun lastConsultTimeDesc(): String {
        if (!lastConsultTime.isNullOrEmpty()) {
            return lastConsultTime.dateT("yyyy.MM.dd  ") + lastConsultTime.dateT("HH:mm:SS")
        }
        return ""
    }

}