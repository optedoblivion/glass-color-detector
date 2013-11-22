package com.holoyolostudios.colorblind.detector.camera;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.holoyolostudios.colorblind.detector.camera.model.FrontFacingCamera;
import com.holoyolostudios.colorblind.detector.camera.model.RearFacingCamera;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * CameraAccessor
 * <p/>
 * Class used for managing the current camera and open/close states of that camera
 * <p/>
 * Created by martin on 8/27/13.
 */
// [TODO][MSB]: Remove this public shit too
/* package */ public final class CameraAccessor {

    // Static members
    private static CameraAccessor mInstance = null;

    // Members
    private WeakReference<Context> mContext = null;
    private BaseCamera mCurrentCamera = null;
    private BaseCamera mFrontFacingCamera = null;
    private BaseCamera mRearFacingCamera = null;

    /**
     * Constructor
     *
     * @param context {@link android.content.Context}
     * @throws {@link IllegalAccessException}
     */
    private CameraAccessor(Context context) throws IllegalAccessException {
        mContext = new WeakReference<Context>(context);
        init();
    }

    /**
     * Create a new instance
     *
     * @param context {@link android.content.Context}
     * @return {@link CameraAccessor}
     * @throws {@link IllegalAccessException}
     */
    /* package */
    static CameraAccessor createInstance(Context context) throws IllegalAccessException {
        if (mInstance == null) {
            mInstance = new CameraAccessor(context);
        }
        return mInstance;
    }

    /**
     * Initialize all cameras available
     *
     * @throws {@link IllegalAccessException}
     */
    private void init() throws IllegalAccessException {
        if (!checkDevicePolicy()) {
            throw new IllegalAccessException("Cannot access any camera device!");
        }
        Log.i("HOLOCAM_INIT", "CameraAccessor::init()::ensureCameraDevices()");
        ensureCameraDevices();
    }

    /**
     * Check if the current camera is open
     *
     * @return {@link java.lang.Boolean}
     */
    /* package */ boolean isCameraOpen() {
        if (mCurrentCamera != null) {
            return mCurrentCamera.isOpen();
        } else {
            return false;
        }
    }

    /**
     * Check the device policy to see if we have access to use the camera
     *
     * @return {@link Boolean} true if access, false if not
     */
    private boolean checkDevicePolicy() {
        boolean ret = false;
        Context c = mContext.get();
        // [TODO][MSB]: Add package manager check for camera?
        // [NOTE][MSB]: Always returns false on SGS3
        if (c != null) {
            DevicePolicyManager dpm = (DevicePolicyManager) c.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ret = !dpm.getCameraDisabled(null);
        }
        return ret;
    }

    /**
     * Make sure we have cameras
     *
     * @return {@link Boolean} true or false
     */
    private boolean ensureCameraDevices() {
        boolean ret = false;
        int numberOfCameras = Camera.getNumberOfCameras();
        Log.i("HOLOCAM_AUTODETECT", "Number of cameras: " + numberOfCameras);
        if (numberOfCameras < 1) {
            ret = false;
        } else {
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i, cameraInfo);
                int facing = cameraInfo.facing;
                switch (facing) {
                    case Camera.CameraInfo.CAMERA_FACING_BACK:
                        mRearFacingCamera = new RearFacingCamera(i, cameraInfo);
                        break;
                    case Camera.CameraInfo.CAMERA_FACING_FRONT:
                        mFrontFacingCamera = new FrontFacingCamera(i, cameraInfo);
                        break;
                    default:
                        break;
                }
            }
            Log.i("HOLOCAM_AUTODETECT", "Detected FFC: " +(mFrontFacingCamera != null));
            Log.i("HOLOCAM_AUTODETECT", "Detected RFC: " +(mRearFacingCamera != null));
            if (mRearFacingCamera != null) {
                mCurrentCamera = mRearFacingCamera;
            } else {
                if (mFrontFacingCamera != null) {
                    mCurrentCamera = mFrontFacingCamera;
                }
            }
            ret = true;
        }
        return ret;

    }

    /**
     * Toggle the current camera
     *
     * @return {@link java.lang.Boolean}
     */
    /* package */
    final boolean toggleCamera() {
        if (mCurrentCamera.isOpen()) {
            mCurrentCamera.close();
        }
        boolean switched = false;
        if (mCurrentCamera == mFrontFacingCamera) {
            if (mRearFacingCamera != null) {
                mCurrentCamera = mRearFacingCamera;
                switched = true;
            }
        } else {
            if (mFrontFacingCamera != null) {
                mCurrentCamera = mFrontFacingCamera;
                switched = true;
            }
        }
        return switched && open();
    }

    /**
     * Switch to the rear facing camera
     *
     * @return {@link java.lang.Boolean} <code>true</code> if a switch occurred, <code>false</code> if not
     */
    /* package */
    final boolean switchToRFC() {
        boolean switched = false;
        if (mRearFacingCamera != null) {
            if (mCurrentCamera.isOpen()) {
                mCurrentCamera.close();
            }
            mCurrentCamera = mRearFacingCamera;
            switched = true;
        }
        return switched && open();
    }

    /**
     * Switch to the front facing camera
     *
     * @return {@link java.lang.Boolean} <code>true</code> if a switch occurred, <code>false</code> if not
     */
    /* package */
    final boolean switchToFFC() {
        boolean switched = false;
        if (mFrontFacingCamera != null) {
            if (mCurrentCamera.isOpen()) {
                mCurrentCamera.close();
            }
            mCurrentCamera = mFrontFacingCamera;
            switched = true;
        }
        return switched && open();
    }

    /**
     * Shutdown the camera manager and all cameras
     */
    /* package */
    final void shutdown() {
        mCurrentCamera = null;
        if (mRearFacingCamera != null) {
            mRearFacingCamera.close();
            mRearFacingCamera = null;
        }
        if (mFrontFacingCamera != null) {
            mFrontFacingCamera.close();
            mFrontFacingCamera = null;
        }
        mContext.clear();
        mInstance = null;
    }

    /**
     * Get the current camera reference
     *
     * @return {@link com.holoyolostudios.holocam.camera.base.BaseCamera}
     */
    /* package */
    // [TODO][MSB]: Remove public when you can!
    public final BaseCamera getCurrentCamera() {
        return mFrontFacingCamera;
//        return mCurrentCamera;
    }

    /**
     * Open the current camera
     *
     * @return {@link java.lang.Boolean}
     */
    /* package */ boolean open() {
        return mCurrentCamera.open();
    }

    /**
     * Close the current camera
     *
     * @return {@link java.lang.Boolean}
     */
    /* pacakge */ boolean close() {
        return mCurrentCamera.close();
    }

    /**
     * Start the preview for the current camera
     *
     * @return {@link java.lang.Boolean}
     * @throws java.io.IOException {@link java.io.IOException}
     */
    /* package */ boolean startPreview() throws IOException {
        Log.i("HOLOCAM_PREVIEW", "CameraAccessor::startPreview()::mCurrentCamera.startPreview();");
        return mCurrentCamera.startPreview();
    }

    /**
     * Stop the preview for the current camera
     *
     * @return {@link java.lang.Boolean}
     */
    /* package */ boolean stopPreview() {
        return mCurrentCamera.stopPreview();
    }

    /* package */ void setPreviewTexture(SurfaceTexture previewTexture) throws IOException {
        mCurrentCamera.setPreviewTexture(previewTexture);
    }

    /* package */ void setPreviewCallback(Camera.PreviewCallback previewCallback, byte[] yuvBuffer) {
        mCurrentCamera.setPreviewCallback(previewCallback, yuvBuffer);
    }

    /* package */ void addCallbackBuffer(byte[] yuvData) {
        mCurrentCamera.addCallbackBuffer(yuvData);
    }

    /* package */ void clearPreviewCallback() {
        mCurrentCamera.setPreviewCallback(null);
    }

    /* package */ Camera.CameraInfo getCameraInfo() {
        return mCurrentCamera.getCameraInfo();
    }

    /* package */ BaseCamera.CameraConfig getCameraConfig() {
        return mCurrentCamera.getCameraConfig();
    }

    /* package */ void setDisplayRotation(int rotation) {
        mCurrentCamera.setCameraDisplayRotation(rotation);
    }

    /* package */ void setDisplayOrientation(int orientation) {
        mCurrentCamera.setCameraDisplayOrientation(orientation);
    }

}
