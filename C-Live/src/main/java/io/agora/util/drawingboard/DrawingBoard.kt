package io.agora.util.drawingboard

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.agora.util.drawingboard.DrawingBoardDialogFragment.Companion.newInstance
import java.lang.ref.WeakReference

/*
* tag==互动白板
* 使用示例：DrawingBoard.from(this).show("4fa84a23868c4d31bf052a83ddc75274", false)
* 其中show参数为房间channel号
* */
class DrawingBoard private constructor(activity: FragmentActivity?, fragment: Fragment?) {
    private val mContext: WeakReference<FragmentActivity?>
    private val mFragment: WeakReference<Fragment?>
    private var mFragmentManager: WeakReference<FragmentManager>? = null

    companion object {
        private const val TAG = "DrawingBoard"
        fun from(fragment: Fragment): DrawingBoard {
            return DrawingBoard(fragment)
        }

        fun from(activity: FragmentActivity): DrawingBoard {
            return DrawingBoard(activity)
        }
    }

    init {
        mContext = WeakReference(activity)
        mFragment = WeakReference(fragment)
    }

    private constructor(fragment: Fragment) : this(fragment.activity, fragment) {
        mFragmentManager = WeakReference(fragment.childFragmentManager)
    }

    private constructor(activity: FragmentActivity) : this(activity, null) {
        mFragmentManager = WeakReference(activity.supportFragmentManager)
    }

    @JvmOverloads
    fun show(channelId: String, disableOperation: Boolean = false) {
        if (TextUtils.isEmpty(channelId)) {
            return
        }
        var ft = mFragmentManager!!.get()!!.beginTransaction()
        val prev = mFragmentManager!!.get()!!.findFragmentByTag(TAG)
        if (prev != null) {
            ft.remove(prev).commit()
            ft = mFragmentManager!!.get()!!.beginTransaction()
        }
        ft.addToBackStack(null)
        val dbFragment = newInstance(channelId, disableOperation)
        dbFragment.show(ft, TAG)
    }
}