package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

class BeanCreateConfirmOrder: MyBaseBean() {
    var account=false
    var orderId=""
    var payType=0
    var payAmount=0.0
    var cardIds=ArrayList<String>()
}