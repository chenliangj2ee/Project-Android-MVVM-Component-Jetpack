package com.chenliang.annotation

/**
 *配置生成器注解
 */

@Target(AnnotationTarget.CLASS)
annotation class MyApiService(
    val mName: String = "",//API接口名称
    val mPath: String = "https://api.fangcunyuedong.com",//生产环境
    val mTestPath: String = "http://api.fangcunyuedong.cn",//测试环境,alpha环境
    val mDevPath: String = "http://172.11.200.3:9200"//开发环境
)

object ApiModel {
    var test = false
    var dev = false
    var release = true

    fun openTest() {
        test = true
        release = false
        dev = false
    }

    fun openDev() {
        dev = true
        release = false
        test = false
    }

    fun openRelease() {
        release = true
        test = false
        dev = false
    }
}