package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2021/12/9
 */
class BeanStudio : MyBaseBean() {
    var id = ""
    var images = arrayListOf<String>()
    var categoryList = arrayListOf<String>()
    var reason = ""
    var verify = 0//1:待审核 2:审核通过 3:审核不通过
}