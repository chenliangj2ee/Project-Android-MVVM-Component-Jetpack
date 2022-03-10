package com.fcyd.expert.bean

import com.mtjk.bean.BeanUser

/**
 * author:chenliang
 * date:2022/2/9
 */
class BeanModel {

    var title = ""

    /**
     * 直播
     */
    var channelName = ""

    /**
     * 咨询
     */
    var consultType = "0"//1视频，2语音

    var userList=ArrayList<BeanUser>()


}