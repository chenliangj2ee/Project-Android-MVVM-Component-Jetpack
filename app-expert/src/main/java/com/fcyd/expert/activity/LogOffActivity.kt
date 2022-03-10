package com.fcyd.expert.activity

import com.fcyd.expert.databinding.ActivityLogoffBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.WebViewActivity
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.mtjk.vm.AccountViewModel
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

/**
 * tag==注销账号
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "注销账号")
class LogOffActivity : MyBaseActivity<ActivityLogoffBinding, AccountViewModel>() {
    override fun initCreate() {

    }

    override fun initClick() {
        super.initClick()
        with(mBinding) {
            mBinding.user =  getBeanUser()
            next.click {
                if(expertlogoff.isChecked){
                    var can = Calendar.getInstance()
                    can.add(Calendar.DAY_OF_MONTH, 90)
                    dialog("确认注销后，我们将在90天内处理你的申请并删除账号信息。手机号和第三方授权将被于${can.timeInMillis.date("yyyy年MM月dd日")}被释放，再次登录将创建一个新账号。")
                        .title("注销账号")
                        .n("确认注销") { logoff() }
                        .y("再考虑一下") {}.show(this@LogOffActivity)
                }else{
                    toast("请同意用户注销协议")
                }
            }
            xieyi.goto(WebViewActivity::class.java, "url", "file:///android_asset/html/user_out.html", "title", "用户注销协议")
        }

    }


    private fun logoff() {
        mViewModel.logoff().obs(this) {
            it.n { goto(LogOffFailActivity::class.java) }
        }
    }
}