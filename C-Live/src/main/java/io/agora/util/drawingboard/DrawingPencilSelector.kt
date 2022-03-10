package io.agora.util.drawingboard

import android.view.View
import cn.bingoogolapple.transformerstip.TransformersTip
import com.mtjk.utils.click
import io.agora.vlive.R
import io.agora.vlive.databinding.DrawingBoardColorItemBinding
import io.agora.vlive.databinding.DrawingBoardLineItemBinding
import kotlinx.android.synthetic.main.drawing_board_pencil_selector.view.*
import java.util.ArrayList

class DrawingPencilSelector(anchorView: View?, layoutResId: Int) :
    TransformersTip(anchorView, layoutResId) {

    private val mlineData: MutableList<BeanLine> = ArrayList()

    private val mColorData: MutableList<BeanColor> = ArrayList()

    private val mLineValue = doubleArrayOf(2.0, 4.0, 6.0, 8.0)

    private val mLineResValue = intArrayOf(
        R.drawable.db_line_1, R.drawable.db_line_2, R.drawable.db_line_3, R.drawable.db_line_4
    )
    private val mColorValue = intArrayOf(
        0x000000,0xea5644,0xe8763b,0xffaa02,0xfcf976,
        0xd4537d,0x8c36ba,0x3460f6,0x71c5f8,0x75c46b
    )
    private val mColorResValue = intArrayOf(
        R.drawable.db_color_1, R.drawable.db_color_2, R.drawable.db_color_3, R.drawable.db_color_4,
        R.drawable.db_color_5, R.drawable.db_color_6, R.drawable.db_color_7, R.drawable.db_color_8,
        R.drawable.db_color_9, R.drawable.db_color_10
    )

    private lateinit var mCurrentLineView: View

    private lateinit var mCurrentColorView: View

    private var mDrawingPencilCallback: DrawingPencilCallback? = null

    override fun initView(contentView: View?) {
        contentView?.lineSelector?.enableRefresh = false
        contentView?.lineSelector?.bindData<BeanLine> { bean ->
            with(bean.binding as DrawingBoardLineItemBinding) {
                mCurrentLineView = lineItem
                lineItem?.setImageResource(bean.resId)
                lineItem?.click {
                    mCurrentLineView?.isSelected = false
                    lineItem?.isSelected = true
                    mCurrentLineView = lineItem
                    mDrawingPencilCallback?.onLineChanged(bean.lineWidth)
                }
            }
        }

        this.contentView.colorSelector.enableRefresh = false
        this.contentView.colorSelector.bindData<BeanColor> { bean ->
            with(bean.binding as DrawingBoardColorItemBinding) {
                colorItem?.setImageResource(bean.resId)
                mCurrentColorView = colorItem
                colorItem?.click {
                    mCurrentColorView?.isSelected = false
                    colorItem?.isSelected = true
                    mCurrentColorView = colorItem
                    mDrawingPencilCallback?.onColorChanged(bean.color)
                }
            }
        }

    }

    fun initData() {
        mlineData?.clear()
        mColorData?.clear()
        for (i in mLineValue.indices) {
            val bean = BeanLine()
            bean.lineWidth = mLineValue[i]
            bean.resId = mLineResValue[i]
            mlineData.add(bean)
        }
        for (i in mColorValue.indices) {
            val bean = BeanColor()
            bean.color = mColorValue[i]
            bean.resId = mColorResValue[i]
            mColorData.add(bean)
        }
        this.contentView.lineSelector.addDatas(mlineData)
        this.contentView.colorSelector.addDatas(mColorData)
    }

    fun setDrawingPencilCallback(callback: DrawingPencilCallback) {
        mDrawingPencilCallback = callback
    }

    interface DrawingPencilCallback{
        fun onLineChanged(value: Double)
        fun onColorChanged(value: Int)
    }
}