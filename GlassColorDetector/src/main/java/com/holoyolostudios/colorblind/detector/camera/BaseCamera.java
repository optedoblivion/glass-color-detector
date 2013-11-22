package com.holoyolostudios.colorblind.detector.camera;

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseCamera
 * <p/>
 * Basic structure for representing a camera to the application
 * <p/>
 *
 * @author Martin Brabham
 */
public class BaseCamera {

    // Constants
    // Error codes
    public static final int ERROR_START_PREVIEW = 1000;
    public static final int ERROR_STOP_PREVIEW = 1001;
    public static final int ERROR_NO_STORAGE = 1002;
    public static final int ERROR_CAMERA_SWITCH = 1003;
    public static final int ERROR_CAMERA_PREVIEW = 1004;
    public static final int ERROR_SET_PREVIEW_DISPLAY = 1005;
    public static final int ERROR_NO_CAMERA_DEVICE = 1006;
    public static final int ERROR_MAX_FILESIZE = 1007;
    public static final int ERROR_SETTING_PARAMETERS = 1008;
    public static final int ERROR_OPENING_CAMERA = 1009;
    public static final int ERROR_CAMERA_DISABLED = 1010;

    // Action codes
    public static final int ACTION_PREVIEW_START = 1000;
    public static final int ACTION_PREVIEW_STOP = 1001;
    public static final int ACTION_PREVIEW_RESTART = 1002;
    public static final int ACTION_PARAMS_SET = 1003;
    public static final int ACTION_CAMERA_FOUND = 1004;
    public static final int ACTION_TORCH_TOGGLED = 1005;
    public static final int ACTION_CAMERA_SWITCHED = 1006;

    // Focus
    public static final int FOCUS_STATE_NONE = 0;
    public static final int FOCUS_STATE_FOCUSING = 1;
    public static final int FOCUS_STATE_SUCCESS = 2;
    public static final int FOCUS_STATE_FAILED = 3;
    private final static int FOCUS_WIDTH = 80;
    private final static int FOCUS_HEIGHT = 80;

    // States
    public static final int STATE_ERROR = -1;
    public static final int STATE_BOOTING = 0;
    public static final int STATE_PREVIEWING = 1;
    public static final int STATE_RECORDING = 2;

    // Misc
    public static final int MAX_FPS = 30000;
    public static final int TARGET_FPS = 24000;

    // Display
    public static final double ASPECT_TOLERANCE = 0.05f;
    public static final int PORTRAIT_DEFAULT_WIDTH = 640;
    public static final int PORTRAIT_DEFAULT_HEIGHT = 480;
    public static final int LANDSCAPE_DEFAULT_WIDTH = 1280;
    public static final int LANDSCAPE_DEFAULT_HEIGHT = 720;

    // Members
    private CameraInfo mCameraInfo = null;
    private Camera mCameraDevice = null;
    private Camera.Parameters mInitialParameters = null;
    private Camera.Parameters mCurrentParameters = null;
    private int mCameraId = -1;
    private String mFocusMode = Parameters.FOCUS_MODE_AUTO;
    private int mFocusState = FOCUS_STATE_NONE;
    private int mCameraDisplayRotation = 0;
    private int mCameraDisplayOrientation = Surface.ROTATION_0;

    // Flags
    private boolean mIsOpen = false;
    private boolean mIsPortrait = true;
    private boolean mIsPreviewing = false;

    /**
     * Constructor
     *
     * @param cameraId   {@link Integer}
     * @param cameraInfo {@link android.hardware.Camera.CameraInfo}
     */
    protected BaseCamera(int cameraId, CameraInfo cameraInfo) {
        mCameraId = cameraId;
        mCameraInfo = cameraInfo;
    }

    /**
     * Set the camera display rotation
     *
     * @param rotation int
     */
    public void setCameraDisplayRotation(int rotation) {
        mCameraDisplayRotation = rotation;
        if (mCurrentParameters != null) {
            mCurrentParameters.setRotation(mCameraDisplayRotation);
        }
    }

    /**
     * Set flag
     *
     * @param isPortrait
     */
    public void setIsPortrait(boolean isPortrait) {
        mIsPortrait = isPortrait;
    }

