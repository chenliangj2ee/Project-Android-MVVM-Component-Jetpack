package com.mentuojiankang.user.bean

import com.stx.xhb.androidx.entity.BaseBannerInfo

/**
 * 轮播图
 */
class BeanBanner : BaseBannerInfo {
    var cover = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0122065754e97832f875a4291747d7.jpg%401280w_1l_2o_100sh.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1647325059&t=1d9ae52d79bdafc41e48176a6943ea92";
    var url = ""
    override fun getXBannerUrl(): Any {
        return cover
    }

    override fun getXBannerTitle(): String {
        return "title"
    }
}