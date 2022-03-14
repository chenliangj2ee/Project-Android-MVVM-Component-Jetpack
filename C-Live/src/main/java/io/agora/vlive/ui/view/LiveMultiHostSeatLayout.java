package io.agora.vlive.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.mtjk.bean.BeanUser;
import com.mtjk.utils.MyFunctionKt;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.vlive.R;
import io.agora.vlive.agora.rtm.model.SeatStateMessage;
import io.agora.vlive.protocol.model.model.SeatInfo;
import io.agora.vlive.utils.Global;
import io.agora.vlive.utils.UserUtil;

/**
 * tag==连麦座位
 */
public class LiveMultiHostSeatLayout extends RelativeLayout {
    private static final int MAX_SEAT = 5;


    public static class SeatItem {
        RelativeLayout layout;
        FrameLayout videoLayout;
        AppCompatImageView operationIcon;
        AppCompatTextView operationText;
        AppCompatTextView nickname;
        AppCompatImageView popup;
        AppCompatImageView voiceState;
        VoiceIndicateGifView voiceIndicate;
        SurfaceView surfaceView;
        public int position;
        public int seatState;
        public int audioMuteState;
        public int videoMuteState;

        // rtc uid used to indicate speaking volumes.
        int rtcUid;
        public String userName;
        public String userId;

        void startIndicate() {
            if (voiceIndicate != null &&
                    voiceIndicate.getVisibility() == View.VISIBLE) {
                voiceIndicate.start(Global.Constants.VOICE_INDICATE_INTERVAL);
            }
        }
    }

    public static final int SEAT_OPEN = 0;
    public static final int SEAT_TAKEN = 1;
    public static final int SEAT_CLOSED = 2;

    public static final int MUTE_NONE = 1;
    public static final int AUDIO_MUTED_BY_ME = 0;
    public static final int AUDIO_MUTED_BY_OWNER = 2;
    public static final int VIDEO_MUTED = 0;

    public List<SeatItem> mSeatList;
    private boolean mIsOwner;
    private boolean mIsHost;
    private String mMyUserId;
    private LiveHostInSeatOnClickedListener mListener;

    public LiveMultiHostSeatLayout(Context context) {
        super(context);
        init();
    }

