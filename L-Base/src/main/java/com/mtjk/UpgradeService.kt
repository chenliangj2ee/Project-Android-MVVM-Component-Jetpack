package com.mtjk

import com.chenliang.annotation.MyApiService
import com.mtjk.annotation.MyRetrofitGo
import com.mtjk.bean.BeanUpgrade
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * 升级
 */
@MyApiService(
    mName = "API_upgrade",
    mPath = "https://wapi.fangcunyuedong.com",
    mTestPath = "https://wapi.fangcunyuedong.cn",
    mDevPath = "http://172.11.200.3:8000"

)
interface UpgradeService {
    @MyRetrofitGo(mTag = "B端升级检测", mDataIsNullToError = true)
    @GET("/mental-boot/app/appVersion/checkBAndroid")
    fun checkB(
        @Query(value = "code") code: Int
    ): Data<BeanUpgrade>

    @MyRetrofitGo(mTag = "C端升级检测", mDataIsNullToError = true)
    @GET("/mental-boot/app/appVersion/checkCAndroid")
    fun checkC(
        @Query(value = "code") code: Int
    ): Data<BeanUpgrade>


}