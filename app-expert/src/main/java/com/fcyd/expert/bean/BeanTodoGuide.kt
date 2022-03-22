package com.fcyd.expert.bean

import com.fcyd.expert.R
import com.mtjk.obj.ObjectLocalTodo

class BeanTodoGuide {
    var title = ""
    var desc = ""
    var todoType = 0

    fun typeDrawable(): Int {
        if(todoType == ObjectLocalTodo.WORKROOM) {
            return R.drawable.item_todo_icon_work_room
        } else if(todoType == ObjectLocalTodo.CONSULT) {
            return R.drawable.item_todo_icon_consult
        }
        return 0
    }
}