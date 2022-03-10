package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mtjk.bean.BeanUser;
import com.mtjk.utils.MyFunctionKt;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TextMessageBean;
import com.tencent.qcloud.tuikit.tuichat.component.face.FaceManager;

public class TextMessageHolder extends MessageContentHolder {

    private TextView msgBodyText;

    private BeanUser user = new BeanUser().get();

    public TextMessageHolder(View itemView) {
        super(itemView);
        msgBodyText = itemView.findViewById(R.id.msg_body_tv);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_text;
    }

    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {
        if (!(msg instanceof TextMessageBean)) {
            return;
        }
        TextMessageBean textMessageBean = (TextMessageBean) msg;

        msgBodyText.setVisibility(View.VISIBLE);
        if (textMessageBean.getText() != null) {
            FaceManager.handlerEmojiText(msgBodyText, textMessageBean.getText(), false);
        } else if (!TextUtils.isEmpty(textMessageBean.getExtra())) {
            FaceManager.handlerEmojiText(msgBodyText, textMessageBean.getExtra(), false);
        } else {
            FaceManager.handlerEmojiText(msgBodyText, TUIChatService.getAppContext().getString(R.string.no_support_msg), false);
        }
        if (properties.getChatContextFontSize() != 0) {
            msgBodyText.setTextSize(properties.getChatContextFontSize());
        }


        Log.i("MyIm",textMessageBean.getSender()+"  "+user.getUserId());

        if (textMessageBean.getSender().equals(user.getUserId())) {
//            if (properties.getRightChatContentFontColor() != 0) {
                msgBodyText.setTextColor(Color.WHITE);
//            }
        } else {
//            if (properties.getLeftChatContentFontColor() != 0) {
            msgBodyText.setTextColor(Color.BLACK);
//            }
        }
    }

}
