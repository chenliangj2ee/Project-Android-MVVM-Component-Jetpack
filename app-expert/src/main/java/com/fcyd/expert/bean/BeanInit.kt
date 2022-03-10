package com.fcyd.expert.bean

import com.mtjk.base.MyBaseBean

/**
 * author:chenliang
 * date:2021/11/18
 */
class BeanInit : MyBaseBean() {
    var studioVerify = 0//工作室装修状态： 1:待审核 2:审核通过 3:审核不通过
    var verify = 0//专家认证：0-初始状态，1-审核中，2-审核成功，3-审核失败',
}