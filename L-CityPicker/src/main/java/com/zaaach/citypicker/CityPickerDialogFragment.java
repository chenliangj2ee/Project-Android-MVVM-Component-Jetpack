package com.zaaach.citypicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtjk.annotation.MyClass;
import com.mtjk.base.MyBaseDialog;
import com.zaaach.citypicker.adapter.CityListAdapter;
import com.zaaach.citypicker.adapter.InnerListener;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.adapter.decoration.DividerItemDecoration;
import com.zaaach.citypicker.adapter.decoration.SectionItemDecoration;
import com.zaaach.citypicker.databinding.CpDialogCityPickerBinding;
import com.zaaach.citypicker.db.DBManager;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;
import com.zaaach.citypicker.util.ScreenUtil;
import com.zaaach.citypicker.view.SideIndexBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 20:50
 */

@MyClass(mDialogGravity = Gravity.BOTTOM,mDialogAnimation = true)
public class CityPickerDialogFragment extends MyBaseDialog<CpDialogCityPickerBinding> implements TextWatcher,
        View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener {
    private RecyclerView mRecyclerView;
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;

    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<HotCity> mHotCities;
    private List<City> mResults;

    private DBManager dbManager;

    private int height;
    private int width;

    private boolean enableAnim = false;
    private int mAnimStyle = R.style.DefaultCityPickerAnimation;
    private LocatedCity mLocatedCity;
    private int locateState;
    private OnPickListener mOnPickListener;


    @Override
    public void initCreate() {
        initData();
        initViews();
    }

    /**
     * 获取实例
     * @param enable 是否启用动画效果
     * @return
     */
    public static CityPickerDialogFragment newInstance(boolean enable){
        final CityPickerDialogFragment fragment = new CityPickerDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("cp_enable_anim", enable);
        fragment.setArguments(args);
        return fragment;
    }



    public void setLocatedCity(LocatedCity location){
        mLocatedCity = location;
    }

    public void setHotCities(List<HotCity> data){
        if (data != null && !data.isEmpty()){
            this.mHotCities = data;
        }
    }

    @SuppressLint("ResourceType")
    public void setAnimationStyle(@StyleRes int resId){
        this.mAnimStyle = resId <= 0 ? mAnimStyle : resId;
    }



    private void initViews() {
        mRecyclerView = getMRootView().findViewById(R.id.cp_city_recyclerview);
        getMRootView().findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(getActivity(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()), 1);
        mAdapter = new CityListAdapter(getActivity(), mAllCities, mHotCities, locateState);
        mAdapter.autoLocate(true);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    mAdapter.refreshLocationItem();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });

        mOverlayTextView = getMRootView().findViewById(R.id.cp_overlay);

        mIndexBar = getMRootView().findViewById(R.id.cp_side_index_bar);
        mIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(getActivity()));
        mIndexBar.setOverlayTextView(mOverlayTextView)
                .setOnIndexChangedListener(this);


    }

    private void initData() {
        Bundle args = getArguments();
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim");
        }
        //初始化热门城市
        if (mHotCities == null || mHotCities.isEmpty()) {
            mHotCities = new ArrayList<>();
            mHotCities.add(new HotCity("北京", "北京市", "1"));
            mHotCities.add(new HotCity("上海", "上海市", "71"));
            mHotCities.add(new HotCity("广州", "广东省", "3"));
            mHotCities.add(new HotCity("深圳", "广东省", "5"));
            mHotCities.add(new HotCity("天津", "天津市", "130"));
            mHotCities.add(new HotCity("杭州", "浙江省", "86"));
            mHotCities.add(new HotCity("南京", "江苏省", "44"));
            mHotCities.add(new HotCity("成都", "四川省", "147"));
            mHotCities.add(new HotCity("武汉", "湖北省", "228"));
        }
        //初始化定位城市，默认为空时会自动回调定位
        if (mLocatedCity == null){
            mLocatedCity = new LocatedCity(getString(R.string.cp_locating), "未知", "0");
            locateState = LocateState.LOCATING;
        }else{
            locateState = LocateState.SUCCESS;
        }

        dbManager = new DBManager(getActivity());
        mAllCities = dbManager.getAllCities();
        mAllCities.add(0, new HotCity("热门城市", "未知", "0"));
        mResults = mAllCities;
    }



    /** 搜索框监听 */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)){
            mResults = mAllCities;
            ((SectionItemDecoration)(mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        }else {
            //开始数据库查找
            mResults = dbManager.searchCity(keyword);
            ((SectionItemDecoration)(mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            if (mResults == null || mResults.isEmpty()){
            }else {
                mAdapter.updateData(mResults);
            }
        }
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cp_cancel) {
            dismiss();
            if (mOnPickListener != null){
                mOnPickListener.onCancel();
            }
        }else if(id == R.id.cp_clear_all){
        }
    }


    @Override
    public void onIndexChanged(String index, int position) {
        //滚动RecyclerView到索引位置
        mAdapter.scrollToSection(index);
    }

    public void locationChanged(LocatedCity location, int state){
        mAdapter.updateLocateState(location, state);
    }

    @Override
    public void dismiss(int position, City data) {
        dismiss();
        if (mOnPickListener != null){
            mOnPickListener.onPick(position, data);
        }
    }

    @Override
    public void locate(){
        if (mOnPickListener != null){
            mOnPickListener.onLocate();
        }
    }

    public void setOnPickListener(OnPickListener listener){
        this.mOnPickListener = listener;
    }


}
