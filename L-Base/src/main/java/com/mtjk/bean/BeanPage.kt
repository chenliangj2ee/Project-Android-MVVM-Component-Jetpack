package com.mtjk.bean

import com.mtjk.base.MyBaseBean

class BeanPage<T> : MyBaseBean() {
    var total = 0
    var records: T? = null
}