package io.agora.vlive.ui.activity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.mtjk.base.obs
import com.mtjk.bean.BeanLiveData
import com.mtjk.utils.dialog
import com.mtjk.utils.initVM
import com.mtjk.utils.log
import com.mtjk.utils.toast
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.vlive.Config
import io.agora.vlive.Live
import io.agora.vlive.R
import io.agora.vlive.agora.rtm.model.GiftRankMessage.GiftRankItem
import io.agora.vlive.agora.rtm.model.NotificationMessage.NotificationItem
import io.agora.vlive.protocol.ClientProxy
import io.agora.vlive.protocol.model.model.UserProfile
import io.agora.vlive.protocol.model.request.Request
import io.agora.vlive.protocol.model.request.RoomRequest
import io.agora.vlive.protocol.model.request.SendGiftRequest
import io.agora.vlive.protocol.model.response.AudienceListResponse
import io.agora.vlive.protocol.model.response.CreateRoomResponse
import io.agora.vlive.protocol.model.response.EnterRoomResponse
import io.agora.vlive.protocol.model.response.EnterRoomResponse.RankInfo
import io.agora.vlive.protocol.model.response.Response
import io.agora.vlive.ui.actionsheets.BackgroundMusicActionSheet.BackgroundMusicActionSheetListener
import io.agora.vlive.ui.actionsheets.BeautySettingActionSheet.BeautyActionSheetListener
import io.agora.vlive.ui.actionsheets.GiftActionSheet.GiftActionSheetListener
import io.agora.vlive.ui.actionsheets.LiveRoomSettingActionSheet.LiveRoomSettingActionSheetListener
import io.agora.vlive.ui.actionsheets.LiveRoomUserListActionSheet
import io.agora.vlive.ui.actionsheets.LiveRoomUserListActionSheet.OnUserSelectedListener
import io.agora.vlive.ui.actionsheets.VoiceActionSheet.VoiceActionSheetListener
import io.agora.vlive.ui.actionsheets.toolactionsheet.LiveRoomToolActionSheet.LiveRoomToolActionSheetListener
import io.agora.vlive.ui.view.*
import io.agora.vlive.ui.view.LiveRoomUserLayout.UserLayoutListener
import io.agora.vlive.ui.view.bottomLayout.LiveBottomButtonLayout
import io.agora.vlive.ui.view.bottomLayout.LiveBottomButtonLayout.LiveBottomButtonListener
import io.agora.vlive.utils.GiftUtil
import io.agora.vlive.utils.Global
import io.agora.vlive.vm.RoomViewModel
import java.util.*

