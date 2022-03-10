package com.mtjk.vm

import com.chenliang.processor.LBase.API_EXPERT
import com.chenliang.processor.LBase.API_USER
import com.mtjk.BaseInit
import com.mtjk.base.MyBaseViewModel
import com.mtjk.bean.BeanUser
import com.mtjk.utils.body

class AccountViewModel : MyBaseViewModel() {

    fun getPhoneCode(phone: String) = go {
        if (BaseInit.isUserApp) {
            API_USER.getPhoneCode(phone)
        } else {
            API_EXPERT.getPhoneCode(phone)
        }

    }

    fun codeLogin(phone: String, key: String, code: String, pushToken: String) = go {
        if (BaseInit.isUserApp) {
            API_USER.codeLogin(body("phone", phone, "key", key, "code", code, "lastLoginDevice", 100, "pushToken", pushToken))
        } else {
            API_EXPERT.codeLogin(body("phone", phone, "key", key, "code", code, "lastLoginDevice", 100, "pushToken", pushToken))
        }

    }

    fun quickLogin(accessToken: String, keyToken: String, pushToken: String) = go {
        if (BaseInit.isUserApp) {
            API_USER.quickLogin(body("accessToken", accessToken, "keyToken", keyToken, "lastLoginDevice", 100, "pushToken", pushToken))
        } else {
            API_EXPERT.quickLogin(body("accessToken", accessToken, "keyToken", keyToken, "lastLoginDevice", 100, "pushToken", pushToken))
        }

    }

    fun updateUserAccount(bean: BeanUser) = go {
        if (BaseInit.isUserApp) {
            API_USER.updateUserAccount(bean)
        } else {
            API_EXPERT.updateUserAccount(bean)
        }
    }

    fun getUserInfo() = go {

        if (BaseInit.isUserApp) {
            API_USER.getUserInfo()
        } else {
            API_EXPERT.getUserInfo()
        }
    }

    fun getUserSig() = go {
        if (BaseInit.isUserApp) {
            API_USER.getUserUserSig()
        } else {
            API_EXPERT.getUserUserSig()
        }
    }

    fun logoff() = go {
        if (BaseInit.isUserApp) {
            API_USER.logoff()
        } else {
            API_EXPERT.logoff()
        }
    }

    /**
     * 获取商品评论
     */
    fun evaluateProductList(productId: String, pageNo: Int, pageSize: Int) = go {
        API_USER.evaluateProductList(productId, pageNo, pageSize)
    }

    /**
     * 获取商家评论
     */
    fun evaluateExpertList(shopId: String, pageNo: Int, pageSize: Int) = go {
        API_USER.evaluateExpertList(shopId, pageNo, pageSize)
    }

}