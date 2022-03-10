package com.mtjk

import com.chenliang.annotation.MyApiService
import com.mtjk.annotation.MyRetrofitGo
import com.mtjk.bean.BeanUser
import com.mtjk.utils.BusCode
import retrofit2.http.*


/**
 * 专家端
 */
@MyApiService(
    mName = "API_expert"
)
interface ExpertApiService {
    @MyRetrofitGo(mTag = "获取验证码", mLoading = true, mFailToast = true)
    @FormUrlEncoded
    @POST("/api-auth/v1/auth-expert/smscode")
    fun getPhoneCode(
        @Field("phone") phone: String
    ): Data<String>

    @MyRetrofitGo(mTag = "验证码登录", mLoading = true, mFailToast = true, mSuccessCode = BusCode.LOGIN_SUCCESS)
    @POST("/api-auth/v1/auth-expert/codeLogin")
    fun codeLogin(
        @Body body: Any
    ): Data<BeanUser>


    @MyRetrofitGo(mTag = "一键登录", mLoading = true, mFailToast = true,mCache = false)
    @POST("/api-auth/v1/auth-expert/onekeyLogin")
    fun quickLogin(
        @Body body: Any
    ): Data<BeanUser>

    @MyRetrofitGo(mTag = "保存用户信息", mLoading = true, mFailToast = true, mSuccessCode = BusCode.UPDATE_EXPERT_INFO)
    @POST("/api-app/v1/app/expert/updateUserAccount")
    fun updateUserAccount(@Body body: Any): Data<Boolean>

    @MyRetrofitGo(mTag = "获取用户信息", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/expert/getUserInfo")
    fun getUserInfo(): Data<BeanUser>

    @MyRetrofitGo(mTag = "获取用户IM需要的Token", mLoading = false, mFailToast = false, mSuccessCode = BusCode.GET_USERSIG_SUCCESS)
    @GET("/api-app/v1/app/user-sig/generateUserSig")
    fun getUserUserSig(): Data<String>


    @MyRetrofitGo(
        mTag = "注销",
        mLoading = true,
        mFailToast = true,
        mSuccessCode = BusCode.EXIT
    )
    @POST("/api-auth/v1/auth-user/shutdownAccount")
    fun logoff(): Data<Boolean>
}