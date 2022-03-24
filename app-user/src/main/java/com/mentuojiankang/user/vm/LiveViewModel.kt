package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mentuojiankang.user.bean.BeanLive
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * author:chenliang
 * date:2021/11/10
 */
class LiveViewModel : MyBaseViewModel() {

    fun getRTMToken(channelName: String) = go { API.getRTCMToken(body("channelName", channelName)) }

    fun getRTCToken(channelName: String) = go { API.getRTCToken(body("channelName", channelName)) }


    fun getLiveList() = go { API.getLiveList() }

    fun bookLive(live: BeanLive) = go { API.bookLive(live) }

    fun getLiveDetail(courseId: String) = go { API.getLiveDetail(courseId)}

    fun getLiveCourse(pageNo: Int, pageSize: Int) = go { API.getLiveCourse(body("pageNo", pageNo, "pageSize", pageSize))}

    fun getLiveSection(courseId: String) = go {API.getLiveSection(courseId)}
}