package io.agora.vlive.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import io.agora.vlive.R;
import io.agora.vlive.bean.BeanParam;
import io.agora.vlive.protocol.model.model.SeatInfo;

/**
 * tag==下麦
 * tag==禁音
 * tag==禁视
 */
public class SeatItemDialog extends Dialog implements View.OnClickListener {
    // Owner has full privilege to operate on any host on the seat
    public static final int MODE_OWNER = 1;

    // Host can only decide to leave the seat.
    public static final int MODE_HOST = 2;

    public boolean isMore = false;

    public interface OnSeatDialogItemClickedListener {
        void onSeatDialogItemClicked(int position, Operation operation);
    }

    public enum Operation {
        mute, unmute, leave, open, close, close_video, open_video;

        @NonNull
        @Override
        public String toString() {
            switch (this) {
                case mute:
                    return "mute";
                case unmute:
                    return "unmute";
                case leave:
                    return "leave";
                case open:
                    return "open";
                case close:
                    return "close";
                default:
                    return "unknown";
            }
        }
    }

    private int mMode;

    private int mSeatState;
    private int mAudioMuteState;
    private int mVideoMuteState;
    private String[] mOperations;
    private AppCompatTextView[] mOpTextViews = new AppCompatTextView[3];
    private OnSeatDialogItemClickedListener mListener;
    private final int mPosition;

    public SeatItemDialog(@NonNull Context context, int seatState, int audioMuteState, int mode,
                          View anchor, int position, @NonNull OnSeatDialogItemClickedListener listener) {
        super(context, R.style.seat_item_dialog);

        BeanParam param = new BeanParam();

        isMore = param.getLiveType() == BeanParam.LiveType.INSTANCE.getAUDIO_MORE() || param.getLiveType() == BeanParam.LiveType.INSTANCE.getVIDEO_MORE();

        mSeatState = seatState;
        mAudioMuteState = audioMuteState;
        mListener = listener;
        mPosition = position;
        mMode = mode;
        init(context, anchor);
    }

    public SeatItemDialog(@NonNull Context context, int seatState, int videoMuteState, int audioMuteState, int mode,
                          View anchor, int position, @NonNull OnSeatDialogItemClickedListener listener) {
        super(context, R.style.seat_item_dialog);
        BeanParam param = new BeanParam().get();

        isMore = param.getLiveType() == BeanParam.LiveType.INSTANCE.getAUDIO_MORE() || param.getLiveType() == BeanParam.LiveType.INSTANCE.getVIDEO_MORE();
        mSeatState = seatState;
        mVideoMuteState = videoMuteState;
        mAudioMuteState = audioMuteState;
        mListener = listener;
        mPosition = position;
        mMode = mode;
        init(context, anchor);
    }

    private void init(Context context, View anchor) {
        mOperations = context.getResources().getStringArray(R.array.live_host_in_seat_operations);
        initDialog(anchor);
    }

    private void initDialog(View anchor) {
        if (isOwner() && mSeatState == SeatInfo.TAKEN) {
            setContentView(R.layout.seat_item_popup_multi);//主播
        } else {
            setContentView(R.layout.seat_item_popup_single);//观众
        }

        initOperations();
        setDialogPosition(anchor);
        setCancelable(true);
    }

    private void setDialogPosition(View anchor) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        hideStatusBar(window);

        params.width = getContext().getResources().
                getDimensionPixelSize(R.dimen.live_host_in_seat_item_dialog_width);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;

        int[] locationsOnScreen = new int[2];
        anchor.getLocationOnScreen(locationsOnScreen);
        params.x = locationsOnScreen[0] + anchor.getMeasuredWidth() - params.width;
        params.y = locationsOnScreen[1] + 40;//+ anchor.getMeasuredHeight();

