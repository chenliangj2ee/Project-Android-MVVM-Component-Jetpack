package io.agora.vlive.vm

import com.chenliang.processor.CLive.API
import com.chenliang.processor.LBase.API_USER
import com.mtjk.BaseInit
import com.mtjk.base.MyBaseViewModel
import com.mtjk.bean.BeanUser
import com.mtjk.utils.body
import com.mtjk.utils.getBeanUser

/**
 * author:chenliang
 * date:2021/12/28
 */
class RoomViewModel : MyBaseViewModel() {

    fun updateUid(uid: String) = go {
        var user = getBeanUser()
        if (BaseInit.isUserApp) {
            API.updateUid2C(body("userId", user!!.userId, "agoraUserId", uid))
        } else {
            API.updateUid2B(body("userId", user!!.userId, "agoraUserId", uid))
        }
    }


    fun checkMessage(content: String) = go { API.checkMessage(body("text", content)) }
}