package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * 课程
 * author:chenliang
 * date:2021/11/11
 */
class CourseViewModel : MyBaseViewModel() {


    /**
     * 我的课程
     */
    fun getMyCourse(pageNo: Int, pageSize: Int) = go {
        API.getMyCourseList(pageNo, pageSize)
    }

    /**
     * 首页课程列表
     */
    fun getIndexCourseList(categoryId: String, pageNo: Int, pageIndex: Int) = go {
        API.getCourseList(categoryId, pageNo, pageIndex)
    }

    /**
     * 获取课程详情
     */
    fun getCourseById(courseId: String) = go {
        API.getCourseById(courseId)
    }

    /**
     * 统计播放次数
     */
    fun playCount(courseId: String) = go {
        API.playCount(body("courseSectionId", courseId))
    }
}