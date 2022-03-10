package com.mtjk.base

import androidx.databinding.BaseObservable
import androidx.databinding.ViewDataBinding
import com.mtjk.utils.MySpUtis
import com.google.gson.Gson
import com.mtjk.annotation.defaultResetToNull
import com.mtjk.annotation.defaultValueReflex
import com.mtjk.bean.BeanUser
import java.io.Serializable


/*
如果需要保存数据到sp，且该类型的数据，只会保存一个对象的数据到sp，可以用以下方法：比如BeanUser继承MyBaseBean，登录后保存BeanUser

     var user = BeanUser()
     user.name = "tom"
     user.age = 12
     user.save()//保存

     var user2 = BeanUser().get()//获取
     user2!!.logJson()

      BeanUser().clear()//清除
 */
open class MyBaseBean() : BaseObservable(), Serializable {
    @Transient
    open var itemType = 0

    @Transient
    open var binding: ViewDataBinding? = null

    @Transient
    var itemPosition = 0

    @Transient
    var itemSelected = false
    open fun save() {

        if (this is BeanUser) {

            var user = get<BeanUser>()

            if (user != null) {
                if (!user.userSig.isNullOrEmpty())
                    this.userSig = user.userSig
                if (!user.token.isNullOrEmpty())
                    this.token = user.token
                if (user.isLogin)
                    this.isLogin = user.isLogin

            }
        }

        MySpUtis.putString(this::class.java.name, Gson().toJson(this))
    }

    open fun <T> get(): T? {
        var data = MySpUtis.getObject(this::class.java.name, this::class.java)
        if (data == null) {
            return null
        } else {
            return data as T
        }
    }

    open fun clear() = MySpUtis.clear(this::class.java.name)

    fun <T> fromJson(json: String): T {
        return Gson().fromJson(json, this::class.java) as T
    }


    fun resetDefault() {
        defaultValueReflex(this)
    }

    fun resetToNull() {
        defaultResetToNull(this)
    }
}


