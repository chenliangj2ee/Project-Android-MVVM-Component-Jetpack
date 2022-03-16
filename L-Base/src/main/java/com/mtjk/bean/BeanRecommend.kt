package com.mtjk.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.utils.anonymous

/**
 * 评论
 * author:chenliang
 * date:2021/11/12
 */
class BeanRecommend : MyBaseBean() {
    var title = ""
    var url = ""
    var orderDate = ""
    var productId = ""
    var score = 5.0F;
    var comment = ""
    var anonymous = 0
    val userAvatar: String? = null
    val userNickName: String? = null
    val gender: Int? = null
    var expertShopId = ""
    var orderId = ""
    var orderServer=0

    fun nameDes()= userNickName?.anonymous()

}