abstract class LiveRoomActivity : LiveBaseActivity(), BeautyActionSheetListener,
    LiveRoomSettingActionSheetListener,
    BackgroundMusicActionSheetListener, GiftActionSheetListener, LiveRoomToolActionSheetListener,
    VoiceActionSheetListener, LiveBottomButtonListener,
    TextView.OnEditorActionListener, UserLayoutListener, OnUserSelectedListener {
    private var mDecorViewRect: Rect? = null
    private var mInputMethodHeight = 0

    // UI components of a live room
    protected var participants: LiveRoomUserLayout? = null
    protected var messageList: LiveRoomMessageList? = null
    protected var bottomButtons: LiveBottomButtonLayout? = null
    protected var messageEditLayout: LiveMessageEditLayout? = null
    protected var messageEditText: AppCompatEditText? = null
    protected var rtcStatsView: RtcStatsView? = null
    protected var curDialog: Dialog? = null

    protected var dialog: DialogFragment? = null
    protected var inputMethodManager: InputMethodManager? = null
    private var mRoomUserActionSheet: LiveRoomUserListActionSheet? = null

    // Rtc Engine requires that the calls of startAudioMixing
    // should not be too frequent if online musics are played.
    // The interval is better not to be fewer than 100 ms.
    @Volatile
    private var mLastMusicPlayedTimeStamp: Long = 0
    private var mActivityFinished = false
    protected var inEarMonitorEnabled = false
    private var mHeadsetWithMicrophonePlugged = false
    var isLink = false
    private val mHeadPhoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (AudioManager.ACTION_HEADSET_PLUG == action) {
                val plugged = intent.getIntExtra("state", -1) == 1
                val hasMic = intent.getIntExtra("microphone", -1) == 1
                mHeadsetWithMicrophonePlugged = plugged && hasMic
                XLog.d("Wired headset is plugged：$mHeadsetWithMicrophonePlugged")
            }
        }
    }
    private val mNetworkReceiver = NetworkReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.viewTreeObserver
            .addOnGlobalLayoutListener { detectKeyboardLayout() }
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val headPhoneFilter = IntentFilter()
        headPhoneFilter.addAction(AudioManager.ACTION_HEADSET_PLUG)
        registerReceiver(mHeadPhoneReceiver, headPhoneFilter)
    }

    override fun onPermissionGranted() {
        if (intent.getBooleanExtra(Global.Constants.KEY_CREATE_ROOM, false)) {
//            createRoom();
            myCreateRoom()
        } else {
//            enterRoom(roomId);
            myEnterRoom()
        }
    }

    private fun myCreateRoom() {
        val beanData = BeanLiveData().get<BeanLiveData>()
        rtcChannelName = beanData!!.channelName
        roomId = ""
        joinRtcChannel()
        joinRtmChannel()
//        log("channelName:$rtcChannelName")

//        initUserCount(response.data.room.currentUsers,  response.data.room.rankUsers);
    }

    private fun myEnterRoom() {
        val beanData = BeanLiveData().get<BeanLiveData>()
        if (beanData == null) {
            finish()
            finish()
            return
        }
        rtcChannelName = beanData!!.channelName
        roomId = ""
        joinRtcChannel()
        joinRtmChannel()
        //
//        initUserCount(response.data.room.currentUsers,
//                response.data.room.rankUsers);
    }

    private fun detectKeyboardLayout() {
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        if (mDecorViewRect == null) {
            mDecorViewRect = rect
        }
        val diff = mDecorViewRect!!.height() - rect.height()

        // The global layout listener may be invoked several
        // times when the activity is launched, we need to care
        // about the value of detected input method height to
        // filter out the cases that are not desirable.
        if (diff == mInputMethodHeight) {
            // The input method is still shown
            return
        }
        if (diff > IDEAL_MIN_KEYBOARD_HEIGHT && mInputMethodHeight == 0) {
            mInputMethodHeight = diff
            onInputMethodToggle(true, diff)
        } else if (mInputMethodHeight > 0) {
            onInputMethodToggle(false, mInputMethodHeight)
            mInputMethodHeight = 0
        }
    }

    protected fun onInputMethodToggle(shown: Boolean, height: Int) {
        val params = messageEditLayout!!.layoutParams as RelativeLayout.LayoutParams
        val change = if (shown) height else -height
        params.bottomMargin += change
        messageEditLayout!!.layoutParams = params
        if (shown) {
            messageEditText!!.requestFocus()
            messageEditText!!.setOnEditorActionListener(this)
        } else {
            messageEditLayout!!.visibility = View.GONE
        }
    }

    /**
     * 创建房间
     */
    private fun createRoom() {}
    protected fun virtualImageIdToName(id: Int): String? {
        return when (id) {
            0 -> "dog"
            1 -> "girl"
            else -> null
        }
    }

    private val channelTypeByTabId: Int
        private get() {
            when (tabId) {
                Config.LIVE_TYPE_MULTI_HOST -> return ClientProxy.ROOM_TYPE_HOST_IN
                Config.LIVE_TYPE_PK_HOST -> return ClientProxy.ROOM_TYPE_PK
                Config.LIVE_TYPE_SINGLE_HOST -> return ClientProxy.ROOM_TYPE_SINGLE
                Config.LIVE_TYPE_VIRTUAL_HOST -> return ClientProxy.ROOM_TYPE_VIRTUAL_HOST
                Config.LIVE_TYPE_ECOMMERCE -> return ClientProxy.ROOM_TYPE_ECOMMERCE
            }
            return -1
        }

    override fun onCreateRoomResponse(response: CreateRoomResponse) {}

    /**
     * 进入房间
     *
     * @param roomId
     */
    protected fun enterRoom(roomId: String?) {
        val request = RoomRequest(config().userProfile.token, roomId)
        sendRequest(Request.ENTER_ROOM, request)
    }

    override fun onEnterRoomResponse(response: EnterRoomResponse) {
        this.log("进入房间成功：" + Gson().toJson(response))
        if (response.code == Response.SUCCESS) {
        }
    }

    private fun initUserCount(total: Int, rankUsers: List<RankInfo>) {
        runOnUiThread { participants!!.reset(total, rankUsers) }
    }

    override fun onActionSheetBeautyEnabled(enabled: Boolean) {
        if (bottomButtons != null) bottomButtons!!.setBeautyEnabled(enabled)
        enablePreProcess(enabled)
    }

    override fun onActionSheetBlurSelected(blur: Float) {
        setBlurValue(blur)
    }

    override fun onActionSheetWhitenSelected(whiten: Float) {
        setWhitenValue(whiten)
    }

    override fun onActionSheetCheekSelected(cheek: Float) {
        setCheekValue(cheek)
    }

    override fun onActionSheetEyeEnlargeSelected(eye: Float) {
        setEyeValue(eye)
    }

    override fun onActionSheetResolutionSelected(index: Int) {
        config().setResolutionIndex(index)
        setVideoConfiguration()
    }

    override fun onActionSheetFrameRateSelected(index: Int) {
        config().setFrameRateIndex(index)
        setVideoConfiguration()
    }

    override fun onActionSheetBitrateSelected(bitrate: Int) {
        config().setVideoBitrate(bitrate)
        setVideoConfiguration()
    }

    override fun onActionSheetSettingBackPressed() {
        dismissActionSheetDialog()
    }

    override fun onActionSheetMusicSelected(index: Int, name: String, url: String) {
        val now = System.currentTimeMillis()
        if (now - mLastMusicPlayedTimeStamp > MIN_ONLINE_MUSIC_INTERVAL) {
            rtcEngine().startAudioMixing(url, false, false, -1)
            if (bottomButtons != null) bottomButtons!!.setMusicPlaying(true)
            mLastMusicPlayedTimeStamp = now
        }
    }

    override fun onActionSheetMusicStopped() {
        rtcEngine().stopAudioMixing()
        if (bottomButtons != null) bottomButtons!!.setMusicPlaying(false)
    }

    override fun onActionSheetGiftSend(name: String, index: Int, value: Int) {
        dismissActionSheetDialog()
        val request = SendGiftRequest(config().userProfile.token, roomId, index)
        sendRequest(Request.SEND_GIFT, request)
    }

    /**
     * @param monitor the ideal monitoring state to be checked
     * @return true if the current audio route is wired or wire-less
     * headset with microphone, the audio route can be enabled.
     * Returns true if the state is allowed to be changed.
     */
    override fun onActionSheetEarMonitoringClicked(monitor: Boolean): Boolean {
        return if (monitor) {
            if (mHeadsetWithMicrophonePlugged) {
                rtcEngine().enableInEarMonitoring(true)
                inEarMonitorEnabled = true
                true
            } else {
                showShortToast(resources.getString(R.string.in_ear_monitoring_failed))
                // In ear monitor state does not change here.
                false
            }
        } else {
            rtcEngine().enableInEarMonitoring(false)
            // It is always allowed to disable the in-ear monitoring.
            inEarMonitorEnabled = false
            true
        }
    }

    override fun onActionSheetRealDataClicked() {
        if (rtcStatsView != null) {
            runOnUiThread {
                val visibility = rtcStatsView!!.visibility
                if (visibility == View.VISIBLE) {
                    rtcStatsView!!.visibility = View.GONE
                } else if (visibility == View.GONE) {
                    rtcStatsView!!.visibility = View.VISIBLE
                    rtcStatsView!!.setLocalStats(0f, 0f, 0f, 0f)
                }

                // Only clicking data button will dismiss
                // the action sheet dialog.
                dismissActionSheetDialog()
            }
        }
    }

    override fun onActionSheetSettingClicked() {
        showActionSheetDialog(ACTION_SHEET_VIDEO, tabIdToLiveType(tabId), isHost, false, this)
    }

    override fun onActionSheetRotateClicked() {
        switchCamera()
    }

    override fun onActionSheetVideoClicked(muted: Boolean) {
        if (isHost || isOwner) {
            rtcEngine().muteLocalVideoStream(muted)
            config().isVideoMuted = muted
        }
    }

    override fun onActionSheetSpeakerClicked(muted: Boolean) {
        if (isHost || isOwner) {
            rtcEngine().muteLocalAudioStream(muted)
            config().isAudioMuted = muted
        }
    }

    override fun onActionSheetAudioRouteSelected(type: Int) {}
    override fun onActionSheetAudioRouteEnabled(enabled: Boolean) {}
    override fun onActionSheetAudioBackPressed() {
        dismissActionSheetDialog()
    }

    override fun onLiveBottomLayoutShowMessageEditor() {
        if (messageEditLayout != null) {
            messageEditLayout!!.visibility = View.VISIBLE
            messageEditText!!.requestFocus()
            inputMethodManager!!.showSoftInput(messageEditText, 0)
        }
    }

    override fun onEditorAction(textView: TextView, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val editable = messageEditText!!.text
            if (TextUtils.isEmpty(editable)) {
                showShortToast(resources.getString(R.string.live_send_empty_message))
            } else {
                sendChatMessage(editable.toString())
                messageEditText!!.setText("")
            }
            inputMethodManager!!.hideSoftInputFromWindow(messageEditText!!.windowToken, 0)
            return true
        }
        return false
    }

    /**
     * tag==直播群聊
     * tag==直播聊天
     */
    private var content = ""
    private fun sendChatMessage(con: String) {
        content = con
        val profile = config().userProfile
        initVM(RoomViewModel::class.java).checkMessage(content).obs(this) {
            it.y {
                if (it) {
                    messageManager!!.sendChatMessage(profile.userId,
                        profile.userName, content, object : ResultCallback<Void?> {
                            override fun onSuccess(aVoid: Void?) {}
                            override fun onFailure(errorInfo: ErrorInfo) {}
                        })
                    messageList!!.addMessage(
                        LiveRoomMessageList.MSG_TYPE_CHAT,
                        profile.userName,
                        content
                    )
                } else {
                    toast("发送失败，存在敏感词")
                }

            }
        }


    }

    protected val isCurDialogShowing: Boolean
        protected get() = curDialog != null && curDialog!!.isShowing

    protected fun closeDialog() {
        if (isCurDialogShowing) {
            curDialog!!.dismiss()
        }
    }

    override fun onUserLayoutShowUserList(view: View) {
        // Show all user info list
        mRoomUserActionSheet =
            showActionSheetDialog(
                ACTION_SHEET_ROOM_USER,
                tabIdToLiveType(tabId),
                isHost,
                true,
                this
            ) as LiveRoomUserListActionSheet
        mRoomUserActionSheet!!.setup(proxy(), this, roomId, config().userProfile.token)
        mRoomUserActionSheet!!.requestMoreAudience()
    }

    override fun onAudienceListResponse(response: AudienceListResponse) {
        val userList: MutableList<UserProfile> = ArrayList()
        for (info in response.data.list) {
            val profile = UserProfile()
            profile.userId = info.userId
            profile.userName = info.userName
            profile.avatar = info.avatar
            userList.add(profile)
        }
        if (mRoomUserActionSheet != null && mRoomUserActionSheet!!.visibility == View.VISIBLE) {
            runOnUiThread { mRoomUserActionSheet!!.appendUsers(userList) }
        }
    }

    override fun onActionSheetUserListItemSelected(userId: String, userName: String) {
        // Called when clicking an online user's name, and want to see the detail
    }

    override fun onRtmChannelMessageReceived(peerId: String, nickname: String, content: String) {
        runOnUiThread {
            messageList!!.addMessage(
                LiveRoomMessageList.MSG_TYPE_CHAT,
                nickname,
                content
            )
        }
    }

    override fun onRtmRoomGiftRankChanged(total: Int, list: List<GiftRankItem>) {
        // The rank of user sending gifts has changed. The client
        // needs to update UI in this callback.
        if (list == null) return
        val rankList: MutableList<RankInfo> = ArrayList()
        for (item in list) {
            val info = RankInfo()
            info.userId = item.userId
            info.userName = item.userName
            info.avatar = item.avatar
            rankList.add(info)
        }
        runOnUiThread { participants!!.reset(rankList) }
    }

    override fun onRtmGiftMessage(
        fromUserId: String,
        fromUserName: String,
        toUserId: String,
        toUserName: String,
        giftId: Int
    ) {
        runOnUiThread {
            val from = if (TextUtils.isEmpty(fromUserName)) fromUserId else fromUserName
            val to = if (TextUtils.isEmpty(toUserName)) toUserId else toUserName
            messageList!!.addMessage(LiveRoomMessageList.MSG_TYPE_GIFT, from, to, giftId)
            val window = GiftAnimWindow(this@LiveRoomActivity, R.style.gift_anim_window)
            window.setAnimResource(GiftUtil.getGiftAnimRes(giftId))
            window.show()
        }
    }

    override fun onRtmChannelNotification(total: Int, list: List<NotificationItem>) {
        // User enter & leave notifications.
        runOnUiThread {

            // update room user count
            participants!!.reset(total)
            for (item in list) {
                messageList!!.addMessage(
                    LiveRoomMessageList.MSG_TYPE_SYSTEM,
                    item.userName,
                    "",
                    item.state
                )
            }
        }
    }

    override fun onRtmLeaveMessage() {
        this.toast("主播已退出直播")
        runOnUiThread { this.leaveRoom() }
    }

    override fun onStart() {
        super.onStart()
        if ((isOwner || isHost) && !config().isVideoMuted) {
            startCameraCapture()
        }
    }

    override fun onRtcRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {

    }

    override fun onRtcStats(stats: IRtcEngineEventHandler.RtcStats) {
        runOnUiThread {
            if (rtcStatsView != null && rtcStatsView!!.visibility == View.VISIBLE) {
                rtcStatsView!!.setLocalStats(
                    stats.rxKBitRate.toFloat(),
                    stats.rxPacketLossRate.toFloat(), stats.txKBitRate.toFloat(),
                    stats.txPacketLossRate.toFloat()
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if ((isHost || isOwner) && !config().isVideoMuted
            && !mActivityFinished
        ) {
            // If now the app goes to background, stop the camera
            // capture if the host is displaying his video.
            stopCameraCapture()
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    private fun showExitDialog() {
        var message = ""
        message = if (isHost || isOwner) {
            "是否结束直播"
        } else {
            "是否离开直播"
        }
        this.dialog("是否离开直播").y("确定") {
            leaveRoom()
            null
        }.show(this)
    }

    open fun userLiveRoom() {}
    open fun stopLive() {}
    var waitLink = false
    protected fun leaveRoom() {
        Live.sendRtmUserExitRoomMessage(this, object : ResultCallback<Any?> {
            override fun onSuccess(o: Any?) {}
            override fun onFailure(errorInfo: ErrorInfo) {}
        })
        if (isHost || isOwner) {
            stopLive()
        }

        if(this is ZhuBoActivity2||this is ZhuBoActivity){
            Live.sendMessage(this,"咨询师离开")
        }else{
            Live.sendMessage(this,"用户离开")
        }

        if (waitLink) {
            cancelWaitLink()
        }
        if (isLink) {
            userLiveRoom()
            finish()
            closeDialog()
            dismissActionSheetDialog()
        } else {
            finish()
            closeDialog()
            dismissActionSheetDialog()
        }
    }

    protected open fun cancelWaitLink() {}
    protected fun leaveRoom(roomId: String?) {}
    override fun finish() {
        super.finish()
        mActivityFinished = true
        stopCameraCapture()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mHeadPhoneReceiver)
    }

    override fun onResponseError(requestType: Int, error: Int, message: String) {
        XLog.e("request:$requestType error:$error msg:$message")
        runOnUiThread {
            showLongToast(
                "request type: " +
                        Request.getRequestString(requestType) + " " + message
            )
        }
    }

    public override fun onResume() {
        super.onResume()
        val filter = IntentFilter(
            ConnectivityManager.CONNECTIVITY_ACTION
        )
        registerReceiver(mNetworkReceiver, filter)
    }


    public override fun onPause() {
        super.onPause()
        unregisterReceiver(mNetworkReceiver)
    }

    protected class NetworkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return
            val info = cm.activeNetworkInfo
            if (info == null || !info.isAvailable || !info.isConnected) {
                Toast.makeText(context, R.string.network_unavailable, Toast.LENGTH_SHORT).show()
            } else {
                val type = info.type
                if (ConnectivityManager.TYPE_WIFI == type) {
                    Toast.makeText(context, R.string.network_switch_to_wifi, Toast.LENGTH_SHORT)
                        .show()
                } else if (ConnectivityManager.TYPE_MOBILE == type) {
                    Toast.makeText(context, R.string.network_switch_to_mobile, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    companion object {
        private val TAG = LiveRoomActivity::class.java.simpleName
        private const val IDEAL_MIN_KEYBOARD_HEIGHT = 200
        private const val MIN_ONLINE_MUSIC_INTERVAL = 100
    }
}