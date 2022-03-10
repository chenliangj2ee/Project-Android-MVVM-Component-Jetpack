package io.agora.vlive;

import com.google.gson.Gson;
import com.mtjk.BaseInit;
import com.mtjk.bean.BeanUser;
import com.mtjk.utils.MyFunctionKt;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.SendMessageOptions;
import io.agora.vlive.agora.rtm.RtmMessageManager;
import io.agora.vlive.agora.rtm.model.SeatInteractionMessage;
import io.agora.vlive.agora.rtm.model.SeatStateMessage;
import io.agora.vlive.bean.BeanAudience;
import io.agora.vlive.bean.BeanLinkUser;
import io.agora.vlive.protocol.model.types.SeatInteraction;
import io.agora.vlive.ui.activity.LiveBaseActivity;
import io.agora.vlive.ui.view.LiveMultiHostSeatLayout2;

/**
 * author:chenliang
 * date:2022/2/8
 */
public class Live {

    public static List<SeatStateMessage.SeatStateMessageDataItem> seats = new ArrayList<>();

    /**
     * 被选中的画板邀请人
     */
    public static List<String> drawUserIds = new ArrayList<>();

    /**
     * 所有在线用户
     */
    public static List<BeanAudience> audienceObjectList = new ArrayList<>();

    /**
     *绘画中的用户集合
     */
    public static List<BeanAudience> drawingUsers = new ArrayList<>();

    static {
        for (int i = 0; i < LiveMultiHostSeatLayout2.MAX_SEAT; i++) {
            seats.add(new SeatStateMessage.SeatStateMessageDataItem());
        }
    }


    public static void initSeats(){
        seats.clear();
        drawingUsers.clear();
        drawUserIds.clear();
        for (int i = 0; i < LiveMultiHostSeatLayout2.MAX_SEAT; i++) {
            seats.add(new SeatStateMessage.SeatStateMessageDataItem());
        }
    }

    public static boolean setSeat(BeanLinkUser user) {

        boolean add = false;

        for (int i = 0; i < Live.seats.size(); i++) {
            if ("".equals(Live.seats.get(i).user.userId)) {
                Live.seats.get(i).seat.state = 1;
                Live.seats.get(i).seat.no = user.getIndex();
                Live.seats.get(i).user.userId = user.getUserId();
                Live.seats.get(i).user.role = 2;
                Live.seats.get(i).user.uid = user.getUid();
                Live.seats.get(i).user.userName = user.getNickName();
                Live.seats.get(i).user.enableVideo = 1;
                Live.seats.get(i).user.enableAudio = 1;
                add = true;
                break;
            }
        }
        return add;

    }


    public static int linkedCount() {

        int count = 0;
        for (int i = 0; i < Live.seats.size(); i++) {
            if (Live.seats.get(i).user.uid != 0) {
                count++;
            }
        }
        MyFunctionKt.log("", "已连麦人数：" + count);
        return count;
    }


    public static void sendPeer(LiveBaseActivity activity, String userId, int type, int role, int cmd, int index, ResultCallback callback) {
        SendMessageOptions option = new SendMessageOptions();
        option.enableOfflineMessaging = true;
        activity.rtmClient().sendMessageToPeer(userId, createMessage(activity, type, role, cmd, index), option, callback);
    }


    private static RtmMessage createMessage(LiveBaseActivity activity, int type, int role, int cmd, int index) {
        BeanUser user = new BeanUser().get();
        SeatInteractionMessage text = new SeatInteractionMessage();
        text.cmd = cmd;
        text.version = 1;
        if (type == SeatInteraction.ANCHOR_UID) {
            text.seats = Live.seats;
        }
        text.data.type = type;
        text.data.fromUser.userId = user.getUserId();//登录用户id
        text.data.fromUser.uid = activity.getUid();
        text.data.fromUser.role = role;
        text.data.fromUser.avatar = user.getAvatar();
        text.data.fromUser.userName = BaseInit.INSTANCE.isUserApp() ? user.getNickName() : user.getRealName();
        text.data.no = index;
        RtmMessage msg = activity.rtmClient().createMessage();
        msg.setText(new Gson().toJson(text));
        MyFunctionKt.log(activity, "发起信令：" + new Gson().toJson(msg));
        return msg;
    }

