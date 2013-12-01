package com.holoyolostudios.colorblind.detector.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import com.holoyolostudios.colorblind.detector.R;

public class FlashButton extends ImageButton {

    private boolean mState = false;

    public FlashButton(Context context) {
        this(context, null);
    }

    public FlashButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlashButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        updateResource();
    }

    public void setTorchFlashOn(boolean state) {
        mState = state;
        updateResource();
    }

    private void updateResource() {
        setImageResource(mState ? R.drawable.ic_camera_flash_on
                : R.drawable.ic_camera_flash_off);
    }

}
