package com.fcyd.expert.vm

import com.chenliang.processor.appexpert.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

class VisitorViewModel : MyBaseViewModel() {

    fun getVisitorList(username: String, pageNo: Int, pageSize: Int) = go { API.getVisitorList(username, pageNo, pageSize) }

    fun getVisitorConsult(userId: String, pageNo: Int, pageSize: Int) = go { API.getVisitorConsult(userId, pageNo, pageSize) }

    fun getVisitorGauge(userId: String, pageNo: Int, pageSize: Int) = go { API.getVisitorGauge(userId, pageNo, pageSize) }

    fun getVisitorConsultDetail(visitorCaseId: String) = go { API.getVisitorConsultDetail(visitorCaseId)}

    fun saveVisitorConsult(body: HashMap<String, Any>) = go { API.saveVisitorConsult(body) }

    fun editVisitorInfo(id: String) = go { API.saveVisitorDetail(body("id", id)) }

}