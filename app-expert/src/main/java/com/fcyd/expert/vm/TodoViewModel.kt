package com.fcyd.expert.vm

import com.chenliang.processor.appexpert.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * author,chenliang
 * date,2021/11/29
 */
class TodoViewModel : MyBaseViewModel() {

    fun getTodoList() = go { API.getTODOList() }

    fun stopTodoService(id: String) = go { API.stopTodoService(body("id", id)) }


}