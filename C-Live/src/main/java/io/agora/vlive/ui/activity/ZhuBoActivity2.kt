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
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
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
import io.agora.vlive.agora.rtm.model.SeatInteractionMessage
import io.agora.vlive.agora.rtm.model.SeatStateMessage.SeatStateMessageDataItem
import io.agora.vlive.bean.BeanAudience
import io.agora.vlive.bean.BeanLinkUser
import io.agora.vlive.bean.BeanParam
import io.agora.vlive.dialog.LinkMicListDialog
import io.agora.vlive.dialog.LiveMoreDialog
import io.agora.vlive.protocol.manager.SeatServiceManager
import io.agora.vlive.protocol.model.model.SeatInfo
import io.agora.vlive.protocol.model.request.ModifySeatStateRequest
import io.agora.vlive.protocol.model.request.ModifyUserStateRequest
import io.agora.vlive.protocol.model.request.Request
import io.agora.vlive.protocol.model.request.SeatInteractionRequest
import io.agora.vlive.protocol.model.response.AudienceListResponse
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
import java.util.*
import kotlin.collections.ArrayList

/**
 * tag==主播-1对多
 */
class ZhuBoActivity2 : LiveRoomActivity(), View.OnClickListener, LiveHostInSeatOnClickedListener,
    InviteUserActionSheetListener,
    OnSeatDialogItemClickedListener {
    /**
     * Helps control UI of the room owner position.
     * Note this manager only change UI, but not
     * involving other logic like start/stop
     * video capture.
     */
    private val linkUsers = ArrayList<BeanLinkUser>()
    var liveParam = BeanParam().get<BeanParam>()

    private inner class OwnerUIManager internal constructor(
        layout: RelativeLayout,
        userId: String,
        iAmOwner: Boolean,
        rtcUid: Int
    ) {
        var userId: String
        var profileIconRes: Int
        var rtcUid: Int
        var userLayout: FrameLayout
        var profileImage: AppCompatImageView? = null
        var audioMuteIcon: AppCompatImageView
        var mIndicateView: VoiceIndicateGifView?
        var localPreview: CameraTextureView? = null
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
                remotePreview!!.setClipToOutline(true)
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
            if (iAmOwner && localPreview != null && localPreview!!.parent === userLayout) {
                userLayout.removeAllViews()
                localPreview = null
            } else if (!iAmOwner && remotePreview != null && remotePreview!!.parent === userLayout) {
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

    private inner class RoomOwnerOutline : ViewOutlineProvider() {
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

    override fun onAnchorUidResponse(uid: Int, seats: MutableList<SeatStateMessageDataItem>?) {

    }

    private var mOwnerUIManager: OwnerUIManager? = null
    private var mInviteUserListActionSheet: InviteUserActionSheet? = null
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

    override fun onRtcJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        this.log("进入频道通知，channel：$channel  uid:$uid   ----------------------")
        this.uid = uid
        sendUidToUsers()
        startLiveSuccess()

        this.initVM(RoomViewModel::class.java).updateUid(uid.toString() + "").obs(this) {
            it.y { log("更新uid成功") }
        }
    }

    fun sendUidToUsers(){
        runOnUiThread {
            postDelayed(1000) {
                log("主播发送uid:")
                Live.sendRtmZhuboIntoMessage(this, uid, object : ResultCallback<Any> {
                    override fun onSuccess(p0: Any?) {
                        log("主播发送uid成功")
                    }

                    override fun onFailure(p0: ErrorInfo?) {
                        log("主播发送uid失败")
                        postDelayed(3000){
                            sendUidToUsers()
                        }
                    }

                })
            }
        }
    }

    override fun onCancelRtmSeatInvited() {
        //观众端调用，主播端不处理
    }

    override fun startLiveSuccess() {
        startTime = System.currentTimeMillis()
        log("startLiveSuccess.........................")
        initVM(LiveViewModel::class.java).start(rtcChannelName!!).obs(this) {
            it.y { log("开始直播状态修改成功") }
        }
    }

    private var user1: ImageView? = null
    private var user2: ImageView? = null
    private var user3: ImageView? = null
    private var usersCount: TextView? = null
    var user = getBeanUser()
    private fun initUI() {
        Live.initSeats()
        InviteUserActionSheet.inviting.clear()
        hideStatusBar(false)
        setContentView(R.layout.activity_host_in_2)
        user1 = findViewById(R.id.user1)
        user2 = findViewById(R.id.user2)
        user3 = findViewById(R.id.user3)
        usersCount = findViewById(R.id.userCount)
        userHeader.load(user!!.avatar, 15)
        username.text = user?.realName
        //连麦列表
        participants = findViewById(R.id.host_in_participant)
        participants?.init()
        participants?.setUserLayoutListener(this)
        close.click { onBackPressed() }


        liveParam?.let {
            liveTitle.text = it.liveTitle
        }


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
        if (isOwner == false) findViewById<View>(R.id.link).visibility = View.GONE
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
        refreshSeat()
        // Start host speaking volume detection
        rtcEngine().enableAudioVolumeIndication(
            Global.Constants.VOICE_INDICATE_INTERVAL,
            Global.Constants.VOICE_INDICATE_SMOOTH, false
        )
        rtcStatsView = findViewById(R.id.multi_host_rtc_stats)
        rtcStatsView?.setCloseListener { view: View? -> rtcStatsView?.visibility = View.GONE }
        requestAudienceList()
    }

    override fun onGlobalLayoutCompleted() {}
    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.live_bottom_btn_close) {
            onBackPressed()
        } else if (id == R.id.live_bottom_btn_more) {
//            LiveRoomToolActionSheet toolSheet = (LiveRoomToolActionSheet)
//                    showActionSheetDialog(ACTION_SHEET_TOOL,
//                            tabIdToLiveType(tabId), isHost, true, this);
//            toolSheet.setEnableInEarMonitoring(inEarMonitorEnabled);
            val moreDialog = LiveMoreDialog(1)
            moreDialog.channelName = rtcChannelName!!
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
                // this button is hidden when current user is not host.
                showActionSheetDialog(ACTION_SHEET_BEAUTY, tabIdToLiveType(tabId), true, true, this)
            }
        } else if (id == R.id.link) {
            if (isOwner || isHost) {
                showLinkDialog()
            }
        } else if (id == R.id.draw) {
            DrawingBoard.from(this).show(rtcChannelName!!)
        }
    }

    var dialogLinkList: LinkMicListDialog? = null

    /**
     * 连麦列表
     */
    private fun showLinkDialog() {
        if (dialogLinkList == null) {
            dialogLinkList = LinkMicListDialog()
        }
        if (dialogLinkList!!.isAdded) {
            dialogLinkList!!.refresh(linkUsers)
        } else {
            dialogLinkList = LinkMicListDialog()
            dialogLinkList!!.setData(linkUsers)
        }
        dialogLinkList!!.setItemClick { beanLinkUser: BeanLinkUser, aBoolean: Boolean ->
            if (aBoolean) { //接受申请
                this.log("接受申请：userId:" + beanLinkUser.userId + "  name:" + beanLinkUser.nickName)

                if (Live.setSeat(beanLinkUser)) {
                    Live.sendPeer(
                        this,
                        beanLinkUser.userId,
                        SeatInteraction.OWNER_ACCEPT,
                        2,
                        1,
                        beanLinkUser.index,
                        object : ResultCallback<Any?> {
                            override fun onSuccess(p0: Any?) {
                                log("接受成功。。。")

                            }

                            override fun onFailure(p0: ErrorInfo?) {
                                log("接受失败。。。${p0!!.toJson()}")
                            }
                        })

                    Live.sendSentMessage(this@ZhuBoActivity2)
                    refreshSeat()
                    mInviteUserListActionSheet?.append(inviteUser)
                    linkUsers.remove(beanLinkUser)
                } else {
                    toast("座位已满")
                }


            } else { //拒绝申请
                Live.sendPeer(
                    this@ZhuBoActivity2,
                    beanLinkUser.userId,
                    SeatInteraction.OWNER_REJECT,
                    3,
                    RtmMessageManager.PEER_MSG_TYPE_SEAT,
                    beanLinkUser.index,
                    object : ResultCallback<Any?> {
                        override fun onSuccess(o: Any?) {
                            this.log("拒绝成功")
                        }

                        override fun onFailure(errorInfo: ErrorInfo) {
                            this.log("拒绝失败")
                        }
                    })

                linkUsers.remove(beanLinkUser)
            }

            dialogLinkList?.notifyDataSetChanged()
//            dialogLinkList!!.dismiss()
            null
        }
        dialogLinkList!!.show(this)
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

    /**
     * tag==申请上麦/邀请上麦-观众列表
     *
     * @param position seat position
     * @param view     the view clicked
     */
    override fun onSeatAdapterHostInviteClicked(position: Int, view: View) {
        this.log("邀请上麦...$position")

        mInviteUserListActionSheet = showActionSheetDialog(
            ACTION_SHEET_INVITE_AUDIENCE, tabIdToLiveType(tabId), isHost, true, this
        ) as InviteUserActionSheet
        mInviteUserListActionSheet!!.setSeatNo(position)
        mInviteUserListActionSheet!!.append(inviteUser)
        mInviteUserListActionSheet!!.show(true)
//        onActionSheetAudienceInvited(0, "", "")
    }


    /**
     * 邀请用户列表
     */
    var inviteUser = ArrayList<AudienceListResponse.AudienceInfo>()

    /**
     * 观众列表
     */
    private fun requestAudienceList() {
        Live.audienceObjectList.clear()
        postDelayed(2000) {
            this.initVM(LiveViewModel::class.java).getUsers(rtcChannelName!!).obs(this) {
                it.y {
                    val users = it.audienceObjectList
                    Live.audienceObjectList = it.audienceObjectList
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

                    inviteUser.clear()

                    for (user in users) {
                        var aud = AudienceListResponse.AudienceInfo()
                        aud.avatar = user.avatar
                        aud.uid = user.agoraUserId
                        aud.userId = user.id
                        aud.userName = user.nickname
                        inviteUser.add(aud)
                    }



                    mInviteUserListActionSheet?.append(inviteUser)
                    mInviteUserListActionSheet?.notifyDataSetChanged()

                }
            }
        }


    }

    /**
     * tag==连麦-观众调用
     *
     * @param position seat position
     * @param view     the view clicked
     */
    override fun onSeatAdapterAudienceApplyClicked(position: Int, view: View) {}
    private fun audienceApplyForSeat(position: Int) {
        mSeatManager!!.apply(roomId, ownerId, position)
    }

    /**
     * 连麦申请
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
        //判断当前申请者是否在申请列表中,防止重复申请
        for (i in linkUsers.indices) {
            if (linkUsers[i].userId == userId) {
                return
            }
        }
        //将新的申请人添加到申请列表


        val user = BeanLinkUser()
        user.userId = userId
        user.uid = uid
        user.avatar = avatar
        user.nickName = userName
        user.index = seatId
        linkUsers.add(user)
        showLinkDialog()
        vibrate()
    }

    /**
     * 取消连麦
     */
    override fun onCancelLinked(
        userId: String,
        uid: Int,
        userName: String,
        avatar: String,
        index: Int
    ) {
        this.log("取消连麦......")
        for (i in linkUsers.indices) {
            if (userId == linkUsers[i].userId) {
                linkUsers.removeAt(i)
                break
            }
        }
        if (dialogLinkList != null && dialogLinkList!!.isAdded) {
            dialogLinkList!!.refresh(linkUsers)
        }
    }

    override fun onRtmApplicationAccepted(
        processId: Long,
        userId: String,
        userName: String,
        index: Int
    ) {
        showShortToast(resources.getString(R.string.apply_seat_success))
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
        this.log("邀请被接受-咨询师回调。。。。userId:$userId  index:$index")

        for (index in Live.seats.indices) {
            if (Live.seats[index].user.userId.isNullOrEmpty()) {
                Live.seats[index].seat.state = 1
                Live.seats[index].seat.no = index
                Live.seats[index].user.userId = userId
                Live.seats[index].user.uid = uid
                Live.seats[index].user.userName = userName
                Live.seats[index].user.enableVideo = 1
                Live.seats[index].user.enableAudio = 1
                break
            }
        }

        Live.sendSentMessage(this)

        refreshSeat()
        InviteUserActionSheet.inviting.remove(userId)
        mInviteUserListActionSheet?.append(inviteUser)
    }

    override fun onRtmApplicationRejected(
        processId: Long,
        userId: String,
        nickname: String,
        index: Int
    ) {
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
        refreshSeat()
        val title = resources.getString(R.string.live_room_host_in_invite_rejected)
        var message = resources.getString(R.string.live_room_host_in_invite_rejected_message)
        message = String.format(message, nickname)

        dialog(message).single(true).show(this)

        for (item in InviteUserActionSheet.inviting) {
            if (item == userId) {
                InviteUserActionSheet.inviting.remove(item)
                mInviteUserListActionSheet?.notifyDataSetChanged()
                break
            }
        }
    }

    override fun onRtmOwnerStateChanged(
        userId: String,
        userName: String,
        uid: Int,
        enableAudio: Int,
        enableVideo: Int
    ) {
        // The server notifies via rtm messages that the room owner has changed his state
        runOnUiThread {
            val audioMuted = enableAudio != SeatInfo.User.USER_AUDIO_ENABLE
            val videoMuted = enableVideo != SeatInfo.User.USER_VIDEO_ENABLE
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
        // The server notifies via rtm messages that seat states have changed
        Live.seats = list
        refreshSeat()
        mInviteUserListActionSheet?.append(inviteUser)
    }

    //音量控制
    override fun onRtcAudioVolumeIndication(
        speakers: Array<IRtcEngineEventHandler.AudioVolumeInfo>,
        totalVolume: Int
    ) {
        if (totalVolume <= 0) return
        runOnUiThread {
            for (info in speakers) {
                if (isOwner && info.uid == 0 || info.uid == ownerRtcUid) {
                    mOwnerUIManager!!.startVoiceIndicateAnim()
                }
                mSeatLayout!!.audioIndicate(speakers, config().userProfile.agoraUid.toInt())
            }
        }
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

    /**
     * Called when a seat needs a surface for video rendering, because someone
     * has become a broadcaster.
     * When I am an audience, or I am a host but this seat does not belong to
     * me, the surface should be created by rtc engine to render to as the
     * remote preview.
     * But when this is my seat, I need to provide a surface to render to
     * for myself as the local preview.
     *
     * @param position seat position starting from 0
     * @param uid      agora rtc uid used to register to rtc engine
     * @param mine     true if I am a host and this seat belongs to me; false otherwise
     */
    override fun onSeatAdapterItemVideoShowed(
        position: Int,
        uid: Int,
        mine: Boolean,
        audioMuted: Boolean,
        videoMuted: Boolean
    ): SurfaceView {
        if (this.uid == uid) {
            isLink = true
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
                // The host does not want to show his video, but still is a host.
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
                if (myUserId == item.userId) { //主播下麦观众
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
        val requestType = type
        val seatRequest = request

        dialog(message).y { downMic(position) }.show(this)
    }

    /**
     * 主播下麦用户
     */
    private fun downMic(position: Int) {
        this.log("下麦UserId:" + Live.seats[position].user.userId)
        this.log("用户列表:" + Gson().toJson(Live.seats))
        Live.sendPeer(
            this,
            Live.seats[position].user.userId,
            SeatInteraction.OWNER_FORCE_LEAVE,
            3,
            RtmMessageManager.PEER_MSG_TYPE_SEAT,
            position,
            object : ResultCallback<Any?> {
                override fun onSuccess(o: Any?) {
                    this.log("下麦通知成功")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    this.log("下麦通知失败" + Gson().toJson(errorInfo))
                }
            })
        Live.seats[position] = SeatStateMessageDataItem()
        refreshSeat()
        Live.sendSentMessage(this)
    }

    override fun onSeatAdapterPositionClosed(position: Int, view: View) {}

    /**
     * tag==邀请上麦-信令/邀请上麦列表中的邀请上麦事件回调
     *
     * @param seatId
     * @param userId
     * @param userName
     */
    override fun onActionSheetAudienceInvited(seatId: Int, userId: String, userName: String) {


        if (Live.linkedCount() + InviteUserActionSheet.inviting.size >= LiveMultiHostSeatLayout2.MAX_SEAT) {
            toast("已到邀请上线")
            return
        }
        InviteUserActionSheet.inviting.add(userId)
        refreshSeat()
        Live.sendPeer(
            this,
            userId,
            SeatInteraction.OWNER_INVITE,
            1,
            RtmMessageManager.PEER_MSG_TYPE_SEAT,
            seatId,
            object : ResultCallback<Any?> {
                override fun onSuccess(o: Any?) {
                    this.log("邀请成功userId：$userId")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    this.log("邀请失败userId：$userId")
                }
            })
//        dismissActionSheetDialog()
    }

    /**
     * 取消邀请
     */
    override fun onCancelAudienceInvited(seatId: Int, userId: String?, userName: String?) {
        refreshSeat()
        Live.sendPeer(
            this,
            userId,
            SeatInteraction.OWNER_CANCEL_INVITE,
            1,
            RtmMessageManager.PEER_MSG_TYPE_SEAT,
            seatId,
            object : ResultCallback<Any?> {
                override fun onSuccess(o: Any?) {
                    this.log("取消邀请成功userId：$userId")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    this.log("取消邀请失败userId：$userId")
                }
            })
//        dismissActionSheetDialog()
    }


    /**
     * tag==邀请上麦
     *
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmSeatInvited(userId: String, userName: String, index: Int) {

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
        // Hosts can only change their own state.
        // The room owner can modify his states and all
        // of the hosts' states in his room.
        val request = ModifyUserStateRequest(
            config().userProfile.token, roomId, userId,
            if (enableAudio) 1 else 0, if (enableVideo) 1 else 0, 1
        )
        sendRequest(Request.MODIFY_USER_STATE, request)
    }

    var boo = false

    @Subscribe(code = BusCode.LIVE_JOIN, threadMode = ThreadMode.MAIN)
    fun join(uid: String) {
        if (boo == false && isOwner == false) {
            ownerRtcUid = uid.toInt()
            isHost = true
            myRtcRole = Constants.CLIENT_ROLE_BROADCASTER
            rtcEngine().setClientRole(myRtcRole)
            this.log("收到消息...................................")
            mOwnerUIManager!!.rtcUid = uid.toInt()
            mOwnerUIManager!!.setOwner(false)
            mOwnerUIManager!!.setAudioMutedValue(false)
            mOwnerUIManager!!.setVideoMutedValue(false)
            boo = true
        }
    }


    /**
     * 更新座位
     */
    private fun refreshSeat() {
        requestAudienceList()
        runOnUiThread { mSeatLayout!!.updateStates(Live.seats) }
    }

    /**
     * 临时一对一使用
     */
    override fun stopLive() {
        this.log("下麦UserId:" + Live.seats[0].user.userId)
        Live.seats[0] = SeatStateMessageDataItem()
        refreshSeat()
        var liveDuration = System.currentTimeMillis() - startTime
        log("本次直播时长：$liveDuration")
        if (startTime > 0) {
            initVM(LiveViewModel::class.java).stop(rtcChannelName!!, liveDuration).obs(this) {
                it.y { log("结束直播状态修改成功") }
                it.n { log("结束直播状态修改失败") }
            }
        }
        Live.stopLiveMessage(this, object : ResultCallback<Any?> {
            override fun onSuccess(o: Any?) {
                this.log("发送成功。。。。")
                isLink = false

            }

            override fun onFailure(errorInfo: ErrorInfo) {
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        logout()
    }


    override fun onRtmMemberJoined(userId: String) {
        this.log("有观众加入....")
        Live.sendPeer(
            this,
            userId,
            SeatInteraction.ANCHOR_UID,
            0,
            RtmMessageManager.PEER_MSG_TYPE_SEAT,
            1,
            object : ResultCallback<Any?> {
                override fun onSuccess(o: Any?) {
                    log("发送主播UID成功")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    log("发送主播UID失败")
                }
            })

        requestAudienceList()

    }

    override fun onRtmMemberExitRoom(userId: String?) {

        for (item in Live.drawingUsers) {
            if (item.id == userId) {
                Live.drawingUsers.remove(item)
                break
            }
        }
        requestAudienceList()
    }

    /**
     * 用户接受画板
     */
    @Subscribe(code = BusCode.LIVE_DRAW_ACCEPT_INVITE)
    fun userAcceptDrawInvite(user: SeatInteractionMessage.SeatInteractionFromUser) {

        var audience = BeanAudience()
        audience.id = user.userId
        audience.nickname = user.userName
        Live.drawingUsers.add(audience)

//        dialog("${user.userName}接受了你的共享画板邀请").single(true).show(this)
        toast("${user.userName}接受了你的共享画板邀请")
    }

    /**
     * 用户拒绝画板
     */
    @Subscribe(code = BusCode.LIVE_DRAW_REJECT_INVITE)
    fun userRejectInvite(userName: String) {
//        dialog("${userName}拒绝了你的共享画板邀请").single(true).show(this)
        toast("${userName}拒绝了你的共享画板邀请")
    }

    @Subscribe(code = BusCode.LIVE_DRAW_SHOW_ZB)
    fun showDraw() {
        rtcChannelName?.let { DrawingBoard.from(this).show(it) }
    }

    companion object {
        private val ROOM_NAME_HINT_COLOR = Color.rgb(101, 101, 101)
        private val ROOM_NAME_COLOR = Color.rgb(235, 235, 235)
    }
}