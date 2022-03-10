package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

/**
 * 轮播图
 */
class BeanIndexRecommend : MyBaseBean() {
    var articleList = ArrayList<BeanArticle>()
    var consultShopList = ArrayList<BeanConsult>()
    var courseList = ArrayList<BeanCourse>()
    var bannerList=ArrayList<BeanBanner>()

}