package com.holoyolostudios.colorblind.detector.camera.view.opengl;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.holoyolostudios.colorblind.detector.camera.BaseCamera;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * CameraGLRenderer
 * <br/>
 *
 * @see {@link android.opengl.GLSurfaceView.Renderer}
 * <p/>
 * Created by martin on 7/31/13.
 */
public class CameraGLRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    // Members
    private CameraGLSurfaceView mSurfaceView = null;
    private SurfaceTexture mSurfaceTexture = null;
    private CameraGLPreviewShape mPreviewShape = null;
    private int mTexture = -1;
    private BaseCamera mCamera = null;
    private int mWidth = 0;
    private int mHeight = 0;

    // Flags
    private boolean mIsPaused = false;
    private boolean mIsPortrait = false;

    // Matrices
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private final float[] mMTX = new float[16];

    /**
     * Constructor
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CameraGLRenderer(CameraGLSurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mTexture = createTexture();
        mSurfaceTexture = new SurfaceTexture(mTexture);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    /**
     * Create a new texture and set it up
     */
    private static int createTexture() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Create shape here
        mPreviewShape = new CameraGLPreviewShape();

        // Prepare preview shape
        mPreviewShape.onPrepare(mTexture);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        GLES20.glViewport(0, 0, (int) (width * (4.0 / 3.0)), height);

//        float ratio = (float) width / height;
//
//        Log.i("CameraGLRenderer", "Initial Perspective Ratio: " + ratio);
////        // Invert aspect ratio
////        if (mIsPortrait) {
////            ratio = 1 / ratio; // Needed for portrait
////            Log.i("CameraGLRenderer", "Inverted Perspective Ratio: " + ratio);
////        }
//
//        // Scale to fit
//        Log.i("CameraGLRenderer", "Surface Size: " + width + "x" + height);
//        if (width >= height * ratio) {
//            width = (int) (height * ratio + .5);
//        } else {
//            height = (int) (width / ratio + .5);
//        }
//
//        // Setup viewport
//        GLES20.glViewport(0, 0, width, height);
//        Log.i("CameraGLRenderer", "Scaled Size: " + width + "x" + height);
//
//        // this projection matrix is applied to object coordinates
//        // in the onDrawFrame() method
//        Matrix.frustumM(mProjMatrix, 0, -1, 1, -1, 1, 3, 7);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        try {

            if (mIsPaused) {
                return;
            }

            if (mCamera == null) {
                return;
            }

            // Clear
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            int angle = mCamera.getCameraConfig().transposition;
//            Log.i("CameraGLRenderer", "transposition: " + angle);

            // Set the camera Z position (View matrix)
            int eyeZ = 0;
            if (mCamera.getCameraConfig().is_ffc) {
                eyeZ = -3;
            } else {
                eyeZ = 3;
            }

            // Setup camera
            Matrix.setLookAtM(mVMatrix, 0, 0, 0, eyeZ, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            synchronized (this) {
                // This updates the data in to display
                mSurfaceTexture.updateTexImage();
                mPreviewShape.checkGlError("updateTexImage");
                // Get matrix to normalize image
                mSurfaceTexture.getTransformMatrix(mMTX);
                mPreviewShape.checkGlError("getTransformMatrix");
            }

            // Calculate the projection and view transformation
            Matrix.multiplyMM(mMTX, 0, mProjMatrix, 0, mVMatrix, 0);
            if (angle != 0) {
                // Set rotation
//                Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
//                Matrix.multiplyMM(mMTX, 0, mRotationMatrix, 0, mVMatrix, 0);
            }

            mPreviewShape.draw(mMTX);
            mPreviewShape.checkGlError("draw");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the surface texture
     *
     * @return {@link android.graphics.SurfaceTexture}
     */
    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    /**
     * Set the camera config
     */
    public void setCamera(BaseCamera camera) {
        mCamera = camera;
    }

    /**
     * Shuts down the preview shape and releases texture
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void shutdown() {
        if (mPreviewShape != null) {
            mPreviewShape.shutdown();
        }
        mSurfaceTexture.release();
        deleteTex();
    }

    /**
     * Delete the texture by pointer reference
     */
    private void deleteTex() {
        int textures[] = new int[1];
        textures[0] = mTexture;
        GLES20.glDeleteTextures(1, textures, 0);
    }

    // Extremely dirty hack
    // [TODO][DV]: remove me and fix properly
    private static final int MSG_UNPAUSE = 1001;
    private static final int RENDERING_UNPAUSE_MS = 500; // 0.5 sec
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_UNPAUSE:
                    mIsPaused = false;
                    break;

            }
        }
    };

    /**
     * Pause the rendering on frames
     *
     * @param paused {@link boolean}
     */
    public void setPaused(boolean paused) {
        if (paused) {
            mHandler.removeMessages(MSG_UNPAUSE);
            mIsPaused = true;
        } else {
            // Extremely dirty hack
            // [TODO][DV]: remove me and fix properly
            mHandler.sendEmptyMessageDelayed(MSG_UNPAUSE, RENDERING_UNPAUSE_MS);
        }
    }

    // FPS tickers
    private int mFrameCount = 0;
    private long mLastTick = 0l;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mSurfaceView != null) {
            // Calculate frames per second (FPS)
            long now = System.currentTimeMillis();
            if ((now - mLastTick) > 1000) {
                mSurfaceView.addFpsTick(mFrameCount);
                Log.d("VIDEO_RECORDER", "Renderer FPS: " + mFrameCount);
                mFrameCount = 0;
                mLastTick = now;
            } else {
                mFrameCount++;
            }
            mSurfaceView.requestRender();
        }
    }
}
