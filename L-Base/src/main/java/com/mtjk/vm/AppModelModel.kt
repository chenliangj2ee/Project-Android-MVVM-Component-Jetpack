package com.mtjk.vm

import com.chenliang.processor.LBase.API_UPGRADE
import com.chenliang.processor.LBase.API_USER
import com.mtjk.BaseInit
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.MyApp
import com.mtjk.utils.body

class AppModelModel : MyBaseViewModel() {

    fun check() = go {
        if (BaseInit.isUserApp) {
            API_UPGRADE.checkC(MyApp.getVersionCode(BaseInit.con!!))
        } else {
            API_UPGRADE.checkB(MyApp.getVersionCode(BaseInit.con!!))
        }

    }


    fun report(roomId: Int, reportText: String) = go { API_USER.report(body("roomId", roomId, "reportText", reportText)) }

}