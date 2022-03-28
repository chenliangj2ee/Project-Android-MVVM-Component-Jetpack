package com.fcyd.expert.vm

import com.chenliang.processor.appexpert.API
import com.fcyd.expert.bean.BeanConsult
import com.fcyd.expert.bean.BeanWeek
import com.mtjk.base.MyBaseViewModel
import com.mtjk.bean.BeanTime
import com.mtjk.utils.body

/**
 * author:chenliang
 * date:2021/12/9
 */
class ConsultViewModel : MyBaseViewModel() {

    /**
     * 发布咨询
     */
    fun releaseConsult(bean: BeanConsult) = go {
        API.releaseConsult(bean)
    }


    fun consultList(state: Int, pageNo: Int, pageSize: Int) =
        go { API.consultList(state, pageNo, pageSize) }

    /**
     * 上架
     */
    fun upConsult(serverId: String) =
        go { API.updateServerState(body("serverId", serverId, "state", 1)) }

    /**
     * 下架
     */
    fun downConsult(serverId: String) =
        go { API.updateServerState(body("serverId", serverId, "state", 3)) }

    /**
     * 删除
     */
    fun deleteConsult(serverId: String) =
        go { API.updateServerState(body("serverId", serverId, "state", 4)) }

    /**
     * 获取咨询详情
     */
    fun getConsultInfo(serverId: String) = go { API.getConsultInfo(serverId) }


    /**
     * 获取咨询详情
     */
    fun getConsultInformation(categoryId: String) = go {
        API.getConsultInformation(categoryId)
    }

    /**
     * 获取咨询服务时间
     */
    fun getServiceTime(consultType: String, day: String, week: String, serverId: String, shopId: String) = go {
        API.getServiceTime(consultType, day, week, serverId, shopId)
    }



    fun getVisitorConsultDetail(orderId: String) = go {
        API.getVisitorConsultDetail(orderId)
    }

    fun saveVisitorConsultDetail(body: HashMap<String, Any>) = go {
        API.saveVisitorConsultDetail(body)
    }
}