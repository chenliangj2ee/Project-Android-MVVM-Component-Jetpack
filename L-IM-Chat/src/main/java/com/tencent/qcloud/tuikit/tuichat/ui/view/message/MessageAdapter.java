package com.tencent.qcloud.tuikit.tuichat.ui.view.message;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TipsMessageBean;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.ICommonMessageAdapter;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.OnItemLongClickListener;
import com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder.MessageContentHolder;
import com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder.MessageHeaderHolder;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.IMessageAdapter;
import com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder.MessageBaseHolder;
import com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder.MessageViewHolderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder.MessageBaseHolder.MSG_TYPE_HEADER_VIEW;


public class MessageAdapter extends RecyclerView.Adapter implements IMessageAdapter, ICommonMessageAdapter {

    private static final String TAG = MessageAdapter.class.getSimpleName();
    private boolean mLoading = true;
    private MessageRecyclerView mRecycleView;
    private List<TUIMessageBean> mDataSource = new ArrayList<>();
    private OnItemLongClickListener mOnItemLongClickListener;

    //消息转发
    private HashMap<String, Boolean> mSelectedPositions = new HashMap<String, Boolean>();
    private boolean isShowMultiSelectCheckBox = false;

    private int mHighShowPosition;

    private boolean isForwardMode = false;

    public void setForwardMode(boolean forwardMode) {
        isForwardMode = forwardMode;
    }

    //获得选中条目的结果，msgId
    public ArrayList<TUIMessageBean> getSelectedItem() {
        if (mSelectedPositions == null || mSelectedPositions.size() == 0) {
            return null;
        }
        ArrayList<TUIMessageBean> selectList = new ArrayList<>();
        for (int i = 0; i < getItemCount() - 1; i++) {
            if (isItemChecked(mDataSource.get(i).getId())) {
                selectList.add(mDataSource.get(i));
            }
        }

        return selectList;
    }

    public void setShowMultiSelectCheckBox(boolean show) {
        isShowMultiSelectCheckBox = show;

        if (!isShowMultiSelectCheckBox && mSelectedPositions != null) {
            mSelectedPositions.clear();
        }
    }

    //设置给定位置条目的选择状态
    public void setItemChecked(String msgID, boolean isChecked) {
        if (mSelectedPositions == null) {
            return;
        }

        mSelectedPositions.put(msgID, isChecked);
    }

    //根据位置判断条目是否选中
    private boolean isItemChecked(String id) {
        if (mSelectedPositions.size() <= 0) {
            return false;
        }

        if (mSelectedPositions.containsKey(id)) {
            return mSelectedPositions.get(id);
        } else {
            return false;
        }
    }

