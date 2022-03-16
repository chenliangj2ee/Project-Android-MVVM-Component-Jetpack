package com.tencent.qcloud.tuikit.tuiconversation.util;

import static com.blankj.utilcode.util.ViewUtils.runOnUiThread;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.mtjk.utils.MyFunctionKt;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.liteav.trtccalling.model.TUICalling;
import com.tencent.liteav.trtccalling.model.impl.TUICallingManager;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;

import java.util.Vector;

/*
 * tag==单聊页面,语音通话
 */
public class IM {
    public IM() {
    }

    public static void gotoChat(String conversationInfoId, String conversationInfoTitle) {
        Bundle param = new Bundle();
        param.putInt(TUIConstants.TUIChat.CHAT_TYPE, V2TIMConversation.V2TIM_C2C);
        param.putString(TUIConstants.TUIChat.CHAT_ID, conversationInfoId);
        param.putString(TUIConstants.TUIChat.CHAT_NAME, conversationInfoTitle);
        TUICore.startActivity(TUIConstants.TUIChat.C2C_CHAT_ACTIVITY_NAME, param);
    }

    public static void gotoServiceChat() {
        IM.gotoChat("1495673911154786306", "吱吱客服");
    }

    public static void gotoAudio(String... ids) {

        // 1. 初始化组件
        TUICallingManager manager = TUICallingManager.sharedInstance();
        // 2. 注册监听器
        manager.setCallingListener(new TUICalling.TUICallingListener() {
            @Override
            public boolean shouldShowOnCallView() {
                MyFunctionKt.log(this, "展示呼叫界面......");
                return false;
            }

            @Override
            public void onCallStart(String[] userIDs, TUICalling.Type type, TUICalling.Role role, View tuiCallingView) {
                MyFunctionKt.log(this, "开始通话......");
            }

            @Override
            public void onCallEnd(String[] userIDs, TUICalling.Type type, TUICalling.Role role, long totalTime) {
                MyFunctionKt.log(this, "结束通话......");
            }

            @Override
            public void onCallEvent(TUICalling.Event event, TUICalling.Type type, TUICalling.Role role, String message) {
                MyFunctionKt.log(this, "通话事件......"+message);
            }
        });
        if (ids.length > 0) {
            // 3.拨打电话
            manager.call(ids, TUICalling.Type.AUDIO);
        }
    }

    public static void gotoVideo(String... ids) {

        // 1. 初始化组件
        TUICallingManager manager = TUICallingManager.sharedInstance();
        // 2. 注册监听器
        manager.setCallingListener(new TUICalling.TUICallingListener() {
            @Override
            public boolean shouldShowOnCallView() {
                return false;
            }

            @Override
            public void onCallStart(String[] userIDs, TUICalling.Type type, TUICalling.Role role, View tuiCallingView) {
                MyFunctionKt.log(this, "onCallStart:========" + userIDs + "========TUICalling.Role" + role);
            }

            @Override
            public void onCallEnd(String[] userIDs, TUICalling.Type type, TUICalling.Role role, long totalTime) {
                MyFunctionKt.log(this, "onCallEnd:========" + userIDs + "========totalTime" + totalTime);
            }

            @Override
            public void onCallEvent(TUICalling.Event event, TUICalling.Type type, TUICalling.Role role, String message) {
                MyFunctionKt.log(this, "onCallEvent:========" + message);
            }
        });
        if (ids.length > 0) {
            // 3.拨打电话
            manager.call(ids, TUICalling.Type.VIDEO);
        }
    }

}
