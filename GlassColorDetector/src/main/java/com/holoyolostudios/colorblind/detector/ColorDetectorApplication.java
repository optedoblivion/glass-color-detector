package com.holoyolostudios.colorblind.detector;

import android.app.Application;
import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;

/**
 * Application instance
 */
public class ColorDetectorApplication extends Application {

    // Static Members
    private static ColorNameCache mColorNameCache = null;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new instance for the ColorNameCache
        mColorNameCache = ColorNameCache.createInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // Make sure to destroy the color name cache
        if (mColorNameCache != null) {
            mColorNameCache.destroy();
        }
    }

}
