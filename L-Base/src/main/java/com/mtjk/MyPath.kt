package com.mtjk

import com.chenliang.annotation.ApiModel

/**
 * author:chenliang
 * date:2022/2/22
 */
object MyPath {
    var expertPath = "https://detail.fangcunyuedong.com/"
    var articlePath = "http://article.fangcunyuedong.com/"


    /**
     * 测评
     */
    var test =
        if (ApiModel.dev) "http://172.11.200.3:8082/scale/about" else if (ApiModel.test) "http://h5.fangcunyuedong.cn/scale/about" else "https://h5.fangcunyuedong.com/scale/about"

    /**
     * 测评报告
     */
    var testResult =
        if (ApiModel.dev) "http://172.11.200.3:8082/scale/report" else if (ApiModel.test) "http://h5.fangcunyuedong.cn/scale/report" else "https://h5.fangcunyuedong.com/scale/report"
}