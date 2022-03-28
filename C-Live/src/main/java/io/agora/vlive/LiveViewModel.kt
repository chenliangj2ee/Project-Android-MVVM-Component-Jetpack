package io.agora.vlive

import com.chenliang.processor.CLive.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * author:chenliang
 * date:2022/2/10
 */
class LiveViewModel : MyBaseViewModel() {

    fun getUsers(channelName: String) = go { API.getRTCMToken(channelName) }

    fun start(channelName: String) = go { API.startLive(body("channelName", channelName)) }
    fun stop(channelName: String,liveSeconds:Long) = go { API.stopLive(body("channelName", channelName,"liveSeconds",liveSeconds)) }
    fun addViewedCount(channelName: String) = go { API.addViewedCount( channelName) }

}