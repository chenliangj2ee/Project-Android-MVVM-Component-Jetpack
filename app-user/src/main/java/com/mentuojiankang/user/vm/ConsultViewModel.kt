package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel

/**
 * 咨询
 * author:chenliang
 * date:2021/11/11
 */
class ConsultViewModel : MyBaseViewModel() {
//    /**
//     * 我的/我的咨询
//     */
//    fun getMyConsultsList(type: Int, pageNo: Int, pageSize: Int) = go {
//        API.getMyConsultsList(0, pageNo, pageSize)
//    }

    /**
     * 首页咨询列表
     */
    fun getIndexConsultList(categoryId: String = "", pageNo: Int, pageSize: Int) = go {
        API.indexConsultList(categoryId, pageNo, pageSize)
    }


    /**
     * 获取咨询详情
     */
    fun getConsultInfo(categoryId: String) = go {
        API.getConsultInfo(categoryId)
    }

    /**
     * 获取咨询服务时间
     */
    fun getServiceTime(consultType: String, day: String, week: String, serverId: String, shopId: String) = go {
        API.getServiceTime(consultType, day, week, serverId, shopId)
    }

    fun getMyConsultList(consultType: Int, pageNo: Int, pageSize: Int) = go {
        API.myConsultList(0, consultType, pageNo, pageSize)
    }
}