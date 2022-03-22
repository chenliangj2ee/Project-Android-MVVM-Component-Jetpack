package com.fcyd.expert.bean

import com.stx.xhb.androidx.entity.BaseBannerInfo
import retrofit2.http.Url

/**
 * 轮播图
 */
class BeanBanner : BaseBannerInfo {
    var cover = ""
    var url = "";
    var path = ""
    override fun getXBannerUrl(): Any {
        return cover+url
    }

    override fun getXBannerTitle(): String {
        return "title"
    }
}