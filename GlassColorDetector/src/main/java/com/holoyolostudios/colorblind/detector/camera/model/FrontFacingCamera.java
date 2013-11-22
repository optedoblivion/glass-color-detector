package com.holoyolostudios.colorblind.detector.camera.model;

import android.hardware.Camera;

import com.holoyolostudios.colorblind.detector.camera.BaseCamera;

/**
 * Created by martin on 8/27/13.
 */
public class FrontFacingCamera extends BaseCamera {

    /**
     * Constructor
     *
     * @param cameraId
     * @param cameraInfo
     */
     public FrontFacingCamera(int cameraId, Camera.CameraInfo cameraInfo) {
        super(cameraId, cameraInfo);
    }

}
