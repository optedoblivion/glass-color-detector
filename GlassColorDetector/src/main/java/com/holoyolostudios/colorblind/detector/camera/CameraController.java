package com.holoyolostudios.colorblind.detector.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * CameraController
 * <p/>
 * This is a controller interface for manipulating the
 * <p/>
 *
 * @author Martin Brabham
 * @since 9/18/13.
 */
public final class CameraController {

    // Access to the camera and manage camera states
    private static CameraAccessor sCameraAccessor = null;

    // Manager references
    private static CameraController mInstance = null;

    // Members
    private WeakReference<Context> mContext = null;
    private CameraOrientationEventListener mOrientationEventListener = null;
    private int mCameraDisplayOrientation = 0;
    private int mSensorOrientation = 0;
    private int mDisplayRotation = 0;

    // Flags
    private boolean mIsPreviewing = false;
    private boolean mWasPreviewing = false;

    /**
     * Constructor
     *
     * @param context {@link android.content.Context}
     */
    private CameraController(Context context) throws IllegalAccessException {

        // Reference context
        mContext = new WeakReference<Context>(context);

        // Initialize camera access
        sCameraAccessor = CameraAccessor.createInstance(context);

        // Initialize orientation monitor
        mOrientationEventListener = new CameraOrientationEventListener(context);
        startRotationListener();
    }

    /**
     * Create a new instance of the manager
     *
     * @param context {@link android.content.Context}
     * @return {@link CameraController}
     */
    public static CameraController createInstance(Context context) throws IllegalAccessException {
        if (mInstance == null) {
            mInstance = new CameraController(context);
        }
        return mInstance;
    }

    /**
     * Fetch the instance of the camera controller
     *
     * @return {@link CameraController}
     * @throws IllegalStateException {@link java.lang.IllegalStateException}
     */
    public static CameraController getInstance() throws IllegalStateException {
        if (mInstance == null) {
            throw new IllegalStateException("Controller hasn't been created!");
        }
        return mInstance;
    }

    /**
     * Called when UI is resuming
     */
    public final void onResume() {
        startRotationListener();
        if (mWasPreviewing) {
            Log.i("HOLOCAM_PREVIEW", "CameraController::onResume()::this.startPreview()");
            startPreview();
        }
//        focusCamera(0, 0);
    }

    /**
     * Called when UI is pausing
     */
    public final void onPause() {
        mWasPreviewing = mIsPreviewing;
        stopPreview();
        stopRotationListener();
    }

    /**
     * Toggle the camera being used
     *
     * @return {@link java.lang.Boolean
     */
    public final boolean toggleCamera() {
        boolean ret = false;
        if (sCameraAccessor != null) {
            ret = sCameraAccessor.toggleCamera();
        }
        return ret;
    }

    /**
     * Open the current camera
     *
     * @return {@link java.lang.Boolean}
     */
    public final boolean open() {
        boolean ret = false;
        if (sCameraAccessor != null) {
            ret = sCameraAccessor.open();
        }
        return ret;
    }

    /**
     * Close the current camera
     *
     * @return {@link java.lang.Boolean}
     */
    public final boolean close() {
        boolean ret = false;
        if (sCameraAccessor != null) {
            ret = sCameraAccessor.close();
        }
        return ret;
    }

    /**
     * Start the camera preview
     *
     * @return {@link java.lang.Boolean}
     */
    public final boolean startPreview() {
        boolean ret = false;
        if (sCameraAccessor != null && !mIsPreviewing) {
            try {
                Log.i("HOLOCAM_PREVIEW", "CameraController::startPreview()::sCameraAccessor.startPreview();");
                ret = sCameraAccessor.startPreview();
                mIsPreviewing = true;
            } catch (IOException e) {
                e.printStackTrace();
                ret = false;
            }
        }
        Log.i("HOLOCAM_PREVIEW", "CameraController::startPreview()::return " + ret + ";");
        return ret;
    }

    /**
     * Stop the camera preview
     *
     * @return {@link java.lang.Boolean}
     */
    public final boolean stopPreview() {
        boolean ret = false;
        if (mIsPreviewing) {
            ret = sCameraAccessor.stopPreview();
        }
        mIsPreviewing = ret;
        return ret;
    }

    /**
     * Take a single snapshot
     *
     * @return {@link Boolean}
     */
    public final boolean takeSingleSnapshot() {
        return snapshot(1);
    }

