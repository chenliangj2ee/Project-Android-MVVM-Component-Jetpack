package com.fcyd.expert.vm

import com.chenliang.processor.LBase.API_EXPERT
import com.chenliang.processor.appexpert.API
import com.fcyd.expert.bean.BeanAuth
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

/**
 * author,chenliang
 * date,2021/11/29
 */
class UserViewModel : MyBaseViewModel() {

    fun inApp(bean: BeanAuth) = go {

        var body = body(
            "avatar", bean.header,
            "cardFront", bean.id_image1,
            "cardNo", bean.id_Num,
            "cardSide", bean.id_image2,
            "cardType", bean.getIdTypeState(),
            "personalIntroduce", bean.xxjs,
            "name", bean.name,
            "sex", bean.sex,
            "years", bean.cynx.replace("年", "").toInt(),
            "introduction", bean.scly,
            "location", bean.address,
            "qualificationCertificateModels", bean.zgzs,
            "caseExperience",
            body(
                "data", if (bean.gajl_zmzl == 0) bean.gajl_images.joinToString(separator = ",") { i -> i } else bean.gajl_text,
                "dataType", bean.gajl_zmzl,
                "duration", bean.gajl_sc!!,
                "type", 1,//1-个案，2-个人体验经历，3-个体督导经历，4-团体督导，5-短程-经历，6-长程-经历
            ),
            "educationExperienceModel",
            body(
                "certificate", bean.jybj_image,
                "education", bean.jybj_xl,
                "startTime", bean.jybj_time.replace(" ", "").split("-")[0],
                "endTime", bean.jybj_time.replace(" ", "").split("-")[1],
                "major", bean.jybj_zy,
                "school", bean.jybj_xx,
            ),
            "groupSupervision",
            body(
                "data", if (bean.ttddjl_zmzl == 0) bean.ttddjl_images.joinToString(separator = ",") { i -> i } else bean.ttddjl_text,
                "dataType", bean.ttddjl_zmzl,
                "duration", bean.ttddjl_sc!!,
                "type", 4,//1-个案，2-个人体验经历，3-个体督导经历，4-团体督导，5-短程-经历，6-长程-经历
            ),
            "individualSupervision",
            body(
                "data", if (bean.gtddjl_zmzl == 0) bean.gtddjl_images.joinToString(separator = ",") { i -> i } else bean.gtddjl_text,
                "dataType", bean.gtddjl_zmzl,
                "duration", bean.gtddjl_sc!!,
                "type", 3,//1-个案，2-个人体验经历，3-个体督导经历，4-团体督导，5-短程-经历，6-长程-经历
            ),
            "longTraining",
            body(
                "data", if (bean.ccsxjl_zmzl == 0) bean.ccsxjl_images.joinToString(separator = ",") { i -> i } else bean.ccsxjl_text,
                "dataType", bean.ccsxjl_zmzl,
                "duration", bean.ccsxjl_sc!!,
                "type", 6,//1-个案，2-个人体验经历，3-个体督导经历，4-团体督导，5-短程-经历，6-长程-经历
            ),
            "personalExperience",
            body(
                "data", if (bean.grtyjl_zmzl == 0) bean.grtyjl_images.joinToString(separator = ",") { i -> i } else bean.grtyjl_text,
                "dataType", bean.grtyjl_zmzl,
                "duration", bean.grtyjl_sc!!,
                "type", 2,//1-个案，2-个人体验经历，3-个体督导经历，4-团体督导，5-短程-经历，6-长程-经历
            ),
            "shortTraining",
            body(
                "data", if (bean.dcsxjl_zmzl == 0) bean.dcsxjl_images.joinToString(separator = ",") { i -> i } else bean.dcsxjl_text,
                "dataType", bean.dcsxjl_zmzl,
                "duration", bean.dcsxjl_sc!!,
                "type", 5,//1-个案，2-个人体验经历，3-个体督导经历，4-团体督导，5-短程-经历，6-长程-经历
            ),
        )

        API.inApp(body)
    }

    fun init() = go { API.init() }

    fun getExpertData() = go { API.getExpertData() }

    fun getIMRTCToken(channelName: String, userId: String) = go { API.getRTCToken(body("channelName", channelName,"userId",userId)) }

    /**
     * 获取钱包列表
     */
    fun getWalletInfo() = go {
        API.getWalletInfo()
    }

    /**
     * 获取钱包明细列表
     */
    fun getWalletDetailList(pageNo: Int, pageSize: Int, startTime: String, endTime: String) = go {
        if(startTime.isNullOrEmpty()) {
            API.getWalletDetailList(body("pageNo", pageNo, "pageSize", pageSize))
        } else if(endTime.isNullOrEmpty()) {
            API.getWalletDetailList(body("pageNo", pageNo, "pageSize", pageSize, "startTime", startTime))
        } else {
            API.getWalletDetailList(body("pageNo", pageNo, "pageSize", pageSize, "startTime", startTime, "endTime", endTime))
        }
    }

    /*
    * 咨询师提现
    * */
    fun WalletWithdraw(amount: Number) = go {
        API.walletWithdraw(body("amount", amount))
    }

    /**
     * 获取钱包明细列表
     */
    fun getWalletWithdrawList(pageNo: Int, pageSize: Int, startTime: String, endTime: String) = go {
        if(startTime.isNullOrEmpty()) {
            API.getWalletWithdrawList(body("pageNo", pageNo, "pageSize", pageSize))
        } else if(endTime.isNullOrEmpty()) {
            API.getWalletWithdrawList(body("pageNo", pageNo, "pageSize", pageSize, "startTime", startTime))
        } else {
            API.getWalletWithdrawList(body("pageNo", pageNo, "pageSize", pageSize, "startTime", startTime, "endTime", endTime))
        }
    }

}