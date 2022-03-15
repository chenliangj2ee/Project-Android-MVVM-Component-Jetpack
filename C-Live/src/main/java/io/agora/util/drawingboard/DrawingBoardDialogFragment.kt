package io.agora.util.drawingboard

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import com.mtjk.BaseInit.isUserApp
import io.agora.vlive.dialog.LiveMoreDialog.Status.showDraw
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import io.agora.util.drawingboard.DrawingPencilSelector.DrawingPencilCallback
import com.herewhite.sdk.Room
import com.herewhite.sdk.WhiteSdkConfiguration
import com.herewhite.sdk.RoomParams
import com.herewhite.sdk.WhiteSdk
import io.agora.vlive.Live
import io.agora.vlive.ui.activity.LiveBaseActivity
import cn.bingoogolapple.transformerstip.gravity.TipGravity
import com.herewhite.sdk.domain.*
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe
import io.agora.vlive.R
import io.agora.vlive.databinding.DialogDrawingBoardBinding
import java.lang.Exception
import java.util.HashMap

/*
 * tag==画板
 * */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DrawingBoardDialogFragment : MyBaseDialog<DialogDrawingBoardBinding>(), DrawingPencilCallback {

    private var mChannelId: String? = null

    //private var mAnimStyle = R.style.DrawingBoardDialogAnimation
    private var mRoom: Room? = null
    private var mMemberState: MemberState? = null
    private var mCurrentColor = 0
    private var mCurrentLine = -1.0
    private var mDisableOperation = false
    private var mPencilSelector: DrawingPencilSelector? = null

    companion object {
        private const val TAG = "DrawingBoardDialog"
        const val PARAM_CHANNEL_ID_KEY = "channel_id"
        const val PARAM_DISABLE_OPERATION_KEY = "disable_operation"

        //互动白板 App Identifier
        private const val APPID = "9WuuEJg7Eey8pE1n5lnzTw/1mXhZlYFfgnTKg"

        @JvmStatic
        fun newInstance(roomId: String, disableOperation: Boolean): DrawingBoardDialogFragment {
            val fragment = DrawingBoardDialogFragment()
            val args = Bundle()
            args.putString(PARAM_CHANNEL_ID_KEY, roomId)
            args.putBoolean(PARAM_DISABLE_OPERATION_KEY, disableOperation)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initCreate() {
        cancelable(false)
        val args = arguments
        if (args != null) {
            mChannelId = args.getString(PARAM_CHANNEL_ID_KEY)
            mDisableOperation = args.getBoolean(PARAM_DISABLE_OPERATION_KEY)
        }
        initView()
        initData()
    }

    private fun initData() {
        if (!TextUtils.isEmpty(mChannelId)) {
            this.initVM(DrawingBoardViewModel::class.java).getRoomToken(mChannelId!!).observe(this) { result ->
                if (result?.data != null && result.data is BeanDrawingBoardRoom) {
                    if (isResumed) {
                        initWhiteBoard(result.data!!.roomUuid, result.data!!.token)
                    }
                }
            }
        }
    }

    private fun initWhiteBoard(uuid: String, roomToken: String) {
        if (!uuid.isNullOrEmpty() && !roomToken.isNullOrEmpty()) {
            // 创建 WhiteSdkConfiguration 对象，设置白板的 App Identifier 和日志参数
            val sdkConfiguration = WhiteSdkConfiguration(APPID, true)
            // 设置数据中心为中国杭州
            sdkConfiguration.region = Region.cn
            //设置显示用户头像
            sdkConfiguration.isUserCursor = true
            // 创建 RoomParams 对象，设置房间参数，用于加入房间。如果你使用 2.15.0 之前版本的 SDK，不需要传 uid 参数。
            val roomParams = RoomParams(uuid, roomToken)
            if (mDisableOperation) {
                roomParams.isDisableDeviceInputs = true
            }
            //画图显示的头像
            val payLoad: MutableMap<String, String> = HashMap()
            payLoad["avatar"] = this.getBeanUser()!!.avatar
            roomParams.userPayload = payLoad

            // 创建 WhiteSdk 对象，用于初始化白板 SDK
            val whiteSdk = WhiteSdk(mBinding.whiteBoard, context, sdkConfiguration)
            // 加入房间
            whiteSdk.joinRoom(roomParams, object : Promise<Room?> {
                override fun then(wRoom: Room?) {
                    mRoom = wRoom
                    setMemberState(Appliance.PENCIL, 0, -1.0)
                }

                override fun catchEx(t: SDKError) {
                    val o: Any? = t.message
                    Log.d(TAG, o.toString())
                }
            })
        }
    }

    private fun initView() {
        if (mDisableOperation) {
            mBinding.tools.visibility = View.GONE
            mBinding.line.visibility = View.INVISIBLE
        } else {
            mBinding.pencil.isSelected = true
            mBinding.pencil.click { clickPencil() }
            mBinding.eraser.click { clickEraser() }
            mBinding.clear.click { clickClear() }
        }
        mBinding.done.click { clickDone() }
    }

    private fun setMemberState(tool: String, color: Int, strokeWidth: Double) {
        if (mRoom == null || tool.isNullOrEmpty()) {
            return
        }
        if (mMemberState == null) {
            mMemberState = MemberState()
        }
        //设置教具
        mMemberState!!.currentApplianceName = tool
        //教具颜色
        setPencilColor(mMemberState, color)
        //教具线条宽度
        setPencilLine(mMemberState, strokeWidth)
        // 设置当前用户教具
        mRoom!!.memberState = mMemberState
    }

    private fun setPencilColor(tool: MemberState?, color: Int) {
        if (color >= 0) {
            tool?.strokeColor = intArrayOf(
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        }
    }

    private fun setPencilLine(tool: MemberState?, strokeWidth: Double) {
        if (strokeWidth >= 0) {
            tool?.strokeWidth = strokeWidth
        }
    }

    private fun cleanScence(retain: Boolean) {
        mRoom?.cleanScene(retain)
    }

//    @SuppressLint("ResourceType")
//    fun setAnimationStyle(@StyleRes resId: Int) {
//        mAnimStyle = if (resId <= 0) mAnimStyle else resId
//    }

    override fun onDestroy() {
        mMemberState = null
        mRoom = null
        mBinding.whiteBoard?.removeAllViews()
        mBinding.whiteBoard?.destroy()
        super.onDestroy()
    }

    private fun clickDone() {
        this.dialog("是否结束共享").y {
            if (isUserApp) {
                showDraw = false
            } else {
                Live.drawingUsers.clear()
                Live.drawUserIds.clear()
                Live.sendDrawCloseMessage(activity as LiveBaseActivity?)
            }
            dismiss()
            null
        }.show(this)
    }

    private fun clickPencil() {
        if (mPencilSelector == null) {
            mPencilSelector =
                DrawingPencilSelector(mBinding.tools, R.layout.drawing_board_pencil_selector)
            mPencilSelector!!.initData()
            mPencilSelector!!.setTipGravity(TipGravity.TO_BOTTOM_CENTER)
                .setTipOffsetXDp(0)
                .setTipOffsetYDp(2)
                .setBackgroundDimEnabled(false)
                .setDismissOnTouchOutside(true)
            mPencilSelector!!.setDrawingPencilCallback(this)
            mPencilSelector!!.setOnDismissListener {
                setMemberState(Appliance.PENCIL, mCurrentColor, mCurrentLine)
                mBinding.pencil?.isSelected = true
                mBinding.eraser?.isSelected = false
            }
        }
        if (mPencilSelector!!.isShowing) {
            mPencilSelector!!.dismissTip()
        } else {
            mPencilSelector!!.show()
        }
        mBinding.pencil?.isSelected = true
        mBinding.eraser?.isSelected = false
    }

    private fun clickEraser() {
        setMemberState(Appliance.ERASER, -1, -1.0)
        mBinding.eraser?.isSelected = true
        mBinding.pencil?.isSelected = false
    }

    private fun clickClear() {
        cleanScence(false)
        setMemberState(Appliance.PENCIL, -1, -1.0)
        mBinding.eraser?.isSelected = false
        mBinding.pencil?.isSelected = true
    }

    override fun onLineChanged(value: Double) {
        mCurrentLine = value
    }

    override fun onColorChanged(value: Int) {
        mCurrentColor = value
    }

    @Subscribe(code = BusCode.LIVE_DRAW_CLOSE_ALL)
    fun closeAllDraw() {
        try {
            dismiss()
        } catch (e: Exception) {
        }
    }
}