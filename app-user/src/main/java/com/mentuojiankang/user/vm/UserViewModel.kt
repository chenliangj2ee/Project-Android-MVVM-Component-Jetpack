package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mentuojiankang.user.bean.BeanEmotionTag
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * 用户
 * author:chenliang
 * date:2021/11/10
 */
class UserViewModel : MyBaseViewModel() {

    /**
     * 保存用户情感标签
     */
    fun saveListTag(beans: ArrayList<BeanEmotionTag>) = go {
        API.saveListTag(beans)
    }

    fun getTodoList() = go { API.getTODOList() }

    fun getIMRTCToken(channelName: String, userId: String) = go { API.getRTCMTokenIM(body("channelName", channelName, "userId", userId)) }

    /**
     * 获取钱包明细列表
     */
    fun getWalletDetailList(pageNo: Int, pageSize: Int, startTime: String, endTime: String) = go {
        if(startTime.isNullOrEmpty()) {
            API.getWalletDetailList(body("pageNo", pageNo, "pageSize", pageSize))
        } else if(endTime.isNullOrEmpty()) {
            API.getWalletDetailList(body("pageNo", pageNo, "pageSize", pageSize, "startTime", startTime))
        } else {
            API.getWalletDetailList(body("pageNo", pageNo, "pageSize", pageSize, "startTime", startTime, "endTime", endTime))
        }
    }
}