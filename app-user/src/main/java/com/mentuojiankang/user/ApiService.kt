package com.mentuojiankang.user

import com.chenliang.annotation.MyApiService
import com.mentuojiankang.user.bean.*
import com.mtjk.annotation.MyRetrofitGo
import com.mtjk.base.MyBaseResponse
import com.mtjk.bean.BeanTime
import com.mtjk.utils.BusCode
import io.agora.vlive.Datas
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

    @MyRetrofitGo(mTag = "情感状态标签", mLoading = true, mFailToast = true, mCache = true)
    @GET("/api-user/v1/account/emotionTag/listTags")
    fun getListTags(): Datas<BeanEmotionTag>


    @MyRetrofitGo(mTag = "获取二级分类", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/course/queryCategoryListByParentId")
    fun getType2(
        @Query("parentId") parentId: Int
    ): Datas<BeanType>


    @MyRetrofitGo(mTag = "获取测评分类", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user/category/list")
    fun getTestTypes(
    ): Datas<BeanType>

    @MyRetrofitGo(
        mTag = "保存用户情感标签",
        mLoading = true,
        mFailToast = true,
        mSuccessCode = BusCode.SAVE_LIST_TAGS_SUCCESS
    )
    @POST("/api-user/v1/account/userEmotionTag/save")
    fun saveListTag(@Body body: Any): Data<Any>


    @MyRetrofitGo(mTag = "用户关注", mLoading = true, mFailToast = true)
    @GET("/api-app/v1/app/user-follow/insertUserFollow")
    fun follow(
        @Query("expertId") expertId: String
    ): Data<Any>


    @MyRetrofitGo(mTag = "取消关注", mLoading = true, mFailToast = true)
    @POST("/api-app/v1/app/user-follow/deleteUserFollow")
    fun cancelFollow(
        @Body body: Any
    ): Data<Any>

    @MyRetrofitGo(mTag = "我的关注", mLoading = false, mFailToast = false)
    @GET("/api-app/v1/app/user-follow/pageUserFollow")
    fun getFollowList(
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanConsultant>


    @MyRetrofitGo(
        mTag = "取消收藏",
        mLoading = true,
        mFailToast = true,
        mSuccessCode = BusCode.REFRESH_MINE_USER_INFO
    )
    @POST("/api-app/v1/app/user-favorite/deleteUserFavorite")
    fun cancelFavorite(
        @Query("productId") productId: String
    ): Data<Any>


    @MyRetrofitGo(
        mTag = "用户收藏",
        mLoading = true,
        mFailToast = true,
        mSuccessCode = BusCode.REFRESH_MINE_USER_INFO
    )
    @GET("/api-app/v1/app/user-favorite/insertUserFavorite")
    fun favorite(
        @Query("type") type: Int,
        @Query("productId") productId: String
    ): Data<Any>

    @MyRetrofitGo(mTag = "我的收藏-测评列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user-favorite/pageUserFavorite?MyRetrofitGo=1")
    fun getFavoriteTests(
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanTest>

    @MyRetrofitGo(mTag = "我的收藏-课程列表", mLoading = false, mFailToast = true)
    @GET("/api-app/v1/app/user-favorite/pageUserFavorite?MyRetrofitGo=2")
    fun getFavoriteCourses(
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanCourse>

    @MyRetrofitGo(mTag = "我的收藏-咨询列表", mLoading = false, mFailToast = false)
    @GET("/api-app/v1/app/user-favorite/pageUserFavorite?MyRetrofitGo=3")
    fun getFavoriteConsults(
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanConsultant>


    @MyRetrofitGo(mTag = "浏览足迹-测评列表", mLoading = false, mFailToast = false)
    @GET("/api-app/v1/app/user-history/pageUserBrowserHistory?MyRetrofitGo=1")
    fun getHistoryTests(
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanTest>

    @MyRetrofitGo(mTag = "浏览足迹-课程列表", mLoading = false, mFailToast = false)
    @GET("/api-app/v1/app/user-history/pageUserBrowserHistory?MyRetrofitGo=2")
    fun getHistoryCourses(
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanCourse>

    @MyRetrofitGo(mTag = "浏览足迹-咨询列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user-history/pageUserBrowserHistory?MyRetrofitGo=3")
    fun getHistoryConsults(
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanConsultant>

    @MyRetrofitGo(mTag = "我的测评", mLoading = false, mFailToast = false, mCache = true)
    @POST("/api-app/v1/web/user/testReport/list")
    fun getMyTest(
        @Body body: Any,
    ): DataPages<BeanTest>


    @MyRetrofitGo(mTag = "我的咨询", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user-service/pageUserService")
    fun getMyConsultsList(
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanConsultant>


    @MyRetrofitGo(mTag = "我的课程", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user-order/pageUserCourse")
    fun getMyCourseList(
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanCourse>


    @MyRetrofitGo(mTag = "首页/课程列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/course/pageByCategory")
    fun getCourseList(
        @Query("categoryId") courseId: String,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanCourse>


    @MyRetrofitGo(mTag = "课程详情", mLoading = true, mFailToast = true, mCache = true)
    @GET("/api-app/v1/app/course/queryCourseById")
    fun getCourseById(
        @Query("courseId") courseId: String
    ): Data<BeanCourse>

    @MyRetrofitGo(mTag = "创建订单", mLoading = true, mFailToast = true)
    @POST("/api-app/v1/app/user-order/createOrderInfo")
    fun createOrderInfo(
        @Body createOrderEntity: Any,
    ): Data<BeanOrder>

    @MyRetrofitGo(mTag = "课程订单创建并确认", mLoading = true, mFailToast = false)
    @POST("/api-app/v1/app/user-order/createAndConfirmOrder")
    fun createConfirmOrderInfo(
        @Body createOrderEntity: Any,
    ): Data<BeanOrder>

    @MyRetrofitGo(
        mTag = "支付确认",
        mLoading = true,
        mFailToast = true,
        mSuccessCode = BusCode.ORDER_REFRESH
    )
    @POST("/api-app/v1/app/user-order/confirmOrderInfo")
    fun paymentConfirm(
        @Body requestEntity: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "我的咨询", mLoading = false, mFailToast = true, mCache = true)
    @GET("/api-app/v1/app/user-order/pageUserConsult")
    fun myConsultList(
        @Query("orderStatus") orderStatus: Int,
        @Query("consultType") consultType: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanMyConsult>

    @MyRetrofitGo(mTag = "订单列表", mLoading = false, mFailToast = true, mCache = true)
    @GET("/api-app/v1/app/user-order/queryOrderInfo")
    fun orderList(
        @Query("orderStatus") orderStatus: Int,
        @Query("consultType") consultType: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanOrder>

    @MyRetrofitGo(mTag = "首页咨询列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/expert-shop/pageByCategory")
    fun indexConsultList(
        @Query("categoryId") courseId: String,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanConsult>

    @MyRetrofitGo(mTag = "获取咨询详情", mLoading = true, mFailToast = true, mCache = true)
    @GET("/api-app/v1/app/expert-server/queryExpertShopDetail")
    fun getConsultInfo(
        @Query("shopId") expertShopId: String
    ): Data<BeanConsultRes>


    @MyRetrofitGo(mTag = "会员卡列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user-bank/queryCardListByUserIds")
    fun getVipCardList(): Datas<BeanCard>

    @MyRetrofitGo(mTag = "获取用户钱包余额以及会员卡列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user-bank/getUserAccountMoney")
    fun getBlance(): Data<BeanMoney>

    @MyRetrofitGo(mTag = "获取订单基础信息", mLoading = true, mFailToast = true)
    @GET("/api-app/v1/app/user-order/getOrderBaseInfoByOrderId")
    fun getOrderInfo(
        @Query("orderId") orderId: String
    ): Data<BeanOrderInfo>

    @MyRetrofitGo(mTag = "服务时间列表", mLoading = true, mFailToast = true, mCache = true)
    @GET("/api-app/v1/app/used-time/queryServerTimeTable")
    fun getServiceTime(
        @Query("consultType") consultType: String,
        @Query("day") day: String,
        @Query("week") week: String,
        @Query("serverId") serverId: String,
        @Query("shopId") shopId: String
    ): Datas<BeanTime>

    @MyRetrofitGo(
        mTag = "取消订单",
        mLoading = true,
        mFailToast = true,
        mCache = false,
        mSuccessCode = BusCode.ORDER_REFRESH
    )
    @GET("/api-app/v1/app/user-order/cancelOrder")
    fun cancelOrder(
        @Query("orderId") orderId: String
    ): Data<Boolean>

    @MyRetrofitGo(
        mTag = "课程评论",
        mLoading = true,
        mFailToast = true,
        mCache = false,
        mSuccessCode = BusCode.ORDER_REFRESH
    )
    @POST("/api-app/v1/app/comment/commentCourse")
    fun orderCourseRecommend(
        @Body commentModel: Any
    ): Data<Any>

    @MyRetrofitGo(
        mTag = "咨询评论",
        mLoading = true,
        mFailToast = true,
        mCache = false,
        mSuccessCode = BusCode.ORDER_REFRESH
    )
    @POST("/api-app/v1/app/comment/commentService")
    fun orderConsultRecommend(
        @Body commentModel: Any
    ): Data<Any>

    @MyRetrofitGo(mTag = "获取充值商品列表", mLoading = true, mFailToast = false, mCache = true)
    @POST("/api-app/v1/app/recharge-goods/list")
    fun getOrderLists(@Body createOrderEntity: Any): Datas<BeanRecharge>


    @MyRetrofitGo(mTag = "统计播放次数", mLoading = false, mFailToast = false, mCache = false)
    @POST("/api-app/v1/app/course/addCoursePlayNumber")
    fun playCount(@Body body: Any): Data<Boolean>


    @MyRetrofitGo(
        mTag = "获取TRC Token",
        mLoading = true,
        mFailToast = true,
        mCache = false
    )
    @POST("/api-app/v1/app/agora/createRtcTokenToC")
    fun getRTCToken(@Body body: Any): Data<String>

    @MyRetrofitGo(
        mTag = "获取TRM Token",
        mLoading = true,
        mFailToast = true,
        mCache = false,
        mHasCacheLoading = true,
        mSuccessCode = BusCode.LIVE_GET_RTM_TOKEN
    )
    @POST("/api-app/v1/app/agora/createRtmTokenToC")
    fun getRTCMToken(@Body body: Any): Data<String>


    @MyRetrofitGo(
        mTag = "获取TRM Token",
        mLoading = true,
        mFailToast = false,
        mCache = false,
        mHasCacheLoading = true,
    )
    @POST("/api-app/v1/app/agora/createRtmTokenToC?MyRetrofitGo=1")
    fun getRTCMTokenIM(@Body body: Any): Data<String>


    @MyRetrofitGo(mTag = "待办列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/backlog/user/queryBacklogList")
    fun getTODOList(): Datas<BeanTODO>

    @MyRetrofitGo(mTag = "首页推荐", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/home/index")
    fun getIndexRecommend(): Data<BeanIndexRecommend>


    @MyRetrofitGo(mTag = "直播列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user/liveCourse/queryPlayBackList")
    fun getLiveList(): Datas<BeanLive>

    @MyRetrofitGo(
        mTag = "直播预定",
        mLoading = true,
        mFailToast = true,
        mCache = false,
        mSuccessCode = BusCode.TODO_REFRESH
    )
    @POST("/api-app/v1/app/backlog/user/saveAppointment")
    fun bookLive(@Body body: Any): Data<String>

    @MyRetrofitGo(mTag = "直播详情", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/user/liveCourse/queryById")
    fun getLiveDetail(@Query("id") id: String): Data<BeanLiveDetail>

    @MyRetrofitGo(mTag = "直播课程", mLoading = false, mFailToast = false, mCache = true)
    @POST("/api-app/v1/app/user/myCourse/liveCourse")
    fun getLiveCourse(@Body body: Any): DataPages<BeanLiveCourse>

    @MyRetrofitGo(mTag = "文章列表", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/cms/article/getPageList")
    fun getArticleList(
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanArticle>

    @MyRetrofitGo(mTag = "文章详情", mLoading = true, mFailToast = true, mCache = true)
    @GET("/api-app/v1/app/cms/article/getDetail")
    fun getArticleInfo(
        @Query("id") id: String
    ): Data<BeanArticle>


    @MyRetrofitGo(mTag = "增加阅读量", mLoading = false, mFailToast = false, mCache = false)
    @POST("/api-app/v1/app/cms/article/addReadCount")
    fun addReadCount(
        @Body body: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "测评列表", mLoading = false, mFailToast = false, mCache = true)
    @POST("/api-app/v1/app/user/testTopic/list")
    fun getTestList(
        @Body body: Any
    ): DataPages<BeanTest>

    @MyRetrofitGo(mTag = "钱包明细", mLoading = false, mFailToast = false, mCache = true)
    @POST("/api-app/v1/app/user/order/pageByTime")
    fun getWalletDetailList(@Body body: Any): DataPages<BeanWalletDetail>

    @MyRetrofitGo(mTag = "获取咨询详情", mLoading = true, mFailToast = true, mCache = false)
    @GET("/api-app/v1/app/user/visitor/case/getOne")
    fun getVisitorConsultDetail(
        @Query("orderId") id: String
    ): Data<BeanVisitorConsultDetail>

    @MyRetrofitGo(mTag = "保存咨询详情", mLoading = true, mFailToast = true, mCache = false)
    @POST("/api-app/v1/app/user/visitor/case/save")
    fun saveVisitorConsultDetail(@Body body: Any): Data<Any>

    //TODO 课程推荐接口占位
    @MyRetrofitGo(mTag = "课程推荐", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/course/xxx")
    fun getCourseRecommendList(
        @Query("courseId") courseId: String,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int
    ): DataPages<BeanCourse>

}