    public static void sendSentMessage(LiveBaseActivity activity) {
        SeatStateMessage message = new SeatStateMessage();
        message.cmd = RtmMessageManager.CHANNEL_MSG_TYPE_SEAT;
        message.data = seats;

        MyFunctionKt.log(activity, "发送消息:" + new Gson().toJson(message));
        activity.getMessageManager().sendChannelMessage(new Gson().toJson(message), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                MyFunctionKt.log(this, "发送消息成功");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                MyFunctionKt.log(this, "发送消息失败");
            }
        });
    }

    public static void sendDrawInviteMessage(LiveBaseActivity activity) {
        SeatStateMessage message = new SeatStateMessage();
        message.cmd = RtmMessageManager.LIVE_DRAWING_INVITE;
        message.drawUserIds = Live.drawUserIds;
        MyFunctionKt.log(activity, "发送画板邀请:" + new Gson().toJson(message));
        activity.getMessageManager().sendChannelMessage(new Gson().toJson(message), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                MyFunctionKt.log(this, "发送消息成功");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                MyFunctionKt.log(this, "发送消息失败");
            }
        });
    }
    public static void sendDrawCloseMessage(LiveBaseActivity activity) {
        SeatStateMessage message = new SeatStateMessage();
        message.cmd = RtmMessageManager.LIVE_DRAWING_CLOSE;
        MyFunctionKt.log(activity, "发送画板关闭:" + new Gson().toJson(message));
        activity.getMessageManager().sendChannelMessage(new Gson().toJson(message), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                MyFunctionKt.log(this, "发送关闭成功");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                MyFunctionKt.log(this, "发送关闭失败");
            }
        });
    }

    public static void sendSentToPeer(LiveBaseActivity activity, String userId, ResultCallback callback) {
        SendMessageOptions option = new SendMessageOptions();
        option.enableOfflineMessaging = true;
        BeanUser user = new BeanUser().get();
        SeatInteractionMessage text = new SeatInteractionMessage();
        text.cmd = RtmMessageManager.PEER_MSG_TYPE_SEAT;
        text.version = 1;
        text.seats = Live.seats;
        text.data.type = SeatInteraction.ANCHOR_TO_USER_SEAT;
        text.data.fromUser.userId = user.getUserId();//登录用户id
        text.data.fromUser.uid = activity.getUid();
        text.data.fromUser.role = 2;
        text.data.fromUser.avatar = user.getAvatar();
        text.data.fromUser.userName = BaseInit.INSTANCE.isUserApp() ? user.getNickName() : user.getRealName();
        text.data.no = 0;
        RtmMessage msg = activity.rtmClient().createMessage();
        msg.setText(new Gson().toJson(text));
        activity.rtmClient().sendMessageToPeer(userId, msg, option, callback);
    }

    public static void sendSentMessage(LiveBaseActivity activity, ResultCallback callback) {
        SeatStateMessage message = new SeatStateMessage();
        message.cmd = RtmMessageManager.CHANNEL_MSG_TYPE_SEAT;
        message.data = seats;

        MyFunctionKt.log(activity, "发送消息:" + new Gson().toJson(message));
        activity.getMessageManager().sendChannelMessage(new Gson().toJson(message), callback);
    }

    public static void stopLiveMessage(LiveBaseActivity activity, ResultCallback callback) {
        SeatStateMessage message = new SeatStateMessage();
        message.cmd = RtmMessageManager.CHANNEL_MSG_TYPE_LEAVE;
        activity.getMessageManager().sendChannelMessage(new Gson().toJson(message), callback);
    }


    public static void sendRtmZhuboIntoMessage(LiveBaseActivity activity, int uid,ResultCallback callback) {
        SeatStateMessage message = new SeatStateMessage();
        message.cmd = RtmMessageManager.LIVE_ZHUBO_UID_UPDATE;
        message.uid =uid;
        MyFunctionKt.log(activity, "主播进入房间，群发UID消息:" + new Gson().toJson(message));
        activity.getMessageManager().sendChannelMessage(new Gson().toJson(message), callback);
    }

    public static void sendRtmUserChangeMessage(LiveBaseActivity activity, ResultCallback callback) {
        SeatStateMessage message = new SeatStateMessage();
        message.cmd = RtmMessageManager.CHANNEL_MSG_USER_JOINED;

        //提供给苹果使用
        BeanUser user = new BeanUser().get();
        message.fromUserId = user.getUserId();

        MyFunctionKt.log(activity, "进入房间，发送消息:" + new Gson().toJson(message));
        activity.getMessageManager().sendChannelMessage(new Gson().toJson(message), callback);
    }

    public static void sendMessage(LiveBaseActivity activity, String content) {

        Config.UserProfile profile = activity.config().getUserProfile();
        activity.getMessageManager().sendChatMessage(profile.getUserId(),
                profile.getUserName(), content, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {

                    }
                });
    }

    public static void sendRtmUserExitRoomMessage(LiveBaseActivity activity, ResultCallback callback) {
        SeatStateMessage message = new SeatStateMessage();
        message.cmd = RtmMessageManager.CHANNEL_MSG_USER_EXIT_ROOM;

        //提供给苹果使用
        BeanUser user = new BeanUser().get();
        message.fromUserId = user.getUserId();

        MyFunctionKt.log(activity, "离开房间，发送消息:" + new Gson().toJson(message));
        activity.getMessageManager().sendChannelMessage(new Gson().toJson(message), callback);
    }


}
