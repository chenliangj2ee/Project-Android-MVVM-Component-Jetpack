package io.agora.vlive.ui.activity

import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import io.agora.capture.video.camera.CameraVideoChannel
import io.agora.capture.video.camera.VideoModule
import io.agora.framework.modules.channels.ChannelManager
import io.agora.vlive.Config
import io.agora.vlive.R
import io.agora.vlive.agora.rtm.model.SeatStateMessage
import io.agora.vlive.bean.BeanParam
import io.agora.vlive.bean.BeanParam.LiveType.AUDIO_MORE
import io.agora.vlive.bean.BeanParam.LiveType.AUDIO_ONE
import io.agora.vlive.bean.BeanParam.LiveType.VIDEO_MORE
import io.agora.vlive.bean.BeanParam.LiveType.VIDEO_ONE
import io.agora.vlive.ui.activity.ZhuBoActivity
import io.agora.vlive.ui.view.CameraTextureView

/**
 * tag==主播准备
 */
class LivePrepareActivity : LiveBaseActivity() {
    private var startButton: AppCompatTextView? = null
    private var mLocalPreviewLayout: FrameLayout? = null
    private var mCameraChannel: CameraVideoChannel? = null
    private var mActivityFinished = false
    private var mPermissionGranted = false

    override fun onGlobalLayoutCompleted() {}
    override fun onRtcJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
    }

    override fun onRtmMemberExitRoom(userId: String?) {

    }

    override fun onCancelRtmSeatInvited() {

    }

    override fun onCancelLinked(
        userId: String?,
        uid: Int,
        userName: String?,
        avatar: String?,
        index: Int
    ) {

    }

    override fun onAnchorUidResponse(
        uid: Int,
        seats: MutableList<SeatStateMessage.SeatStateMessageDataItem>?
    ) {

    }

    val liveParam = BeanParam().get<BeanParam>()
    private fun initUI() {
        hideStatusBar(false)
        setContentView(R.layout.activity_live_prepare)
        startButton = findViewById(R.id.start)

        if (liveParam!!.liveType == AUDIO_ONE) {
            val intent = Intent(this@LivePrepareActivity, ZhuBoActivity::class.java)
            if (getIntent().extras != null) {
                intent.putExtras(getIntent().extras!!)
            }
            startActivity(intent)
            finish()
            return
        }

        startButton?.setOnClickListener(View.OnClickListener {

            if (liveParam!!.liveType == VIDEO_MORE || liveParam!!.liveType == AUDIO_MORE) {
                val intent = Intent(this@LivePrepareActivity, ZhuBoActivity2::class.java)
                if (getIntent().extras != null) {
                    intent.putExtras(getIntent().extras!!)
                }
                startActivity(intent)
            }

            if (liveParam!!.liveType == VIDEO_ONE || liveParam!!.liveType == AUDIO_ONE) {
                val intent = Intent(this@LivePrepareActivity, ZhuBoActivity::class.java)
                if (getIntent().extras != null) {
                    intent.putExtras(getIntent().extras!!)
                }
                startActivity(intent)
            }



            finish()
        })
        VideoModule.instance().init(this)
        mCameraChannel = VideoModule.instance()
            .getVideoChannel(ChannelManager.ChannelID.CAMERA) as CameraVideoChannel?
        mLocalPreviewLayout = findViewById(R.id.local_preview_layout)
        changeUIStyles()


    }

    private fun changeUIStyles() {
        if (tabId == Config.LIVE_TYPE_VIRTUAL_HOST) {
            hideStatusBar(true)
            config().isBeautyEnabled = true
            startCameraCapture()
        } else {
            hideStatusBar(false)
            startCameraCapture()
            mLocalPreviewLayout!!.addView(CameraTextureView(this))
        }
    }

    override fun onPermissionGranted() {
        mPermissionGranted = true
        initUI()
    }

    override fun onStart() {
        super.onStart()
        if (mPermissionGranted) {
            startCameraCapture()
        }
    }


    override fun onStop() {
        super.onStop()
        if (mCameraChannel != null && !mActivityFinished
            && mCameraChannel!!.hasCaptureStarted()
        ) {
            mCameraChannel!!.stopCapture()
        }

    }

    override fun onRtmInvitationAccepted(
        processId: Long,
        userId: String?,
        userName: String?,
        userAvatar:String?,
        uid: Int,
        index: Int
    ) {
    }

    override fun finish() {
        super.finish()
        mActivityFinished = true
        if (mCameraChannel != null
            && mCameraChannel!!.hasCaptureStarted()
        ) {
            mCameraChannel!!.stopCapture()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopCameraCapture()
        logout()
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}
    override fun startLiveSuccess() {}
}