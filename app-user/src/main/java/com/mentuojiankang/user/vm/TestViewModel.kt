package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * 测评
 * author:chenliang
 * date:2021/11/10
 */
class TestViewModel : MyBaseViewModel() {

    /**
     * 我的测评
     */
    fun getMyTest(pageNo: Int, pageSize: Int) =
        go { API.getMyTest(body("pageNo", pageNo, "pageSize", pageSize)) }


    /**
     * 根据分类获取测评列表
     */
    fun getTestList(id: String, pageNo: Int, pageSize: Int) =
        go { API.getTestList(body("categoryId", id, "pageNo", pageNo, "pageSize", pageSize)) }

}