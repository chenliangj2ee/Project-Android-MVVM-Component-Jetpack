package io.agora.vlive.bean

import android.widget.FrameLayout
import com.mtjk.base.MyBaseBean
import com.mtjk.bean.BeanUser

/**
 * author:chenliang
 * date:2022/2/11
 */
class BeanLinkUser : BeanUser() {
    var state = 2
    var index = 0;
    var video: FrameLayout? = null

    var uid=0;
}