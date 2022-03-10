package io.agora.vlive.view

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View
import android.widget.RelativeLayout
import com.mtjk.utils.click
import com.mtjk.utils.log
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.vlive.bean.BeanLinkUser
import io.agora.vlive.Live
import io.agora.vlive.R
import io.agora.vlive.agora.rtm.RtmMessageManager
import io.agora.vlive.databinding.LiveSeatListBinding
import io.agora.vlive.protocol.model.types.SeatInteraction
import io.agora.vlive.ui.view.CameraSurfaceView
import io.agora.vlive.ui.activity.LiveBaseActivity
import kotlinx.android.synthetic.main.layout_link_listview.view.*

/**
 * author:chenliang
 * date:2022/2/11
 */
class LinkListView : RelativeLayout {

    var users = ArrayList<BeanLinkUser>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

        var view = View.inflate(context!!, R.layout.layout_link_listview, this)

        for (i in 1..6) {
            var user = BeanLinkUser()
            user.index = i
            users.add(user)

        }

        listview.disable()
        listview.bindData<BeanLinkUser> { user ->
            with(user.binding as LiveSeatListBinding) {
                user.video = video
                this.root.click { link(getContext() as LiveBaseActivity, "1486542211490267138", user) }
            }
        }
        listview.addDatas(users)

    }

    //    val peerId = "1486542211490267138" //接收者RTM登录ID
    private fun link(activity: LiveBaseActivity, peerId: String, user: BeanLinkUser) {
        Live.sendPeer(
            activity,
            peerId,
            SeatInteraction.AUDIENCE_APPLY,
            3,
            RtmMessageManager.PEER_MSG_TYPE_SEAT,
            user.index,
            object : ResultCallback<Any?> {
                override fun onSuccess(o: Any?) {
                    this.log("连麦成功")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    this.log("连麦失败")
                }
            })
    }


    fun refreshUi() {

    }


    private fun initVideo(user: BeanLinkUser, surfaceView: SurfaceView?) {
        if (surfaceView != null && user.video != null) {
            user.video!!.removeAllViews()
            user.video!!.addView(surfaceView)
        }
    }


    private fun initVideoSurfaceView(user: BeanLinkUser, surfaceView: SurfaceView?) {

        val surfaceView = CameraSurfaceView(context)


        if (surfaceView != null && user.video != null) {
            user.video!!.removeAllViews()
            user.video!!.addView(surfaceView)
        }
    }
}
