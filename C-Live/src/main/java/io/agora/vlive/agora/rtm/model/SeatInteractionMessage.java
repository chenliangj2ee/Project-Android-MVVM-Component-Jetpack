package io.agora.vlive.agora.rtm.model;

import java.util.ArrayList;
import java.util.List;

import io.agora.vlive.bean.BeanLinkUser;

public class SeatInteractionMessage extends AbsRtmMessage {
    public int version;

    public List<SeatStateMessage.SeatStateMessageDataItem> seats;

    public SeatInteractionInfo data = new SeatInteractionInfo();

    public class SeatInteractionInfo {
        public int no;
        public int type;
        public long processId;
        public SeatInteractionFromUser fromUser = new SeatInteractionFromUser();
    }

    public class SeatInteractionFromUser {
        public String userId;
        public String userName;
        public String avatar;
        public int uid;
        public int role;
    }
}