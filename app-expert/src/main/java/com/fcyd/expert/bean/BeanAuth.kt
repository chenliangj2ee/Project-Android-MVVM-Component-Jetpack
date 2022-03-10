package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean
import com.mtjk.bean.BeanCertificate

/**
 * author:chenliang
 * date:2021/12/6
 */
class BeanAuth : MyBaseBean() {
    var header = ""
    var name = ""
    var sex = 0//0，未选择，1，男，2女
    var address = ""

    var id_type = ""
    var id_image1 = ""
    var id_image2 = ""
    var id_Num = ""

    //从业年限
    var cynx = ""

    //教育背景
    var jybj_xl = ""
    var jybj_xx = ""
    var jybj_zy = ""
    var jybj_time = ""
    var jybj_starttime = ""
    var jybj_endtime = ""
    var jybj_image = ""

    //资格证书
    var zgzs = arrayListOf<BeanCertificate>()//name,path

    //个案经历
    var gajl_sc = ""//时长
    var gajl_zmzl: Int = 0//0:图片，1文本
    var gajl_text = ""
    var gajl_images = arrayListOf<String>()

    //个人体验经历
    var grtyjl_sc = ""//时长
    var grtyjl_zmzl = 0//0:图片，1文本
    var grtyjl_text = ""
    var grtyjl_images = arrayListOf<String>()


    //个体督导经历
    var gtddjl_sc = ""//时长
    var gtddjl_zmzl = 0//0:图片，1文本
    var gtddjl_text = ""
    var gtddjl_images = arrayListOf<String>()

    //团体督导经历
    var ttddjl_sc = ""//时长
    var ttddjl_zmzl = 0//0:图片，1文本
    var ttddjl_text = ""
    var ttddjl_images = arrayListOf<String>()

    //短程督导经历
    var dcsxjl_sc = ""//时长
    var dcsxjl_zmzl = 0//0:图片，1文本
    var dcsxjl_text = ""
    var dcsxjl_images = arrayListOf<String>()

    //长程督导经历
    var ccsxjl_sc = ""//时长
    var ccsxjl_zmzl = 0//0:图片，1文本
    var ccsxjl_text = ""
    var ccsxjl_images = arrayListOf<String>()

    //擅长领域
    var scly = ""

    //详细介绍
    var xxjs = ""

    fun getIdTypeState() = when (id_type) {
        "身份证" -> 1
        "护照" -> 2
        "港澳通行证" -> 3
        "台湾通行证" -> 4
        else -> 1
    }


}
