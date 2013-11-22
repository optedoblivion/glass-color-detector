package com.holoyolostudios.colorblind.detector;

import android.app.Application;
import android.widget.Toast;

import com.holoyolostudios.colorblind.detector.camera.CameraController;
import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;

/**
 * Application instance
 */
public class ColorDetectorApplication extends Application {

    // Static Members
    private static ColorNameCache mColorNameCache = null;
    private static CameraController mCameraController = null;

    @Override
    public void onCreate() {

        try {
            mCameraController = CameraController.createInstance(this);
            super.onCreate();
        } catch (IllegalAccessException e) {
            Toast.makeText(this, "Cannot access camera!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            onTerminate();
        }

        // Create a new instance for the ColorNameCache
        mColorNameCache = ColorNameCache.createInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        if (mCameraController != null) {
            mCameraController.shutdown();
        }

        // Make sure to destroy the color name cache
        if (mColorNameCache != null) {
            mColorNameCache.destroy();
        }
    }

    /**
     * Get the reference to the camera controller
     *
     * @return {@link com.holoyolostudios.colorblind.detector.camera.CameraController}
     */
    public static CameraController getCameraController() {
        return mCameraController;
    }

}
