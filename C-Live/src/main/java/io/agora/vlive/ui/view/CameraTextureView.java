package io.agora.vlive.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import io.agora.framework.modules.consumers.TextureViewConsumer;

public class CameraTextureView extends TextureView {
    public CameraTextureView(Context context) {
        super(context);
        setTextureViewConsumer();
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextureViewConsumer();
    }

    private void setTextureViewConsumer() {
        setSurfaceTextureListener(new TextureViewConsumer());
    }
}
