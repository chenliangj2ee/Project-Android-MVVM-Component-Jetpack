package com.tencent.qcloud.tuikit.tuiconversation.ui.page;

import static com.tencent.imsdk.base.ThreadUtils.runOnUiThread;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chenliang.annotation.MyRoute;
import com.mtjk.BaseInit;
import com.mtjk.bean.BeanUser;
import com.mtjk.utils.BusCode;
import com.mtjk.utils.MyFunctionKt;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMConversationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.PopWindowUtil;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuiconversation.R;
import com.tencent.qcloud.tuikit.tuiconversation.bean.ConversationInfo;
import com.tencent.qcloud.tuicore.component.fragments.BaseFragment;
import com.tencent.qcloud.tuicore.component.action.PopActionClickListener;
import com.tencent.qcloud.tuicore.component.action.PopDialogAdapter;
import com.tencent.qcloud.tuicore.component.action.PopMenuAction;
import com.tencent.qcloud.tuikit.tuiconversation.presenter.ConversationPresenter;
import com.tencent.qcloud.tuikit.tuiconversation.ui.view.ConversationLayout;
import com.tencent.qcloud.tuikit.tuiconversation.ui.view.ConversationListLayout;
import com.tencent.qcloud.tuikit.tuiconversation.util.TUIConversationLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;

/*
 * tag==会话列表
 */
@MyRoute(mPath = "/im/main")
public class TUIConversationFragment extends BaseFragment {

