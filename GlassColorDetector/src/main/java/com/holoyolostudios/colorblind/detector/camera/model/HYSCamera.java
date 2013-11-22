package com.holoyolostudios.colorblind.detector.camera.model;

import android.hardware.Camera;

/**
 * HYSCamera
 *
 * @author Martin Brabham
 * @since 11/21/13
 */
public class HYSCamera {

    // Constants
    public static final int INVALID_CAMERA_ID = -1;

    // Members
    private Camera mCamera = null;
    private int mCameraId = INVALID_CAMERA_ID;

    /**
     * Constructor
     *
     * @param camera {@link android.hardware.Camera}
     */
    public HYSCamera(Camera camera) {
        mCamera = camera;
    }


}
