package com.mtjk

import com.chenliang.annotation.MyApiService
import com.mtjk.bean.BeanPage
import com.mtjk.annotation.MyRetrofitGo
import com.mtjk.bean.BeanUser
import com.mtjk.base.MyBaseResponse
import com.mtjk.bean.BeanRecommend
import com.mtjk.utils.BusCode
import retrofit2.Call
import retrofit2.http.*

typealias Data<T> = Call<MyBaseResponse<T>>
typealias Datas<T> = Call<MyBaseResponse<List<T>>>
typealias DataPages<T> = Call<MyBaseResponse<BeanPage<List<T>>>>

/**
 * 观众端
 */
@MyApiService(
    mName = "API_user"
)
interface UserApiService {
    @MyRetrofitGo(mTag = "获取验证码", mLoading = true, mFailToast = true)
    @FormUrlEncoded
    @POST("/api-auth/v1/auth-user/smscode")
    fun getPhoneCode(
        @Field("phone") phone: String
    ): Data<String>

    @MyRetrofitGo(mTag = "验证码登录", mLoading = true, mFailToast = true, mSuccessCode = BusCode.LOGIN_SUCCESS)
    @POST("/api-auth/v1/auth-user/codeLogin")
    fun codeLogin(
        @Body body: Any
    ): Data<BeanUser>


    @MyRetrofitGo(mTag = "一键登录", mLoading = true, mFailToast = true)
    @POST("/api-auth/v1/auth-user/onekeyLogin")
    fun quickLogin(
        @Body body: Any
    ): Data<BeanUser>

    @MyRetrofitGo(mTag = "保存用户信息", mLoading = false, mFailToast = true, mSuccessCode = BusCode.REFRESH_MINE_USER_INFO)
    @POST("/api-app/v1/app/user/updateUserAccount")
    fun updateUserAccount(@Body body: Any): Data<Boolean>

    @MyRetrofitGo(mTag = "获取用户信息", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user/getUserInfo")
    fun getUserInfo(): Data<BeanUser>

    @MyRetrofitGo(mTag = "获取用户IM需要的Token", mLoading = false, mFailToast = false, mSuccessCode = BusCode.GET_USERSIG_SUCCESS)
    @GET("/api-app/v1/app/user-sig/generateUserSig")
    fun getUserUserSig(): Data<String>


    @MyRetrofitGo(
        mTag = "投诉",
        mLoading = true,
        mFailToast = true,
        mSuccessCode = BusCode.SAVE_LIST_TAGS_SUCCESS
    )
    @POST("/api-app/v1/app/user-report/saveOrUpdateUserReport")
    fun report(@Body body: Any): Data<Boolean>

    @MyRetrofitGo(
        mTag = "注销",
        mLoading = true,
        mFailToast = true,
        mSuccessCode = BusCode.EXIT
    )
    @POST("/api-auth/v1/auth-user/shutdownAccount")
    fun logoff(): Data<Boolean>


    @MyRetrofitGo(
        mTag = "评论列表",
        mLoading = false,
        mFailToast = false,
        mCache = true
    )
    @GET("/api-app/v1/app/comment/queryCourseComment")
    fun evaluateProductList(
        @Query(value = "productId") productId: String,
        @Query(value = "pageNo") pageNo: Int,
        @Query(value = "pageSize") pageSize: Int
    ): DataPages<BeanRecommend>

    @MyRetrofitGo(
        mTag = "评论列表",
        mLoading = false,
        mFailToast = false,
        mCache = true
    )
    @GET("/api-app/v1/app/comment/queryServiceComment")
    fun evaluateExpertList(
        @Query(value = "expertShopId") productId: String,
        @Query(value = "pageNo") pageNo: Int,
        @Query(value = "pageSize") pageSize: Int
    ): DataPages<BeanRecommend>
}