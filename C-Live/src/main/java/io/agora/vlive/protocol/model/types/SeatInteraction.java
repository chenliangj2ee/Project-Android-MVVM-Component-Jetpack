package io.agora.vlive.protocol.model.types;

/**
 * tag==信令值
 */
public class SeatInteraction {
    public static final int OWNER_INVITE = 1;//主播邀请
    public static final int AUDIENCE_APPLY = 2;//观众申请
    public static final int OWNER_REJECT = 3;//主播拒绝
    public static final int AUDIENCE_REJECT = 4;//观众拒绝
    public static final int OWNER_ACCEPT = 5;//主播接受
    public static final int AUDIENCE_ACCEPT = 6;//观众接受
    public static final int OWNER_FORCE_LEAVE = 7;//主播离开
    public static final int HOST_LEAVE = 8;//观众下麦

    public static final int AUDIENCE_CANCEL_APPLY = 9;//观众取消连麦申请
    public static final int ANCHOR_UID = 10;//主播给进入的观众发送uid
    public static final int ANCHOR_TO_USER_SEAT = 11;//主播给进入的观众发送座位信息

    public static final int OWNER_CANCEL_INVITE = 12;//主播取消邀请

    public static final int LIVE_DRAWING_ACCEPT_INVITE = 13;//画板同意
    public static final int LIVE_DRAWING_REJECT_INVITE = 14;//画板拒绝


}
