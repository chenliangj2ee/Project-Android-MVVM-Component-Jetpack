package io.agora.vlive.dialog

import android.view.Gravity
import com.mtjk.BaseInit
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.dialog.ReportDialog
import com.mtjk.utils.*
import io.agora.util.drawingboard.DrawingBoard
import io.agora.vlive.Live
import io.agora.vlive.databinding.DialogLiveMoreBinding

/**
 * tag==投诉
 * tag==异常反馈
 * author:chenliang
 * date:2021/11/30
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class LiveMoreDialog(roomId: Int) : MyBaseDialog<DialogLiveMoreBinding>() {
    var roomId = roomId
    var channelName = ""

    object Status {
        var showDraw = true
    }

    override fun initCreate() {
        mBinding.tousu.click {
            var dialog = ReportDialog(roomId)
            dialog.show(this)
            dismiss()
        }
        mBinding.draw.show(LiveMoreDialog.Status.showDraw)
        mBinding.draw.click {

            if (BaseInit.isUserApp) {
                //展示画板
                send(BusCode.LIVE_DRAW_SHOW_GZ)
            } else {

                if (Live.audienceObjectList.isEmpty()) {
                    toast("暂无观众")
                } else {
                    DrawingSelectDialog().show(this)
                }


            }
            dismiss()
        }
        mBinding.yichang.click { }
    }

    fun channelName(channelName: String) {
        this.channelName = channelName
    }


}