    private View mBaseView;
    private ConversationLayout mConversationLayout;
    private ListView mConversationPopList;
    private PopDialogAdapter mConversationPopAdapter;
    private PopupWindow mConversationPopWindow;
    private List<PopMenuAction> mConversationPopActions = new ArrayList<>();
    private ConversationPresenter presenter;
    private TextView conversation_title;
    private String judgeTimeEnd;
    private View conversation_empty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.conversation_fragment, container, false);
        initView();
        //负责判断会话列表数据是否为空
        judege_Item_Countnum();
        RxBus.get().register(this);
        return mBaseView;
    }

    private void initView() {
        // 从布局文件中获取会话列表面板
        mConversationLayout = mBaseView.findViewById(R.id.conversation_layout);
        //conversation设置标题
        conversation_title = mBaseView.findViewById(R.id.conversation_title);
        conversation_empty = mBaseView.findViewById(R.id.conversation_empty);

        presenter = new ConversationPresenter();
        presenter.setConversationListener();

        mConversationLayout.setPresenter(presenter);

        // 会话列表面板的默认UI和交互初始化
        mConversationLayout.initDefault();
        // 通过API设置ConversataonLayout各种属性的样例，开发者可以打开注释，体验效果
        //ConversationLayoutSetting.customizeConversation(mConversationLayout);
        //点击会话列表的条目
        mConversationLayout.getConversationList().setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ConversationInfo conversationInfo) {
                //此处为demo的实现逻辑，更根据会话类型跳转到相关界面，开发者可根据自己的应用场景灵活实现
                startChatActivity(conversationInfo);
            }
        });
        //长按会话列表条目
        mConversationLayout.getConversationList().setOnItemLongClickListener(new ConversationListLayout.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, int position, ConversationInfo conversationInfo) {
                startPopShow(view, conversationInfo);
            }
        });
        initPopMenuAction();
        restoreConversationItemBackground();
        judgeTime();

    }
    @Subscribe(code = BusCode.CONVERSATION_ITEM_NUM)
    public void judege_Item_Countnum() {
//        MyFunctionKt.log(this,"获取会话列表数量.....");
        V2TIMManager.getConversationManager().getConversationList(0, 10, new V2TIMValueCallback<V2TIMConversationResult>() {
            @Override
            public void onError(int code, String desc) {
                TUIConversationLog.v("TUIConversationFragment", "loadConversation getConversationList error, code = " + code + ", desc = " + desc);
            }
            @Override
            public void onSuccess(V2TIMConversationResult v2TIMConversationResult) {
                List<V2TIMConversation> v2TIMConversationList = v2TIMConversationResult.getConversationList();
                //如果会话列表数组为空就展示默认图
                if (v2TIMConversationList.size()<1) {
                    conversation_empty.setVisibility(View.VISIBLE);
                    mConversationLayout.setVisibility(View.GONE);
                } else {
                    conversation_empty.setVisibility(View.GONE);
                    mConversationLayout.setVisibility(View.VISIBLE);
                }
//               MyFunctionKt.log(this, "onSuccess: v2TIMConversationList"+v2TIMConversationList.size());
            }
        });
    }

    public void judgeTime() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int time = Integer.parseInt(str);
        if (time >= 0 && time <= 6) {
            judgeTimeEnd = "凌晨好,";
        }
        if (time > 6 && time <= 12) {
            judgeTimeEnd = "上午好,";
        }
        if (time > 12 && time <= 13) {
            judgeTimeEnd = "中午好,";
        }
        if (time > 13 && time <= 18) {
            judgeTimeEnd = "下午好,";
        }
        if (time > 18 && time <= 24) {
            judgeTimeEnd = "晚上好,";
        }

        BeanUser beanUser = new BeanUser().get();
        if (beanUser != null && judgeTimeEnd != null) {
            if(BaseInit.INSTANCE.isUserApp()){
                conversation_title.setText(judgeTimeEnd + beanUser.getNickName());
            }else{
                conversation_title.setText(judgeTimeEnd + beanUser.getRealName());
            }

        }
    }


    public void restoreConversationItemBackground() {
        if (mConversationLayout.getConversationList().getAdapter() != null &&
                mConversationLayout.getConversationList().getAdapter().isClick()) {
            mConversationLayout.getConversationList().getAdapter().setClick(false);
            mConversationLayout.getConversationList().getAdapter().notifyItemChanged(mConversationLayout.getConversationList().getAdapter().getCurrentPosition());
        }
    }

    private void initPopMenuAction() {
        // 设置长按conversation显示PopAction
        List<PopMenuAction> conversationPopActions = new ArrayList<PopMenuAction>();
        PopMenuAction action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.chat_top));
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int index, Object data) {
                mConversationLayout.setConversationTop((ConversationInfo) data, new IUIKitCallback() {
                    @Override
                    public void onSuccess(Object data) {

                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        ToastUtil.toastLongMessage(module + ", Error code = " + errCode + ", desc = " + errMsg);
                    }
                });
            }
        });
        conversationPopActions.add(action);
        action = new PopMenuAction();
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int index, Object data) {
                mConversationLayout.deleteConversation((ConversationInfo) data);
            }
        });
        action.setActionName(getResources().getString(R.string.chat_delete));
        conversationPopActions.add(action);

        action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.clear_conversation_message));
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int index, Object data) {
                mConversationLayout.clearConversationMessage((ConversationInfo) data);
            }
        });
        conversationPopActions.add(action);

        mConversationPopActions.clear();
        mConversationPopActions.addAll(conversationPopActions);
    }

    /**
     * 长按会话item弹框
     *
     * @param conversationInfo 会话数据对象
     * @param locationX        长按时X坐标
     * @param locationY        长按时Y坐标
     */
    private void showItemPopMenu(final ConversationInfo conversationInfo, float locationX, float locationY) {
        if (mConversationPopActions == null || mConversationPopActions.size() == 0)
            return;
        View itemPop = LayoutInflater.from(getActivity()).inflate(R.layout.pop_menu_layout, null);
        mConversationPopList = itemPop.findViewById(R.id.pop_menu_list);
        mConversationPopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopMenuAction action = mConversationPopActions.get(position);
                if (action.getActionClickListener() != null) {
                    action.getActionClickListener().onActionClick(position, conversationInfo);
                }
                mConversationPopWindow.dismiss();
                restoreConversationItemBackground();
            }
        });

        for (int i = 0; i < mConversationPopActions.size(); i++) {
            PopMenuAction action = mConversationPopActions.get(i);
            if (conversationInfo.isTop()) {
                if (action.getActionName().equals(getResources().getString(R.string.chat_top))) {
                    action.setActionName(getResources().getString(R.string.quit_chat_top));
                }
            } else {
                if (action.getActionName().equals(getResources().getString(R.string.quit_chat_top))) {
                    action.setActionName(getResources().getString(R.string.chat_top));
                }

            }
        }
        mConversationPopAdapter = new PopDialogAdapter();
        mConversationPopList.setAdapter(mConversationPopAdapter);
        mConversationPopAdapter.setDataSource(mConversationPopActions);
        mConversationPopWindow = PopWindowUtil.popupWindow(itemPop, mBaseView, (int) locationX, (int) locationY);
        mConversationPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                restoreConversationItemBackground();
            }
        });
        BackgroundTasks.getInstance().postDelayed(new Runnable() {
            @Override
            public void run() {
                mConversationPopWindow.dismiss();
            }
        }, 10000); // 10s后无操作自动消失
    }

    private void startPopShow(View view, ConversationInfo info) {
        showItemPopMenu(info, view.getX(), view.getY() + view.getHeight() / 2);
    }

    //会话列表，点击跳转到聊天页面
    private void startChatActivity(ConversationInfo conversationInfo) {
        Bundle param = new Bundle();
        param.putInt(TUIConstants.TUIChat.CHAT_TYPE, conversationInfo.isGroup() ? V2TIMConversation.V2TIM_GROUP : V2TIMConversation.V2TIM_C2C);
        param.putString(TUIConstants.TUIChat.CHAT_ID, conversationInfo.getId());
        param.putString(TUIConstants.TUIChat.CHAT_NAME, conversationInfo.getTitle());
        if (conversationInfo.getDraft() != null) {
            param.putString(TUIConstants.TUIChat.DRAFT_TEXT, conversationInfo.getDraft().getDraftText());
            param.putLong(TUIConstants.TUIChat.DRAFT_TIME, conversationInfo.getDraft().getDraftTime());
        }
        param.putBoolean(TUIConstants.TUIChat.IS_TOP_CHAT, conversationInfo.isTop());

        if (conversationInfo.isGroup()) {
            param.putString(TUIConstants.TUIChat.FACE_URL, conversationInfo.getIconPath());
            param.putString(TUIConstants.TUIChat.GROUP_TYPE, conversationInfo.getGroupType());
        }
        if (conversationInfo.isGroup()) {
            TUICore.startActivity(TUIConstants.TUIChat.GROUP_CHAT_ACTIVITY_NAME, param);
        } else {
            TUICore.startActivity(TUIConstants.TUIChat.C2C_CHAT_ACTIVITY_NAME, param);
        }
    }

}
