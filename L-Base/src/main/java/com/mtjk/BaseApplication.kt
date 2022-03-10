package com.mtjk

import com.chenliang.processor.LBase.MyRoutePath
import com.mtjk.base.MyBaseApplication
import com.mtjk.utils.log

/**
 * 需要再application里配置meta-data
 */
class BaseApplication : MyBaseApplication() {

    override fun onCreate() {
        super.onCreate()
        log("基类 BaseApplication onCreate ....")
        BaseInit.registerApi(UpgradeService::class.java)
        if (BaseInit.isUserApp)
            BaseInit.registerApi(UserApiService::class.java)
        else
            BaseInit.registerApi(ExpertApiService::class.java)

        BaseInit.initMyRoute(MyRoutePath)
    }
}