package com.mentuojiankang.user.wxapi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mentuojiankang.user.R
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.mentuojiankang.user.wxapi.WXEntryActivity
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import java.lang.Exception

class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
    private var api: IWXAPI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("initCreate")
        setContentView(R.layout.activity_wxpay_entry)
        api = WXAPIFactory.createWXAPI(this, "wx4a3828868bbdc84f")
        try {
            val intent = intent
            api!!.handleIntent(intent, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent) {
        log("onNewIntent")
        super.onNewIntent(intent)
        setIntent(intent)
        api!!.handleIntent(intent, this)
    }

    override fun onReq(baseReq: BaseReq) {}
    override fun onResp(baseResp: BaseResp) {
        var beanUser = getBeanUser()
        log("onPayFinish, errCode = " + baseResp.errCode)
        if (baseResp.type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            val launchMiniProResp = baseResp as WXLaunchMiniProgram.Resp
            val extraData = launchMiniProResp.extMsg // 对应下面小程序中的app-parameter字段的value
            log("onPayFinish = $extraData")
            //支付成功
            if ("success" == extraData) {
                beanUser?.backWxZfb=false
                send(BusCode.PAYMENT_RESULT)
                finish()
            } else if ("failure" == extraData) {
                //支付失败
                beanUser?.backWxZfb=false
                send(BusCode.PAYMENT_RESULT)
                finish()
            } else {
                beanUser?.backWxZfb=false
                send(BusCode.PAYMENT_RESULT)
                toast("返回值为空$extraData")
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "com.mentuojiankang.user.wxapi.WXEntryActivity"
    }
}