    /**
     * Take a number of snapshots
     *
     * @param count {@link Integer}
     * @return {@link Boolean}
     */
    public final boolean snapshot(int count) {
        boolean ret = false;
        return ret;
    }

//    /**
//     * Focus the camera
//     *
//     * @param x
//     * @param y
//     * @return {@link Boolean}
//     */
//    public final boolean focusCamera(int x, int y) {
//        return focusCamera(x, y, null);
//    }
//
//    /**
//     * Focus the camera
//     *
//     * @param x
//     * @param y
//     * @param {@link FocusListener}
//     * @return {@link Boolean}
//     */
//    public final boolean focusCamera(int x, int y, FocusAction.FocusListener listener) {
//        boolean ret = false;
//        return ret;
//    }

    /**
     * Toggle the state of the torch if available
     *
     * @return {@link java.lang.Boolean}
     */
    public final boolean toggleTorch() {
        boolean ret = false;
        return ret;
    }

    /**
     * Set the preview callback for fetching frames using {@link android.hardware.Camera.PreviewCallback#onPreviewFrame(byte[], android.hardware.Camera)}
     *
     * @param previewCallback {@link android.hardware.Camera.PreviewCallback}
     */
    public final void setPreviewCallback(Camera.PreviewCallback previewCallback, byte[] data) {
        if (sCameraAccessor != null) {
            sCameraAccessor.setPreviewCallback(previewCallback, data);
        }
    }

    /**
     * Add a callback buffer for the next frame
     *
     * @param data byte[]
     */
    public final void addCallbackBuffer(byte[] data) {
        if (sCameraAccessor != null) {
            sCameraAccessor.addCallbackBuffer(data);
        }
    }

    /**
     * Will shutdown all of the managers
     */
    public void shutdown() {
        if (sCameraAccessor != null) {
            sCameraAccessor.shutdown();
        }
    }

    /**
     * Start the orientation listener
     */
    public void startRotationListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.enable();
        }
    }

    /**
     * Stop the orientation listener
     */
    public void stopRotationListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
    }

    /**
     * Get the display rotation
     *
     * @return {@link Integer}
     */
    protected int calcDisplayRotation() {

        int rotation = 0;

        Context context = mContext.get();
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            rotation = wm.getDefaultDisplay().getRotation();
            context = null;
        }

        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }

        return 0;
    }

    /**
     * Calculate the display orientation
     *
     * @param cameraInfo {@link android.hardware.Camera.CameraInfo}
     * @return {@link Integer}
     */
    protected int calcDisplayOrientation(Camera.CameraInfo cameraInfo) {
        int result;
        try {
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (cameraInfo.orientation + mDisplayRotation) % 360;
                result = (360 - result) % 360; // Compensates the mirror
            } else {
                result = (cameraInfo.orientation - mDisplayRotation + 360) % 360;
            }
        } catch (RuntimeException e) {
            // Ignore exception, set result to default orientation
            result = 0;
        }
        return result;
    }

    /**
     * @deprecated
     * This is old and I want to get away from doing it}
     * @return
     */
    public CameraAccessor getCameraAccessor() {
        return sCameraAccessor;
    }

    /**
     * Set the display orientation
     */
    public void updateDisplayOrientation() {

        // Get the display rotation of the actual screen display
        mDisplayRotation = calcDisplayRotation();

        if (sCameraAccessor != null) {
            // Get the display rotation for the camera, the actual rotation and angle of the camera
            // to fix the preview frame rotation
            mCameraDisplayOrientation = calcDisplayOrientation(sCameraAccessor.getCameraInfo());
            sCameraAccessor.setDisplayRotation(mDisplayRotation);
            sCameraAccessor.setDisplayOrientation(mCameraDisplayOrientation);
        }

    }

    /**
     * CameraOrientationEventListener<br>
     * Handle orientation events for the camera <br>
     *
     * @author Martin Brabham
     * @see {@link android.view.OrientationEventListener}
     */
    private class CameraOrientationEventListener extends
            OrientationEventListener {

        /**
         * Constructor
         *
         * @param context {@link android.content.Context}
         */
        public CameraOrientationEventListener(Context context) {
            super(context);
        }

        /**
         * Round out the given orientation value
         *
         * @param orientation int given orientation
         * @return rounded orientation
         */
        public int roundOrientation(int orientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }

        /**
         * Process new orientation
         */
        @Override
        public void onOrientationChanged(int orientation) {

            if (orientation == ORIENTATION_UNKNOWN) {
                return;
            }

            // Setup orientation
            mSensorOrientation = roundOrientation(orientation);

            // Make a call to update the orientation variables
            updateDisplayOrientation();

        }
    }

}
