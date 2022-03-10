package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean
import java.io.Serializable

/**
 * author:chenliang
 * date:2021/11/12
 */
class BeanTest : MyBaseBean(), Serializable {
    var id = ""
    var scaleName = ""//标题
    var briefIntro = ""//简介
    var chargeStatus = 0//0免费，1收费
    var coverImage = ""//封面
    var testCount = 0//测评量
    var effectiveTime = 0//时间
    var subjectCount = 0//题目数量

}