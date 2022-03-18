package io.agora.vlive.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceView
import android.view.TextureView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mtjk.base.obs
import com.mtjk.utils.getBeanUser
import com.mtjk.utils.initVM
import com.mtjk.utils.log
import com.mtjk.utils.toast
import gorden.rxbus2.RxBus
import io.agora.capture.video.camera.CameraManager
import io.agora.capture.video.camera.VideoModule
import io.agora.framework.RtcVideoConsumer
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmChannelAttribute
import io.agora.rtm.RtmChannelMember
import io.agora.vlive.Config
import io.agora.vlive.Live
import io.agora.vlive.R
import io.agora.vlive.agora.rtc.RtcEventHandler
import io.agora.vlive.agora.rtm.RtmMessageListener
import io.agora.vlive.agora.rtm.RtmMessageManager
import io.agora.vlive.agora.rtm.model.GiftRankMessage.GiftRankItem
import io.agora.vlive.agora.rtm.model.NotificationMessage.NotificationItem
import io.agora.vlive.agora.rtm.model.PKStateMessage.PKStateMessageBody
import io.agora.vlive.agora.rtm.model.SeatStateMessage.SeatStateMessageDataItem
import io.agora.vlive.ui.BaseActivity
import io.agora.vlive.utils.Global
import io.agora.vlive.vm.RoomViewModel

/**
 * Common capabilities of a live room. Such as, camera capture，
 * , agora rtc, messaging, permission check, communication with
 * the back-end server, and so on.
 */
abstract class LiveBaseActivity : BaseActivity(), RtcEventHandler, RtmMessageListener {
    // values of a live room
    var roomName: String? = null
    var roomId: String? = null
    var isOwner = false
    var ownerId: String? = null
    var isHost = false
    var myRtcRole = 0
    var ownerRtcUid = 0
    var tabId = 0
    var uid = getBeanUser()!!.uid

    // Current rtc channel generated by server
    // and obtained when entering the room.
    public var rtcChannelName: String? = null
    var messageManager: RtmMessageManager? = null
    private var mCameraVideoManager: CameraManager? = null


