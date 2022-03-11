package io.agora.vlive.ui.activity

import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.gson.Gson
import com.mtjk.base.MyBaseDialog
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.dialog.DialogLoading
import com.mtjk.utils.*
import gorden.rxbus2.RxBus
import gorden.rxbus2.Subscribe
import gorden.rxbus2.ThreadMode
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.video.VideoCanvas
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.util.drawingboard.DrawingBoard
import io.agora.vlive.Live
import io.agora.vlive.LiveViewModel
import io.agora.vlive.R
import io.agora.vlive.agora.rtm.RtmMessageManager
import io.agora.vlive.agora.rtm.model.SeatStateMessage.SeatStateMessageDataItem
import io.agora.vlive.bean.BeanParam
import io.agora.vlive.dialog.LinkMicListDialog
import io.agora.vlive.dialog.LiveMoreDialog
import io.agora.vlive.dialog.WaitingLinkMicDialog
import io.agora.vlive.protocol.manager.SeatServiceManager
import io.agora.vlive.protocol.model.model.SeatInfo
import io.agora.vlive.protocol.model.request.ModifySeatStateRequest
import io.agora.vlive.protocol.model.request.ModifyUserStateRequest
import io.agora.vlive.protocol.model.request.Request
import io.agora.vlive.protocol.model.request.SeatInteractionRequest
import io.agora.vlive.protocol.model.response.SeatStateResponse
import io.agora.vlive.protocol.model.types.SeatInteraction
import io.agora.vlive.ui.actionsheets.InviteUserActionSheet
import io.agora.vlive.ui.actionsheets.InviteUserActionSheet.InviteUserActionSheetListener
import io.agora.vlive.ui.view.*
import io.agora.vlive.ui.view.LiveMultiHostSeatLayout2.LiveHostInSeatOnClickedListener
import io.agora.vlive.ui.view.SeatItemDialog.OnSeatDialogItemClickedListener
import io.agora.vlive.ui.view.bottomLayout.LiveBottomButtonLayout
import io.agora.vlive.utils.Global
import io.agora.vlive.utils.UserUtil
import io.agora.vlive.vm.RoomViewModel
import kotlinx.android.synthetic.main.activity_host_in.*
import kotlinx.android.synthetic.main.activity_host_in.close
import kotlinx.android.synthetic.main.activity_host_in.liveTitle
import kotlinx.android.synthetic.main.activity_host_in.userCount
import kotlinx.android.synthetic.main.activity_host_in.userHeader
import kotlinx.android.synthetic.main.activity_host_in.username
import kotlinx.android.synthetic.main.activity_host_in_2.*
import java.lang.Exception

/**
 * tag==观众-1对多
 */
