package com.fcyd.expert.activity

import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import com.fcyd.expert.bean.BeanFeedback
import com.fcyd.expert.bean.BeanOrder
import com.fcyd.expert.databinding.ActivityConsultFeedbackBinding
import com.fcyd.expert.vm.OrderViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.R
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.dip2px
import com.mtjk.utils.toast
import kotlinx.android.synthetic.main.activity_consult_feedback.*

/**
 * tag==写咨询方案
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "写咨询方案")
class ConsultFeedbackActivity :
    MyBaseActivity<ActivityConsultFeedbackBinding, OrderViewModel>() {

    @MyField
    var order: BeanOrder? = null
    var bean = BeanFeedback()

    override fun initCreate() {
        mBinding.bean = bean
        var sendButton = TextView(this)
        sendButton.text = "发送"
        sendButton.textSize = 10f
        sendButton.gravity = Gravity.CENTER
        sendButton.setTextColor(Color.WHITE)
        sendButton.setBackgroundResource(R.drawable.base_selector_button_dark_20)
        mToolBar.addCustomView(sendButton, (40).dip2px(), (20).dip2px())
        sendButton.click {
            if (bean.content.length < 20) {
                toast("最少20个字符")
                return@click
            }

            mViewModel.feedback(order!!.orderId, bean.content).obs(this) { it.y { finish() } }

        }


    }


}