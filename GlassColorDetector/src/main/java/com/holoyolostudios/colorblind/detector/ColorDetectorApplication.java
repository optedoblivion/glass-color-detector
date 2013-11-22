package com.holoyolostudios.colorblind.detector;

import android.app.Application;
import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;

public class ColorDetectorApplication extends Application {

    // Static Members
    private static ColorNameCache mColorNameCache = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mColorNameCache = ColorNameCache.createInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mColorNameCache != null) {
            mColorNameCache.destroy();
        }
    }

}
