package com.fcyd.expert

import com.chenliang.annotation.MyApiService
import com.fcyd.expert.bean.*
import com.mtjk.annotation.MyRetrofitGo
import com.mtjk.base.MyBaseResponse
import com.mtjk.bean.BeanPage
import com.mtjk.utils.BusCode
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

typealias Data<T> = Call<MyBaseResponse<T>>
typealias Datas<T> = Call<MyBaseResponse<List<T>>>
typealias DataPages<T> = Call<MyBaseResponse<BeanPage<List<T>>>>

/**
 * author:chenliang
 * date:2021/11/5
 */
@MyApiService(
    mName = "API"
)
interface ApiService {

    @MyRetrofitGo(mTag = "专家认证", mLoading = true, mFailToast = true, mSuccessCode = BusCode.AUTH_FINISH)
    @POST("/api-app/v1/app/expert-authentication/inApp")
    fun inApp(
        @Body inAppEntity: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "工作室装修", mLoading = true, mFailToast = true, mSuccessCode = BusCode.STUDIO_EDIT_SUCCESS)
    @POST("/api-app/v1/app/expert-studio/updateExpertStudioDetail")
    fun updateStudioInfo(
        @Body verifyExpertShop: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "工作室详情", mLoading = true, mCache = true, mFailToast = false)
    @GET("/api-app/v1/app/expert-studio/queryExpertStudioDetail")
    fun getStudioInfo(

    ): Data<BeanStudio>

    @MyRetrofitGo(mTag = "发布咨询", mLoading = true, mFailToast = true, mSuccessCode = BusCode.REFRESH_CONSULT_LIST)
    @POST("/api-app/v1/app/expert-server/saveOrUpdateExpertServer")
    fun releaseConsult(
        @Body createExpertServerEntity: Any
    ): Data<Boolean>


    @MyRetrofitGo(mTag = "咨询管理列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/expert-server/queryServerManagerList")
    fun consultList(
        @Query(value = "state") state: Int,
        @Query(value = "pageNo") pageNo: Int,
        @Query(value = "pageSize") pageSize: Int
    ): DataPages<BeanConsult>


    @MyRetrofitGo(mTag = "咨询上架/下架/删除", mLoading = true, mFailToast = true, mCache = false, mSuccessCode = BusCode.REFRESH_CONSULT_LIST)
    @POST("/api-app/v1/app/expert-server/updateServerState")
    fun updateServerState(
        @Body updateServerState: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "获取咨询详情", mLoading = true, mFailToast = true, mCache = true, mDataIsNullToError = true)
    @GET("/api-app/v1/app/expert-server/queryServerDetailById")
    fun getConsultInfo(
        @Query(value = "serverId") serverId: String
    ): Data<BeanConsult>


    @MyRetrofitGo(mTag = "创建订单", mLoading = true, mFailToast = true)
    @POST("/api-app/v1/app/expert-order/createOrderInfo")
    fun createOrderInfo(
        @Body createOrderEntity: Any,
    ): Data<BeanOrder>

    @MyRetrofitGo(mTag = "支付确认", mLoading = true, mFailToast = true, mSuccessCode = BusCode.PAYMENT_SUCCESS)
    @POST("/api-app/v1/app/expert-order/confirmOrderInfo")
    fun paymentConfirm(
        @Body requestEntity: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "订单列表", mLoading = false, mFailToast = true, mCache = true)
    @GET("/api-app/v1/app/expert-order/queryOrderInfo")
    fun orderList(
        @Query("orderStatus") orderStatus: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanOrder>

    @MyRetrofitGo(mTag = "订单确认", mLoading = true, mFailToast = true, mCache = false, mSuccessCode = BusCode.ORDER_REFRESH)
    @POST("/api-app/v1/app/expert-server/saveRoomIdByOrderId")
    fun orderConfirm(
        @Body body: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "获取工作室情感标签", mLoading = true, mFailToast = true, mCache = true)
    @GET("/api-app/v1/app/expert-studio/queryExpertStudioCategory")
    fun getStudioTags(
        @Query("parentId") parentId: Int
    ): Datas<BeanTags>

    @MyRetrofitGo(mTag = "查询审核状态", mLoading = false, mFailToast = false, mCache = false, mSuccessCode = BusCode.INIT)
    @GET("/api-app/v1/app/expert-studio/queryExpertStudioState")
    fun init(): Data<BeanInit>


    @MyRetrofitGo(mTag = "结束咨询", mLoading = true, mFailToast = true, mCache = false, mSuccessCode = BusCode.ORDER_REFRESH)
    @POST("/api-app/v1/app/expert-server/updateOrderState")
    fun stopConsult(
        @Body body: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "咨询方案", mLoading = true, mFailToast = true, mCache = false, mSuccessCode = BusCode.ORDER_REFRESH)
    @POST("/api-app/v1/app/user-diagnosis/saveOrUpdateUserDiagnosis")
    fun feedback(
        @Body body: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "统计专家当月累计收入", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/expert-server/sumCumulativeMoneyThisMonth")
    fun getExpertData(): Data<BeanIncome>


    @MyRetrofitGo(mTag = "获取TRC Token", mLoading = true, mFailToast = true, mCache = false)
    @POST("/api-app/v1/app/agora/createRtcTokenToB")
    fun getRTCToken(@Body body: Any): Data<String>

    @MyRetrofitGo(mTag = "获取TRM Token", mLoading = true, mFailToast = true, mCache = false, mSuccessCode = BusCode.LIVE_GET_RTM_TOKEN)
    @POST("/api-app/v1/app/agora/createRtmTokenToB")
    fun getRTMToken(@Body body: Any): Data<String>


    @MyRetrofitGo(mTag = "待办列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/backlog/expert/queryBacklogList")
    fun getTODOList(): Datas<BeanTODO>

    @MyRetrofitGo(mTag = "我的直播", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/live-course/queryLiveCourseManage")
    fun getMyLiveList(): Datas<BeanMyLive>

    @MyRetrofitGo(mTag = "停止服务", mLoading = true, mFailToast = true, mCache = false)
    @POST("/api-app/v1/app/backlog/expert/manualClose")
    fun stopTodoService(@Body body: Any): Data<Boolean>

    @MyRetrofitGo(mTag = "我的钱包", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/expert/shopBank/getDetail")
    fun getWalletInfo(): Data<BeanWallet>

    @MyRetrofitGo(mTag = "钱包明细", mLoading = false, mFailToast = false, mCache = true)
    @POST("/api-app/v1/app/expert/order/shopIncome/page")
    fun getWalletDetailList(@Body body: Any): DataPages<BeanWalletDetail>

    @MyRetrofitGo(mTag = "咨询师提现", mLoading = false, mFailToast = false, mCache = true)
    @POST("/api-app/v1/app/expert/order/withdraw")
    fun walletWithdraw(@Body body: Any): Data<BeanWalletWithdrawResult>

    @MyRetrofitGo(mTag = "提现明细", mLoading = false, mFailToast = false, mCache = true)
    @POST("/api-app/v1/app/expert/order/withdraw/page")
    fun getWalletWithdrawList(@Body body: Any): DataPages<BeanWalletWithdraw>
}