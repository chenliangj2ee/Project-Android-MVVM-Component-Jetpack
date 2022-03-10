package com.mentuojiankang.user.bean

import com.mtjk.annotation.MyDefault
import com.mtjk.base.MyBaseBean
import com.mtjk.bean.BeanCertificate

/**
 * 咨询师
 * author:chenliang
 * date:2021/11/12
 */
class BeanConsult : MyBaseBean() {
    //service id
    val id = ""

    val accountId = "";

    //用户id
    val expertId = ""


    //真实姓名
    val realName = ""

    //昵称
    val nickName = ""

    //头像
    val avatar = ""

    //从业年限
    val years = 0


    //毕业院校
    @MyDefault(mValue = "")
    val school = ""

    //学历
    @MyDefault(mValue = "")
    val education = ""

    //专业
    @MyDefault(mValue = "")
    val major = ""

    //地址
    val location = ""

    //擅长
    val introduction = ""
    val serviceDuration = 0;

    //评分
    val score = 0.0

    //咨询量
    val consultNumber = 0

    //是否关注
    var follow = false

    //是否收藏
    var favorite = false

    //咨询状态
    val state = false
    val introVideo: ArrayList<String>? = null
    val qualification: ArrayList<String>? = null

    var list = ArrayList<BeanConsultService>()
    var certificateList = ArrayList<BeanCertificate>()

    fun qualificationDes(): String {
        return qualification?.joinToString(",") ?: ""
    }

    fun certificateDes(): String {
        if (certificateList == null)
            return ""
        return certificateList?.joinToString(separator = ",") { it.certificateName }
    }
}