    var startTime: Long = 0
    private var startEnd: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keepScreenOn(window)
        checkPermissions()
        RxBus.get().register(this)
    }

    protected fun checkPermissions() {
        if (!permissionArrayGranted()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ)
        } else {
            performInit()
        }
    }

    private fun permissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun permissionArrayGranted(): Boolean {
        var granted = true
        for (per in PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false
                break
            }
        }
        return granted
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQ) {
            if (permissionArrayGranted()) {
                performInit()
            } else {
                toast("应用未给予必要权限")
                finish()
            }
        }
    }

    private fun performInit() {
        initRoom()
        onPermissionGranted()
    }

    /**
     * 初始化房间
     */
    private fun initRoom() {
        registerRtcHandler(this)
        val intent = intent
        roomName = intent.getStringExtra(Global.Constants.KEY_ROOM_NAME) + ""
        roomId = intent.getStringExtra(Global.Constants.KEY_ROOM_ID) + ""
        isOwner = intent.getBooleanExtra(Global.Constants.KEY_IS_ROOM_OWNER, false)
        ownerId = intent.getStringExtra(Global.Constants.KEY_ROOM_OWNER_ID) + ""
        isHost = isOwner
        myRtcRole =
            if (isOwner) Constants.CLIENT_ROLE_BROADCASTER else Constants.CLIENT_ROLE_AUDIENCE
        tabId = intent.getIntExtra(Global.Constants.TAB_KEY, 0)
        messageManager = RtmMessageManager.instance()
        messageManager?.init(rtmClient())
        messageManager?.registerMessageHandler(this)
        messageManager?.setCallbackThread(Handler(mainLooper))
        initCameraIfNeeded()
    }

    private fun initCameraIfNeeded() {
        if (mCameraVideoManager == null) {
            mCameraVideoManager = cameraVideoManager()
        }
        if (mCameraVideoManager != null) {
            mCameraVideoManager!!.enablePreprocessor(
                config().isBeautyEnabled
            )
        }
        if (mCameraVideoManager != null) mCameraVideoManager!!.preprocessor
    }

    protected abstract fun onPermissionGranted()
    override fun onStop() {
        super.onStop()
        removeRtcHandler(this)
    }

    protected fun joinRtcChannel() {
        rtcEngine().setClientRole(myRtcRole)
        rtcEngine().setVideoSource(RtcVideoConsumer(VideoModule.instance()))
        setVideoConfiguration()

//        log("加入直播RTCTOKEN:${config().userProfile.rtcToken}")
        rtcEngine().joinChannelWithUserAccount(
            config().userProfile.rtcToken,
            rtcChannelName,
            config().userProfile.userId
        )

    }

    protected fun setupRemoteVideo(uid: Int): SurfaceView {
//        this.log("setupRemoteVideo:uid$uid")
        val surfaceView = RtcEngine.CreateRendererView(this)
        rtcEngine().setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        return surfaceView
    }

    protected fun removeRemoteVideo(uid: Int) {
        rtcEngine().setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }

    protected fun setVideoConfiguration() {
        rtcEngine().setVideoEncoderConfiguration(
            config().createVideoEncoderConfig(
                tabIdToLiveType(
                    tabId
                )
            )
        )
    }

    protected fun tabIdToLiveType(tabId: Int): Int {
        return if (tabId == Config.LIVE_TYPE_MULTI_HOST || tabId == Config.LIVE_TYPE_SINGLE_HOST || tabId == Config.LIVE_TYPE_PK_HOST || tabId == Config.LIVE_TYPE_VIRTUAL_HOST || tabId == Config.LIVE_TYPE_ECOMMERCE
        ) {
            tabId
        } else 0
    }

    protected fun startCameraCapture() {
        initCameraIfNeeded()
        if (mCameraVideoManager != null) {
            enablePreProcess(config().isBeautyEnabled)
            mCameraVideoManager!!.startCapture()
        }
    }

    protected fun setLocalPreview(surfaceView: SurfaceView?) {
        initCameraIfNeeded()
        if (mCameraVideoManager != null) {
            mCameraVideoManager!!.setLocalPreview(surfaceView)
        }
    }

    protected fun setLocalPreview(textureView: TextureView?) {
        initCameraIfNeeded()
        if (mCameraVideoManager != null) {
            mCameraVideoManager!!.setLocalPreview(textureView)
        }
    }

    protected fun switchCamera() {
        initCameraIfNeeded()
        if (mCameraVideoManager != null) {
            mCameraVideoManager!!.switchCamera()
        }
    }

    protected fun stopCameraCapture() {
        initCameraIfNeeded()
        if (mCameraVideoManager != null) {
            mCameraVideoManager!!.stopCapture()
        }
    }

    protected fun enablePreProcess(enabled: Boolean) {
        initCameraIfNeeded()
        if (mCameraVideoManager != null) {
            mCameraVideoManager!!.enablePreprocessor(enabled)
        }
    }

    protected fun setBlurValue(blur: Float) {
        initCameraIfNeeded()
    }

    protected fun setWhitenValue(whiten: Float) {
        initCameraIfNeeded()
    }

    protected fun setCheekValue(cheek: Float) {
        initCameraIfNeeded()
    }

    protected fun setEyeValue(eye: Float) {
        initCameraIfNeeded()
    }

    protected fun joinRtmChannel() {
//        this.log("进入房间用户id：" + config().userProfile.userId)
//        this.log("进入房间用户rtcChannelName：$rtcChannelName")
        messageManager!!.joinChannel(rtcChannelName, object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                this.log("进入聊天房间成功")

                if(this@LiveBaseActivity is ZhuBoActivity2||this@LiveBaseActivity is ZhuBoActivity){
                    Live.sendMessage(this@LiveBaseActivity,"咨询师进入")
                }else{
                    Live.sendMessage(this@LiveBaseActivity,"用户进入")
                }


                Live.sendRtmUserChangeMessage(this@LiveBaseActivity, object : ResultCallback<Any?> {
                    override fun onSuccess(o: Any?) {}
                    override fun onFailure(errorInfo: ErrorInfo) {}
                })
                //                MyFunctionKt.toast(this,"进入聊天房间成功");
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                this.log("进入聊天房间失败:$errorInfo")
                //                MyFunctionKt.toast(this,"进入聊天房间失败");
            }
        })
    }

    protected fun leaveRtmChannel(callback: ResultCallback<Void?>?) {
        messageManager!!.leaveChannel(callback)
    }

    override fun onRtmConnectionStateChanged(state: Int, reason: Int) {}
    override fun onRtmTokenExpired() {}
    override fun onRtmPeersOnlineStatusChanged(map: Map<String, Int>) {}
    override fun onRtmMemberCountUpdated(memberCount: Int) {}
    override fun onRtmAttributesUpdated(attributeList: List<RtmChannelAttribute>) {}
    override fun onRtmMemberJoined(userId: String) {}
    override fun onRtmMemberLeft(rtmChannelMember: RtmChannelMember) {}
    override fun onRtmSeatInvited(userId: String, userName: String, index: Int) {}

    /**
     * 申请上麦通知
     *
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmSeatApplied(
        userId: String,
        uid: Int,
        userName: String,
        avatar: String,
        index: Int
    ) {
        this.log("申请上麦......")
    }

    /**
     * 连麦接受通知
     *
     * @param processId
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmInvitationAccepted(
        processId: Long, userId: String, userName: String,
        index: Int
    ) {
        this.log("连麦接受通知......")
    }

    /**
     * 连麦邀请通知
     *
     * @param processId
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmApplicationAccepted(
        processId: Long, userId: String, userName: String,
        index: Int
    ) {
        this.log("连麦邀请通知......")
    }

    /**
     * 邀请拒绝通知
     *
     * @param processId
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmInvitationRejected(
        processId: Long, userId: String, userName: String,
        index: Int
    ) {
        this.log("连麦拒绝通知......")
    }

    /**
     * 申请连麦拒绝通知
     *
     * @param processId
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmApplicationRejected(
        processId: Long, userId: String, userName: String,
        index: Int
    ) {
        this.log("申请连麦拒绝通知......")
    }

    /**
     * 主播离场通知
     *
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmOwnerForceLeaveSeat(userId: String, userName: String, index: Int) {
        this.log(" 主播离场通知......")
    }

    public fun logout() {
        rtmClient().logout(object : ResultCallback<Void?> {
            override fun onSuccess(unused: Void?) {
                this.log("直播RTC退出成功...")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                this.log("直播RTC退出失败...")
            }
        })
    }

    /**
     * 主机离开座位
     *
     * @param userId
     * @param userName
     * @param index
     */
    override fun onRtmHostLeaveSeat(userId: String, userName: String, index: Int) {
        this.log(" 主机离开座位......userId:$userId")
    }

    override fun onRtmPkReceivedFromAnotherHost(
        userId: String,
        userName: String,
        pkRoomId: String
    ) {
    }

    override fun onRtmPkAcceptedByTargetHost(userId: String, userName: String, pkRoomId: String) {}
    override fun onRtmPkRejectedByTargetHost(userId: String, userName: String, pkRoomId: String) {}

    /**
     * 收到消息通知
     *
     * @param peerId
     * @param nickname
     * @param content
     */
    override fun onRtmChannelMessageReceived(peerId: String, nickname: String, content: String) {
        this.log(" 收到消息通知......")
    }

    override fun onRtmRoomGiftRankChanged(total: Int, list: List<GiftRankItem>) {

    }

    override fun onRtmOwnerStateChanged(
        userId: String, userName: String, uid: Int, enableAudio: Int,
        enableVideo: Int
    ) {
    }

    override fun onRtmSeatStateChanged(data: List<SeatStateMessageDataItem>) {}
    override fun onRtmReceivePKEvent(messageData: PKStateMessageBody) {}
    override fun onRtmGiftMessage(
        fromUserId: String,
        fromUserName: String,
        toUserId: String,
        toUserName: String,
        giftId: Int
    ) {
    }

    override fun onRtmChannelNotification(total: Int, list: List<NotificationItem>) {
        this.log(" onRtmChannelNotification......")
    }

    override fun onRtmProductPurchased(productId: String, count: Int) {}
    override fun onRtmProductStateChanged(productId: String, state: Int) {}
    override fun onRtmLeaveMessage() {
        this.log("下麦通知......")
    }


    open abstract fun startLiveSuccess()
    override fun onRtcRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        this.log("onRtcRemoteVideoStateChanged......")
    }

    override fun onRtcStats(stats: IRtcEngineEventHandler.RtcStats) {}
    override fun onRtcChannelMediaRelayStateChanged(state: Int, code: Int) {}
    override fun onRtcChannelMediaRelayEvent(code: Int) {}
    override fun onRtcAudioVolumeIndication(
        speakers: Array<IRtcEngineEventHandler.AudioVolumeInfo>,
        totalVolume: Int
    ) {
    }

    override fun onRtcAudioRouteChanged(routing: Int) {}
    public override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unRegister(this)
    }

    override fun finish() {
        super.finish()
        rtcEngine().leaveChannel()
        if (messageManager != null) {
            messageManager!!.removeMessageHandler(this)
            messageManager!!.leaveChannel(object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {}
                override fun onFailure(errorInfo: ErrorInfo) {}
            })
        }
    }


    companion object {
        protected val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val PERMISSION_REQ = 1
    }
}