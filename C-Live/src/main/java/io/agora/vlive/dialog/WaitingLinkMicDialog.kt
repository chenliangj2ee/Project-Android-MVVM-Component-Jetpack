package io.agora.vlive.dialog

import android.view.Gravity
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.utils.click
import io.agora.vlive.databinding.DialogWaitingLinkMicBinding

/**
 * tag==等待连麦
 * author:chenliang
 * date:2021/11/23
 */
@MyClass(mDialogGravity = Gravity.BOTTOM,mDialogAnimation = true)
class WaitingLinkMicDialog : MyBaseDialog<DialogWaitingLinkMicBinding>() {
    var expertAvatar = ""
    var myAvatar = ""
    var cancelFun: (() -> Unit?)? = null
    override fun initCreate() {
        with(mRootView) {
            mBinding.myAvatar = myAvatar
            mBinding.expertAvatar = expertAvatar
            mBinding.cancel?.click {
                cancelFun?.let { it1 -> it1() }
                dismiss()
            }
            this?.click { dismiss() }
        }
    }

    fun cancel(func: () -> Unit) {
        this.cancelFun = func
    }

    fun setAvatar(myAvatar: String, expertAvatar: String) {
        this.myAvatar = myAvatar
        this.expertAvatar = expertAvatar
    }

}