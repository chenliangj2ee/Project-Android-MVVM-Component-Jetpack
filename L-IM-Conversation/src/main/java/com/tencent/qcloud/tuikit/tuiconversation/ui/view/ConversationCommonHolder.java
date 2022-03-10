package com.tencent.qcloud.tuikit.tuiconversation.ui.view;

import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.component.UnreadCountTextView;
import com.tencent.qcloud.tuicore.util.DateTimeUtil;
import com.tencent.qcloud.tuikit.tuiconversation.R;
import com.tencent.qcloud.tuikit.tuiconversation.bean.ConversationInfo;
import com.tencent.qcloud.tuikit.tuiconversation.bean.DraftInfo;
import com.tencent.qcloud.tuikit.tuiconversation.component.face.FaceManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//会话列表item
public class ConversationCommonHolder extends ConversationBaseHolder {

    public ConversationIconView conversationIconView;
    protected LinearLayout leftItemLayout;
    protected TextView titleText;
    protected TextView messageText;
    protected TextView timelineText;
    protected UnreadCountTextView unreadText;
    protected TextView atInfoText;
    protected ImageView disturbView;
    protected CheckBox multiSelectCheckBox;
    private boolean isForwardMode = false;

    public ConversationCommonHolder(View itemView) {
        super(itemView);
        leftItemLayout = rootView.findViewById(R.id.item_left);
        conversationIconView = rootView.findViewById(R.id.conversation_icon);
        titleText = rootView.findViewById(R.id.conversation_title);
        messageText = rootView.findViewById(R.id.conversation_last_msg);
        timelineText = rootView.findViewById(R.id.conversation_time);
        unreadText = rootView.findViewById(R.id.conversation_unread);
        atInfoText = rootView.findViewById(R.id.conversation_at_msg);
        disturbView = rootView.findViewById(R.id.not_disturb);
        multiSelectCheckBox = rootView.findViewById(R.id.select_checkbox);
    }

    public void setForwardMode(boolean forwardMode) {
        isForwardMode = forwardMode;
    }

    public void layoutViews(ConversationInfo conversation, int position) {
        Log.i("ConversationCommonHolder", "layoutViews: "+conversation.getUnRead());
        if (conversation.isTop() && !isForwardMode) {
            leftItemLayout.setBackgroundColor(rootView.getResources().getColor(R.color.conversation_top_color));
        } else {
            leftItemLayout.setBackgroundColor(Color.WHITE);
        }

        titleText.setText(conversation.getTitle());
        messageText.setText("");
        timelineText.setText("");
        DraftInfo draftInfo = conversation.getDraft();
        if (draftInfo != null && !TextUtils.isEmpty(draftInfo.getDraftText())) {
            messageText.setText(draftInfo.getDraftText());
            timelineText.setText(DateTimeUtil.getTimeFormatText(new Date(draftInfo.getDraftTime() * 1000)));
        } else {
            HashMap<String, Object> param = new HashMap<>();
            param.put(TUIConstants.TUIChat.V2TIMMESSAGE, conversation.getLastMessage());
            String lastMsgDisplayString = (String) TUICore.callService(TUIConstants.TUIChat.SERVICE_NAME, TUIConstants.TUIChat.METHOD_GET_DISPLAY_STRING, param);
            // 如果最后一条消息是自定义消息, 获取要显示的字符
            if (lastMsgDisplayString != null) {
                String result = emojiJudge(lastMsgDisplayString);
                messageText.setText(Html.fromHtml(result));
                messageText.setTextColor(rootView.getResources().getColor(R.color.list_bottom_text_bg));
            }
            if (conversation.getLastMessage() != null) {
                timelineText.setText(DateTimeUtil.getTimeFormatText(new Date(conversation.getLastMessageTime() * 1000)));
            }
        }

        if (conversation.isShowDisturbIcon()) {
            Log.i("setunread","ConversationCommonHolder========layoutViews");
            if (conversation.getUnRead() > 0) {
                unreadText.setVisibility(View.VISIBLE);
                unreadText.setText("");

                if (messageText.getText() != null) {
                    String text = messageText.getText().toString();
                    messageText.setText("[" + conversation.getUnRead() + rootView.getContext().getString(R.string.message_num) + "] " + text);
                }
            } else {
                unreadText.setVisibility(View.GONE);
            }
        }else if (conversation.getUnRead() > 0) {
            unreadText.setVisibility(View.VISIBLE);
            if (conversation.getUnRead() > 99) {
                unreadText.setText("99+");
            } else {
                unreadText.setText("" + conversation.getUnRead());
            }
        } else {
            unreadText.setVisibility(View.GONE);
        }

        if (draftInfo != null && !TextUtils.isEmpty(draftInfo.getDraftText())) {
            atInfoText.setVisibility(View.VISIBLE);
            atInfoText.setText(R.string.drafts);
            atInfoText.setTextColor(Color.RED);
        } else {
            if (conversation.getAtInfoText().isEmpty()) {
                atInfoText.setVisibility(View.GONE);
            } else {
                atInfoText.setVisibility(View.VISIBLE);
                atInfoText.setText(conversation.getAtInfoText());
                atInfoText.setTextColor(Color.RED);
            }
        }

        conversationIconView.setRadius(mAdapter.getItemAvatarRadius());
        if (mAdapter.getItemDateTextSize() != 0) {
            timelineText.setTextSize(mAdapter.getItemDateTextSize());
        }
        if (mAdapter.getItemBottomTextSize() != 0) {
            messageText.setTextSize(mAdapter.getItemBottomTextSize());
        }
        if (mAdapter.getItemTopTextSize() != 0) {
            titleText.setTextSize(mAdapter.getItemTopTextSize());
        }
        if (!mAdapter.hasItemUnreadDot()) {
            unreadText.setVisibility(View.GONE);
        }

        conversationIconView.setConversation(conversation);

        if (conversation.isShowDisturbIcon() && !isForwardMode) {
            disturbView.setVisibility(View.VISIBLE);
        } else {
            disturbView.setVisibility(View.GONE);
        }

        if (isForwardMode) {
            messageText.setVisibility(View.GONE);
            timelineText.setVisibility(View.GONE);
            unreadText.setVisibility(View.GONE);
            atInfoText.setVisibility(View.GONE);
        }

        //// 由子类设置指定消息类型的views
        layoutVariableViews(conversation, position);
    }


