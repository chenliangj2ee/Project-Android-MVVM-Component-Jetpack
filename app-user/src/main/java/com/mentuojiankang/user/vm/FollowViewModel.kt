package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * 关注
 * author:chenliang
 * date:2021/11/11
 */
class FollowViewModel : MyBaseViewModel() {

    /**
     * 关注
     */
    fun follow(expertId: String) = go {
        API.follow(expertId)
    }

    /**
     * 取消关注
     */
    fun cancelFollow(expertId: String) = go {
        API.cancelFollow(body("expertId", expertId))
    }

    /**
     * 我的关注
     */
    fun getFollows(pageNo: Int, pageSize: Int) = go {
        API.getFollowList(pageNo, pageSize)
    }
}