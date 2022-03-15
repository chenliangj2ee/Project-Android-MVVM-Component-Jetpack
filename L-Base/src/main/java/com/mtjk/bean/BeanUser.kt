package com.mtjk.bean

import com.fasterxml.jackson.annotation.JsonAlias
import com.google.gson.annotations.SerializedName
import com.mtjk.base.MyBaseBean


open class BeanUser : MyBaseBean() {
    var account = ""
    var avatar = ""
    var belongCompany = ""
    var browseCount = "0"
    var favoriteCount = "0"
    var followExpertCount = "0"
    var gender = 0

    @SerializedName(value = "nickName", alternate = ["nickname"])
    var nickName = ""
    var occupation = ""//职业
    var phone = ""
    var userId = ""
    var shopId = ""
    var age = 0
    var uid=0

    var realName = ""
    var vipLevel = 0
    var linkMicId = ""
    var linkAccept = false
    var inviteID = ""
    var userSig = ""
    var expertArea = ""//专家地区
    var expertWorkingYears = ""//专家工作年限
    var years = 0
    var expertExpertise = ""//专家擅长领域
    var expertEducation = ""//专家教育背景
    var expertEertificate = ""//专家证书
    var expertIntroduce = ""//专家介绍
    var isLogin = false
    var pushToken = ""//push的token
    var backWxZfb = false
    var rtcToken = ""
    var rtmToken = ""
    var rtcTokenIM=""
    var marriageState: String
        get() {
            if (maritalStatus == 0)
                return ""
            if (maritalStatus == 1)
                return "已婚"
            if (maritalStatus == 2)
                return "未婚"
            return ""
        }
        set(value) {
            when (value) {
                "" -> gender == 0
                "已婚" -> gender == 1
                "未婚" -> gender == 2
            }
        }

    var address = ""
    //介绍

    var personalIntroduce = ""
    //擅长领域

    var introduction = ""

    //婚姻状态
    var maritalStatus = 0

    //长居地
    var location = ""

    //教育背景
    var educationExperienceModel = educationExperienceModel()

    //资格证书
    var qualificationCertificateModels = arrayListOf<BeanCertificate>()

    //所属行业、
    var industry = ""
    var token = ""
    var man: Boolean
        get() = 1 == gender
        set(value) = TODO()
    var woman: Boolean
        get() = 2 == gender
        set(value) = TODO()

    var genderDes: String
        get() {
            if (gender == 0)
                return ""
            if (gender == 1)
                return "男"
            if (gender == 2)
                return "女"
            return ""
        }
        set(value) {
            when (value) {
                "" -> gender == 0
                "男" -> gender == 1
                "女" -> gender == 2
            }

        }

    var phoneDes: String
        get() {
            return phone.replaceRange(3, 7, "****")
        }
        set(value) {}
}