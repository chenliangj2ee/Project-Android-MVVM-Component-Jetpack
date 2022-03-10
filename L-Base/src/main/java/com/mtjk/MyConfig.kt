package com.mtjk

import com.chenliang.annotation.MySpConfig

/**
 *
 * @Project: MVVM-Component
 * @Package: com.chenliang.baselibrary.utils
 * @author: chenliang
 * @date: 2021/07/29
 */


class MyConfig {
    @MySpConfig
    var pushToken: String = ""

    @MySpConfig
    var isAgree: Boolean = false

    @MySpConfig
    var floatDialog: Boolean = false


    @MySpConfig
    var notifyDialog: Boolean = false

}