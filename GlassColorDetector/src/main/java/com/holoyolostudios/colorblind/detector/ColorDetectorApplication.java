package com.holoyolostudios.colorblind.detector;

import android.app.Application;
import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;

/**
 * Application instance
 */
public class ColorDetectorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new instance for the ColorNameCache
        ColorNameCache.createInstance();
    }

}
