package com.fcyd.expert.vm

import com.chenliang.processor.appexpert.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * author:chenliang
 * date:2021/11/10
 */
class LiveViewModel : MyBaseViewModel() {

    fun getRTMToken(channelName: String) = go { API.getRTMToken(body("channelName", channelName)) }

    fun getRTCToken(channelName: String) = go { API.getRTCToken(body("channelName",channelName)) }


    fun getMyLiveList() = go { API.getMyLiveList() }
}