package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2021/12/6
 */
class BeanWork(icon: Int, title: String, cls: Class<*>?,color:String) : MyBaseBean() {
    var icon = icon
    var title = title
    var cls = cls
    var color = color

}