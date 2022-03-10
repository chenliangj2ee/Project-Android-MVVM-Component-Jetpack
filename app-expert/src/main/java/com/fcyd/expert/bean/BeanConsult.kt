package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.bean.BeanTime

/**
 * author:chenliang
 * date:2021/11/18
 */
class BeanConsult : MyBaseBean() {
    var consultDay = ""
    var consultEndTime = ""
    var consultNum = 0
    var consultStartTime = ""
    var consultType = arrayListOf<Int>() //1语音；2视频
    var coverImage = ""
    var id = ""
    var price = 0F
    var priceType = 0//0免费；1收费
    var reason = ""
    var salePrice = 0F
    var serverType = 1//1一对一；2一对多
    var state = 0
    var title = ""
    var verify = 0//0-初始，1-待审核，2-通过，3-失败
    var commentSum = 0//评论量
    var orderSum = 0//咨询量
    var upTime = ""

    var consultTime = ConsultTime()

    class ConsultTime : MyBaseBean() {
        var timeIntervals = ArrayList<BeanTime>()
        var week = ArrayList<Int>()
    }
}