        window.setAttributes(params);
    }

    private void hideStatusBar(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        window.getDecorView().setSystemUiVisibility(flag |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initOperations() {
        if (isOwner() && mSeatState == SeatInfo.TAKEN) {
            mOpTextViews[0] = findViewById(R.id.seat_item_dialog_op1);//禁言
            mOpTextViews[1] = findViewById(R.id.seat_item_dialog_op2);//下麦
            mOpTextViews[2] = findViewById(R.id.seat_item_dialog_op3);//视频

            if (isMore==false) {
                mOpTextViews[0].setVisibility(View.GONE);
                mOpTextViews[2].setVisibility(View.GONE);
                findViewById(R.id.line1).setVisibility(View.GONE);
                findViewById(R.id.line2).setVisibility(View.GONE);
            }

        } else {
            mOpTextViews[0] = findViewById(R.id.seat_item_dialog_op1);
            mOpTextViews[1] = findViewById(R.id.audio);
            mOpTextViews[2] = findViewById(R.id.video);
            if (isMore==false) {
                mOpTextViews[1].setVisibility(View.GONE);
                mOpTextViews[2].setVisibility(View.GONE);
                findViewById(R.id.line1).setVisibility(View.GONE);
                findViewById(R.id.line2).setVisibility(View.GONE);
            }

        }

        mOpTextViews[0].setOnClickListener(this);

        if (mOpTextViews[1] != null)
            mOpTextViews[1].setOnClickListener(this);
        if (mOpTextViews[2] != null)
            mOpTextViews[2].setOnClickListener(this);
        if (!isOwner() || mSeatState != LiveMultiHostSeatLayout.SEAT_TAKEN) {
            if (!isOwner()) {
                // Other hosts are only allowed to
                // leave their seat by the pop up window
                mOpTextViews[0].setText(mOperations[2]);
                mOpTextViews[1].setVisibility(View.GONE);
                if (isMore && mAudioMuteState == LiveMultiHostSeatLayout.MUTE_NONE) {
                    mOpTextViews[1].setText("用户禁言");
                    mOpTextViews[1].setVisibility(View.VISIBLE);
                } else if (isMore && mAudioMuteState == 0) {//主播禁言
                    mOpTextViews[1].setText("解除禁言");
                } else if (isMore && mAudioMuteState == 2) {//自己禁言
                    mOpTextViews[1].setVisibility(View.VISIBLE);
                    mOpTextViews[1].setText("解除禁言");
                }

                if (isMore && mOpTextViews[2] != null) {
                    if (mVideoMuteState == 1) {
                        mOpTextViews[2].setText("关闭视频");
                        mOpTextViews[2].setVisibility(View.VISIBLE);
                    } else if (mVideoMuteState == 0) {//主播禁言
                        mOpTextViews[2].setText("开启视频");
                    } else if (mVideoMuteState == 2) {//自己禁言
                        mOpTextViews[2].setVisibility(View.VISIBLE);
                        mOpTextViews[2].setText("开启视频");
                    }
                }


            } else if (mSeatState == LiveMultiHostSeatLayout.SEAT_OPEN) {
                mOpTextViews[0].setText(mOperations[4]);

            } else if (mSeatState == LiveMultiHostSeatLayout.SEAT_CLOSED) {
                mOpTextViews[0].setText(mOperations[3]);
            }


            return;
        }

        mOpTextViews[1].setOnClickListener(this);


        if (mAudioMuteState != LiveMultiHostSeatLayout.MUTE_NONE) {
            mOpTextViews[0].setText(mOperations[1]);
        } else {
            mOpTextViews[0].setText(mOperations[0]);
        }

        if (isMore && mOpTextViews[2] != null) {
            if (mVideoMuteState == 1) {
                mOpTextViews[2].setText("关闭视频");
                mOpTextViews[2].setVisibility(View.VISIBLE);
            } else if (mVideoMuteState == 0) {//主播禁言
                mOpTextViews[2].setText("开启视频");
            } else if (mVideoMuteState == 2) {//自己禁言
                mOpTextViews[2].setVisibility(View.VISIBLE);
                mOpTextViews[2].setText("开启视频");
            }
        }


        mOpTextViews[1].setText(mOperations[2]);
//        mOpTextViews[2].setText(mOperations[4]);
    }

    @Override
    public void onClick(View view) {
        Operation op = Operation.mute;
        int id = view.getId();
        if (id == R.id.seat_item_dialog_op1) {
            if (!isOwner()) {
                op = Operation.leave;
            } else {
                if (mSeatState == LiveMultiHostSeatLayout.SEAT_OPEN) {
                    op = Operation.close;
                } else if (mSeatState == LiveMultiHostSeatLayout.SEAT_CLOSED) {
                    op = Operation.open;
                } else if (mSeatState == LiveMultiHostSeatLayout.SEAT_TAKEN) {
                    if (mAudioMuteState == LiveMultiHostSeatLayout.MUTE_NONE) {
                        op = Operation.mute;
                    } else {
                        op = Operation.unmute;
                    }
                }
            }
        } else if (id == R.id.seat_item_dialog_op2) {
            op = Operation.leave;
        } else if (id == R.id.seat_item_dialog_op3 || id == R.id.video) {
            if (mVideoMuteState == 1) {
                op = Operation.close_video;
            } else {
                op = Operation.open_video;
            }
        } else if (id == R.id.audio) {
            if (mSeatState == LiveMultiHostSeatLayout.SEAT_TAKEN) {
                if (mAudioMuteState == LiveMultiHostSeatLayout.MUTE_NONE) {
                    op = Operation.mute;
                } else {
                    op = Operation.unmute;
                }
            }
        }
        dismiss();
        mListener.onSeatDialogItemClicked(mPosition, op);
    }

    private boolean isOwner() {
        return mMode == MODE_OWNER;
    }
}
