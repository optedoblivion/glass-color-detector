package com.holoyolostudios.colordetector;

import android.app.Application;

import com.holoyolostudios.colordetector.colors.ColorNameCache;

/**
 * ColorDetectorApplication
 * <p/>
 * Main application instance
 * <p/>
 *
 * @author Martin Brabham
 * @see {@link android.app.Application}
 */
public class ColorDetectorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new instance for the ColorNameCache
        ColorNameCache.createInstance();

    }

}
