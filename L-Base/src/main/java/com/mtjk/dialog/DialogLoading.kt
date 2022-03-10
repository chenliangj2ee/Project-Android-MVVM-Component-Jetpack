package com.mtjk.dialog

import android.app.Activity
import android.app.Dialog
import com.mtjk.base.R

/**
 * author:chenliang
 * date:2022/2/24
 */
class DialogLoading(act:Activity) {

    var dialog = Dialog(act)

    fun show(){
        dialog.setContentView(R.layout.base_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show()
    }
    fun dismiss(){
        dialog.dismiss()
    }
}