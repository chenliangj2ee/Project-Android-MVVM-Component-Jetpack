package io.agora.vlive

import com.chenliang.annotation.MyApiService
import com.mtjk.annotation.MyRetrofitGo
import com.mtjk.base.MyBaseResponse
import com.mtjk.utils.BusCode
import io.agora.util.drawingboard.BeanDrawingBoardRoom
import io.agora.vlive.bean.BeanLiveUser
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

typealias Data<T> = Call<MyBaseResponse<T>>
typealias Datas<T> = Call<MyBaseResponse<List<T>>>

/**
 * author:chenliang
 * date:2021/11/5
 */
@MyApiService(
    mName = "API"
)
interface ApiService {

    @MyRetrofitGo(mTag = "获取RTCTOKEN", mLoading = false, mFailToast = false, mCache = true)
    @GET("/api-app/v1/app/agora/channel/userList")
    fun getRTCMToken(@Query(value = "channelName") channelName: String): Data<BeanLiveUser>

    @MyRetrofitGo(mTag = "更新用户UID")
    @GET("/api-app/v1/app/user/agora/updateUserInfo")
    fun updateUid2B(@Body any: Any): Data<Boolean>

    @MyRetrofitGo(mTag = "更新用户UID")
    @POST("/api-app/v1/app/user/agora/updateUserInfo")
    fun updateUid2C(@Body any: Any): Data<Boolean>

    @MyRetrofitGo(mTag = "开始直播")
    @POST("/api-app/v1/app/liveRoom/start")
    fun startLive(@Body any: Any): Data<Boolean>

    @MyRetrofitGo(mTag = "结束直播")
    @POST("/api-app/v1/app/liveRoom/closed")
    fun stopLive(@Body any: Any): Data<Boolean>


    @MyRetrofitGo(mTag = "更新观看次数", mLoading = false, mFailToast = false, mCache = false)
    @POST("/api-app/v1/app/liveRoom/addViewedCount")
    fun addViewedCount(
        @Body body: Any
    ): Data<Boolean>

    @MyRetrofitGo(mTag = "获取画板房间TOKEN", mLoading = true, mFailToast = true, mCache = false,mHasCacheLoading = true)
    @POST("/api-app/v1/app/agora/whiteBoard/token")
    fun getDrawingBoardRoomToken(@Body any: Any): Data<BeanDrawingBoardRoom>
}