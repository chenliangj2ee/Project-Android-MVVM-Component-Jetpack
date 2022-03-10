package com.tencent.qcloud.tuikit.tuicontact.bean;

import com.tencent.imsdk.BaseConstants;
import com.tencent.imsdk.v2.V2TIMFriendApplication;

import java.io.Serializable;

public class FriendApplicationBean implements Serializable {
    /**
     * ## 别人发给我的加好友请求
     */
    public static final int FRIEND_APPLICATION_COME_IN = V2TIMFriendApplication.V2TIM_FRIEND_APPLICATION_COME_IN;

    /**
     * ## 我发给别人的加好友请求
     */
    public static final int FRIEND_APPLICATION_SEND_OUT = V2TIMFriendApplication.V2TIM_FRIEND_APPLICATION_SEND_OUT;

    /**
     * ## 别人发给我的和我发给别人的加好友请求。仅在拉取时有效。
     */
    public static final int FRIEND_APPLICATION_BOTH = V2TIMFriendApplication.V2TIM_FRIEND_APPLICATION_BOTH;

    /**
     * ## 同意加好友（建立单向好友）
     */
    public static final int FRIEND_ACCEPT_AGREE = V2TIMFriendApplication.V2TIM_FRIEND_ACCEPT_AGREE;

    /**
     * ## 同意加好友并加对方为好友（建立双向好友）
     */
    public static final int FRIEND_ACCEPT_AGREE_AND_ADD = V2TIMFriendApplication.V2TIM_FRIEND_ACCEPT_AGREE_AND_ADD;

    public static final int ERR_SUCC = BaseConstants.ERR_SUCC;
    public static final int ERR_SVR_FRIENDSHIP_INVALID_PARAMETERS = BaseConstants.ERR_SVR_FRIENDSHIP_INVALID_PARAMETERS;
    public static final int ERR_SVR_FRIENDSHIP_COUNT_LIMIT = BaseConstants.ERR_SVR_FRIENDSHIP_COUNT_LIMIT;
    public static final int ERR_SVR_FRIENDSHIP_PEER_FRIEND_LIMIT = BaseConstants.ERR_SVR_FRIENDSHIP_PEER_FRIEND_LIMIT;
    public static final int ERR_SVR_FRIENDSHIP_IN_SELF_BLACKLIST = BaseConstants.ERR_SVR_FRIENDSHIP_IN_SELF_BLACKLIST;
    public static final int ERR_SVR_FRIENDSHIP_ALLOW_TYPE_DENY_ANY = BaseConstants.ERR_SVR_FRIENDSHIP_ALLOW_TYPE_DENY_ANY;
    public static final int ERR_SVR_FRIENDSHIP_IN_PEER_BLACKLIST = BaseConstants.ERR_SVR_FRIENDSHIP_IN_PEER_BLACKLIST;
    public static final int ERR_SVR_FRIENDSHIP_ALLOW_TYPE_NEED_CONFIRM = BaseConstants.ERR_SVR_FRIENDSHIP_ALLOW_TYPE_NEED_CONFIRM;

    private String userId;
    private String nickName;
    private String addWording;
    private int addType;
    private V2TIMFriendApplication friendApplication;
    public FriendApplicationBean() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddWording() {
        return addWording;
    }

    public void setAddWording(String addWording) {
        this.addWording = addWording;
    }

    public int getAddType() {
        return addType;
    }

    public void setAddType(int addType) {
        this.addType = addType;
    }

    public void setFriendApplication(V2TIMFriendApplication friendApplication) {
        this.friendApplication = friendApplication;
    }

    public V2TIMFriendApplication getFriendApplication() {
        return friendApplication;
    }

    public FriendApplicationBean convertFromTimFriendApplication(V2TIMFriendApplication v2TIMFriendApplication) {
        if (v2TIMFriendApplication == null) {
            return this;
        }
        setAddType(v2TIMFriendApplication.getType());
        setNickName(v2TIMFriendApplication.getNickname());
        setAddWording(v2TIMFriendApplication.getAddWording());
        setUserId(v2TIMFriendApplication.getUserID());
        setFriendApplication(v2TIMFriendApplication);
        return this;
    }
}
