package io.agora.vlive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.PatternFlattener;
import com.elvishew.xlog.formatter.message.json.DefaultJsonFormatter;
import com.elvishew.xlog.formatter.message.throwable.DefaultThrowableFormatter;
import com.elvishew.xlog.formatter.message.xml.DefaultXmlFormatter;
import com.elvishew.xlog.formatter.stacktrace.DefaultStackTraceFormatter;
import com.elvishew.xlog.formatter.thread.DefaultThreadFormatter;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.mtjk.BaseInit;
import com.mtjk.base.MyBaseApplication;
import com.mtjk.bean.BeanUser;
import com.mtjk.utils.BusCode;
import com.mtjk.utils.MyApp;
import com.mtjk.utils.MyFunctionKt;
import com.tencent.bugly.crashreport.CrashReport;

import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;
import io.agora.capture.video.camera.CameraManager;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.vlive.agora.AgoraEngine;
import io.agora.vlive.agora.rtc.RtcEventHandler;
import io.agora.vlive.bean.BeanParam;
import io.agora.vlive.protocol.ClientProxy;
import io.agora.vlive.ui.activity.GuanZhongActivity;
import io.agora.vlive.ui.activity.GuanZhongActivity2;
import io.agora.vlive.ui.activity.LivePrepareActivity;
import io.agora.vlive.utils.Global;
import io.agora.vlive.utils.UserUtil;

public class AgoraLiveApplication extends MyBaseApplication {
    public static final String APPID = "3c95c45959d44cffb5b7af20d1322c6a";

    private SharedPreferences mPref;
    private Config mConfig;
    private AgoraEngine mAgoraEngine;
    private CameraManager mCameraVideoManager;

    public static AgoraLiveApplication app;

    @Override
    public void onCreate() {
        BaseInit.INSTANCE.registerApi(ApiService.class);

        if (MyApp.INSTANCE.isMainProcess()) {
            MyFunctionKt.log(this, "直播 application初始化。。。。");
            super.onCreate();
            app = this;
            mPref = getSharedPreferences(Global.Constants.SF_NAME, Context.MODE_PRIVATE);
            mConfig = new Config(this);
            initXLog();
            initVideoGlobally();
            initCrashReport();

        }

    }


