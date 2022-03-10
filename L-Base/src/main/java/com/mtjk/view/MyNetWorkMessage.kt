package com.mtjk.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.mtjk.base.R
import com.mtjk.utils.MyNetWorkUtils
import com.mtjk.utils.hasNetWork
import com.mtjk.utils.networkChange
import com.mtjk.utils.show

class MyNetWorkMessage : LinearLayout {
    lateinit var root: View

    var boo = false;

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initViews()
    }

    fun showNetworkError(boo: Boolean) {
        this.boo = boo
        if (boo) {
            show(!hasNetWork())
            networkChange { show(!it) }
        } else {
            show(false)
        }
    }

    fun enable() {
        networkChange { show(!it && !MyNetWorkUtils.isConnected()) }
    }


    private fun initViews() {
        root =
            LayoutInflater.from(context).inflate(R.layout.base_layout_network_message, this, true)

    }


}