package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * author:chenliang
 * date:2021/11/10
 */
class ArticleViewModel : MyBaseViewModel() {

    fun getArticleList(pageNo: Int, pageSize: Int) = go { API.getArticleList(pageNo, pageSize) }

    fun addReadCount(id: String) = go { API.addReadCount(body("id", id)) }

    fun getArticleInfo(id: String) = go { API.getArticleInfo(id) }
}