package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean

/**
 * 咨询师详情
 * author:chenliang
 * date:2021/11/12
 */
class BeanConsultRes : MyBaseBean() {
    var expertDetailEntity: BeanConsult? = null
    var expertServerBaseModelList: ArrayList<BeanConsultService>? = null
}