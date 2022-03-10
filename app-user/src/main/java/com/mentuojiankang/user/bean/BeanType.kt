package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2021/11/12
 */
class BeanType : MyBaseBean() {
    var id: String = ""
    var name: String = ""//课程，咨询，文章
    var parentId: String = ""

    var categoryName = ""//测评分类
}