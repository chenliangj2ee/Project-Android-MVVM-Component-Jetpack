package com.tencent.qcloud.tim.demo;

import static com.blankj.utilcode.util.ViewUtils.runOnUiThread;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.BusUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.heytap.msp.push.HeytapPushManager;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.meizu.cloud.pushsdk.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.mtjk.BaseInit;
import com.mtjk.account.LoginActivity;
import com.mtjk.base.MyBaseApplication;
import com.mtjk.bean.BeanUser;
import com.mtjk.utils.BusCode;
import com.mtjk.utils.MyFunctionKt;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversationListener;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tim.demo.bean.CallModel;
import com.tencent.qcloud.tim.demo.bean.OfflineMessageBean;
import com.tencent.qcloud.tim.demo.bean.UserInfo;
import com.tencent.qcloud.tim.demo.signature.GenerateTestUserSig;
import com.tencent.qcloud.tim.demo.thirdpush.HUAWEIHmsMessageService;
import com.tencent.qcloud.tim.demo.thirdpush.OPPOPushImpl;
import com.tencent.qcloud.tim.demo.thirdpush.OfflineMessageDispatcher;
import com.tencent.qcloud.tim.demo.thirdpush.ThirdPushTokenMgr;
import com.tencent.qcloud.tim.demo.utils.BrandUtil;
import com.tencent.qcloud.tim.demo.utils.DemoLog;
import com.tencent.qcloud.tim.demo.utils.PrivateConstants;
import com.tencent.qcloud.tim.demo.utils.TUIUtils;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuiconversation.util.IM;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class DemoApplication extends MyBaseApplication {
    private static final String TAG = DemoApplication.class.getSimpleName();
    private static DemoApplication instance;

    public static DemoApplication instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyFunctionKt.log(this, " IM application ???????????????onCreate------------------------");
        instance = this;
//        RxBus.get().register(this);
        // bugly??????
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(V2TIMManager.getInstance().getVersion());
        CrashReport.initCrashReport(getApplicationContext(), PrivateConstants.BUGLY_APPID, true, strategy);

        /**
         * TUIKit??????????????????
         *
         * @param context  ?????????????????????????????????????????????ApplicationContext
         * @param sdkAppID ???????????????????????????????????????sdkAppID
         * @param configs  TUIKit?????????????????????????????????????????????????????????????????????API??????
         */
        try {
            JSONObject buildInfoJson = new JSONObject();
            buildInfoJson.put("buildBrand", BrandUtil.getBuildBrand());
            buildInfoJson.put("buildManufacturer", BrandUtil.getBuildManufacturer());
            buildInfoJson.put("buildModel", BrandUtil.getBuildModel());
            buildInfoJson.put("buildVersionRelease", BrandUtil.getBuildVersionRelease());
            buildInfoJson.put("buildVersionSDKInt", BrandUtil.getBuildVersionSDKInt());
            // ??????????????? app ?????????????????????????????????????????????????????? app ??????????????????????????? SDK ??????SDK ?????????????????????????????????????????????
            V2TIMManager.getInstance().callExperimentalAPI("setBuildInfo", buildInfoJson.toString(), new V2TIMValueCallback<Object>() {
                @Override
                public void onSuccess(Object o) {
                    DemoLog.i(TAG, "setBuildInfo success");
                }

                @Override
                public void onError(int code, String desc) {
                    DemoLog.i(TAG, "setBuildInfo code:" + code + " desc:" + desc);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //im?????????
        TUIUtils.init(this, GenerateTestUserSig.SDKAPPID, null, new V2TIMSDKListener() {
            @Override
            public void onConnecting() {
                super.onConnecting();
                MyFunctionKt.log(this, "??????IM????????????.......................");
            }

            @Override
            public void onConnectSuccess() {
                super.onConnectSuccess();
                MyFunctionKt.log(this, "??????IM???????????????.......................");
            }

            @Override
            public void onConnectFailed(int code, String error) {
                super.onConnectFailed(code, error);
                MyFunctionKt.log(this, "??????IM???????????????.......................");
            }

            @Override
            public void onKickedOffline() {
                super.onKickedOffline();
                MyFunctionKt.log(this, "??????IM???????????????.......................");
            }

            @Override
            public void onUserSigExpired() {
                super.onUserSigExpired();
                MyFunctionKt.log(this, "??????IM??? token??????.......................");
            }

            @Override
            public void onSelfInfoUpdated(V2TIMUserFullInfo info) {
                super.onSelfInfoUpdated(info);
                MyFunctionKt.log(this, "??????IM?????????????????????.......................");
            }
        });
        HeytapPushManager.init(this, true);
        if (BrandUtil.isBrandXiaoMi()) {
            // ??????????????????
            if (BaseInit.INSTANCE.isUserApp()) {
                MiPushClient.registerPush(this, PrivateConstants.XM_PUSH_APPID, PrivateConstants.XM_PUSH_APPKEY);
            } else {
                MiPushClient.registerPush(this, PrivateConstants.EXPERT_XM_PUSH_APPID, PrivateConstants.EXPERT_XM_PUSH_APPKEY);
            }
        } else if (BrandUtil.isBrandHuawei()) {
            // ???????????????????????????????????????Push???????????????????????????
            HmsMessaging.getInstance(this).turnOnPush().addOnCompleteListener(new com.huawei.hmf.tasks.OnCompleteListener<Void>() {
                @Override
                public void onComplete(com.huawei.hmf.tasks.Task<Void> task) {
                    if (task.isSuccessful()) {
                        DemoLog.i(TAG, "huawei turnOnPush Complete");
                        MyFunctionKt.log(this, "huawei turnOnPush Complete.......................");
                    } else {
                        MyFunctionKt.log(this, "huawei turnOnPush failed: ret=" + task.getException().getMessage());
                    }
                }
            });
        } else if (MzSystemUtils.isBrandMeizu(this)) {
            // ??????????????????
            PushManager.register(this, PrivateConstants.MZ_PUSH_APPID, PrivateConstants.MZ_PUSH_APPKEY);
        } else if (BrandUtil.isBrandVivo()) {
            // vivo????????????
            PushClient.getInstance(getApplicationContext()).initialize();
        } else if (HeytapPushManager.isSupportPush()) {
            // oppo???????????????????????????????????????????????????????????????token?????????????????????MainActivity??????
        } else if (BrandUtil.isGoogleServiceSupport()) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                MyFunctionKt.log(this, "getInstanceId failed exception = " + task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            MyFunctionKt.log(this, "google fcm getToken = " + token);
                            ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
                        }
                    });
        }

        registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback());
        initLoginStatusListener();
        //??????????????????????????????
//        login();

    }

    private void registerUnreadListener() {
        V2TIMManager.getConversationManager().addConversationListener(unreadListener);
        V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                runOnUiThread(() -> unreadListener.onTotalUnreadMessageCountChanged(aLong));
            }

            @Override
            public void onError(int code, String desc) {

            }
        });
    }

    private final V2TIMConversationListener unreadListener = new V2TIMConversationListener() {
        @Override
        public void onTotalUnreadMessageCountChanged(long totalUnreadCount) {
            MyFunctionKt.sendSelf("" + totalUnreadCount, BusCode.IM_UNREADNUM_CHANGE);
            HUAWEIHmsMessageService.updateBadge(BaseInit.INSTANCE.getCon(), (int) totalUnreadCount);
        }
    };

    @Subscribe(code = BusCode.GET_USERSIG_SUCCESS)
    public void login() {
        BeanUser user = new BeanUser().get();
        if (user != null) {
            String userId = user.getUserId();
            String userSig = user.getUserSig();
            if (userSig != null && userId != null) {
                TUIUtils.login(userId, userSig, new V2TIMCallback() {
                    @Override
                    public void onError(final int code, final String desc) {
                        BeanUser user=new BeanUser().get();
                        if(user.isLogin()){
                            MyFunctionKt.log(this, "IM????????????.......????????????");
                            //???????????????????????????token??????????????????IM
                            MyFunctionKt.postDelayed(this, 3000, new Function0<Unit>() {
                                @Override
                                public Unit invoke() {
                                    MyFunctionKt.send(this, BusCode.LOGIN_SUCCESS);
                                    return null;
                                }
                            });
                        }

                    }

                    @Override
                    public void onSuccess() {
                        MyFunctionKt.log(this, "??????IM???????????????.......................");
                        BackgroundTasks.getInstance().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        updateProfile(user);
                                        getUnreadListener();
                                        registerUnreadListener();
                                        prepareThirdPushToken();
                                    }
                                }, 100);
                    }
                });
            } else {
                user.clear();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        } else {
            return;
        }
    }

    @Subscribe(code = BusCode.UPDATE_IM_USERINFO)
    public void updateProfile(BeanUser user) {
        UserInfo.getInstance().setName(user.getNickName() + "");
        UserInfo.getInstance().setUserId(user.getUserId());
        String avatarurl = user.getAvatar();
        MyFunctionKt.log(this,"??????IM????????????"+avatarurl);
        V2TIMUserFullInfo v2TIMUserFullInfo = new V2TIMUserFullInfo();
        // ??????
        if (!TextUtils.isEmpty(avatarurl)) {
            v2TIMUserFullInfo.setFaceUrl(avatarurl);
            UserInfo.getInstance().setAvatar(avatarurl);
        }
        // ??????
        if (BaseInit.INSTANCE.isUserApp()) {
            v2TIMUserFullInfo.setNickname(user.getNickName());
        } else {
            v2TIMUserFullInfo.setNickname(user.getRealName());
        }

        V2TIMManager.getInstance().setSelfInfo(v2TIMUserFullInfo, new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                MyFunctionKt.log(this,"??????IM??????????????????");
            }

            @Override
            public void onSuccess() {
                MyFunctionKt.log(this,"??????IM??????????????????");
            }
        });
    }

    @Override
    public void networkChange(boolean network) {
        super.networkChange(network);
        //????????????????????????????????????
        if (network) {
            login();
        }
    }


    public void initLoginStatusListener() {
        V2TIMManager.getInstance().addIMSDKListener(loginStatusListener);
    }


    private final V2TIMSDKListener loginStatusListener = new V2TIMSDKListener() {
        @Override
        public void onKickedOffline() {
            MyFunctionKt.toast(this, "????????????????????????????????????");
            MyFunctionKt.log(this, "????????????????????????????????????...................");
            logout();
        }

        @Override
        public void onUserSigExpired() {
            MyFunctionKt.toast(this, "?????????????????????????????????");
            MyFunctionKt.log(this, "?????????????????????????????????...................");
            logout();
        }
    };

    //????????????
    public void logout() {
        TUIUtils.logout(new V2TIMCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int code, String desc) {

            }
        });
        UserInfo.getInstance().setToken("");
        UserInfo.getInstance().setAutoLogin(false);
        BaseInit.INSTANCE.exit();
    }

    class StatisticActivityLifecycleCallback implements ActivityLifecycleCallbacks {
        private int foregroundActivities = 0;
        private boolean isChangingConfiguration;

        private final V2TIMConversationListener unreadListener = new V2TIMConversationListener() {
            @Override
            public void onTotalUnreadMessageCountChanged(long totalUnreadCount) {
                HUAWEIHmsMessageService.updateBadge(DemoApplication.this, (int) totalUnreadCount);
            }
        };

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
//            if (bundle != null) { // ???bundle??????????????????????????????
//                // ??????????????????,????????????????????????
//                BeanUser user = new BeanUser().get();
//                user.clear();
//                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            foregroundActivities++;
            if (foregroundActivities == 1 && !isChangingConfiguration) {
                // ??????????????????
                MyFunctionKt.log(this, "IM app ????????????.......");
                V2TIMManager.getOfflinePushManager().doForeground(new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                        MyFunctionKt.log(this, "doForeground err = " + code + ", desc = " + desc);
                    }

                    @Override
                    public void onSuccess() {
                        DemoLog.i(TAG, "");
                        MyFunctionKt.log(this, "doForeground success....");
                    }
                });
                MyFunctionKt.log(this, "??????IM????????????......");
                V2TIMManager.getConversationManager().removeConversationListener(unreadListener);
            }
            isChangingConfiguration = false;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            foregroundActivities--;
            if (foregroundActivities == 0) {
                // ??????????????????
                MyFunctionKt.log(this, "IM app????????????");
                V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        int totalCount = aLong.intValue();
                        V2TIMManager.getOfflinePushManager().doBackground(totalCount, new V2TIMCallback() {
                            @Override
                            public void onError(int code, String desc) {
                                MyFunctionKt.log(this, "doBackground err = " + code + ", desc = " + desc);
                            }

                            @Override
                            public void onSuccess() {
                                MyFunctionKt.log(this, "doBackground success.........");
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String desc) {

                    }
                });
                MyFunctionKt.log(this, "??????IM????????????......");
                V2TIMManager.getConversationManager().addConversationListener(unreadListener);
            }
            isChangingConfiguration = activity.isChangingConfigurations();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }


    @Subscribe(code = BusCode.MAIN_IN)
    private void prepareThirdPushToken() {
//        MyFunctionKt.log(this, "IM prepareThirdPushToken......");
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
        if (BrandUtil.isBrandHuawei()) {
            // ??????????????????
            new Thread() {
                @Override
                public void run() {
                    try {
                        // read from agconnect-services.json
                        String appId = AGConnectServicesConfig.fromContext(getApplicationContext()).getString("client/app_id");
                        String token = HmsInstanceId.getInstance(getApplicationContext()).getToken(appId, "HCM");
//                        MyFunctionKt.log(TAG, "huawei get token:" + token);
                        if (!TextUtils.isEmpty(token)) {
                            ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
                            ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
                        }
                    } catch (ApiException e) {
                    }
                }
            }.start();
        } else if (BrandUtil.isBrandVivo()) {
            String regId = PushClient.getInstance(getApplicationContext()).getRegId();
//            MyFunctionKt.log(TAG, "vivopush open vivo push success regId = " + regId);
            // vivo????????????
//            MyFunctionKt.log(TAG, "vivo support push: " + PushClient.getInstance(getApplicationContext()).isSupport());
            PushClient.getInstance(getApplicationContext()).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    if (state == 0) {
                        String regId = PushClient.getInstance(getApplicationContext()).getRegId();
//                        MyFunctionKt.log(TAG, "vivopush open vivo push success regId = " + regId);
                        ThirdPushTokenMgr.getInstance().setThirdPushToken(regId);
                        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
                    } else {
                        // ??????vivo?????????????????????state = 101 ?????????vivo???????????????????????????vivo??????????????????https://dev.vivo.com.cn/documentCenter/doc/156
//                        MyFunctionKt.log(TAG, "vivopush open vivo push fail state = " + state);
                    }
                }
            });
        } else if (HeytapPushManager.isSupportPush()) {
            if (BaseInit.INSTANCE.isUserApp()) {
                // oppo????????????
                OPPOPushImpl oppo = new OPPOPushImpl();
                oppo.createNotificationChannel(getApplicationContext());
                // oppo??????????????????????????????????????????init(...)????????????????????????????????????
                HeytapPushManager.init(this, false);
                HeytapPushManager.register(this, PrivateConstants.OPPO_PUSH_APPKEY, PrivateConstants.OPPO_PUSH_APPSECRET, oppo);
            } else {
                // oppo????????????
                OPPOPushImpl oppo = new OPPOPushImpl();
                oppo.createNotificationChannel(getApplicationContext());
                // oppo??????????????????????????????????????????init(...)????????????????????????????????????
                HeytapPushManager.init(this, false);
                HeytapPushManager.register(this, PrivateConstants.EXPERT_OPPO_PUSH_APPKEY, PrivateConstants.EXPERT_OPPO_PUSH_APPSECRET, oppo);
            }
        }
    }

    private void getUnreadListener() {
        V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
            @Override
            public void onSuccess(Long aLong) {
//                MyFunctionKt.log(this, "IM getTotalUnreadMessageCount  $aLong.................");
                MyFunctionKt.sendSelf("" + aLong, BusCode.IM_UNREADNUM_CHANGE);
            }

            @Override
            public void onError(int code, String desc) {

            }
        });

    }

    /**
     * ??????????????????????????????Splash?????????Intent???BusCode.SPLASH_IN
     *
     * @param intent
     */
    @Subscribe(code = BusCode.SPLASH_IN)
    public void parseOfflineMessage(Intent intent) {
//        MyFunctionKt.log(this, "IM parseOfflineMessage.................");
        OfflineMessageBean bean = OfflineMessageDispatcher.parseOfflineMessage(intent);
        if (bean != null) {
            OfflineMessageDispatcher.redirect(bean);
        }

    }


    @Subscribe(code = BusCode.MAIN_RESUME)
    public void handleOfflinePush(Intent intent) {
        BackgroundTasks.getInstance().postDelayed(new Runnable() {
            @Override
            public void run() {
                registerUnreadListener();
//                MyFunctionKt.log(this, "IM Main onResume.................");
                // TODO: 2022/2/22 ??????????????????
//                if (V2TIMManager.getInstance().getLoginStatus() == V2TIMManager.V2TIM_STATUS_LOGOUT) {
//                    Bundle bundle = new Bundle();
//                    if (intent != null && intent.getExtras() != null) {
//                        bundle.putAll(intent.getExtras());
//                    }
//                    Intent intent1=new Intent(BaseInit.INSTANCE.getCon(),LoginActivity.class);
//                    BaseInit.INSTANCE.getCon().startActivity(intent1);
//                    BaseInit.INSTANCE.getAct().finish();
//                    return;
//                }

                final OfflineMessageBean bean = OfflineMessageDispatcher.parseOfflineMessage(intent);
                if (bean != null) {
                    BaseInit.INSTANCE.getAct().setIntent(null);
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancelAll();
                    }

                    if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CHAT) {
                        if (TextUtils.isEmpty(bean.sender)) {
                            return;
                        }
                        TUIUtils.startChat(bean.sender, bean.nickname, bean.chatType);
                    } else if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CALL) {
                        handleOfflinePushCall(bean);
                    }
                }
            }
        }, 1000);

    }

    void handleOfflinePushCall(OfflineMessageBean bean) {
        if (bean == null || bean.content == null) {
            return;
        }
        final CallModel model = new Gson().fromJson(bean.content, CallModel.class);
        DemoLog.i(TAG, "bean: " + bean + " model: " + model);
        if (model != null) {
            long timeout = V2TIMManager.getInstance().getServerTime() - bean.sendTime;
            if (timeout >= model.timeout) {
                ToastUtil.toastLongMessage(DemoApplication.instance().getString(R.string.call_time_out));
            } else {
                TUIUtils.startCall(bean.sender, bean.content);
            }
        }
    }


    /**
     * ??????????????????????????????Splash?????????Intent???BusCode.SPLASH_IN
     *
     * @param intent
     */
    @Subscribe(code = BusCode.IM_GOTO_CHAT)
    public void gotoChat(Intent intent) {
        IM.gotoChat(intent.getStringExtra("id"), intent.getStringExtra("name"));
    }


}
