package io.agora.vlive.agora.rtm.model;

import java.util.ArrayList;
import java.util.List;

public class SeatStateMessage extends AbsRtmMessage {
    public List<SeatStateMessageDataItem> data;
    public List<String> drawUserIds;

    public static class SeatStateMessageDataItem {
        public SeatState seat = new SeatState();
        public UserState user = new UserState();
    }

    public static class SeatState {
        public int no;
        public int state;
    }

    public static class UserState {
        public String userId = "";
        public String userName;
        public String avatar;
        public int uid;
        public int anchorCloseAudio = 1;//0:主播关闭，1：正常，2:自己关闭
        public int anchorCloseVideo = 1;//0:主播关闭，1：正常，2:自己关闭
        public int enableAudio;//0:主播关闭，1：正常，2:自己关闭
        public int enableVideo;//0:主播关闭，1：正常，2:自己关闭
        public int role = 1;
    }
}