    /**
     * Set the camera orientation
     *
     * @param orientation int
     */
    public void setCameraDisplayOrientation(int orientation) {
        mCameraDisplayOrientation = orientation;
    }

    /**
     * Check if we are in portrait
     *
     * @return
     */
    private boolean isPortrait() {
        boolean isit = true;
        switch (mCameraDisplayOrientation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                isit = true;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                isit = false;
                break;
        }
        return isit;
    }

    /**
     * Tells caller whether or not this camera is open
     *
     * @return
     */
    public boolean isOpen() {
        return mIsOpen;
    }

    /**
     * Open the camera device
     *
     * @return {@link Boolean}
     * @throws {@link IllegalStateException}
     */
    /* package */ boolean open() throws IllegalStateException {
        boolean ret = true;
        if (mIsOpen) {
            return ret;
        }
        mCameraDevice = Camera.open(mCameraId);
        if (mCameraDevice == null) {
            ret = false;
        } else {
            if (mCurrentParameters == null) {
                mInitialParameters = mCameraDevice.getParameters();

                // Default the sizes to set the desired aspect ratio and size
                if (mIsPortrait) {
                    mInitialParameters.setPreviewSize(PORTRAIT_DEFAULT_WIDTH,
                            PORTRAIT_DEFAULT_HEIGHT);
                } else {
                    mInitialParameters.setPreviewSize(LANDSCAPE_DEFAULT_WIDTH,
                            LANDSCAPE_DEFAULT_HEIGHT);
                }

                mInitialParameters.setRotation(mCameraDisplayRotation);

                Size size = mInitialParameters.getPictureSize();
                mInitialParameters.setPreviewSize(size.width, size.height);

                try {
                    setCameraParameters(mInitialParameters);
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                    Log.e("BaseCamera", "We lost the parameters!");
                }
            } else {
                try {
                    // Default the sizes to set the desired aspect ratio and size
                    if (mIsPortrait) {
                        mCurrentParameters.setPreviewSize(PORTRAIT_DEFAULT_WIDTH,
                                PORTRAIT_DEFAULT_HEIGHT);
                    } else {
                        mCurrentParameters.setPreviewSize(LANDSCAPE_DEFAULT_WIDTH,
                                LANDSCAPE_DEFAULT_HEIGHT);
                    }
                    Size size = mInitialParameters.getPictureSize();
                    mInitialParameters.setPreviewSize(size.width, size.height);
                    mCurrentParameters.setRotation(mCameraDisplayRotation);
                    setCameraParameters();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                    Log.e("BaseCamera", "We lost the parameters!");
                }
            }
            mIsOpen = true;
        }
        return ret;
    }

