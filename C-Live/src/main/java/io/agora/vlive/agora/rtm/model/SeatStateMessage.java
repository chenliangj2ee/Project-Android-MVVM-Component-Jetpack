package io.agora.vlive.agora.rtm.model;

import java.util.ArrayList;
import java.util.List;

public class SeatStateMessage extends AbsRtmMessage {
    public List<SeatStateMessageDataItem> data;
    public List<String> drawUserIds = new ArrayList<>();
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
        public int enableAudio;
        public int enableVideo;
        public int role = 1;
    }
}
