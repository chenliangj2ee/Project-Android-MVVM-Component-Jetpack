package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel

/**
 * 浏览足迹
 * author:chenliang
 * date:2021/11/11
 */
class HistoryViewModel : MyBaseViewModel() {
    /**
     * 测评足迹
     */
    fun getHistoryTests(pageNo: Int, pageSize: Int) = go {
        API.getHistoryTests(200, pageNo, pageSize)
    }

    /**
     * 课程足迹
     */
    fun getHistoryCourses(pageNo: Int, pageSize: Int) = go {
        API.getHistoryCourses(100, pageNo, pageSize)
    }

    /**
     * 咨询足迹
     */
    fun getHistoryConsults(pageNo: Int, pageSize: Int) = go {
        API.getHistoryConsults(500, pageNo, pageSize)
    }
}