class GuanZhongActivity2() : LiveRoomActivity(), View.OnClickListener,
    LiveHostInSeatOnClickedListener, InviteUserActionSheetListener,
    OnSeatDialogItemClickedListener {
    /**
     * Helps control UI of the room owner position.
     * Note this manager only change UI, but not
     * involving other logic like start/stop
     * video capture.
     */


    private inner class OwnerUIManager internal constructor(
        layout: RelativeLayout,
        userId: String,
        iAmOwner: Boolean,
        rtcUid: Int
    ) {
        var userId: String
        var profileIconRes: Int
        var rtcUid: Int

        // Contains either user profile icon (when video is not
        // available), or user video (local or remote)
        var userLayout: FrameLayout
        var profileImage: AppCompatImageView? = null
        var audioMuteIcon: AppCompatImageView
        var mIndicateView: VoiceIndicateGifView?

        // for local rendering
        var localPreview: CameraTextureView? = null

        // for remote rendering, generated by rtc engine.
        var remotePreview: SurfaceView? = null

        // If I am the room owner
        var iAmOwner = false
        var videoMuted = false
        var audioMuted = false
        fun setOwner(isOwner: Boolean) {
            iAmOwner = isOwner
            videoMuted = !iAmOwner
            audioMuted = !iAmOwner
            audioMuteIcon.visibility = if (audioMuted) View.VISIBLE else View.GONE
            mIndicateView!!.visibility = if (audioMuted) View.GONE else View.VISIBLE
        }

        // Called only once after entering the room and
        // find out that I am the owner from server response
        fun asOwner(audioMuted: Boolean, videoMuted: Boolean) {
            iAmOwner = true
            if (!videoMuted) showVideoUI()
            audioMuteIcon.visibility = if (audioMuted) View.VISIBLE else View.GONE
            mIndicateView!!.visibility = if (audioMuted) View.GONE else View.VISIBLE
            rtcEngine().muteLocalAudioStream(audioMuted)
            this.videoMuted = videoMuted
            this.audioMuted = audioMuted
        }

        private fun showUserProfile() {
            profileImage = AppCompatImageView(userLayout.context)
            profileImage!!.setImageResource(profileIconRes)
            profileImage!!.clipToOutline = true
            profileImage!!.outlineProvider = RoomOwnerOutline()
            profileImage!!.scaleType = ImageView.ScaleType.FIT_XY
            userLayout.removeAllViews()
            userLayout.addView(profileImage)
        }

        fun showVideoUI() {
            userLayout.removeAllViews()
            profileImage = null
            if (iAmOwner) {
                localPreview = CameraTextureView(userLayout.context)
                localPreview!!.clipToOutline = true
                //                localPreview.setOutlineProvider(new RoomOwnerOutline());
                userLayout.addView(localPreview)
            } else {
                this.log("粉丝进群，初始化主播画面")
                remotePreview = setupRemoteVideo(ownerRtcUid)
                remotePreview!!.setZOrderMediaOverlay(true)
                remotePreview!!.clipToOutline = true
                //                remotePreview.setOutlineProvider(new RoomOwnerOutline());
                rtcEngine().setupRemoteVideo(
                    VideoCanvas(
                        remotePreview,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        ownerRtcUid
                    )
                )
                userLayout.addView(
                    remotePreview,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        }

        fun removeVideoUI(showProfile: Boolean) {
            if (iAmOwner && (localPreview != null) && (
                        localPreview!!.parent === userLayout)
            ) {
                userLayout.removeAllViews()
                localPreview = null
            } else if (!iAmOwner && (remotePreview != null) && (
                        remotePreview!!.parent === userLayout)
            ) {
                userLayout.removeAllViews()
                remotePreview = null
            }
            if (showProfile) showUserProfile()
        }

        fun setAudioMutedValue(muted: Boolean) {
            if (muted == audioMuted && audioMuteIcon.visibility == View.VISIBLE) return
            audioMuted = muted
            audioMuteIcon.visibility = if (audioMuted) View.VISIBLE else View.GONE
            mIndicateView!!.visibility = if (audioMuted) View.GONE else View.VISIBLE
        }

        fun setVideoMutedValue(muted: Boolean) {
            if (muted == videoMuted) {
                if (muted && profileImage == null) {
                    // already in mute state, and profile image showed
                    return
                } else if (!muted && (localPreview != null || remotePreview != null)) {
                    // already in preview state, and preview is displayed.
                    return
                }
            }
            videoMuted = muted
            if (videoMuted) {
                removeVideoUI(true)
            } else {
                showVideoUI()
            }
        }

        fun startVoiceIndicateAnim() {
            if (mIndicateView != null &&
                mIndicateView!!.visibility == View.VISIBLE
            ) {
                mIndicateView!!.start(Global.Constants.VOICE_INDICATE_INTERVAL.toLong())
            }
        }

        init {
            userLayout = layout.findViewById(R.id.room_owner_video_layout)
            this.userId = userId
            profileIconRes = UserUtil.getUserProfileIcon(userId)
            audioMuteIcon = layout.findViewById(R.id.live_host_in_owner_mute_icon)
            mIndicateView = layout.findViewById(R.id.live_host_in_owner_voice_indicate)
            this.rtcUid = rtcUid
            setOwner(iAmOwner)
            //            showUserProfile();
        }
    }

    private inner class RoomOwnerOutline() : ViewOutlineProvider() {
        private val mRadius = 0
        override fun getOutline(view: View, outline: Outline) {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            val leftMargin = 0
            val topMargin = 200
            val selfRect = Rect(
                leftMargin, topMargin,
                rect.right - rect.left - leftMargin,
                rect.bottom - rect.top - topMargin
            )
            outline.setRoundRect(selfRect, mRadius.toFloat())
        }
    }

    private var mOwnerUIManager: OwnerUIManager? = null
    private val mInviteUserListActionSheet: InviteUserActionSheet? = null
    private var mSeatLayout: LiveMultiHostSeatLayout2? = null

    // Generated by back end server according to room id
    private val mSeatInfoList: List<SeatInfo>? = null
    private var mSeatManager: SeatServiceManager? = null
    private val mTopLayoutCalculated = false
    override fun onPermissionGranted() {
        mSeatManager = SeatServiceManager(application())
        initUI()
        super.onPermissionGranted()
    }

    override fun startLiveSuccess() {

    }


    private var user1: ImageView? = null
    private var user2: ImageView? = null
    private var user3: ImageView? = null
    private var usersCount: TextView? = null

    var liveParam = BeanParam().get<BeanParam>()

    private fun initUI() {
        LiveMoreDialog.Status.showDraw = false
        hideStatusBar(false)
        setContentView(R.layout.activity_host_in_2)
        user1 = findViewById(R.id.user1)
        user2 = findViewById(R.id.user2)
        user3 = findViewById(R.id.user3)
        usersCount = findViewById(R.id.userCount)



        liveParam?.let {
            userHeader.load(it.userHeader, 15)
            username.text = it.userName
            liveTitle.text = it.liveTitle
        }


        //连麦列表
        participants = findViewById(R.id.host_in_participant)
        participants?.init()
        participants?.setUserLayoutListener(this)
        close.click { onBackPressed() }
        //底部操作按钮
        bottomButtons = findViewById(R.id.host_in_bottom_layout)
        bottomButtons?.init()
        bottomButtons?.setLiveBottomButtonListener(this)
        bottomButtons?.setRole(if (isOwner) LiveBottomButtonLayout.ROLE_OWNER else if (isHost) LiveBottomButtonLayout.ROLE_HOST else LiveBottomButtonLayout.ROLE_AUDIENCE)
        if (isOwner || isHost) {
            bottomButtons?.setBeautyEnabled(config().isBeautyEnabled)
        }
        findViewById<View>(R.id.live_bottom_btn_close).setOnClickListener(this)
        findViewById<View>(R.id.live_bottom_btn_more).setOnClickListener(this)
        findViewById<View>(R.id.live_bottom_btn_fun1).setOnClickListener(this)
        findViewById<View>(R.id.live_bottom_btn_fun2).setOnClickListener(this)
        findViewById<View>(R.id.link).setOnClickListener(this)
        if (!isOwner) findViewById<View>(R.id.link).visibility = View.GONE
        //消息列表
        messageList = findViewById(R.id.message_list)
        messageList?.init()
        messageEditLayout = findViewById(R.id.message_edit_layout)
        messageEditText = messageEditLayout?.findViewById(LiveMessageEditLayout.EDIT_TEXT_ID)
        messageList?.addMessage(
            0,
            "通知",
            "欢迎来到直播间。直播间内禁止主播诱导观众私下交易，禁止出现违法违规、色情低俗、诱导欺诈、抽烟酗酒等内容。谨慎判断，理性消费，避免财产及人身损失，发现违规行为请及时投诉。"
        )
        //主播画面
        mOwnerUIManager = OwnerUIManager(
            findViewById<RelativeLayout>(
                R.id.room_owner_layout
            ), ownerId!!, isOwner, ownerRtcUid
        )
        if (isOwner) {
            startCameraCapture()
            mOwnerUIManager!!.showVideoUI()
        }
        mSeatLayout = findViewById(R.id.live_host_in_seat_layout)
        mSeatLayout!!.setOwner(isOwner)
        mSeatLayout!!.setHost(isHost)
        mSeatLayout!!.setMyUserId(config().userProfile.userId)
        mSeatLayout!!.setSeatListener(this)

        // Start host speaking volume detection
        rtcEngine().enableAudioVolumeIndication(
            Global.Constants.VOICE_INDICATE_INTERVAL,
            Global.Constants.VOICE_INDICATE_SMOOTH, false
        )
        rtcStatsView = findViewById(R.id.multi_host_rtc_stats)
        rtcStatsView?.setCloseListener({ view: View? -> rtcStatsView?.setVisibility(View.GONE) })

        requestAudienceList()

    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.live_bottom_btn_close) {
            onBackPressed()
        } else if (id == R.id.live_bottom_btn_more) {
            val moreDialog = LiveMoreDialog(1)
            moreDialog.show(this)
        } else if (id == R.id.live_bottom_btn_fun1) {
            if (isOwner) {
                showActionSheetDialog(
                    ACTION_SHEET_BG_MUSIC,
                    tabIdToLiveType(tabId),
                    true,
                    true,
                    this
                )
            } else {
                showActionSheetDialog(ACTION_SHEET_GIFT, tabIdToLiveType(tabId), false, true, this)
            }
        } else if (id == R.id.live_bottom_btn_fun2) {
            if (isOwner || isHost) {
                showActionSheetDialog(ACTION_SHEET_BEAUTY, tabIdToLiveType(tabId), true, true, this)
            }
        } else if (id == R.id.link) {
            if (isOwner || isHost) {
                val dialog = LinkMicListDialog()
                dialog.show(this)
            }
        }
    }

    override fun finish() {
        super.finish()
        if (isHost) {
            stopCameraCapture()
            mOwnerUIManager!!.removeVideoUI(false)
        }
        bottomButtons?.clearStates(application())
    }

    private fun becomeBroadcaster(audioMuted: Boolean, videoMuted: Boolean) {
        isHost = true
        mSeatLayout!!.setHost(true)
        mSeatLayout!!.setMyUserId(config().userProfile.userId)
        bottomButtons?.setRole(LiveBottomButtonLayout.ROLE_HOST)
        bottomButtons?.setBeautyEnabled(config().isBeautyEnabled)
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        config().isAudioMuted = audioMuted
        config().isVideoMuted = videoMuted
        if (!videoMuted) {
            startCameraCapture()
        } else {
            stopCameraCapture()
        }
    }

    private fun becomeAudience() {
        isHost = false
        stopCameraCapture()
        mSeatLayout!!.setHost(false)
        bottomButtons?.setRole(LiveBottomButtonLayout.ROLE_AUDIENCE)
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
        config().isAudioMuted = true
        config().isVideoMuted = true
    }

    override fun onRequestSeatStateResponse(response: SeatStateResponse) {
        super.onRequestSeatStateResponse(response)
    }

    override fun onSeatAdapterHostInviteClicked(position: Int, view: View) {}

    /**
     * tag==连麦
     *
     * @param position seat position
     * @param view     the view clicked
     */
    override fun onSeatAdapterAudienceApplyClicked(position: Int, view: View) {
        if (waitLink) {
            waitLink()
        } else {

            if (isLink) {
                toast("已上麦")
                return
            }


            dialog("您是否申请上麦？").n { }
                .y {
                    Live.sendPeer(
                        this,
                        liveParam!!.userId,
                        SeatInteraction.AUDIENCE_APPLY,
                        3,
                        RtmMessageManager.PEER_MSG_TYPE_SEAT,
                        position,
                        object : ResultCallback<Any?> {
                            override fun onSuccess(o: Any?) {
                                this.log("发起连麦成功")
                                waitLinkSeatIndex = position
                                waitLink()
                            }

                            override fun onFailure(errorInfo: ErrorInfo) {
                                this.log("发起连麦失败:" + errorInfo.toJson())
                                toast("申请失败，请重试")
                            }
                        })
                }.show(this)


        }
    }

    private var waitLinkSeatIndex = 0
    private var waitingLinkMicDialog: WaitingLinkMicDialog? = null

    /**
     * 连麦等待
     */
    fun waitLink() {
        runOnUiThread(Runnable {
            waitLink = true
            val user = getBeanUser()
            waitingLinkMicDialog = WaitingLinkMicDialog()
            if (liveParam !== null) {
                waitingLinkMicDialog!!.setAvatar(liveParam!!.userHeader, user!!.avatar)
            } else {
                waitingLinkMicDialog!!.setAvatar(user!!.avatar, user!!.avatar)
            }

            waitingLinkMicDialog!!.cancel {
                cancelLink()
                waitingLinkMicDialog!!.dismiss()
                null
            }
            waitingLinkMicDialog!!.show(this@GuanZhongActivity2)
        })
    }

    /**
     * 取消连麦申请
     */
    private fun cancelLink() {
        Live.sendPeer(
            this,
            liveParam!!.userId,
            SeatInteraction.AUDIENCE_CANCEL_APPLY,
            3,
            RtmMessageManager.PEER_MSG_TYPE_SEAT,
            waitLinkSeatIndex,
            object : ResultCallback<Any?> {
                override fun onSuccess(o: Any?) {
                    waitLink = false
                    this.log("取消连麦成功")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    this.log("取消连麦失败")
                }
            })
    }

    private fun audienceApplyForSeat(position: Int) {
        mSeatManager!!.apply(roomId, ownerId, position)
    }

    /**
     * 申请上麦-弹框通知-主播
     *
     * @param userId
     * @param userName
     * @param seatId
     */
    override fun onRtmSeatApplied(
        userId: String,
        uid: Int,
        userName: String,
        avatar: String,
        seatId: Int
    ) {
    }

    override fun onCancelLinked(
        userId: String,
        uid: Int,
        userName: String,
        avatar: String,
        index: Int
    ) {
    }

    override fun onRtmInvitationAccepted(
        processId: Long,
        userId: String,
        userName: String,
        index: Int
    ) {
    }

    /**
     * 主播向观众发起邀请后，观众接受邀请，主播端的回调
     * 将连麦用户的信息保存到对应的座位数组，然后发消息到RTM，通知其他观众更新座位UI
     *
     * @param processId
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmInvitationAccepted(
        processId: Long,
        userId: String,
        userName: String,
        uid: Int,
        index: Int
    ) {
    }

    /**
     * 连麦被拒绝后的回调
     *
     * @param processId
     * @param userId
     * @param nickname
     * @param index
     */
    override fun onRtmApplicationRejected(
        processId: Long,
        userId: String,
        nickname: String,
        index: Int
    ) {
        stopWaitLink()

        dialog("您的连麦被咨询师拒绝").single(true).y { }.show(this)
    }

    /**
     * tag==邀请被拒绝
     *
     * @param processId
     * @param userId
     * @param nickname
     * @param index
     */
    override fun onRtmInvitationRejected(
        processId: Long,
        userId: String,
        nickname: String,
        index: Int
    ) {
    }

    override fun onRtmOwnerStateChanged(
        userId: String,
        userName: String,
        uid: Int,
        enableAudio: Int,
        enableVideo: Int
    ) {
        runOnUiThread {
            val audioMuted: Boolean = enableAudio != SeatInfo.User.USER_AUDIO_ENABLE
            val videoMuted: Boolean = enableVideo != SeatInfo.User.USER_VIDEO_ENABLE
            mOwnerUIManager!!.setAudioMutedValue(audioMuted)
            mOwnerUIManager!!.setVideoMutedValue(videoMuted)
            config().isAudioMuted = audioMuted
            config().isVideoMuted = videoMuted
        }
    }

    /**
     * 当连麦者信息变动时，比如下麦，上麦
     *
     * @param list
     */

    override fun onRtmSeatStateChanged(list: List<SeatStateMessageDataItem>) {
        Live.seats = list
        refreshSeat()
        val user = getBeanUser()

        if (list.any { it.user.userId == user!!.userId }) {
            isLink = true
            stopWaitLink()
        } else {
            isLink = false
        }

        log("更新座位.....................")
    }

    private fun stopWaitLink() {
        waitLink = false
        if (waitingLinkMicDialog == null)
            return
        if (waitingLinkMicDialog!!.isAdded) waitingLinkMicDialog!!.dismiss()
    }

    //音量控制
    override fun onRtcAudioVolumeIndication(
        speakers: Array<IRtcEngineEventHandler.AudioVolumeInfo>,
        totalVolume: Int
    ) {
        if (totalVolume <= 0) return
        runOnUiThread({
            for (info: IRtcEngineEventHandler.AudioVolumeInfo in speakers) {
                if (isOwner && info.uid == 0 || info.uid == ownerRtcUid) {
                    mOwnerUIManager!!.startVoiceIndicateAnim()
                }
                mSeatLayout!!.audioIndicate(
                    speakers,
                    config().getUserProfile().getAgoraUid().toInt()
                )
            }
        })
    }

    override fun onSeatAdapterMoreClicked(
        position: Int,
        view: View,
        seatState: Int,
        audioMuteState: Int
    ) {
        if (isOwner || isHost) {
            val mode = if (isOwner) SeatItemDialog.MODE_OWNER else SeatItemDialog.MODE_HOST
            val dialog = SeatItemDialog(
                this, seatState,
                audioMuteState, mode, view, position, this
            )
            dialog.show()
        }
    }

    override fun onSeatAdapterItemVideoShowed(
        position: Int,
        uid: Int,
        mine: Boolean,
        audioMuted: Boolean,
        videoMuted: Boolean
    ): SurfaceView {
        if (this.uid == uid) {
            isLink = true
            //            rtcEngine().setClientRole( Constants.CLIENT_ROLE_BROADCASTER);
        }
        this.log("初始化座位直播画面.......$uid")
        val surfaceView: SurfaceView
        if (mine) {
            surfaceView = CameraSurfaceView(this)
            becomeBroadcaster(audioMuted, videoMuted)
        } else {
            surfaceView = setupRemoteVideo(uid)
        }
        return surfaceView
    }

    override fun onSeatAdapterItemVideoRemoved(
        position: Int,
        uid: Int,
        view: SurfaceView,
        mine: Boolean,
        remainsHost: Boolean
    ) {
        if (!mine) {
            removeRemoteVideo(uid)
        } else {
            if (!remainsHost) {
                becomeAudience()
            } else {
                isHost = true
                mSeatLayout!!.setHost(true)
                mSeatLayout!!.setMyUserId(config().userProfile.userId)
                bottomButtons?.setRole(LiveBottomButtonLayout.ROLE_HOST)
                bottomButtons?.setBeautyEnabled(config().isBeautyEnabled)
            }
        }
    }

    override fun onSeatAdapterItemMyAudioMuted(position: Int, muted: Boolean) {
        rtcEngine().muteLocalAudioStream(muted)
        config().isAudioMuted = muted
    }

    override fun onSeatDialogItemClicked(position: Int, operation: SeatItemDialog.Operation) {
        val item = mSeatLayout!!.getSeatItem(position)
        var title: String? = null
        var message: String? = null
        var request: Request? = null
        var type = 0
        when (operation) {
            SeatItemDialog.Operation.mute -> {
                title = resources.getString(R.string.dialog_multi_host_mute_title)
                message = resources.getString(R.string.dialog_multi_host_mute_message)
                message = String.format(message, item.userName)
                type = Request.MODIFY_USER_STATE
                request = ModifyUserStateRequest(
                    config().userProfile.token, roomId, item.userId,
                    0,  // Notify that the seat has disabled audio
                    // keep video state unchanged
                    if (item.videoMuteState == SeatInfo.User.USER_VIDEO_ENABLE) 1 else 0,
                    1 // Always enable chat
                )
            }
            SeatItemDialog.Operation.unmute -> {
                title = resources.getString(R.string.dialog_multi_host_unmute_title)
                message = resources.getString(R.string.dialog_multi_host_unmute_message)
                message = String.format(message, item.userName)
                type = Request.MODIFY_USER_STATE
                request = ModifyUserStateRequest(
                    config().userProfile.token, roomId, item.userId,
                    1,  // Notify that the seat has enabled audio
                    // keep video state unchanged
                    if (item.videoMuteState == SeatInfo.User.USER_VIDEO_ENABLE) 1 else 0,
                    1 // Always enable chat
                )
            }
            SeatItemDialog.Operation.leave -> {
                title = resources.getString(R.string.dialog_multi_host_leave_title)
                val myUserId = config().userProfile.userId
                val interaction: Int
                if ((myUserId == item.userId)) { //主播下麦观众
                    message = resources.getString(R.string.dialog_multi_host_leave_message_host)
                    interaction = SeatInteraction.HOST_LEAVE
                } else {
                    message = resources.getString(R.string.dialog_multi_host_leave_message_owner)
                    message = String.format(
                        message,
                        if (TextUtils.isEmpty(item.userName)) item.userId else item.userName
                    )
                    interaction = SeatInteraction.OWNER_FORCE_LEAVE
                }
                type = Request.SEAT_INTERACTION
                request = SeatInteractionRequest(
                    config().userProfile.token,
                    roomId, item.userId, position, interaction
                )
            }
            SeatItemDialog.Operation.open -> {
                title = resources.getString(R.string.dialog_multi_host_open_seat_title)
                message = resources.getString(R.string.dialog_multi_host_open_seat_message)
                type = Request.MODIFY_SEAT_STATE
                request = ModifySeatStateRequest(
                    config().userProfile.token, roomId,
                    item.userId, position, SeatInfo.OPEN
                )
            }
            SeatItemDialog.Operation.close -> {
                title = resources.getString(R.string.dialog_multi_host_block_seat_title)
                message = resources.getString(R.string.dialog_multi_host_block_seat_message)
                type = Request.MODIFY_SEAT_STATE
                request = ModifySeatStateRequest(
                    config().userProfile.token, roomId,
                    null, position, SeatInfo.CLOSE
                )
            }
        }


        dialog(message).y {
            isLink = false
            Live.seats[position] = SeatStateMessageDataItem()
            refreshSeat()
            Live.sendSentMessage(this)
        }.show(this)


    }

    override fun onSeatAdapterPositionClosed(position: Int, view: View) {}
    override fun onActionSheetAudienceInvited(seatId: Int, userId: String, userName: String) {}
    override fun onCancelAudienceInvited(seatId: Int, userId: String?, userName: String?) {
        //主播端处理，观众端不需要处理
    }

    /**
     * tag==邀请上麦
     *
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmSeatInvited(userId: String, userName: String, index: Int) {
        if (isOwner) return
        val title =
            resources.getString(R.string.live_room_host_in_invite_user_list_action_sheet_title)
        var message: String = "${userName}邀请你上麦位\n是否接受？"
        message = String.format((message), userName, index)


        dialog = dialog(message).n("拒绝") {
            Live.sendPeer(
                this,
                userId,
                SeatInteraction.AUDIENCE_REJECT,
                0,
                RtmMessageManager.PEER_MSG_TYPE_SEAT,
                index,
                object : ResultCallback<Any?> {
                    override fun onSuccess(o: Any?) {
                        this.toast("拒绝失败")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        this.toast("拒绝失败")
                    }
                })
        }.y("接受") {
            Live.sendPeer(
                this,
                userId,
                SeatInteraction.AUDIENCE_ACCEPT,
                0,
                RtmMessageManager.PEER_MSG_TYPE_SEAT,
                index,
                object : ResultCallback<Any?> {
                    override fun onSuccess(o: Any?) {
                        this.log("接受成功")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        this.log("接受失败" + Gson().toJson(errorInfo))
                    }
                })
        }.show(this)


    }

    override fun onActionSheetVideoClicked(muted: Boolean) {
        super.onActionSheetVideoClicked(muted)
        if (!isOwner && !isHost) return
        if (muted) {
            stopCameraCapture()
        } else {
            startCameraCapture()
        }
        val myUserId = config().userProfile.userId
        if (isOwner) {
            mOwnerUIManager!!.setVideoMutedValue(muted)
            modifyUserState(myUserId, !mOwnerUIManager!!.audioMuted, !muted)
        } else if (isHost) {
            val item = mSeatLayout!!.getSeatItem(myUserId)
            if (item != null) {
                modifyUserState(
                    myUserId, item.audioMuteState ==
                            SeatInfo.User.USER_AUDIO_ENABLE, !muted
                )
            }
        }
    }

    override fun onActionSheetSpeakerClicked(muted: Boolean) {
        super.onActionSheetSpeakerClicked(muted)
        val myUserId = config().userProfile.userId
        if (isOwner) {
            modifyUserState(myUserId, !muted, !mOwnerUIManager!!.videoMuted)
        } else if (isHost) {
            val item = mSeatLayout!!.getSeatItem(myUserId)
            if (item != null) {
                modifyUserState(
                    myUserId,
                    !muted,
                    item.videoMuteState == SeatInfo.User.USER_VIDEO_ENABLE
                )
            }
        }
    }

    private fun modifyUserState(
        userId: String, enableAudio: Boolean,
        enableVideo: Boolean
    ) {
        val request = ModifyUserStateRequest(
            config().userProfile.token, roomId, userId,
            if (enableAudio) 1 else 0, if (enableVideo) 1 else 0, 1
        )
        sendRequest(Request.MODIFY_USER_STATE, request)
    }

    var boo = false

    @Subscribe(code = BusCode.LIVE_JOIN, threadMode = ThreadMode.MAIN)
    fun join(uid: String) {
//        if (!boo && !isOwner) {
//            ownerRtcUid = uid.toInt()
//            isHost = true
//            myRtcRole = Constants.CLIENT_ROLE_BROADCASTER
//            rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
//            this.log("收到消息...................................")
//            mOwnerUIManager!!.rtcUid = uid.toInt()
//            mOwnerUIManager!!.setOwner(false)
//            mOwnerUIManager!!.setAudioMutedValue(false)
//            mOwnerUIManager!!.setVideoMutedValue(false)
//            boo = true
//        }
    }

    override fun onAnchorUidResponse(uid: Int, seats: MutableList<SeatStateMessageDataItem>?) {
        log("获取主播，座位信息成功..............")
        runOnUiThread {
            ownerRtcUid = uid.toInt()
            isHost = true
            myRtcRole = Constants.CLIENT_ROLE_BROADCASTER
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
            mOwnerUIManager!!.rtcUid = uid.toInt()
            mOwnerUIManager!!.setOwner(false)
            mOwnerUIManager!!.setAudioMutedValue(false)
            mOwnerUIManager!!.setVideoMutedValue(false)
            requestAudienceList()

            seats?.let { onRtmSeatStateChanged(seats) }
        }

    }


    override fun onRtcJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        this.log("进入频道通知，channel：$channel  uid:$uid")
        this.uid = uid
        this.initVM(RoomViewModel::class.java).updateUid(uid.toString() + "").obs(this) {
            it.y { log("更新uid成功") }
        }
        this.initVM(LiveViewModel::class.java).addViewedCount(rtcChannelName!!).obs(this) {
            it.y { log("更新观看次数") }
        }
    }

    override fun onRtmMemberExitRoom(userId: String?) {
        this.log("有观众退出....")
        requestAudienceList()
    }

    /**
     * 取消邀请
     */
    override fun onCancelRtmSeatInvited() {
        //观众端处理
        curDialog?.dismiss()
        dialog?.dismiss()
    }

    /**
     * 临时一对一使用（用户正在连麦中，将座位1的账号设置为空，群发消息）
     */
    override fun userLiveRoom() {
        var user = getBeanUser()
        this.log("下麦UserId:" + Live.seats[0].user.userId)

        for (i in Live.seats.indices) {
            if (Live.seats[i].user.userId == user!!.userId) {
                Live.seats[i] = SeatStateMessageDataItem()
                break
            }
        }

        refreshSeat()
        Live.sendSentMessage(this, object : ResultCallback<Any?> {
            override fun onSuccess(o: Any?) {
                this.log("发送成功。。。。")
                isLink = false

            }

            override fun onFailure(errorInfo: ErrorInfo) {

            }
        })
    }

    /**
     * 更新座位
     */
    private fun refreshSeat() {
        requestAudienceList()
        runOnUiThread { mSeatLayout!!.updateStates(Live.seats) }
    }


    /**
     * 用户连麦等待中，当用户退出Activity时，通知主播，取消连麦
     */
    override fun cancelWaitLink() {
        super.cancelWaitLink()
        cancelLink()
    }

    override fun onDestroy() {
        super.onDestroy()
        logout()
    }

    override fun onRtmMemberJoined(userId: String) {
        this.log("有观众加入....")
        requestAudienceList()
    }

    private fun requestAudienceList() {

        rtcChannelName ?: return

        this.log("加载观众数据....")
        postDelayed(3000) {
            this.initVM(LiveViewModel::class.java).getUsers(rtcChannelName!!).obs(this) {
                it.y {
                    val users = it.audienceObjectList
                    this.log("观众列表" + Gson().toJson(users))
                    userCount.text = users.size.toString()
                    viewNum.text = "${it.viewedCount}人看过"
                    if (users != null) {
                        user1!!.show(users.size > 0)
                        user2!!.show(users.size > 1)
                        user3!!.show(users.size > 2)
                        if (users.size > 0) {
                            user1!!.load(users[0].avatar, 30.dip2px())
                        }
                        if (users.size > 1) {
                            user2!!.load(users[1].avatar, 30.dip2px())
                        }
                        if (users.size > 2) {
                            user3!!.load(users[2].avatar, 30.dip2px())
                        }
                    }
//                mInviteUserListActionSheet!!.append(userList)
                }
            }
        }
    }

    private var drawInviteDialog: MyDialog? = null

    @Subscribe(code = BusCode.LIVE_DRAW_INVITE_MESSAGE)
    public fun drawInvite(list: ArrayList<String>) {
        var user = getBeanUser()
        if (list.any { user!!.userId == it }) {
            drawInviteDialog = dialog("咨询师向你发送在线共享画板").n("取消") {
                LiveMoreDialog.Status.showDraw = true
            }.y("接受") {
                LiveMoreDialog.Status.showDraw = true
                Live.sendPeer(
                    this,
                    liveParam!!.userId,
                    SeatInteraction.LIVE_DRAWING_ACCEPT_INVITE,
                    0,
                    RtmMessageManager.PEER_MSG_TYPE_SEAT,
                    0,
                    object : ResultCallback<Any?> {
                        override fun onSuccess(o: Any?) {
                            this.log("接受画板成功")
                            disableDraw = false
                            showDraw()
                        }

                        override fun onFailure(errorInfo: ErrorInfo) {
                            this.log("接受画板失败")
                        }
                    })
            }
            drawInviteDialog?.show(this)
        } else {
            disableDraw = true
            showDraw()
        }
    }

    var disableDraw = false

    @Subscribe(code = BusCode.LIVE_DRAW_SHOW_GZ)
    fun showDraw() {
        rtcChannelName?.let { DrawingBoard.from(this).show(it, disableDraw) }
    }

    @Subscribe(code = BusCode.LIVE_DRAW_CLOSE_ALL)
    fun closeAllDraw() {
        try {
            toast("主播结束共享画板")
            disableDraw = false;
            drawInviteDialog?.dismiss()
        }catch (e:Exception){

        }

    }

    @Subscribe(code = BusCode.LIVE_UPDATE_ZHUBO_UID)
    fun updateZhuBoUid(uid: String) {
        log("主播更新uid，刷新UI")
        onAnchorUidResponse(uid.toInt(), null)
    }

    companion object {
        private val ROOM_NAME_HINT_COLOR = Color.rgb(101, 101, 101)
        private val ROOM_NAME_COLOR = Color.rgb(235, 235, 235)
    }
}