    public OnItemLongClickListener getOnItemClickListener() {
        return this.mOnItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public void setHighShowPosition(int mHighShowPosition) {
        this.mHighShowPosition = mHighShowPosition;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = MessageViewHolderFactory.getInstance(parent, this, viewType);
        if (holder instanceof MessageContentHolder) {
            ((MessageContentHolder) holder).isForwardMode = isForwardMode;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final TUIMessageBean msg = getItem(position);
        if (holder instanceof MessageBaseHolder) {
            final MessageBaseHolder baseHolder = (MessageBaseHolder) holder;
            baseHolder.setOnItemClickListener(mOnItemLongClickListener);
            String msgId = "";
            if (msg != null) {
                msgId = msg.getId();
            }

            switch (getItemViewType(position)) {
                case MSG_TYPE_HEADER_VIEW:
                    if (isForwardMode) {
                        ((MessageHeaderHolder) baseHolder).setLoadingStatus(false);
                    } else {
                        ((MessageHeaderHolder) baseHolder).setLoadingStatus(mLoading);
                    }
                    break;
                default:
                    if (position == mHighShowPosition) {
                        final Handler mHandlerData = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                baseHolder.mContentLayout.setBackgroundColor(TUIChatService.getAppContext().getResources().getColor(R.color.line));
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        baseHolder.mContentLayout.setBackgroundColor(TUIChatService.getAppContext().getResources().getColor(R.color.chat_background_color));
                                        mHighShowPosition = -1;
                                    }
                                }, 600);
                            }
                        };

                        mHandlerData.postDelayed(runnable, 200);
                    }
                    break;
            }
            baseHolder.layoutViews(msg, position);
            setCheckBoxStatus(position, msgId, baseHolder);
        }
    }

    // 设置多选框的选中状态和点击事件
    private void setCheckBoxStatus(final int position, final String msgId, MessageBaseHolder baseHolder) {
        if (baseHolder.mMutiSelectCheckBox == null) {
            return;
        }
        if (!isShowMultiSelectCheckBox) {
            baseHolder.mMutiSelectCheckBox.setVisibility(View.GONE);
        } else {
            baseHolder.mMutiSelectCheckBox.setVisibility(View.VISIBLE);

            //设置条目状态
            baseHolder.mMutiSelectCheckBox.setChecked(isItemChecked(msgId));
            //checkBox的监听
            baseHolder.mMutiSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isItemChecked(msgId)) {
                        setItemChecked(msgId, false);
                    } else {
                        setItemChecked(msgId, true);
                    }
                }
            });

            //条目view的监听
            baseHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isItemChecked(msgId)) {
                        setItemChecked(msgId, false);
                    } else {
                        setItemChecked(msgId, true);
                    }
                    notifyItemChanged(position);
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycleView = (MessageRecyclerView) recyclerView;
        mRecycleView.setItemViewCacheSize(5);
    }

    public void showLoading() {
        if (isForwardMode) {
            return;
        }
        if (mLoading) {
            return;
        }
        mLoading = true;
        notifyItemChanged(0);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof MessageContentHolder) {
            ((MessageContentHolder) holder).msgContentFrame.setBackground(null);
        }
    }

    @Override
    public void onViewNeedRefresh(final int type, TUIMessageBean locateMessage) {
        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoading = false;
                if (type == MessageRecyclerView.DATA_CHANGE_SCROLL_TO_POSITION) {
                    notifyDataSetChanged();
                    int position = getMessagePosition(locateMessage);
                    mRecycleView.scrollToPosition(position);
                    mRecycleView.setHighShowPosition(position);
                }
            }
        });
    }

    @Override
    public void onViewNeedRefresh(final int type, final int value) {
        BackgroundTasks.getInstance().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoading = false;
                if (type == MessageRecyclerView.DATA_CHANGE_TYPE_REFRESH) {
                    notifyDataSetChanged();
                    mRecycleView.scrollToEnd();
                } else if (type == MessageRecyclerView.DATA_CHANGE_TYPE_ADD_BACK) {
                    notifyItemRangeInserted(mDataSource.size() + 1, value);
                } else if (type == MessageRecyclerView.DATA_CHANGE_NEW_MESSAGE) {
                    notifyItemRangeInserted(mDataSource.size() + 1, value);
                    mRecycleView.onMsgAddBack();
                } else if (type == MessageRecyclerView.DATA_CHANGE_TYPE_UPDATE) {
                    notifyDataSetChanged();
                } else if (type == MessageRecyclerView.DATA_CHANGE_TYPE_ADD_FRONT) {
                    //加载条目为数0，只更新动画
                    if (value != 0) {
                        //加载过程中有可能之前第一条与新加载的最后一条的时间间隔不超过5分钟，时间条目需去掉，所以这里的刷新要多一个条目
                        if (getItemCount() > value) {
                            notifyItemRangeInserted(0, value);
                        } else {
                            notifyItemRangeInserted(0, value);
                        }
                    }
                } else if (type == MessageRecyclerView.DATA_CHANGE_TYPE_DELETE) {
                    notifyItemRemoved(value);
                    notifyDataSetChanged();
                } else if (type == MessageRecyclerView.DATA_CHANGE_TYPE_LOAD) {
                    notifyDataSetChanged();
                    RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager && mDataSource != null && !mDataSource.isEmpty()) {
                        ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(mDataSource.size() - 1, 0);
                    }
                }
                refreshLoadView();
            }
        }, 100);
    }

    private void refreshLoadView() {
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return MSG_TYPE_HEADER_VIEW;
        }
        TUIMessageBean msg = getItem(position);
        // 撤回消息显示为 TIPS 类型
        if (msg.getStatus() == TUIMessageBean.MSG_STATUS_REVOKE) {
            return TUIChatService.getInstance().getViewType(TipsMessageBean.class);
        }
        return TUIChatService.getInstance().getViewType(msg.getClass());
    }

    @Override
    public void onDataSourceChanged(List<TUIMessageBean> dataSource) {
        mDataSource = dataSource;
    }


    @Override
    public void onScrollToEnd() {
        if (mRecycleView != null) {
            mRecycleView.scrollToEnd();
        }
    }


    public int getMessagePosition(TUIMessageBean message) {
        int positon = 0;
        if (mDataSource == null || mDataSource.isEmpty()) {
            return positon;
        }

        for (int i = 0; i < mDataSource.size(); i++) {
            if (mDataSource.get(i) == message) {
                positon = i;
            }
        }
        return positon + 1;
    }

    public TUIMessageBean getItem(int position) {
        if (position == 0 || mDataSource == null || mDataSource.size() == 0) {
            return null;
        }
        if (position >= mDataSource.size() + 1) {
            return null;
        }
        return mDataSource.get(position - 1);
    }

}