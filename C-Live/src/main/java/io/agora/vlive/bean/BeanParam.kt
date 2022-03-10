package io.agora.vlive.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2022/2/15
 */
class BeanParam : MyBaseBean() {
    object LiveType {
        var VIDEO_ONE = 0
        var AUDIO_ONE = 1
        var VIDEO_MORE = 2
        var AUDIO_MORE = 3
    }

    var userId = ""
    var userName = ""
    var userHeader = "";
    var liveTitle = ""
    var liveChannelName = "";
    var liveType = 0;
}