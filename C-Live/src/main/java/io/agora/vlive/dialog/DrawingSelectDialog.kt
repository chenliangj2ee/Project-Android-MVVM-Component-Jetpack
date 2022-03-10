package io.agora.vlive.dialog

import android.view.Gravity
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe
import io.agora.util.drawingboard.DrawingBoard
import io.agora.vlive.Live
import io.agora.vlive.bean.BeanParam
import io.agora.vlive.bean.BeanParam.LiveType.AUDIO_ONE
import io.agora.vlive.bean.BeanParam.LiveType.VIDEO_ONE
import io.agora.vlive.databinding.DialogDrawingSelectedBinding
import io.agora.vlive.ui.activity.LiveBaseActivity
import kotlinx.android.synthetic.main.dialog_drawing_selected.*

/**
 * tag==画板工具
 * author:chenliang
 * date:2021/11/23
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DrawingSelectDialog : MyBaseDialog<DialogDrawingSelectedBinding>() {

    var liveParam = BeanParam().get<BeanParam>()
    var user = getBeanUser()

    override fun initCreate() {

    }


    override fun initClick() {

        var isOne = liveParam!!.liveType == AUDIO_ONE || liveParam!!.liveType == VIDEO_ONE

        mBinding.send.isEnabled = isOne
        mBinding.selectUser.hide(isOne)
        mBinding.selectUser.click { DrawingSelectorUserDialog().show(this) }
        mBinding.close.click { dismiss() }
        mBinding.send.click {
            /**
             * 一对一
             */
            if (liveParam!!.liveType == AUDIO_ONE || liveParam!!.liveType == VIDEO_ONE) {
                Live.drawUserIds.clear()
                Live.drawUserIds.add(liveParam!!.userId)
                Live.sendDrawInviteMessage(activity as LiveBaseActivity)
            } else {
                Live.drawUserIds.clear()
                Live.audienceObjectList.filter { it.itemSelected }
                    .forEach { Live.drawUserIds.add(it.id) }
                Live.sendDrawInviteMessage(activity as LiveBaseActivity)
            }
            //展示画板
            send(BusCode.LIVE_DRAW_SHOW_ZB)
            dismiss()
        }
    }

    @Subscribe(code = BusCode.LIVE_DRAW_SELECTED_USER_FINISH)
    fun selectedUserEvent() {
        users.text = "${Live.audienceObjectList.filter { it.itemSelected }.size}人"
        mBinding.send.isEnabled = Live.audienceObjectList.any { it.itemSelected }
    }

}