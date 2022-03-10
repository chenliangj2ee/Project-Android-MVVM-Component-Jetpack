package com.mtjk.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2022/1/7
 */
class BeanUpgrade : MyBaseBean() {
    var versionCode:Int = 0
    var versionName = ""
    var upgradeDescription = ""
    var forceUpdate = 0 //1强制升级
    var fileUrl = ""
}