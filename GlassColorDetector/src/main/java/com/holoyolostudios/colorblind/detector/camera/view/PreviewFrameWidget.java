package com.holoyolostudios.colorblind.detector.camera.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.holoyolostudios.colorblind.detector.R;
import com.holoyolostudios.colorblind.detector.camera.BaseCamera;
import com.holoyolostudios.colorblind.detector.camera.CameraController;
import com.holoyolostudios.colorblind.detector.camera.view.opengl.CameraGLSurfaceView;

import java.io.IOException;

/**
 * PreviewFrameLayout <br>
 * Layout to handle the preview aspect ratio and the position of the gripper <br>
 *
 * @author Martin Brabham
 */
public class PreviewFrameWidget extends RelativeLayout {

    /*
     * Constants
     */

    // View ID
    private static final int SURFACE_VIEW_IDENTIFIER = 0xF7282001;

    // Aspect Ratios
    public static final double ASPECT_RATIO_16_9 = 16.0 / 9.0;
    public static final double ASPECT_RATIO_4_3 = 4.0 / 3.0;

    // Members
    private Context mContext = null;
    private BaseCamera mCamera = null;
    private CameraController mCameraController = null;
    private Point mScreenSize = null;
    private OnTouchListener mOnTouchListener = null;

    // Default to 16:9 as per specifications
    private double mAspectRatio = ASPECT_RATIO_4_3;

    // Views
    private FrameLayout mPreviewFrame = null;
    private CameraGLSurfaceView mSurfaceView = null;
    private View mTouchControllerView = null;

    // Flags
    private boolean mIsPortrait = true;

