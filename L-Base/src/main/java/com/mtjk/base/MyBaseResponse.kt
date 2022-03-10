package com.mtjk.base

import com.mtjk.utils.log
import com.mtjk.utils.toast
import java.io.Serializable

open class MyBaseResponse<T> : MyBaseBean(), Serializable {
    var cache = false
    var code: Int = -1
    var msg: String = "程序异常！"
    var error: String = ""
    var data: T = Any() as T
    var token = "";
    var cacheJson = "";

    /**
     * 网络数据请求成功
     */
    fun y(func: (data: T) -> Unit) {
        if (this.code == 0 && !this.cache) {
            if (data != null) {
                func(data)
            } else {
                try {
                    var result = Any() as T
                    func(result)
                } catch (e: ClassCastException) {
                    try {
                        var cla = e.message!!.split("cannot be cast to")[1].trim()
                        var result = Class.forName(cla).newInstance() as T
                        func(result)
                    } catch (e: Exception) {
                        if(e.message!!.contains("java.lang.Class<java.lang.Boolean>")){
                            func(true as T)
                        }else{
                            toast("接口异常")
                            log("${e.message}")
                        }

                    }
                }


            }
        }
    }

    /**
     * 缓存数据请求成功
     */
    fun c(func: (data: T) -> Unit) {
        if (this.code == 0 && this.cache) func(data)
    }

    /**
     * 网络数据请求失败
     */
    fun n(func: (message: String) -> Unit) {
        if (this.code != 0 && !this.cache) func(msg)
    }

}
