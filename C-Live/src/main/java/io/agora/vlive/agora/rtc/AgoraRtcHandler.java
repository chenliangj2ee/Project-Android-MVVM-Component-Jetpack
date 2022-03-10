package io.agora.vlive.agora.rtc;

import com.mtjk.bean.BeanUser;
import com.mtjk.utils.BusCode;
import com.mtjk.utils.MyFunctionKt;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.vlive.AgoraLiveApplication;

public class AgoraRtcHandler extends IRtcEngineEventHandler {
    private List<RtcEventHandler> mHandlers;

    public AgoraRtcHandler() {
        mHandlers = new ArrayList<>();
    }

    public void registerEventHandler(RtcEventHandler handler) {
        if (!mHandlers.contains(handler)) {
            mHandlers.add(handler);
        }
    }

    public void removeEventHandler(RtcEventHandler handler) {
        mHandlers.remove(handler);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
//        MyFunctionKt.log(this, "RTC onJoinChannelSuccess，channel：" + channel + "  uid:" + uid);
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcJoinChannelSuccess(channel, uid, elapsed);
        }
    }


    @Override
    public void onUserJoined(int i, int i1) {
        super.onUserJoined(i, i1);
        MyFunctionKt.sendSelf(i + "", BusCode.LIVE_JOIN);
    }

    @Override
    public void onLocalUserRegistered(int i, String s) {
        super.onLocalUserRegistered(i, s);
    }

    @Override
    public void onError(int i) {
        super.onError(i);
        if(i==110) {
            BeanUser user = new BeanUser().get();
            AgoraLiveApplication.app.rtcEngine().renewToken(user.getRtcToken());
        }
        MyFunctionKt.log(this, "RTC onError。。。。。。。。" + i);
    }

    @Override
    public void onWarning(int i) {
        super.onWarning(i);
        MyFunctionKt.log(this, "RTC onWarning。。。。。。。。" + i);
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcRemoteVideoStateChanged(uid, state, reason, elapsed);
        }
    }

    @Override
    public void onRtcStats(RtcStats stats) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcStats(stats);
        }
    }

    @Override
    public void onChannelMediaRelayStateChanged(int state, int code) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcChannelMediaRelayStateChanged(state, code);
        }
    }

    @Override
    public void onChannelMediaRelayEvent(int code) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcChannelMediaRelayEvent(code);
        }
    }

    @Override
    public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcAudioVolumeIndication(speakers, totalVolume);
        }
    }

    @Override
    public void onAudioRouteChanged(int routing) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcAudioRouteChanged(routing);
        }
    }
}