    /**
     * Reconnect to the camera device
     *
     * @return {@link Boolean}
     */
    public boolean reconnect() {
        boolean ret = false;
        if (mCameraDevice != null) {
            Log.i("BaseCamera", "Reconnecting to camera: " + mCameraId);
            try {
                mCameraDevice.reconnect();
                ret = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Close the camera device
     *
     * @return {@link Boolean}
     */
    /* package */ boolean close() {
        boolean ret = false;
        if (!mIsOpen) {
            return true;
        }
        if (mCameraDevice != null) {
            if (mFocusState == FOCUS_STATE_FOCUSING) {
                mCameraDevice.cancelAutoFocus();
            }
            Log.i("BaseCamera", "Releasing camera");
            mCameraDevice.release();
            mCameraDevice = null;
            mIsOpen = false;
            ret = true;
        }
        return ret;
    }

    /**
     * Get the camera parameters
     *
     * @return {@link android.hardware.Camera.Parameters}
     */
    public final Camera.Parameters getCameraParameters() {
        return mCurrentParameters;
    }

    /**
     * Set the current parameters to the camera
     *
     * @throws {@link IllegalArgumentException}
     */
    /* package */ void setCameraParameters() throws IllegalArgumentException {
        setCameraParameters(mCurrentParameters);
    }

    /**
     * Set new updated camera parameters
     *
     * @param parameters {@link android.hardware.Camera.Parameters}
     * @throws {@link IllegalArgumentException}
     */
    /* package */ void setCameraParameters(Camera.Parameters parameters) throws IllegalArgumentException {
        if (parameters == null) {
            throw new IllegalArgumentException("'parameters' cannot be null!");
        }

        mCurrentParameters = parameters;
        applyCameraParameters();

    }

    /**
     * Set the preview surface
     *
     * @param surface {@link android.view.SurfaceHolder}
     * @throws java.io.IOException {@link java.io.IOException}
     */
    public void setPreviewSurface(SurfaceHolder surface) throws IOException {
        Log.i("BaseCamera", "setPreviewDisplay(" + surface + ")");
        if (mCameraDevice != null) {
            Log.i("BaseCamera", "SurfaceHolder for preview set!");
            mCameraDevice.setPreviewDisplay(surface);
        }
    }

    /**
     * Set the preview texture
     * }
     *
     * @param texture {@link android.graphics.SurfaceTexture}
     * @throws java.io.IOException {@link java.io.IOException}
     */
    public void setPreviewTexture(SurfaceTexture texture) throws IOException {
        Log.i("BaseCamera", "setPreviewTexture(" + texture + ")");
        if (mCameraDevice != null) {
            Log.i("BaseCamera", "SurfaceHolder for preview set!");
            mCameraDevice.setPreviewTexture(texture);
        }
    }

    /**
     * Set the preview callback for grabbing frames
     *
     * @param callback {@link android.hardware.Camera.PreviewCallback}
     */
    public void setPreviewCallback(PreviewCallback callback) {
        if (mCameraDevice != null) {
            mCameraDevice.setPreviewCallback(callback);
        }
    }

    /**
     * Set the preview callback for grabbing frames
     *
     * @param callback {@link android.hardware.Camera.PreviewCallback}
     */
    public void setPreviewCallback(PreviewCallback callback, final byte[] data) {
        if (mCameraDevice != null) {
            mCameraDevice.setPreviewCallbackWithBuffer(callback);
            addCallbackBuffer(data);
        }
    }

    /**
     * Add the callback buffer
     *
     * @param data byte array
     */
    public void addCallbackBuffer(byte[] data) {
        if (mCameraDevice != null) {
            mCameraDevice.addCallbackBuffer(data);
        }
    }

    /**
     * Get the current camera parameters
     *
     * @return {@link android.hardware.Camera.Parameters}
     */
    public Camera.Parameters getParameters() {
        return mCurrentParameters;
    }

    /**
     * Starts the hardware camera's preview
     *
     * @return {@link Boolean} true or false
     * @throws java.io.IOException
     */
    public boolean startPreview() throws IOException {
        boolean ret = false;
        String log = "Starting preview...";
        if (mCameraDevice != null && !mIsPreviewing) {
            if (!mIsOpen) {
                if (!open()) {
                    log += "failed to open the camera!";
                    Log.i("HOLOCAM_PREVIEW", log);
                    mIsPreviewing = false;
                    return false;
                }
            }
            mCameraDevice.startPreview();
            log += "Success!";
            ret = true;
        } else {
            if (mCameraDevice == null) {
                log += "failed, camera device is null!";
            } else {
                log += "failed, already started!";
            }
        }
        Log.i("HOLOCAM_PREVIEW", log);
        mIsPreviewing = ret;
        return ret;
    }

    /**
     * Stops the camera preview
     *
     * @return {@link Boolean} true or false
     */
    public boolean stopPreview() {
        boolean ret = false;
        String log = "Stopping preview...";
        if (mCameraDevice != null && mIsPreviewing) {
            mCameraDevice.stopPreview();
            log += "Success!";
            ret = true;
            mIsPreviewing = false;
        } else {
            log += "Failed!";
        }
        Log.i("BaseCamera", log);
        return ret;
    }

    /**
     * Start smooth zooming
     *
     * @param newZoomAmount {@link Integer}
     * @param listener      {@link android.hardware.Camera.OnZoomChangeListener}
     */
    /* package */ void startSmoothZoom(int newZoomAmount, Camera.OnZoomChangeListener listener) {
        if (mCameraDevice != null) {
            mCameraDevice.startSmoothZoom(newZoomAmount);
            if (listener != null) {
                mCameraDevice.setZoomChangeListener(listener);
            }
        }
    }

    /**
     * Toggle torch on and off
     *
     * @return {@link Boolean} true if on, false if off
     */
    /* package */ boolean toggleTorch() {
        boolean ret = false;
        if (isTorchAvailable()) {
            if (mCurrentParameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
                mCurrentParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            } else {
                mCurrentParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                ret = true;
            }
            setCameraParameters();
        }
        return ret;
    }

    /**
     * Check if torch is available
     *
     * @return true or false
     */
    public boolean isTorchAvailable() {
        return isSupported(Parameters.FLASH_MODE_TORCH, mCameraDevice.getParameters().getSupportedFlashModes());
    }

    /**
     * Configures and applies the parameters to the camera
     */
    protected void applyCameraParameters() {
        Log.d("BaseCamera", "Setting parameters");

        // Get current picture size
        if (mCurrentParameters == null) {
            return;
        }

        Size pSize = mCurrentParameters.getPreviewSize();
        int previewWidth = pSize.width;
        int previewHeight = pSize.height;

        // // If we have a preferred size from the camera, then use that
        // if (mCurrentParameters.getPreferredPreviewSizeForVideo() != null) {
        // previewWidth =
        // mCurrentParameters.getPreferredPreviewSizeForVideo().width;
        // previewHeight =
        // mCurrentParameters.getPreferredPreviewSizeForVideo().height;
        // }

        Log.d("BaseCamera", "Picture Size: (" + previewWidth
                + "x" + previewHeight + ")");

        // Setup aspect ratio
        double aspectRatio = (double) previewWidth / (double) previewHeight;
        Log.d("BaseCamera", "Picture Aspect Ratio: "
                + aspectRatio);

        // Setup optimal preview size
        List<Size> sizes = mCurrentParameters.getSupportedPreviewSizes();
        Log.d("BaseCamera", "Available Sizes:");
        for (Size s : sizes) {
            Log.d("BaseCamera", "\tSize: (" + s.width + "x"
                    + s.height + ")");
        }
        Size optimalSize = getOptimalPreviewSize(sizes, (double) previewWidth
                / (double) previewHeight);
        mCurrentParameters.setPreviewSize(optimalSize.width, optimalSize.height);
        Log.d("BaseCamera", "Setting optimal size: ("
                + optimalSize.width + "x" + optimalSize.height + ")");

        // Set auto scene mode if possible
        if (isSupported(Parameters.SCENE_MODE_AUTO,
                mCurrentParameters.getSupportedSceneModes())) {
            Log.d("BaseCamera", "SCENE_MODE_AUTO: TRUE");
            mCurrentParameters.setSceneMode(Parameters.SCENE_MODE_AUTO);
        } else {
            Log.d("BaseCamera", "SCENE_MODE_AUTO: FALSE");
        }

        // Check for flash support
        List<String> supportedFlash = mCurrentParameters.getSupportedFlashModes();
        String flashMode = mCurrentParameters.getFlashMode();
        if (flashMode == null || !flashMode.equals(Parameters.FLASH_MODE_TORCH)) {
            flashMode = Parameters.FLASH_MODE_OFF;
        }

        if (isSupported(flashMode, supportedFlash)) {
            mCurrentParameters.setFlashMode(flashMode);
            Log.d("BaseCamera", "Flash mode '" + flashMode + "' supported");
        } else {
            Log.d("BaseCamera", "Flash mode '" + flashMode + "' not supported");
        }

        // Setup white balance
        String whiteBalance = Parameters.WHITE_BALANCE_AUTO;
        if (isSupported(whiteBalance, mCurrentParameters.getSupportedWhiteBalance())) {
            Log.d("BaseCamera", "WHITE_BALANCE_AUTO: TRUE");
            mCurrentParameters.setWhiteBalance(whiteBalance);
        } else {
            Log.d("BaseCamera", "WHITE_BALANCE_AUTO: FALSE");
        }

        // Setup preview range
        int[] range = getOptimalFPSRange();
        Log.d("BaseCamera", "Setting FPS Range ("
                + range[0] + "-" + range[1] + "fps)");
        mCurrentParameters.setPreviewFpsRange(range[0], range[1]);

        // Set camera rotation
        mCameraDevice.setDisplayOrientation(mCameraDisplayRotation);

//            mCurrentParameters.setRecordingHint(true);

        setFocusPoint(0, 0);

        // Grab focus mode
        mFocusMode = mCurrentParameters.getFocusMode();
        if (isSupported(Parameters.FOCUS_MODE_AUTO,
                mCurrentParameters.getSupportedFocusModes())) {
            Log.d("BaseCamera", "FOCUS_MODE_AUTO: TRUE");
            mFocusMode = Parameters.FOCUS_MODE_AUTO;
            mCurrentParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        } else {
            Log.d("BaseCamera", "FOCUS_MODE_AUTO: FALSE");
            Log.d("BaseCamera", "Focus Mode: " +
                    mFocusMode);
        }

        // Set new camera parameters
        Log.d("BaseCamera", "Setting parameters to camera");
        try {
            if (mCameraDevice != null) {
                mCameraDevice.setParameters(mCurrentParameters);
            }
        } catch (RuntimeException e) {
            Log.e("BaseCamera", "Failed to set camera parameters");
            e.printStackTrace();
        }
    }

    public void setFocusPoint(int x, int y) {
        if (mCurrentParameters == null || mCameraDevice == null) {
            return;
        }
        int numFocusAreas = mCurrentParameters.getMaxNumFocusAreas();
        if (numFocusAreas > 0) {
            List<Camera.Area> focusArea = new ArrayList<Camera.Area>();
            focusArea.add(new Camera.Area(new Rect(x, y, x + FOCUS_WIDTH, y + FOCUS_HEIGHT), 1000));

            mCurrentParameters.setFocusAreas(focusArea);

            try {
                mCameraDevice.setParameters(mCurrentParameters);
            } catch (Exception e) {
                // Ignore, we might be setting it too
                // fast since previous attempt
            }
        }
    }

    /**
     * Get the best optimal preview size based on the given available sizes and
     * the targeted aspect ratio
     *
     * @param sizes       {@link java.util.List < android.hardware.Camera.Size >}
     * @param targetRatio double aspect ratio
     * @return {@link android.hardware.Camera.Size}
     */
    @SuppressWarnings("deprecation")
    protected Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
        if (sizes == null || sizes.size() < 1) {
            return null;
        }

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Size pSize = mCurrentParameters.getPreviewSize();
        int targetHeight = pSize.height;

        // Detect the correct size
        for (Size size : sizes) {
            // Find the aspect ratio and size
            double ratio = (double) size.width / (double) size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                Log.d("BaseCamera", "Target Height: " + targetHeight);
                Log.d("BaseCamera", "New Size: " + size.width + "x" + size.height);
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Fallback size detection discard width
        if (optimalSize == null) {
            // We couldn't find an aspect ratio!
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    /**
     * Get the optimal FPS Range
     *
     * @return integer array
     */
    protected final int[] getOptimalFPSRange() {
        int fpsRange[] = new int[2];
        fpsRange[0] = 5000;
        fpsRange[1] = 30000;
        if (mCurrentParameters != null) {
            List<int[]> ranges = mCurrentParameters
                    .getSupportedPreviewFpsRange();
            Log.d("BaseCamera", "Supported FPS ranges:");
            for (int[] range : ranges) {
                Log.d("BaseCamera", "Min: " + range[0]);
                Log.d("BaseCamera", "Max: " + range[1]);
            }

            // Iterate and test ranges
            for (int[] range : ranges) {
                fpsRange[0] = range[0];
                fpsRange[1] = range[1];
            }
        }

        return fpsRange;
    }

    /**
     * Checks if a value is supported
     *
     * @param value     <code>String</code>
     * @param supported {@link java.util.List}
     * @return <code>boolean</code>
     */
    public static boolean isSupported(String value, List<String> supported) {
        return supported != null && supported.indexOf(value) >= 0;
    }

    /**
     * Get the camera configuration
     *
     * @return {@link CameraConfig}
     */
    public final CameraConfig getCameraConfig() {
        CameraConfig config = new CameraConfig();
        config.is_ffc = (mCameraId == CameraInfo.CAMERA_FACING_FRONT);
        config.orientation = mCameraDisplayOrientation;
        config.rotation = mCameraDisplayRotation;
        config.transposition = getTransposition();
        if (getParameters() != null) {
            Size size = getParameters().getPreviewSize();
            if (size != null) {
                config.pWidth = size.width;
                config.pHeight = size.height;
            }
            config.aspect_ratio = (float) size.width / (float) size.height;
        }
        return config;
    }

    /**
     * Get the transposition value (rotation angle)
     *
     * @return 0, 90, 270, or 360 either positive or negative
     */
    public final int getTransposition() {
        int transposition = 0;
        switch (mCameraDisplayRotation) {
            case 0:
                if ((mCameraId == CameraInfo.CAMERA_FACING_FRONT)) {
                    transposition = 90;
                } else {
                    transposition = -90;
                }
                break;
            case 90:
                transposition = 0;
                break;
            case 180:
                if ((mCameraId == CameraInfo.CAMERA_FACING_FRONT)) {
                    transposition = -90;
                } else {
                    transposition = 90;
                }
                break;
            case 270:
                transposition = 180;
                break;
        }
        return transposition;
    }

    /**
     * Get the camera info
     * <p/>
     * return {@link android.hardware.Camera.CameraInfo}
     */
    public final CameraInfo getCameraInfo() {
        return mCameraInfo;
    }

    // Flag for when focusing fails
    private boolean mDisableAutoFocus = false;

    /**
     * Start the focusing
     *
     * @return {@link Boolean}
     */
    /* package */ boolean startFocus(Camera.AutoFocusCallback autoFocusCallback) {
        boolean ret = false;
        if (mFocusMode == null || mDisableAutoFocus || mCameraDevice == null) {
            return ret;
        }

        mCameraDevice.cancelAutoFocus();

        try {
            if (isAutoFocusSupported()) {
                mFocusState = BaseCamera.FOCUS_STATE_FOCUSING;
                mCameraDevice.autoFocus(autoFocusCallback);
                ret = true;
            } else {

                mFocusState = BaseCamera.FOCUS_STATE_NONE;
            }
        } catch (Exception e) {
            mFocusState = BaseCamera.FOCUS_STATE_NONE;
            Log.e("BaseCamera",
                    "Failed to autoFocus. Current camera's focus mode: "
                            + mFocusMode);
            mDisableAutoFocus = true;
            ret = false;
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Check whether or not this camera supports auto focus
     *
     * @return {@link boolean}
     */
    public boolean isAutoFocusSupported() {
        return (!(mFocusMode.equals(Parameters.FOCUS_MODE_INFINITY)
                || mFocusMode.equals(Parameters.FOCUS_MODE_FIXED) || mFocusMode
                .equals(Parameters.FOCUS_MODE_EDOF)));
    }

    /**
     * Cancel out of the focus
     */
    /* package */ void cancelFocus() {

        try {
            if (mCameraDevice != null) {
                mCameraDevice.cancelAutoFocus();
            }
        } catch (Exception e) {
            Log.e("BaseCamera",
                    "Failed to cancel auto focus. Current camera's focus mode: "
                            + mFocusMode);
            e.printStackTrace();
        }

    }

    /**
     * CameraConfig
     * <br/>
     * Current configuration state of the camera
     */
    public class CameraConfig implements Serializable {

        // Serialization stuff
        private static final long serialVersionUID = -5832859254663223802L;
        private static final int VERSION = 1;

        public boolean is_ffc;
        public int rotation;
        public int transposition;
        public int orientation;
        public int pWidth;
        public int pHeight;
        public int bitrate;
        public float aspect_ratio;

        /**
         * Serialization of this object
         *
         * @param oos {@link java.io.ObjectOutputStream}
         * @throws java.io.IOException
         */
        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.writeInt(VERSION);
            oos.writeBoolean(is_ffc);
            oos.writeInt(rotation);
            oos.writeInt(pWidth);
            oos.writeInt(pHeight);
        }

        /**
         * Deserialization of this object.
         *
         * @param ois {@link java.io.ObjectInputStream}
         * @throws ClassNotFoundException
         * @throws java.io.IOException
         */
        private void readObject(ObjectInputStream ois) throws ClassNotFoundException,
                IOException {
            int version = ois.readInt();
            is_ffc = ois.readBoolean();
            rotation = ois.readInt();
            pWidth = ois.readInt();
            pHeight = ois.readInt();
        }

    }

}
