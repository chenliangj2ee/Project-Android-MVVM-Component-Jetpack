package com.fcyd.expert.vm

import com.chenliang.processor.appexpert.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * author:chenliang
 * date:2021/12/9
 */
class StudioViewModel : MyBaseViewModel() {

    fun updateStudioInfo(images: ArrayList<String>, categoryList: ArrayList<String>) = go {
        API.updateStudioInfo(body("images", images, "categoryList", categoryList))
    }

    fun getStudioInfo() = go {
        API.getStudioInfo()
    }

    fun getStudioTags() = go {
        API.getStudioTags(0)
    }
}