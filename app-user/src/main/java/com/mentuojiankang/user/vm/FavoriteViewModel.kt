package com.mentuojiankang.user.vm

import com.chenliang.processor.appuser.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * 收藏
 * author:chenliang
 * date:2021/11/11
 */
class FavoriteViewModel : MyBaseViewModel() {
    /**
     * 收藏
     */
    fun favorite(type: Int, productId: String) = go {
        API.favorite(type, productId)
    }

    /**
     * 取消收藏
     */
    fun cancelFavorite(productId: String) = go {
        API.cancelFavorite(productId)
    }

    /**
     * 测评收藏列表
     */
    fun getFavoriteTests(pageNo: Int, pageSize: Int) = go {
        API.getFavoriteTests(200, pageNo, pageSize)
    }

    /**
     * 课程收藏列表
     */
    fun getFavoriteCourses(pageNo: Int, pageSize: Int) = go {
        API.getFavoriteCourses(100, pageNo, pageSize)
    }

    /**
     * 咨询收藏列表
     */
    fun getFavoriteConsults(pageNo: Int, pageSize: Int) = go {
        API.getFavoriteConsults(500, pageNo, pageSize)
    }
}