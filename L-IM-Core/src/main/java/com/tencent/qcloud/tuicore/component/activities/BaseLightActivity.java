package com.tencent.qcloud.tuicore.component.activities;

import static com.mtjk.annotation.MyClassKt.activityFullScreen;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.xubo.statusbarutils.StatusBarUtils;
import com.tencent.qcloud.tuicore.R;


public class BaseLightActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activityFullScreen(this)) {
            StatusBarUtils.setStatusBarTransparen(this);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            StatusBarUtils.setStatusBarColor(this, Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
