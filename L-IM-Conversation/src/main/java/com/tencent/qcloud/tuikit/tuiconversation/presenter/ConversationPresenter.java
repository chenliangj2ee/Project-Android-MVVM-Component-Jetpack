package com.tencent.qcloud.tuikit.tuiconversation.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mtjk.utils.BusCode;
import com.mtjk.utils.MyFunctionKt;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuiconversation.TUIConversationService;
import com.tencent.qcloud.tuikit.tuiconversation.bean.ConversationInfo;
import com.tencent.qcloud.tuikit.tuiconversation.interfaces.ConversationEventListener;
import com.tencent.qcloud.tuikit.tuiconversation.model.ConversationProvider;
import com.tencent.qcloud.tuikit.tuiconversation.util.ConversationUtils;
import com.tencent.qcloud.tuikit.tuiconversation.ui.interfaces.IConversationListAdapter;
import com.tencent.qcloud.tuikit.tuiconversation.util.TUIConversationLog;
import com.tencent.qcloud.tuikit.tuiconversation.util.TUIConversationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import gorden.rxbus2.RxBus;

public class ConversationPresenter {
    private static final String TAG = ConversationPresenter.class.getSimpleName();

    private final static int GET_CONVERSATION_COUNT = 100;

    ConversationEventListener conversationEventListener;

    private final ConversationProvider provider;

    private IConversationListAdapter adapter;

    private final List<ConversationInfo> loadedConversationInfoList = new ArrayList<>();

    private int totalUnreadCount;

    public ConversationPresenter() {
        provider = new ConversationProvider();
    }

    public void setConversationListener() {
        conversationEventListener = new ConversationEventListener() {
            @Override
            public void deleteConversation(String chatId, boolean isGroup) {
                ConversationPresenter.this.deleteConversation(chatId, isGroup);
            }

            @Override
            public void deleteConversation(String conversationId) {
                ConversationPresenter.this.deleteConversation(conversationId);
            }

            @Override
            public void setConversationTop(String chatId, boolean isChecked, IUIKitCallback<Void> iuiKitCallBack) {
                ConversationPresenter.this.setConversationTop(chatId, isChecked, iuiKitCallBack);
            }

            @Override
            public boolean isTopConversation(String chatId) {
                return ConversationPresenter.this.isTopConversation(chatId);
            }

            @Override
            public int getUnreadTotal() {
                return totalUnreadCount;
            }

            @Override
            public void updateTotalUnreadMessageCount(long count) {
                ConversationPresenter.this.updateTotalUnreadMessageCount(count);
            }

            @Override
            public void onNewConversation(List<ConversationInfo> conversationList) {
                ConversationPresenter.this.onNewConversation(conversationList);
            }

            @Override
            public void onConversationChanged(List<ConversationInfo> conversationList) {
                ConversationPresenter.this.onConversationChanged(conversationList);
            }

            @Override
            public void onFriendRemarkChanged(String id, String remark) {
                ConversationPresenter.this.onFriendRemarkChanged(id, remark);
            }
        };
        TUIConversationService.getInstance().setConversationEventListener(conversationEventListener);
    }

