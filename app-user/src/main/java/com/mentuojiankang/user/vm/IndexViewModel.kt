package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel

/**
 * author:chenliang
 * date:2021/11/10
 */
class IndexViewModel : MyBaseViewModel() {

    fun getIndexRecommend() = go { API.getIndexRecommend() }
    
}