package io.agora.vlive.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2022/2/11
 */
class BeanLiveUser : MyBaseBean() {
    var broadcasters = ArrayList<String>()
    var audience = ArrayList<String>()
    var audienceObjectList = ArrayList<BeanAudience>()
    var viewedCount=0

}