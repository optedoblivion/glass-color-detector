/*
 * Copyright 2014 Martin Brabham
 * Copyright 2014 Daniel Velazco
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.holoyolostudios.colorvision.colorlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.holoyolostudios.colorvision.colorlib.R;

/**
 * ColorProgressBar
 * <p/>
 * Layout to display a label and progress bar
 * <p/>
 *
 * @author Martin Brabham
 * @author Daniel Velazco
 * @see {@link RelativeLayout}
 */
public class ColorProgressBar extends RelativeLayout {

    // Views
    private ProgressBar mProgressBar = null;
    private TextView mLabel = null;

    /**
     * Constructor
     *
     * @param context  {@link Context}
     */
    public ColorProgressBar(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context  {@link Context}
     * @param attrs    {@link AttributeSet}
     */
    public ColorProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context  {@link Context}
     * @param attrs    {@link AttributeSet}
     * @param defStyle {@link Integer}
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
     * @param text {@link String}
     */
    public void setLabelText(String text) {
        if (mLabel != null) {
            mLabel.setText(text);
        }
    }

    /**
     * Set the color progress
     *
     * @param colorProgress {@link Integer}
     */
    public void setColorProgress(int colorProgress) {
        colorProgress = (colorProgress > 255) ? 255 : colorProgress;
        mProgressBar.setProgress(colorProgress);
    }

}
