package com.holoyolostudios.colordetector;

import android.app.Application;
import com.holoyolostudios.colordetector.colors.ColorNameCache;

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
