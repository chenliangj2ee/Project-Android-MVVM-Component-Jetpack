package io.agora.vlive.ui.actionsheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtjk.utils.MyFunctionKt;

import java.util.ArrayList;
import java.util.List;

import io.agora.vlive.Live;
import io.agora.vlive.R;
import io.agora.vlive.protocol.model.response.AudienceListResponse;
import io.agora.vlive.protocol.model.response.AudienceListResponse.AudienceInfo;
import io.agora.vlive.utils.UserUtil;

/**
 * tag==邀请上麦/弹框
 */
public class InviteUserActionSheet extends AbstractActionSheet {
    /**
     * 记录正在邀约中的人
     */
    public static ArrayList<String> inviting = new ArrayList<>();

    public interface InviteUserActionSheetListener extends AbsActionSheetListener {
        void onActionSheetAudienceInvited(int seatId, String userId, String userName);
        void onCancelAudienceInvited(int seatId, String userId, String userName);
    }

    private InviteUserActionSheetListener mListener;
    private RoomUserAdapter mAdapter;
    private int mSeatNo;

    public void notifyDataSetChanged(){
        mAdapter.notifyDataSetChanged();
    }

    public InviteUserActionSheet(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(
                R.layout.action_room_host_in_audience_list, this, true);
        RecyclerView recyclerView = findViewById(R.id.live_room_action_sheet_host_in_audience_list_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        mAdapter = new RoomUserAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setActionSheetListener(AbsActionSheetListener listener) {
        if (listener instanceof InviteUserActionSheetListener) {
            mListener = (InviteUserActionSheetListener) listener;
        }
    }

    public void setSeatNo(int seat) {
        mSeatNo = seat;
    }

    public void append(List<AudienceInfo> userList) {
        mAdapter.append(userList);
    }

    private class RoomUserAdapter extends RecyclerView.Adapter<RoomUserViewHolder> {
        private List<AudienceInfo> mUserList = new ArrayList<>();

        void append(List<AudienceInfo> userList) {
            mUserList.clear();
            mUserList.addAll(userList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RoomUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RoomUserViewHolder(LayoutInflater.
                    from(getContext()).inflate(R.layout.action_invite_audience_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RoomUserViewHolder holder, final int position) {
            AudienceInfo info = mUserList.get(position);
            holder.name.setText(UserUtil.getUserText(info.userId, info.userName));
            holder.icon.setImageDrawable(UserUtil.getUserRoundIcon(getResources(), info.userId));
            holder.button.setOnClickListener(view -> {
                if (mListener != null) {
                    if (InviteUserActionSheet.inviting.contains(info.userId)) {
                        mListener.onCancelAudienceInvited(info.seatNo, info.userId, info.userName);
                        InviteUserActionSheet.inviting.remove(info.userId);
                    } else {
                        mListener.onActionSheetAudienceInvited(mSeatNo, info.userId, info.userName);
                        info.seatNo=mSeatNo;

                    }
                }

                notifyDataSetChanged();

            });
            holder.button.setVisibility(View.VISIBLE);
            for (int i = 0; i < Live.seats.size(); i++) {
                if (mUserList.get(position).userId.equals(Live.seats.get(i).user.userId)) {
                    holder.button.setVisibility(View.GONE);
                }
            }

            MyFunctionKt.log(this,"inviting:"+inviting.size());
            if (InviteUserActionSheet.inviting.contains(info.userId)) {
                holder.button.setText("取消邀请");
            }else{
                holder.button.setText("邀请上麦");
            }

        }

        @Override
        public int getItemCount() {
            return mUserList == null || mUserList.isEmpty() ? 0 : mUserList.size();
        }
    }

    private static class RoomUserViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView icon;
        AppCompatTextView name;
        AppCompatButton button;

        RoomUserViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.live_room_action_sheet_user_list_item_icon);
            name = itemView.findViewById(R.id.live_room_action_sheet_user_list_item_name);
            button = itemView.findViewById(R.id.live_room_action_sheet_user_list_item_invite_btn);
        }
    }
}