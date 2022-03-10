package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * author:chenliang
 * date:2021/11/10
 */
class AppViewModel : MyBaseViewModel() {

    fun getListTag() = go { API.getListTags() }

    fun getType2() = go { API.getType2(0) }

    fun getTestType() = go { API.getTestTypes() }

}