package com.mentuojiankang.user.bean

import com.mtjk.base.MyBaseBean

/**
 * 情感标签
 * author:chenliang
 * date:2021/11/10
 */
class BeanEmotionTag : MyBaseBean() {
    var emotionId = ""
    var emotionName = ""
    var emotionState = 0
    @Transient
    var isSelected = false;

}