    /**
     * Constructor
     *
     * @param context {@link android.content.Context}
     */
    public PreviewFrameWidget(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context {@link android.content.Context}
     * @param attrs   {@link android.util.AttributeSet}
     */
    public PreviewFrameWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context  {@link android.content.Context}
     * @param attrs    {@link android.util.AttributeSet}
     * @param defStyle Integer style definition
     */
    public PreviewFrameWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFinishInflate() {
        if (isInEditMode()) {
            return;
        }
        mPreviewFrame = (FrameLayout) findViewById(R.id.grp_camera_preview_frame);
        mTouchControllerView = findViewById(R.id.grp_touch_controller);
        setAspectRatio(ASPECT_RATIO_4_3);
        setSurfaceView();
    }

    /**
     * Set the screen size
     *
     * @param screenSize {@link android.graphics.Point}
     */
    public void setScreenSize(Point screenSize) {
        mScreenSize = screenSize;
    }

    /**
     * Set the camera to display
     *
     * @param camera {@link BaseCamera}
     * @throws {@link IllegalArgumentException}
     */
    public void setCamera(BaseCamera camera) throws IllegalArgumentException {
        if (camera == null) {
            throw new IllegalArgumentException("'camera' cannot be null!");
        }
        mCamera = camera;
        setAspectRatio(mCamera.getCameraConfig().aspect_ratio);
        if (mSurfaceView != null) {
            mSurfaceView.setCamera(mCamera);
        }
        try {
            if (mSurfaceView == null) {
                setSurfaceView();
            }
            mCamera.setPreviewTexture(mSurfaceView.getRenderer().getSurfaceTexture());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    /**
     * Set the aspect ratio of the preview
     *
     * @param ratio double the aspect ratio value
     * @throws IllegalArgumentException {@link IllegalArgumentException}
     */
    private void setAspectRatio(double ratio) throws IllegalArgumentException {
        if (ratio <= 0.0) {
            Log
                    .e("PreviewFrameWidget", "Invalid ratio '" + ratio + "'. Defaulting to 4:3 if portrait, 16:9 is landscape");
            if (mIsPortrait) {
                ratio = ASPECT_RATIO_4_3;
            } else {
                ratio = ASPECT_RATIO_16_9;
            }
        }

        // Check for portrait
        if (mIsPortrait) {
            ratio = 1 / ratio;
            if (mSurfaceView != null) {
                mSurfaceView.setScaleType(CameraGLSurfaceView.SCALE_TYPE_CENTER_CROP);
            }
        } else {
            if (mSurfaceView != null) {
                mSurfaceView.setScaleType(CameraGLSurfaceView.SCALE_TYPE_NONE);
            }
        }

        Log.i("PreviewFrameWidget", "isPortrait: " + mIsPortrait);
        Log.i("PreviewFrameWidget", "mAspectRatio: " + mAspectRatio);
        Log.i("PreviewFrameWidget", "new ratio: " + ratio);

        // Set ratio and re-layout if its not the same
        if (mAspectRatio != ratio) {
            mAspectRatio = ratio;
            requestLayout();
        }
    }

    public boolean isPortrait() {
        return mIsPortrait;
    }

    /**
     * Set the portrait flag
     *
     * @param isPortrait {@link Boolean}
     */
    public void setIsPortrait(boolean isPortrait) {
        if (isPortrait != mIsPortrait) {
            mIsPortrait = isPortrait;
            mSurfaceView.setCamera(mCamera);
            setAspectRatio(mCamera.getCameraConfig().aspect_ratio);
        }
    }

    /**
     * Get the current apsect ratio
     *
     * @return {@link Double}
     */
    public double getAspectRatio() {
        return mAspectRatio;
    }

    /**
     * Reset the {@link CameraGLSurfaceView}
     */
    public void setSurfaceView() {
        if (mSurfaceView != null) {
            mPreviewFrame.removeView(findViewById(SURFACE_VIEW_IDENTIFIER));
            mSurfaceView = null;
        }
        mSurfaceView = new CameraGLSurfaceView(mContext);
        mSurfaceView.setId(SURFACE_VIEW_IDENTIFIER);
        if (mCamera != null) {
            mSurfaceView.setCamera(mCamera);
            setAspectRatio(mCamera.getCameraConfig().aspect_ratio);
        }
        mPreviewFrame.addView(mSurfaceView);
    }

    /**
     * Perform pause actions
     */
    public void onPause() {
        mTouchControllerView.setOnTouchListener(null);
    }

    /**
     * Perform resume actions
     */
    public void onResume() {
        mTouchControllerView.setOnTouchListener(mOnTouchListener);
    }

    /**
     * Set the ontouch listener
     * Overridden to only store the reference
     *
     * @param listener {@link android.view.View.OnTouchListener}
     */
    @Override
    public void setOnTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
    }

    /**
     * Set the camera controller instance
     *
     * @param cameraController {@link com.holoyolostudios.holocam.camera.base.CameraController}
     */
    public void setCameraController(CameraController cameraController) {
        mCameraController = cameraController;
    }

    /**
     * Pause the drawing on the preview
     */
    public void pausePreview() {
        if (mSurfaceView != null) {
            mSurfaceView.setPaused(true);
        }
    }

    /**
     * Resume the drawing on the preview
     */
    public void resumePreview() {
        if (mSurfaceView != null) {
            mSurfaceView.setPaused(false);
        }
    }

    /**
     * Shutdown this preview frame
     */
    public void shutdown() {
        mPreviewFrame.removeView(findViewById(SURFACE_VIEW_IDENTIFIER));
    }

//    @Override
//    protected void onMeasure(int widthSpec, int heightSpec) {
//        int fullWidth = 0;
//        int fullHeight = 0;
//
//        if (mScreenSize != null) {
//            fullWidth = mScreenSize.x;
//            fullHeight = mScreenSize.y;
//        }
//
//        setMeasuredDimension(fullWidth, fullHeight);
//        if (fullWidth == 0) {
//            return;
//        }
//
//        // Ask children to follow the new preview dimension.
//        super.onMeasure(MeasureSpec.makeMeasureSpec((int) fullWidth, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec((int) fullHeight, MeasureSpec.EXACTLY));
//    }
//
//    /**
//     * Handle layout <br>
//     * {@inheritDoc}
//     */
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        // Need this since its dynamically sized and the layout editor will shit itself
//        if (isInEditMode()) {
//            return;
//        }
//
//        Log.d("PreviewFrameWidget", "onLayout: ");
//        Log.d("PreviewFrameWidget", "\tChanged: " + changed);
//        Log.d("PreviewFrameWidget", "\tleft: " + l);
//        Log.d("PreviewFrameWidget", "\ttop: " + t);
//        Log.d("PreviewFrameWidget", "\tright: " + r);
//        Log.d("PreviewFrameWidget", "\tbottom: " + b);
//
//        if (!changed) {
//            return;
//        }
//
//        int fullWidth = 0;
//        int fullHeight = 0;
//
//        if (mScreenSize != null) {
//            fullWidth = mScreenSize.x;
//            fullHeight = mScreenSize.y;
//        }
//
//        Log.d("PreviewFrameWidget", "Using aspect ratio: "
//                + mAspectRatio);
//
//        int widthSpec = r - l;
//        int heightSpec = b - t;
//
//        int previewWidth = MeasureSpec.getSize(widthSpec);
//        int previewHeight = MeasureSpec.getSize(heightSpec);
//
//        Log.d("PreviewFrameWidget", "Pre-scale dimens: ("
//                + previewWidth + "x" + previewHeight + ")");
//
//        int hPadding = this.getPaddingLeft() + this.getPaddingRight();
//        int vPadding = this.getPaddingTop() + this.getPaddingBottom();
//
//        previewWidth -= hPadding;
//        previewHeight -= vPadding;
//
//        // Resize the frame and preview for the new aspect ratio
//        if (previewWidth > previewHeight * mAspectRatio) {
//            previewWidth = (int) (previewHeight * mAspectRatio + .5);
//        } else {
//            previewHeight = (int) (previewWidth / mAspectRatio + .5);
//        }
//
//        // Add the padding of the border.
//        previewWidth += hPadding;
//        previewHeight += vPadding;
//
//        Log.d("PreviewFrameWidget", "Post-scale dimens: ("
//                + previewWidth + "x" + previewHeight + ")");
//
//        // Measure out view
//        if (mPreviewFrame != null) {
//            mPreviewFrame.measure(MeasureSpec.makeMeasureSpec(previewWidth,
//                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
//                    previewHeight, MeasureSpec.EXACTLY));
//            mPreviewFrame.layout(l, t, r, b);
//        }
//
////        float width = (float) (previewWidth * mAspectRatio);
////        float height = (float) (previewHeight * mAspectRatio);
////
////        // Center the child SurfaceView within the parent.
////        if (mPreviewFrame != null) {
////            Log.d("PreviewFrameWidget", "Layout: (" + (int) ((fullWidth - width)) / 2 + ", "
////                    + (int) ((fullHeight - height)) / 2 + ", " + (int) ((fullWidth
////                    + width)) / 2 + ", " + (int) ((fullHeight + height)) / 2 + ")");
////            mPreviewFrame.layout((int) ((fullWidth - width)) / 2, (int) ((fullHeight - height))
////                    / 2, (int) ((fullWidth + width)) / 2, (int) ((fullHeight + height)) / 2);
////        }
//
//    }

    /**
     * Add a callback for the surface holder
     *
     * @param callback {@link android.view.SurfaceHolder.Callback}
     */
    public void addPreviewDataCallback(SurfaceHolder.Callback callback) {
        if (callback == null) return;
        if (mSurfaceView != null) {
            SurfaceHolder holder = mSurfaceView.getHolder();
            if (holder != null) {
                holder.addCallback(callback);
            }
        }
    }

    /**
     * Remove a callback from the surface holder
     *
     * @param callback {@link android.view.SurfaceHolder.Callback}
     */
    public void removePreviewDataCallback(SurfaceHolder.Callback callback) {
        if (callback == null) return;
        if (mSurfaceView != null) {
            SurfaceHolder holder = mSurfaceView.getHolder();
            if (holder != null) {
                holder.removeCallback(callback);
            }
        }
    }

    /**
     * Get the surface holder reference
     *
     * @return {@link android.view.SurfaceHolder}
     */
    public SurfaceHolder getHolder() {
        return mSurfaceView.getHolder();
    }

    /**
     * Get the surface view
     *
     * @return {@link CameraGLSurfaceView}
     */
    public CameraGLSurfaceView getSurfaceView() {
        return mSurfaceView;
    }

}
