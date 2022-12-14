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
    var title = ""
    var verify = 0//0-初始，1-待审核，2-通过，3-失败
    var commentSum = 0//评论量
    var orderSum = 0//咨询量
    var upTime = ""
    val introVideo: ArrayList<String>? = null
    var consultTime = ConsultTime()
    var introduction=""
    val serviceDuration = 0;
    //评分
    val score = 0.0

    //咨询量
    val consultNumber = 0

    //是否关注
    var follow = false

    //是否收藏
    var favorite = false
    var avatar=""
    var realName=""

    //地址
    val location = ""
    //咨询状态
    val state = false
    val qualification: ArrayList<String>? = null
    class ConsultTime : MyBaseBean() {
        var timeIntervals = ArrayList<BeanTime>()
        var week = ArrayList<Int>()
    }
    fun qualificationDes(): String {
        return qualification?.joinToString(",") ?: ""
    }
    fun weekDes(): String {
        var des = arrayListOf<String>()
        consultTime.week.sort()
            consultTime.week.forEach {
                when (it) {
                    1 -> {
                        des.add("周一")
                    }
                    2 -> {
                        des.add("周二")
                    }
                    3 -> {
                        des.add("周三")
                    }
                    4 -> {
                        des.add("周四")
                    }
                    5 -> {
                        des.add("周五")
                    }
                    6 -> {
                        des.add("周六")
                    }

                }

            }
        if(  consultTime.week.contains(0)){
            des.add("周日")
        }
      return   des.joinToString("/")
    }
}