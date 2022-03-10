package com.mentuojiankang.user.bean

import com.mtjk.annotation.MyDefault
import com.mtjk.base.MyBaseBean

/**课程
 * author:chenliang
 * date:2021/11/12
 */
class BeanCourse : MyBaseBean() {
    var coverImage: String = ""//封面
    var id: String = ""//课程id
    var introduction: String = ""//描述
    var playCount: Int = 0//播放量
    var price: Double = 0.0//原价
    var sectionCount: Int = 0//节数
    var state: Int = 0//状态
    var subTitle: String = ""//二级标题
    var title: String = ""//标题
    var discountPrice: Double = 0.0//折扣价格
    var favorite: Boolean = false//是否收藏
    var buy: Boolean = false//是否购买
    var coverVideo = ""//视频介绍
    var detailUrl = ""
    var expertName = ""
    @MyDefault(mValue = "")
    var major = ""
    @MyDefault(mValue = "")
    var school = ""
    @MyDefault(mValue = "")
    var education = ""
    var sectionList: ArrayList<BeanSection>? = null

    fun playTimeDes(): String {
        if (playCount in 0..10000) {
            return playCount.toString()
        }
        if (playCount > 10000) {
            "${playCount / 10000}万"
        }
        return ""
    }
}