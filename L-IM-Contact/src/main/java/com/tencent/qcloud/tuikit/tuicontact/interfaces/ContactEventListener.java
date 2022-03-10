package com.tencent.qcloud.tuikit.tuicontact.interfaces;

import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.bean.FriendApplicationBean;

import java.util.List;

public abstract class ContactEventListener {
    /**
     * 好友申请新增通知，两种情况会收到这个回调：
     * 1. 自己申请加别人好友
     * 2. 别人申请加自己好友
     */
    public void onFriendApplicationListAdded(List<FriendApplicationBean> applicationList) {
    }

    /**
     * 好友申请删除通知，四种情况会收到这个回调
     * 1. 调用 deleteFriendApplication 主动删除好友申请
     * 2. 调用 refuseFriendApplication 拒绝好友申请
     * 3. 调用 acceptFriendApplication 同意好友申请且同意类型为 V2TIM_FRIEND_ACCEPT_AGREE 时
     * 4. 申请加别人好友被拒绝
     */
    public void onFriendApplicationListDeleted(List<String> userIDList) {
    }

    /**
     * 好友申请已读通知，如果调用 setFriendApplicationRead 设置好友申请列表已读，会收到这个回调（主要用于多端同步）
     */
    public void onFriendApplicationListRead() {
    }

    /**
     * 好友新增通知
     */
    public void onFriendListAdded(List<ContactItemBean> users) {
    }

    /**
     * 好友删除通知，，两种情况会收到这个回调：
     * 1. 自己删除好友（单向和双向删除都会收到回调）
     * 2. 好友把自己删除（双向删除会收到）
     */
    public void onFriendListDeleted(List<String> userList) {
    }

    /**
     * 黑名单新增通知
     */
    public void onBlackListAdd(List<ContactItemBean> infoList) {
    }

    /**
     * 黑名单删除通知
     */
    public void onBlackListDeleted(List<String> userList) {
    }

    /**
     * 好友资料更新通知
     */
    public void onFriendInfoChanged(List<ContactItemBean> infoList) {
    }
    /**
     * 好友资料更新通知
     */
    public void onFriendRemarkChanged(String id, String remark) {
    }
}
