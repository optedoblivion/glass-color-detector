package com.holoyolostudios.colorblind.detector.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holoyolostudios.colorblind.detector.R;

/**
 * ColorProgressBar
 * <p/>
 * Layout to display a label and progress bar
 * <p/>
 */
public class ColorProgressBar extends RelativeLayout {

    // Views
    private ProgressBar mProgressBar = null;
    private TextView mLabel = null;

    /**
     * Constructor
     *
     * @param context  {@link android.content.Context}
     */
    public ColorProgressBar(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context  {@link android.content.Context}
     * @param attrs    {@link android.util.AttributeSet}
     */
    public ColorProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context  {@link android.content.Context}
     * @param attrs    {@link android.util.AttributeSet}
     * @param defStyle {@link java.lang.Integer}
     */
    public ColorProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * {@inheritDoc}
     */
    public void onFinishInflate() {
        mProgressBar = (ProgressBar) findViewById(R.id.pb_color);
        mProgressBar.setMax(255);
        mLabel = (TextView) findViewById(R.id.tv_label);
        super.onFinishInflate();
    }

    /**
     * Set the label text
     *
     * @param text {@link java.lang.String}
     */
    public void setLabelText(String text) {
        if (mLabel != null) {
            mLabel.setText(text);
        }
    }

    /**
     * Set the color progress
     *
     * @param colorProgress {@link java.lang.Integer}
     */
    public void setColorProgress(int colorProgress) {
        colorProgress = (colorProgress > 255) ? 255 : colorProgress;
        mProgressBar.setProgress(colorProgress);
    }

}
