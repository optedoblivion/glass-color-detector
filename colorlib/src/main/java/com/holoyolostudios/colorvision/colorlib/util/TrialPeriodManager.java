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
package com.holoyolostudios.colorvision.colorlib.util;

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
     * @param context {@link Context}
     */
    private TrialPeriodManager(Context context) {

    }

    /**
     * Create a new static instance
     *
     * @param context {@link Context}
     * @return {@link com.holoyolostudios.colorvision.colorlib.util.TrialPeriodManager}
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
