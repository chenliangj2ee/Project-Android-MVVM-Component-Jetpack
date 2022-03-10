package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2021/12/6
 */
class BeanMenu(icon: Int, title: String, cls: Class<*>?, color: String, fragmentRoute: String) : MyBaseBean() {
    var icon = icon
    var title = title
    var cls = cls
    var color = color
    var fragmentRoute = fragmentRoute

}