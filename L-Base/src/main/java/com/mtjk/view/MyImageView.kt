package com.mtjk.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.mtjk.base.R
import com.mtjk.utils.log

/**
 * 1-设置默认图
 * app:my_default="@drawable/default_header"
 * 2-设置宽高比
 * app:my_ratio="2:1"
 */

@SuppressLint("AppCompatCustomView")
class MyImageView : ImageView {
    var default = -1
    private var ratio: Float = 0.0F

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.MyImageView)
        default = typedArray!!.getResourceId(R.styleable.MyImageView_my_default, -1)
        var ratioString = typedArray!!.getString(R.styleable.MyImageView_my_ratio)
        if (ratioString != null && ratioString.contains(":")) {
            ratio = ratioString.split(":")[1].toFloat() / ratioString.split(":")[0].toFloat()
        }

        if (default != -1) {
            setImageResource(default)
        }

    }


    fun setImageColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageTintList = ColorStateList.valueOf(color)
        }
    }

    fun setImageColor(color: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageTintList = ColorStateList.valueOf(Color.parseColor(color))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (ratio != 0.0F) {
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            var heightMeasureSpec = MeasureSpec.makeMeasureSpec((widthSize * ratio).toInt(), MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}