package com.mtjk.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.mtjk.base.R
import com.mtjk.utils.log
import com.mtjk.utils.px2dip
import com.mtjk.utils.show
import com.mtjk.utils.sp2px
import kotlinx.android.synthetic.main.layout_edit_item.view.*

/**
 * author:chenliang
 * date:2021/11/8
 */
class ItemEditView : LinearLayout {
    open lateinit var editText: EditText
    lateinit var item:View

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.ItemEditView)
        var icon = typedArray?.getResourceId(R.styleable.ItemEditView_my_icon, -1)
        var myTitle = typedArray?.getString(R.styleable.ItemEditView_my_title)
        var myValue = typedArray?.getString(R.styleable.ItemEditView_my_value)
        var myValueHint = typedArray?.getString(R.styleable.ItemEditView_my_value_hint)
        var lineShow = typedArray?.getBoolean(R.styleable.ItemEditView_my_lineShow, true)
        var rightShow = typedArray?.getBoolean(R.styleable.ItemEditView_my_rightShow, true)
        var edit = typedArray?.getBoolean(R.styleable.ItemEditView_my_edit, true)
        var must = typedArray?.getBoolean(R.styleable.ItemEditView_my_must_input, false)
        var textsize = typedArray?.getDimension(R.styleable.ItemEditView_my_textSize, -1f)

        var digits = typedArray?.getString(R.styleable.ItemEditView_my_digits)
        var length = typedArray?.getInt(R.styleable.ItemEditView_my_max_length, 0)
        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        item = View.inflate(context!!, R.layout.layout_edit_item, null)
        editText = item.editValue
        if (icon != null) {
            if (icon > 0) {
                item.icon.setImageResource(icon)
                item.icon.show(true)
            }
        }
        if (myTitle != null) {
            item.title.text = myTitle
        }
        if (myValueHint != null && "" != myValueHint) {
            item.textValue.text = myValueHint
            item.editValue.hint = myValueHint
        }
        if (length != null && length > 0) {
            item.editValue.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
        }
        if (myValue != null && "" != myValue) {
            item.textValue.text = myValue
        }
        if (edit != null) {
            item.editValue.show(edit)
            item.textValue.show(edit == false)
        }

        if (digits != null) {
            item.editValue.keyListener = DigitsKeyListener.getInstance(digits);
        }

        if (must != null) {
            item.must.show(must)
        }

        if (textsize != null && textsize > 0) {
            item.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textsize)
            item.editValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,textsize)
            item.textValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,textsize)
        }

        lineShow?.let { item.line.show(it) }
        rightShow?.let { item.right_icon.show(it) }

        addView(item, params)
    }

    fun setValue(value: String) {
        editText.setText(value)
        textValue.text = value
    }

    fun setMaxLength(length: Int) {
        item.editValue.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
    }

}

// SET 方法
@BindingAdapter("my_value")
fun setValue(item: ItemEditView, content: String?) {

    if (item != null && content != null) {
        var oldValue = item.editValue.text.toString()
        if (oldValue != content) {
            item.editValue.setText(content)
            item.textValue.text = content
            item.editValue.setSelection(content.length)
        }

    }
}

// GET 方法
@InverseBindingAdapter(attribute = "my_value", event = "contentAttrChanged")
fun getValue(item: ItemEditView): String {
    return item.editValue.text.trim().toString();
}

@BindingAdapter(value = ["contentAttrChanged"])
fun setChangeListener(item: ItemEditView, listener: InverseBindingListener) {
    item.editValue.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            listener.onChange();
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    })
}