    @Subscribe(code = BusCode.LIVE_GET_RTM_TOKEN, threadMode = ThreadMode.MAIN)
    public void login() {
        MyFunctionKt.log(this, "RTM直播开始登录.....................................");
        BeanUser user = new BeanUser().get();
        try {
            MyFunctionKt.log(this, "RTM登录TOKEN:" + user.getRtmToken());
            MyFunctionKt.log(this, "RTM登录USERID:" + user.getUserId());
            if (rtmClient() == null) {
                initEngine(APPID);
            }
            rtmClient().login(user.getRtmToken(), user.getUserId(), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    MyFunctionKt.log(this, "RTM直播登录成功");
                    if (BaseInit.INSTANCE.isUserApp()) {
                        startIntoLive();
                    } else {
                        startLive();
                    }
                }


                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    MyFunctionKt.log(this, "RTM直播登录失败:" + errorInfo.toString());
                }
            });
        } catch (Exception e) {
            MyFunctionKt.log(this, "RTM直播登录失败:" + e.getMessage());
            e.printStackTrace();
        }


    }


    public Config config() {
        return mConfig;
    }

    public SharedPreferences preferences() {
        return mPref;
    }

    public void initEngine(String appId) {
        mAgoraEngine = new AgoraEngine(this, appId);
    }

    public RtcEngine rtcEngine() {
        return mAgoraEngine != null ? mAgoraEngine.rtcEngine() : null;
    }

    public RtmClient rtmClient() {
        return mAgoraEngine != null ? mAgoraEngine.rtmClient() : null;
    }

    public ClientProxy proxy() {
        return ClientProxy.instance();
    }

    public void registerRtcHandler(RtcEventHandler handler) {
        mAgoraEngine.registerRtcHandler(handler);
    }

    public void removeRtcHandler(RtcEventHandler handler) {
        mAgoraEngine.removeRtcHandler(handler);
    }

    public CameraManager cameraVideoManager() {
        return mCameraVideoManager;
    }

    private void initVideoGlobally() {
        new Thread(() -> {
            mCameraVideoManager = new CameraManager(this, null);
//            mCameraVideoManager.setCameraStateListener(preprocessor);
        }).start();
    }

    private void initXLog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ?
                        LogLevel.DEBUG : LogLevel.INFO)                         // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                .tag("AgoraLive")                                               // Specify TAG, default: "X-LOG"
                //.t()                                                            // Enable thread info, disabled by default
                .st(Global.Constants.LOG_CLASS_DEPTH)                           // Enable stack trace info with depth 2, disabled by default
                // .b()                                                            // Enable border, disabled by default
                .jsonFormatter(new DefaultJsonFormatter())                      // Default: DefaultJsonFormatter
                .xmlFormatter(new DefaultXmlFormatter())                        // Default: DefaultXmlFormatter
                .throwableFormatter(new DefaultThrowableFormatter())            // Default: DefaultThrowableFormatter
                .threadFormatter(new DefaultThreadFormatter())                  // Default: DefaultThreadFormatter
                .stackTraceFormatter(new DefaultStackTraceFormatter())          // Default: DefaultStackTraceFormatter
                .build();

        Printer androidPrinter = new AndroidPrinter();                          // Printer that print the log using android.util.Log

        String flatPattern = "{d yy/MM/dd HH:mm:ss} {l}|{t}: {m}";
        Printer filePrinter = new FilePrinter                                   // Printer that print the log to the file system
                .Builder(UserUtil.appLogFolderPath(this))         // Specify the path to save log file
                .fileNameGenerator(new DateFileNameGenerator())                 // Default: ChangelessFileNameGenerator("log")
                .backupStrategy(new FileSizeBackupStrategy(
                        Global.Constants.APP_LOG_SIZE))                         // Default: FileSizeBackupStrategy(1024 * 1024)
                .cleanStrategy(new FileLastModifiedCleanStrategy(
                        Global.Constants.LOG_DURATION))
                .flattener(new PatternFlattener(flatPattern))                   // Default: DefaultFlattener
                .build();

        XLog.init(                                                              // Initialize XLog
                config,                                                         // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                androidPrinter,
                filePrinter);
    }

    private void initCrashReport() {
        String buglyAppId = getResources().getString(R.string.bugly_app_id);
        if (TextUtils.isEmpty(buglyAppId)) {
            XLog.i("Bugly app id not found, crash report initialize skipped");
        } else {
            CrashReport.initCrashReport(getApplicationContext(),
                    buglyAppId, BuildConfig.DEBUG);
        }
    }

    @Override
    public void onTerminate() {
        XLog.i("onApplicationTerminate");
        super.onTerminate();
        mAgoraEngine.release();
    }

    /**
     * 主播进入房间
     */
    @Subscribe(code = BusCode.LIVE_INTO_1)
    public void startLive() {

        BeanUser user = new BeanUser().get();

        Config.UserProfile profile = config().getUserProfile();
        profile.setUserId(user.getUserId());
        profile.setUserName(BaseInit.INSTANCE.isUserApp() ? user.getNickName() : user.getRealName());
        profile.setRtmToken(user.getRtmToken());
        profile.setRtcToken(user.getRtcToken());
//        profile.setRtcToken("0063c95c45959d44cffb5b7af20d1322c6aIADRQH6FoRHiMbi1u8rIgLYkHv9nKxBFL1FzqgSw6LXEwq/a4JoAAAAAIgDL5xHfMLAMYgQAAQAvsAxiAgAvsAxiAwAvsAxiBAAvsAxi");
        config().setAppId(APPID);

        if (config().appIdObtained()) {
            Intent intent = new Intent(BaseInit.INSTANCE.getCon(), LivePrepareActivity.class);
            intent.putExtra(Global.Constants.TAB_KEY, 1);
            intent.putExtra(Global.Constants.KEY_IS_ROOM_OWNER, true);
            intent.putExtra(Global.Constants.KEY_CREATE_ROOM, true);
            startActivity(intent);
        }
    }


    /**
     * 粉丝进入房间
     */
    @Subscribe(code = BusCode.LIVE_INTO_2)
    public void startIntoLive() {

        MyFunctionKt.log(this, "粉丝开始进入直播....................................");
        BeanUser user = new BeanUser().get();

        Config.UserProfile profile = config().getUserProfile();
        profile.setUserId(user.getUserId());
        profile.setUserName(BaseInit.INSTANCE.isUserApp() ? user.getNickName() : user.getRealName());
        profile.setRtmToken(user.getRtmToken());
        profile.setRtcToken(user.getRtcToken());
//        profile.setRtcToken("0063c95c45959d44cffb5b7af20d1322c6aIADaIgjlAIBItNj33P/ZzT2P48P8WsoZiLE77q+tisqSdq/a4JoAAAAAIgAWylBUmOoMYgQAAQCY6gxiAgCY6gxiAwCY6gxiBACY6gxi");
        config().setAppId(APPID);

        if (config().appIdObtained()) {
            BeanParam liveParam = new BeanParam().get();
            if (liveParam.getLiveType() == BeanParam.LiveType.INSTANCE.getVIDEO_MORE() || liveParam.getLiveType() == BeanParam.LiveType.INSTANCE.getAUDIO_MORE()) {
                Intent intent = new Intent(BaseInit.INSTANCE.getCon(), GuanZhongActivity2.class);
                intent.putExtra(Global.Constants.TAB_KEY, 1);
                intent.putExtra(Global.Constants.KEY_IS_ROOM_OWNER, false);
                intent.putExtra(Global.Constants.KEY_CREATE_ROOM, false);
                startActivity(intent);
            }

            if (liveParam.getLiveType() == BeanParam.LiveType.INSTANCE.getVIDEO_ONE() || liveParam.getLiveType() == BeanParam.LiveType.INSTANCE.getAUDIO_ONE()) {
                Intent intent = new Intent(BaseInit.INSTANCE.getCon(), GuanZhongActivity.class);
                intent.putExtra(Global.Constants.TAB_KEY, 1);
                intent.putExtra(Global.Constants.KEY_IS_ROOM_OWNER, false);
                intent.putExtra(Global.Constants.KEY_CREATE_ROOM, false);
                startActivity(intent);
            }
        }
    }
}