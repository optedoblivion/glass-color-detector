package com.holoyolostudios.colorvision;

import android.app.Application;

import com.holoyolostudios.colorvision.colors.ColorNameCache;

/**
 * ColorVisionApplication
 * <p/>
 * Main application instance
 * <p/>
 *
 * @author Martin Brabham
 * @see {@link android.app.Application}
 */
public class ColorVisionApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new instance for the ColorNameCache
        ColorNameCache.createInstance();

    }

    public void onTerminate() {
        ColorNameCache.getInstance().destroy();
        super.onTerminate();
    }

}