    public LiveMultiHostSeatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.live_host_in_seat_layout, null, true);
        addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mSeatList = new ArrayList<>(MAX_SEAT);
        mSeatList.add(getItemLayout(findViewById(R.id.live_host_seat_item_1), 0, "1"));
        mSeatList.add(getItemLayout(findViewById(R.id.live_host_seat_item_2), 1, "2"));
        mSeatList.add(getItemLayout(findViewById(R.id.live_host_seat_item_3), 2, "3"));
        mSeatList.add(getItemLayout(findViewById(R.id.live_host_seat_item_4), 3, "4"));
        mSeatList.add(getItemLayout(findViewById(R.id.live_host_seat_item_5), 4, "5"));
        initSeats();
    }

    private SeatItem getItemLayout(RelativeLayout layout, int position, String id) {
        AppCompatTextView idText = layout.findViewById(R.id.host_in_seat_item_id);
        idText.setText(id);

        final SeatItem item = new SeatItem();
        item.layout = layout;
        item.position = position;
        item.videoLayout = layout.findViewById(R.id.host_in_seat_item_video_layout);
        item.operationIcon = layout.findViewById(R.id.seat_item_operation_icon);
        item.operationText = layout.findViewById(R.id.seat_item_operation_text);
        item.nickname = layout.findViewById(R.id.host_in_seat_item_nickname);
        item.popup = layout.findViewById(R.id.seat_item_owner_popup_btn);
        item.voiceState = layout.findViewById(R.id.host_in_seat_item_voice_state_icon);
        item.voiceIndicate = layout.findViewById(R.id.host_in_seat_item_voice_indicate);

        item.operationIcon.setOnClickListener(view -> {
            if (item.seatState == SEAT_OPEN && mListener != null) {
                if (mIsOwner) {//邀请上麦，先注释掉，后期在家
//                    mListener.onSeatAdapterHostInviteClicked(item.position, view);
                } else {
                    mListener.onSeatAdapterAudienceApplyClicked(item.position, view);
                }
            } else if (item.seatState == SEAT_CLOSED && mListener != null) {
                mListener.onSeatAdapterPositionClosed(item.position, view);
            }
        });

        item.popup.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onSeatAdapterMoreClicked(item.position, view, item.seatState, item.audioMuteState);
            }
        });

        return item;
    }

    private void initSeats() {
        for (SeatItem item : mSeatList) {
            item.seatState = SEAT_OPEN;
            item.audioMuteState = MUTE_NONE;
            item.videoMuteState = MUTE_NONE;
            refreshSeatStates(item.position, null, null);
        }

    }

    public void setOwner(boolean isOwner) {
        mIsOwner = isOwner;
    }

    public void setHost(boolean isHost) {
        mIsHost = isHost;
    }

    public void setMyUserId(String userId) {
        mMyUserId = userId;
    }

    public void setSeatListener(LiveHostInSeatOnClickedListener listener) {
        mListener = listener;
    }

    public SeatItem getSeatItem(int position) {
        return (0 <= position && position < MAX_SEAT) ? mSeatList.get(position) : null;
    }

    public SeatItem getSeatItem(@NonNull String userId) {
        for (SeatItem item : mSeatList) {
            if (item.seatState == SeatInfo.TAKEN &&
                    userId.equals(item.userId)) {
                return item;
            }
        }

        return null;
    }

    public void updateStates(@Nullable List<SeatStateMessage.SeatStateMessageDataItem> list) {
        for (int i = 0; i < MAX_SEAT; i++) {
            if (list == null) {
                refreshSeatStates(i, null, null);
            } else {
                SeatStateMessage.SeatStateMessageDataItem item = list.get(i);
                refreshSeatStates(i, item.seat, item.user);
            }
        }
    }

    private void refreshSeatStates(int position, @Nullable SeatStateMessage.SeatState seat,
                                   @Nullable SeatStateMessage.UserState user) {
        SeatItem curSeatState = mSeatList.get(position);
        int oldSeatState = curSeatState.seatState;
        int newSeatState = seat == null ? oldSeatState : seat.state;

        if (oldSeatState != newSeatState) {
            if (oldSeatState == SEAT_TAKEN && (newSeatState == SEAT_OPEN || newSeatState == SEAT_CLOSED)) {
                if (mListener != null && curSeatState.surfaceView != null) {
                    mListener.onSeatAdapterItemVideoRemoved(
                            curSeatState.position, curSeatState.rtcUid, curSeatState.surfaceView,
                            mMyUserId.equals(curSeatState.userId), false);
                }
                clearSeatStates(curSeatState);
                curSeatState.seatState = newSeatState;

                if (user != null) {
                    if (mListener != null && mMyUserId.equals(user.userId)) {
                        mListener.onSeatAdapterItemMyAudioMuted(position,
                                user.enableAudio != MUTE_NONE);
                    }
                    curSeatState.audioMuteState = user.enableAudio;
                }
            } else if (newSeatState == SEAT_TAKEN && oldSeatState == SEAT_OPEN && user != null) {
                SurfaceView surfaceView = mListener.onSeatAdapterItemVideoShowed(position,
                        user.uid, mMyUserId.equals(user.userId),
                        user.enableAudio != MUTE_NONE, false);
                ;
                if (mListener != null && user.enableVideo == MUTE_NONE) {

                } else if (user.enableVideo == VIDEO_MUTED) {
                    // The new host's video is previously muted
                    if (mListener != null) {
                        if (curSeatState.surfaceView != null)
                            mListener.onSeatAdapterItemVideoRemoved(
                                    curSeatState.position, curSeatState.rtcUid, curSeatState.surfaceView,
                                    mMyUserId.equals(curSeatState.userId), true);
                    }


                }

                setSeatStates(curSeatState, seat, user, surfaceView);

                if (mListener != null && mMyUserId.equals(user.userId)) {
                    mListener.onSeatAdapterItemMyAudioMuted(position,
                            user.enableAudio != MUTE_NONE);
                }
                curSeatState.audioMuteState = user.enableAudio;
            } else {
                curSeatState.seatState = newSeatState;
            }
        } else if (newSeatState == SEAT_TAKEN) {
            int oldAudioState = curSeatState.audioMuteState;
            int oldVideoState = curSeatState.videoMuteState;
            int newAudioState = oldAudioState;
            int newVideoState = oldVideoState;
            if (user != null) {
                newAudioState = user.enableAudio;
                newVideoState = user.enableVideo;
            }

            if (newAudioState != oldAudioState) {
                if (mListener != null && mMyUserId.equals(user.userId)) {
                    mListener.onSeatAdapterItemMyAudioMuted(position,
                            newAudioState != MUTE_NONE);
                }
                curSeatState.audioMuteState = newAudioState;
            }

            if (newVideoState != oldVideoState) {
                if (newVideoState == MUTE_NONE) {
                    SurfaceView surfaceView = null;
                    if (mListener != null) {
                        surfaceView = mListener.onSeatAdapterItemVideoShowed(
                                position, curSeatState.rtcUid,
                                mMyUserId.equals(curSeatState.userId),
                                newAudioState != MUTE_NONE,
                                newVideoState != MUTE_NONE);
                    }

                    setSeatStates(curSeatState, seat, user, surfaceView);
                } else if (newVideoState == VIDEO_MUTED) {
                    if (mListener != null && curSeatState.surfaceView != null) {
                        mListener.onSeatAdapterItemVideoRemoved(
                                curSeatState.position, curSeatState.rtcUid,
                                curSeatState.surfaceView,
                                mMyUserId.equals(curSeatState.userId), true);
                    }

                    curSeatState.videoMuteState = user.enableVideo;
                }
            }
        }
        showUserProfile(curSeatState, user);
        updateItemUI(curSeatState);
    }

    private void setSeatStates(@NonNull SeatItem seatState, SeatStateMessage.SeatState seat,
                               SeatStateMessage.UserState user, SurfaceView surfaceView) {
        if (surfaceView != null && seatState.videoLayout != null) {
            seatState.surfaceView = surfaceView;
            seatState.videoLayout.removeAllViews();
            seatState.videoLayout.addView(surfaceView);
        }

        seatState.userId = user.userId;
        seatState.userName = user.userName;
        seatState.rtcUid = user.uid;
        seatState.videoMuteState = user.enableVideo;
        seatState.audioMuteState = user.enableAudio;
        seatState.seatState = seat.state;
    }

    private void clearSeatStates(@NonNull SeatItem seatState) {
        if (containsChild(seatState.videoLayout, seatState.surfaceView)) {
            seatState.videoLayout.removeView(seatState.surfaceView);
            seatState.surfaceView = null;
        }

        seatState.userId = null;
        seatState.userName = null;
        seatState.rtcUid = 0;
        seatState.videoMuteState = MUTE_NONE;
        seatState.audioMuteState = MUTE_NONE;
        seatState.seatState = SEAT_OPEN;
    }

    private void showUserProfile(@NonNull SeatItem seatState, @Nullable SeatStateMessage.UserState user) {
//        seatState.videoLayout.removeAllViews();
        AppCompatImageView icon = new AppCompatImageView(getContext());
//        icon.setImageResource(UserUtil.getUserProfileIcon(seatState.userId));
        if (user.enableVideo == 0) {
            seatState.videoLayout.removeAllViews();
            if (user != null && user.avatar != null) {
                MyFunctionKt.load(icon, user.avatar, 1);
                seatState.videoLayout.addView(icon);
            }
        }

    }

    private boolean containsChild(ViewGroup parent, View child) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (child == parent.getChildAt(i)) {
                return true;
            }
        }
        return false;
    }

    private void updateItemUI(SeatItem item) {

        if (item.seatState == SEAT_TAKEN) {
            item.nickname.setVisibility(VISIBLE);
            item.videoLayout.setVisibility(VISIBLE);
            item.voiceState.setVisibility(VISIBLE);
            item.operationIcon.setVisibility(GONE);
            item.operationText.setVisibility(GONE);
            item.voiceIndicate.setVisibility(VISIBLE);

            if (item.audioMuteState != SeatInfo.User.USER_AUDIO_ENABLE) {
                item.voiceState.setVisibility(VISIBLE);
                item.voiceState.setImageResource(R.drawable.host_seat_item_mute_icon);
            } else {
                item.voiceState.setVisibility(GONE);
            }

            item.nickname.setText(item.userName);
        } else {
            item.nickname.setVisibility(View.GONE);
            item.videoLayout.removeAllViews();
            item.videoLayout.setVisibility(GONE);
            item.voiceState.setVisibility(GONE);
            item.operationIcon.setVisibility(View.VISIBLE);
            item.voiceIndicate.setVisibility(GONE);

            if (item.seatState == SEAT_OPEN) {
                item.operationIcon.setImageResource(R.drawable.live_seat_invite);
                item.operationText.setVisibility(VISIBLE);
                item.operationText.setText(mIsOwner ?
                        R.string.live_host_in_seat_state_open_host :
                        R.string.live_host_in_seat_state_open_audience);

                if (mIsOwner) {//邀请上麦，先注释掉，后期再加
                    item.operationIcon.setVisibility(View.GONE);
                    item.operationText.setVisibility(View.GONE);
                } else {
                    item.operationIcon.setVisibility(View.VISIBLE);
                    item.operationText.setVisibility(View.VISIBLE);
                }

            } else if (item.seatState == SEAT_CLOSED) {
                item.operationIcon.setImageResource(R.drawable.live_seat_close);
                item.operationText.setVisibility(VISIBLE);
                item.operationText.setText(R.string.live_host_in_seat_state_blocked);
            }
        }

        if (mIsOwner || mIsHost && mMyUserId != null && mMyUserId.equals(item.userId)) {
            item.popup.setVisibility(VISIBLE);
        } else {
            item.popup.setVisibility(GONE);
        }

        if (item.userId == null) {
            item.popup.setVisibility(GONE);
        }


    }

    public void audioIndicate(IRtcEngineEventHandler.AudioVolumeInfo[] audioVolumes, int myRtcUid) {
        for (IRtcEngineEventHandler.AudioVolumeInfo info : audioVolumes) {
            for (SeatItem item : mSeatList) {
                if (mIsHost && info.uid == 0 && myRtcUid == item.rtcUid) {
                    // Need special care here because when I am one of the hosts,
                    // the uid returned in volume indication is 0.
                    // Double check is necessary to see if this is my seat
                    item.startIndicate();
                } else if (item.rtcUid == info.uid) {
                    item.startIndicate();
                }
            }
        }
    }


    public interface LiveHostInSeatOnClickedListener {
        /**
         * Called when the owner clicks the "+" icon and whats
         * to select an audience to be the host.
         *
         * @param position seat position
         * @param view     the view clicked
         */
        void onSeatAdapterHostInviteClicked(int position, View view);

        /**
         * Called when the audience clicks the "+" icon of a seat
         * and tells the owner that he wants to be a host
         *
         * @param position seat position
         * @param view     the view clicked
         */
        void onSeatAdapterAudienceApplyClicked(int position, View view);

        /**
         * Called when the owner wants to close a seat and
         * no one can be a host of this seat
         *
         * @param position seat position
         * @param view     the view clicked
         */
        void onSeatAdapterPositionClosed(int position, View view);

        /**
         * Called when the "more" button is clicked for other
         * operations of this seat.
         *
         * @param position       the seat position
         * @param view           the "more" button of this seat
         * @param seatState      current seat states (open, taken or closed)
         * @param audioMuteState if the seat is taken, the audio mute state of
         *                       current user
         */
        void onSeatAdapterMoreClicked(int position, View view, int seatState, int audioMuteState);

        /**
         * Called when the video of a seat position needs to be showed.
         * /* The callback method needs to return a SurfaceView.
         *
         * @param position seat position
         * @param uid      the rtc uid of the user in this seat
         * @param mine     if the seat video belongs to me if I am a host now
         **/
        SurfaceView onSeatAdapterItemVideoShowed(int position, int uid, boolean mine, boolean audioMuted, boolean videoMuted);

        /**
         * Called when the video of a seat position is about to be removed.
         * This means that either the host mutes his video, or he leaves
         * or is forced to leave his seat.
         *
         * @param position seat position
         * @param uid      rtc uid
         * @param view     the video view of this seat
         */
        void onSeatAdapterItemVideoRemoved(int position, int uid, SurfaceView view, boolean mine, boolean remainsHost);

        /**
         * Called when I am the host and control my audio mute states
         *
         * @param position
         * @param muted
         */
        void onSeatAdapterItemMyAudioMuted(int position, boolean muted);
    }
}
