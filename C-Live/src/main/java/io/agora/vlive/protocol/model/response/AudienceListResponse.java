package io.agora.vlive.protocol.model.response;

import java.util.List;

public class AudienceListResponse extends Response {
    public RoomProfile data;

    public class RoomProfile {
        public int count;
        public int total;
        public String next;
        public List<AudienceInfo> list;
    }

    public static class AudienceInfo {
        public String userId;
        public String userName;
        public String avatar;
        public String uid;
        public int seatNo;
        public boolean inviting = false;//邀请中
    }
}
