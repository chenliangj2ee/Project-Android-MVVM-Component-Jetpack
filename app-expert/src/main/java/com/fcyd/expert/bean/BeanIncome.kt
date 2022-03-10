package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean
import java.math.BigDecimal

/**
 * author:chenliang
 * date:2021/11/18
 */
class BeanIncome : MyBaseBean() {
    var allMoney: Double = 0.0
    var thisMonthMoney: Double = 0.0


    fun allMoneyDes() = String.format("%.2f", allMoney)
    fun thisMonthMoneyDes() = String.format("%.2f", thisMonthMoney)
}