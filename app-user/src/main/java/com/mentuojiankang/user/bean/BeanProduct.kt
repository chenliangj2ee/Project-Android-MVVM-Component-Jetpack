package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2021/11/18
 */
class BeanProduct : MyBaseBean() {
    var coverImage: String = ""
    var price: Double = 0.0
    var productId: String = ""
    var saleCount: Double = 0.0
    var salePrice: Double = 0.0
    var categoryName: String = ""
    var state: Int = 0
    var productName: String = ""
    var title: String = ""
    var type: Int = 0

    var consultDay: String = ""
    var consultStartTime: String = ""
    var consultEndTime: String = ""

    var id: String = ""
}