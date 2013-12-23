package com.holoyolostudios.colordetector.util;

import android.content.Context;

/**
 * TrialPeriodManager
 *
 * @author Martin Brabham
 * @since 12/19/13
 */
public class TrialPeriodManager {

    // Constant
    public static final long EXPIRATION_TIMESTAMP = 1391040000;

    // Static instance
    private static TrialPeriodManager sInstance = null;

    /**
     * Constructor
     *
     * @param context {@link android.content.Context}
     */
    private TrialPeriodManager(Context context) {

    }

    /**
     * Create a new static instance
     *
     * @param context {@link android.content.Context}
     * @return {@link com.holoyolostudios.colordetector.util.TrialPeriodManager}
     */
    public static TrialPeriodManager createInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TrialPeriodManager(context);
        }
        return sInstance;
    }

    public boolean isTrialOver() {
        return System.currentTimeMillis() > EXPIRATION_TIMESTAMP;
    }

}