    public void setAdapter(IConversationListAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * ??????????????????
     *
     * @param nextSeq ????????????????????????????????????????????? 0???????????????????????????????????????????????????????????? nextSeq
     */
    public void loadConversation(long nextSeq) {
        TUIConversationLog.i(TAG, "loadConversation");
        provider.loadConversation(nextSeq, GET_CONVERSATION_COUNT, new IUIKitCallback<List<ConversationInfo>>() {
            @Override
            public void onSuccess(List<ConversationInfo> conversationInfoList) {
//                MyFunctionKt.log(this,"????????????????????????....");
                TUIConversationLog.i(TAG, "loadConversation:nextSeq=0" + conversationInfoList);
                onLoadConversationCompleted(conversationInfoList);
                RxBus.get().send(BusCode.CONVERSATION_ITEM_NUM);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                MyFunctionKt.log(this,"????????????????????????....");
                if (adapter != null) {
                    adapter.onLoadingStateChanged(false);
                }
            }
        });
    }

    public void loadMoreConversation() {
        provider.loadMoreConversation(GET_CONVERSATION_COUNT, new IUIKitCallback<List<ConversationInfo>>() {
            @Override
            public void onSuccess(List<ConversationInfo> data) {
                MyFunctionKt.log(this,"??????????????????????????????....");
                onLoadConversationCompleted(data);
                RxBus.get().send(BusCode.CONVERSATION_ITEM_NUM);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                MyFunctionKt.log(this,"??????????????????????????????....");
                if (adapter != null) {
                    adapter.onLoadingStateChanged(false);
                }

            }
        });
    }

    private void onLoadConversationCompleted(List<ConversationInfo> conversationInfoList) {
        onNewConversation(conversationInfoList);
        if (adapter != null) {
            adapter.onLoadingStateChanged(false);
        }
        provider.getTotalUnreadMessageCount(new IUIKitCallback<Long>() {
            @Override
            public void onSuccess(Long data) {
                totalUnreadCount = data.intValue();
                Log.i("TUIConversationFragment", "conversationPresenter" + totalUnreadCount);
                updateUnreadTotal(totalUnreadCount);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    public boolean isLoadFinished() {
        return provider.isLoadFinished();
    }

    /**
     * ???????????????
     *
     * @param conversationInfoList ??????????????????
     */
    public void onNewConversation(List<ConversationInfo> conversationInfoList) {
        TUIConversationLog.i(TAG, "onNewConversation conversations:" + conversationInfoList);

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (ConversationInfo conversationInfo : conversationInfoList) {
            if (!ConversationUtils.isNeedUpdate(conversationInfo)) {
                TUIConversationLog.i(TAG, "onNewConversation conversationInfo " + conversationInfo.toString());
                infos.add(conversationInfo);
            }
        }
        if (infos.size() == 0) {
            return;
        }

        List<ConversationInfo> exists = new ArrayList<>();
        Iterator<ConversationInfo> iterator = infos.iterator();
        while (iterator.hasNext()) {
            ConversationInfo update = iterator.next();
            for (int i = 0; i < loadedConversationInfoList.size(); i++) {
                ConversationInfo cacheInfo = loadedConversationInfoList.get(i);
                // ??????
                if (cacheInfo.getConversationId().equals(update.getConversationId())) {
                    loadedConversationInfoList.set(i, update);
                    iterator.remove();
                    exists.add(update);
                    break;
                }
            }
        }

        // ???????????????????????????????????? recyclerview ?????????
        Collections.sort(infos);
        loadedConversationInfoList.addAll(infos);
        if (adapter != null) {
            Collections.sort(loadedConversationInfoList);
            adapter.onDataSourceChanged(loadedConversationInfoList);
            Log.i("setunread","ConversationPresenter=====onNewConversation");
            for (ConversationInfo info : infos) {
                int index = loadedConversationInfoList.indexOf(info);
                if (index != -1) {
                    adapter.onItemInserted(index);
                }
            }

            for (ConversationInfo info : exists) {
                int index = loadedConversationInfoList.indexOf(info);
                if (index != -1) {
                    adapter.onItemChanged(index);
                }
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param conversationInfoList ???????????????????????????
     */
    public void onConversationChanged(List<ConversationInfo> conversationInfoList) {

        TUIConversationLog.i(TAG, "onConversationChanged conversations:" + conversationInfoList);

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (ConversationInfo conversationInfo : conversationInfoList) {
            if (!ConversationUtils.isNeedUpdate(conversationInfo)) {
                TUIConversationLog.i(TAG, "onConversationChanged conversationInfo " + conversationInfo.toString());
                infos.add(conversationInfo);
            }
        }
        if (infos.size() == 0) {
            return;
        }
        Collections.sort(infos);
        HashMap<ConversationInfo, Integer> indexMap = new HashMap<>();
        for (int j = 0; j < infos.size(); j++) {
            ConversationInfo update = infos.get(j);
            for (int i = 0; i < loadedConversationInfoList.size(); i++) {
                ConversationInfo cacheInfo = loadedConversationInfoList.get(i);
                //??????????????????????????????????????????????????????
                if (cacheInfo.getConversationId().equals(update.getConversationId())) {
                    loadedConversationInfoList.set(i, update);
                    indexMap.put(update, i);
                    break;
                }
            }
        }
        if (adapter != null) {
            Collections.sort(loadedConversationInfoList);
            adapter.onDataSourceChanged(loadedConversationInfoList);
            int minRefreshIndex = Integer.MAX_VALUE;
            int maxRefreshIndex = Integer.MIN_VALUE;
            for (ConversationInfo info : infos) {
                Integer oldIndexObj = indexMap.get(info);
                if (oldIndexObj == null) {
                    continue;
                }
                int oldIndex = oldIndexObj;
                int newIndex = loadedConversationInfoList.indexOf(info);
                if (newIndex != -1) {
                    minRefreshIndex = Math.min(minRefreshIndex, Math.min(oldIndex, newIndex));
                    maxRefreshIndex = Math.max(maxRefreshIndex, Math.max(oldIndex, newIndex));
                }
            }
            int count;
            if (minRefreshIndex == maxRefreshIndex) {
                count = 1;
            } else {
                count = maxRefreshIndex - minRefreshIndex + 1;
            }
            if (count > 0 && maxRefreshIndex >= minRefreshIndex) {
                adapter.onItemRangeChanged(minRefreshIndex, count);
            }
        }
        RxBus.get().send(BusCode.CONVERSATION_ITEM_NUM);
    }

    public void updateTotalUnreadMessageCount(long totalUnreadCount) {
        this.totalUnreadCount = (int) totalUnreadCount;
        updateUnreadTotal(this.totalUnreadCount);
    }

    /**
     * ????????????????????????
     *
     * @param unreadTotal
     */
    public void updateUnreadTotal(int unreadTotal) {
        TUIConversationLog.i(TAG, "updateUnreadTotal:" + unreadTotal);
        totalUnreadCount = unreadTotal;
        HashMap<String, Object> param = new HashMap<>();
        param.put(TUIConstants.TUIConversation.TOTAL_UNREAD_COUNT, totalUnreadCount);
        TUICore.notifyEvent(TUIConstants.TUIConversation.EVENT_UNREAD, TUIConstants.TUIConversation.EVENT_SUB_KEY_UNREAD_CHANGED, param);
    }

    /**
     * ?????????????????????
     *
     * @param conversation
     */
    public void setConversationTop(final ConversationInfo conversation, final IUIKitCallback<Void> callBack) {
        TUIConversationLog.i(TAG, "setConversationTop" + "|conversation:" + conversation);
        final boolean setTop = !conversation.isTop();

        provider.setConversationTop(conversation.getConversationId(), setTop, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                conversation.setTop(setTop);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIConversationLog.e(TAG, "setConversationTop code:" + errCode + "|desc:" + errMsg);
                if (callBack != null) {
                    callBack.onError("setConversationTop", errCode, errMsg);
                }
            }
        });

    }

    /**
     * ??????????????????
     *
     * @param id    ??????ID
     * @param isTop ????????????
     */
    public void setConversationTop(String id, final boolean isTop, final IUIKitCallback<Void> callBack) {
        TUIConversationLog.i(TAG, "setConversationTop id:" + id + "|isTop:" + isTop);
        ConversationInfo conversation = null;
        for (int i = 0; i < loadedConversationInfoList.size(); i++) {
            ConversationInfo info = loadedConversationInfoList.get(i);
            if (info.getId().equals(id)) {
                conversation = info;
                break;
            }
        }
        if (conversation == null) {
            return;
        }
        final ConversationInfo conversationInfo = conversation;
        final String conversationId = conversation.getConversationId();
        provider.setConversationTop(conversationId, isTop, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                conversationInfo.setTop(isTop);
                TUIConversationUtils.callbackOnSuccess(callBack, null);
            }

            @Override
            public void onError(String module, int code, String desc) {
                TUIConversationLog.e(TAG, "setConversationTop code:" + code + "|desc:" + desc);
                if (callBack != null) {
                    callBack.onError("setConversationTop", code, desc);
                }
            }
        });
    }

    public boolean isTopConversation(String conversationID) {
        for (int i = 0; i < loadedConversationInfoList.size(); i++) {
            ConversationInfo info = loadedConversationInfoList.get(i);
            if (info.getId().equals(conversationID)) {
                return info.isTop();
            }
        }
        return false;
    }

    /**
     * ??????????????????????????????????????????imsdk?????????
     *
     * @param conversation ????????????
     */
    public void deleteConversation(ConversationInfo conversation) {
        TUIConversationLog.i(TAG, "deleteConversation conversation:" + conversation);
        if (conversation == null) {
            return;
        }
        provider.deleteConversation(conversation.getConversationId(), new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                ToastUtil.toastShortMessage("??????????????????");
                int index = loadedConversationInfoList.indexOf(conversation);
                boolean isRemove = loadedConversationInfoList.remove(conversation);
                if (adapter != null && isRemove && index != -1) {
                    adapter.onItemRemoved(index);
                }
                getUnreadListener();
                MyFunctionKt.send(0,BusCode.CONVERSATION_ITEM_NUM);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });

    }

    public void getUnreadListener() {
        //??????????????????????????????
        BackgroundTasks.getInstance().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
                            @Override
                            public void onSuccess(Long aLong) {
                                MyFunctionKt.sendSelf("" + aLong, BusCode.IM_UNREADNUM_CHANGE);
                                Log.i("TUIConversationFragment", "itemDelete" + aLong);
                            }

                            @Override
                            public void onError(int code, String desc) {

                            }
                        });
                    }
                }, 1000);
    }

    /**
     * ????????????
     *
     * @param conversation ????????????
     */
    public void clearConversationMessage(ConversationInfo conversation) {
        if (conversation == null || TextUtils.isEmpty(conversation.getConversationId())) {
            TUIConversationLog.e(TAG, "clearConversationMessage error: invalid conversation");
            return;
        }

        provider.clearHistoryMessage(conversation.getId(), conversation.isGroup(), new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                getUnreadListener();
                MyFunctionKt.send(0,BusCode.CONVERSATION_ITEM_NUM);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param id C2C???????????? userID???Group?????? ID
     */
    public void deleteConversation(String id, boolean isGroup) {
        ConversationInfo conversationInfo = null;
        for (int i = 0; i < loadedConversationInfoList.size(); i++) {
            ConversationInfo info = loadedConversationInfoList.get(i);
            if (isGroup == info.isGroup() && info.getId().equals(id)) {
                conversationInfo = info;
                break;
            }
        }
        deleteConversation(conversationInfo);
    }

    public void deleteConversation(String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            return;
        }
        ConversationInfo conversationInfo = null;
        for (int i = 0; i < loadedConversationInfoList.size(); i++) {
            ConversationInfo info = loadedConversationInfoList.get(i);
            if (info.getConversationId().equals(conversationId)) {
                conversationInfo = info;
                break;
            }
        }
        deleteConversation(conversationInfo);
    }

    public void onFriendRemarkChanged(String id, String remark) {
        for (int i = 0; i < loadedConversationInfoList.size(); i++) {
            ConversationInfo info = loadedConversationInfoList.get(i);
            if (info.getId().equals(id) && !info.isGroup()) {
                String title = info.getShowName();
                if (!TextUtils.isEmpty(remark)) {
                    title = remark;
                }
                info.setTitle(title);
                adapter.onDataSourceChanged(loadedConversationInfoList);
                if (adapter != null) {
                    adapter.onItemChanged(i);
                }
                break;
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    public void updateAdapter() {
        if (adapter != null) {
            adapter.onViewNeedRefresh();
        }
    }

}