    private static String emojiJudge(String text){
        if (TextUtils.isEmpty(text)){
            return "";
        }

        String[] emojiList = FaceManager.getEmojiFilters();
        if (emojiList ==null || emojiList.length == 0){
            return text;
        }

        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        ArrayList<EmojiData> emojiDataArrayList = new ArrayList<>();
        //遍历找到匹配字符并存储
        int lastMentionIndex = -1;
        while (m.find()) {
            String emojiName = m.group();
            int start;
            if (lastMentionIndex != -1) {
                start = text.indexOf(emojiName, lastMentionIndex);
            } else {
                start = text.indexOf(emojiName);
            }
            int end = start + emojiName.length();
            lastMentionIndex = end;

            int index = findeEmoji(emojiName);
            String[] emojiListValues = FaceManager.getEmojiFiltersValues();
            if (index != -1 && emojiListValues != null && emojiListValues.length >= index){
                emojiName = emojiListValues[index];
            }


            EmojiData emojiData =new EmojiData();
            emojiData.setStart(start);
            emojiData.setEnd(end);
            emojiData.setEmojiText(emojiName);

            emojiDataArrayList.add(emojiData);
        }

        //倒叙替换
        if (emojiDataArrayList.isEmpty()){
            return text;
        }
        for (int i = emojiDataArrayList.size() - 1; i >= 0; i--){
            EmojiData emojiData = emojiDataArrayList.get(i);
            String emojiName = emojiData.getEmojiText();
            int start = emojiData.getStart();
            int end = emojiData.getEnd();

            if (!TextUtils.isEmpty(emojiName) && start != -1 && end != -1) {
                sb.replace(start, end, emojiName);
            }
        }
        return sb.toString();
    }

    private static int findeEmoji(String text){
        int result = -1;
        if (TextUtils.isEmpty(text)){
            return result;
        }

        String[] emojiList = FaceManager.getEmojiFilters();
        if (emojiList ==null || emojiList.length == 0){
            return result;
        }

        for (int i = 0; i < emojiList.length; i++){
            if (text.equals(emojiList[i])){
                result = i;
                break;
            }
        }

        return result;
    }

    public void layoutVariableViews(ConversationInfo conversationInfo, int position) {

    }

    private static class EmojiData{
        private int start;
        private int end;
        private String emojiText;

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public String getEmojiText() {
            return emojiText;
        }

        public void setEmojiText(String emojiText) {
            this.emojiText = emojiText;
        }
    }
}
