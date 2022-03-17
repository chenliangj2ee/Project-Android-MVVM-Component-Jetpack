package com.mtjk.view

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mtjk.base.R

class WalletItemDecoration(val mContext: Context?, val mGroupListener: OnGroupListener?) : RecyclerView.ItemDecoration() {

    private val mGroupDividerHeight: Int

    private var mDividerPaint: Paint? = null

    private var mTextPaint: Paint? = null

    private val mArrowBitmap: Bitmap

    init {
        mGroupDividerHeight = dp2Px(30)
        mArrowBitmap = BitmapFactory.decodeResource(
            mContext!!.resources,
            R.drawable.item_wallet_group_arrow
        )
        initPaint()
    }

    private fun initPaint() {
        mDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDividerPaint!!.color = Color.parseColor("#fff6f7f8")
        mDividerPaint!!.style = Paint.Style.FILL

        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.color = Color.parseColor("#ff515357")
        mTextPaint!!.textSize = sp2Px(14).toFloat()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        getGroupName(position) ?: return
        if (position == 0 || isGroupFirst(position)) {
            outRect.top = mGroupDividerHeight
        } else {
            outRect.top = dp2Px(0)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        //当前屏幕可见子view数
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i) ?: continue
            val childAdapterPosition = parent.getChildAdapterPosition(childView)
            val left = parent.paddingLeft
            val right = parent.paddingRight
            val bottom = childView.top
            if (isGroupFirst(childAdapterPosition)) {
                val top = bottom - mGroupDividerHeight
                canvas.drawRect(left.toFloat(), top.toFloat(),(childView.width - right).toFloat(),bottom.toFloat(),mDividerPaint!!)
                //分组文字
                val baseLine = (top + bottom) / 2f - (mTextPaint!!.descent() + mTextPaint!!.ascent()) / 2f
                drawGroupContent(canvas, getGroupName(childAdapterPosition), left, baseLine)
            } else {
                val top = bottom - dp2Px(0)
                canvas.drawRect(left.toFloat(), top.toFloat(), (childView.width - right).toFloat(), bottom.toFloat(), mDividerPaint!!)
            }
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val firstVisibleView = parent.getChildAt(0) ?: return
        val firstVisiblePosition = parent.getChildAdapterPosition(firstVisibleView)
        val groupName = getGroupName(firstVisiblePosition)
        val left = parent.paddingLeft
        val right = firstVisibleView.width - parent.paddingRight
        if (firstVisibleView.bottom <= mGroupDividerHeight && isGroupFirst(firstVisiblePosition + 1)) {
            canvas.drawRect(left.toFloat(),0f,right.toFloat(),firstVisibleView.bottom.toFloat(),mDividerPaint!!)
            val baseLine = firstVisibleView.bottom / 2f - (mTextPaint!!.descent() + mTextPaint!!.ascent()) / 2f
            drawGroupContent(canvas, groupName, left, baseLine)
        } else {
            canvas.drawRect(left.toFloat(), 0f, right.toFloat(), mGroupDividerHeight.toFloat(), mDividerPaint!!)
            val baseLine = mGroupDividerHeight / 2f - (mTextPaint!!.descent() + mTextPaint!!.ascent()) / 2f
            drawGroupContent(canvas, groupName, left, baseLine)
        }
    }

    private fun drawGroupContent(canvas: Canvas, groupName: String?, left: Int, baseLine: Float) {
        canvas.drawText(groupName!!, (left + dp2Px(10)).toFloat(), baseLine, mTextPaint!!)
        canvas.drawBitmap(mArrowBitmap, (left + dp2Px(85)).toFloat(), baseLine - dp2Px(14), mTextPaint)
    }

    private fun getGroupName(position: Int): String? {
        return mGroupListener?.getGroupName(position)
    }

    private fun isGroupFirst(position: Int): Boolean {
        return if (position == 0) {
            true
        } else {
            val preGroupName = getGroupName(position - 1)
            val groupName = getGroupName(position)
            !groupName.isNullOrEmpty() && !TextUtils.equals(preGroupName, groupName)
        }
    }

    private fun dp2Px(dpValue: Int): Int {
        return if (mContext == null) {
            0
        } else TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue.toFloat(),
            mContext.resources.displayMetrics
        ).toInt()
    }

    private fun sp2Px(spValue: Int): Int {
        return if (mContext == null) {
            0
        } else TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spValue.toFloat(),
            mContext.resources.displayMetrics
        ).toInt()
    }

    interface OnGroupListener {
        fun getGroupName(position: Int): String?
    }
}