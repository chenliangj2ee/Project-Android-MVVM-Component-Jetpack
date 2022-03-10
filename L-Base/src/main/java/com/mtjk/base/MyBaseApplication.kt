package com.mtjk.base

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.multidex.MultiDexApplication
import com.chenliang.processor.LBase.MySp
import com.mtjk.BaseInit
import com.mtjk.utils.MyApp
import com.mtjk.utils.log
import com.mtjk.utils.networkChange
import com.tencent.android.tpush.XGIOperateCallback
import com.tencent.android.tpush.XGPushConfig
import com.tencent.android.tpush.XGPushManager
import com.tendcloud.tenddata.TCAgent
import gorden.rxbus2.RxBus
import java.lang.Exception
import java.lang.reflect.Method


/**
 *
 * @Project: MVVM-Component
 * @Package: com.chenliang.baselibrary
 * @author: chenliang
 * @date: 2021/7/12
 */
abstract class MyBaseApplication : MultiDexApplication() {
    var moduleApps = ArrayList<Application>()


    override fun onCreate() {
        super.onCreate()
        RxBus.get().register(this)
        moduleApps.forEach { it.onCreate() }
        initApp()

        networkChange {
            try{
                networkChange(it)
            }catch (e:Exception){

            }

        }
    }

    open fun networkChange(network: Boolean) {

    }

    private fun initApp() {
        if (!MySp.isAgree())
            return
        val start = System.currentTimeMillis()
//        JLibrary.InitEntry(this); //移动安全联盟统一SDK初始化
        //        JLibrary.InitEntry(this); //移动安全联盟统一SDK初始化
        TCAgent.LOG_ON = true
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        TCAgent.init(this)
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(true)

        //推送日志开关
        XGPushConfig.enableDebug(this, true)
        XGPushManager.registerPush(this, object : XGIOperateCallback {
            override fun onSuccess(data: Any, flag: Int) {
                //token在设备卸载重装的时候有可能会变
                MySp.setPushToken(data as String)
//                log("TPush", "注册成功，设备token为：${MySp.getPushToken()}")
            }

            override fun onFail(data: Any, errCode: Int, msg: String) {
//                log("TPush", "注册失败，错误码：$errCode,错误信息：$msg")
            }
        })
    }

    private fun initEnvironmentModel() {
        if (BuildConfig.isDev) {
            BaseInit.openDev()
        }

        if (BuildConfig.isTest) {
            BaseInit.openTest()
        }

        if (BuildConfig.isRelease) {
            BaseInit.openRelease()
        }

        if (BuildConfig.isUserApp) {
            BaseInit.isUserApp = true
            log("观众端App初始化")
        }
        if (BuildConfig.isExpertApp) {
            BaseInit.isExpertApp = true
            log("专家端App初始化")
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initModuleApplication()
    }


    private fun initModuleApplication() {
        if (BaseInit.con != null)
            return
        BaseInit.init(this)
        initEnvironmentModel()
        var info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        if (info.metaData == null)
            return
        var apps = info.metaData.keySet()
        apps.forEach {
            try {
                var cla = Class.forName(it.toString())
                var app = cla.newInstance()
                if (app is Application && cla.name != this::class.java.name) {
                    initModuleAppAttach(app)
                }

            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun initModuleAppAttach(app: Application) {
        val method: Method? =
            Application::class.java.getDeclaredMethod("attach", Context::class.java)
        if (method != null) {
            method.isAccessible = true
            method.invoke(app, baseContext)
            moduleApps.add(app)
        }
    }

}