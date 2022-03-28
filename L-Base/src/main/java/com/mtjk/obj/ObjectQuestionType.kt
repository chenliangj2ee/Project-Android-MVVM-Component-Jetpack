package com.mtjk.obj

object ObjectQuestionType {
    //恋爱情感
    const val TYPE_LOVE = 1

    //婚姻家庭
    const val TYPE_MARRY = 2

    //亲子教育
    const val TYPE_CHILD = 3

    //人际社交
    const val TYPE_SOCIETY = 4

    //职场发展
    const val TYPE_JOB = 5

    //个人成长
    const val TYPE_GROW = 6

    //其他
    const val TYPE_OTHERS = 7

    fun getTypeString(type: Int): String {
        if(type == TYPE_LOVE) {
            return "恋爱情感"
        } else if(type == TYPE_MARRY) {
            return "婚姻家庭"
        } else if(type == TYPE_CHILD) {
            return "亲子教育"
        } else if(type == TYPE_SOCIETY) {
            return "人际社交"
        } else if(type == TYPE_JOB) {
            return "职场发展"
        } else if(type == TYPE_GROW) {
            return "个人成长"
        } else if(type == TYPE_OTHERS) {
            return "其他"
        }
        return "其他"
    }
}
