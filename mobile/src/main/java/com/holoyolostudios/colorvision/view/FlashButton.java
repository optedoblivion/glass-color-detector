package com.holoyolostudios.colorvision.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import com.holoyolostudios.colorvision.R;

/**
 * FlashButton
 * <p/>
 * Widget for toggling torch
 * <p/>
 *
 * @author Daniel Velazco
 * @author Martin Brabham
 * @see {@link ImageButton}
 */
public class FlashButton extends ImageButton {

    // Flags
    private boolean mState = false;

    /**
     * Constructor
     *
     * @param context {@link Context}
     */
    public FlashButton(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context {@link Context}
     * @param attrs   {@link AttributeSet}
     */
    public FlashButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context  {@link Context}
     * @param attrs    {@link AttributeSet}
     * @param defStyle {@link Integer}
     */
    public FlashButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Initialize view
     */
    private void init() {
        updateResource();
    }

    /**
     * Set the state of the button
     *
     * @param state {@link Boolean}
     */
    public void setTorchFlashOn(boolean state) {
        mState = state;
        updateResource();
    }

    /**
     * Handle resource updating
     */
    private void updateResource() {
        setImageResource(mState ? R.drawable.ic_camera_flash_on : R.drawable.ic_camera_flash_off);
    }

}
