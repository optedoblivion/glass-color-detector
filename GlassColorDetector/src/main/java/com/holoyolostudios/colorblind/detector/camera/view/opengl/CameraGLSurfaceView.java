package com.holoyolostudios.colorblind.detector.camera.view.opengl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;

import com.holoyolostudios.colorblind.detector.camera.BaseCamera;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CameraGLSurfaceView
 * <br/>
 * <br/>
 * @see {@link android.opengl.GLSurfaceView}
 * Created by martin on 8/27/13.
 */
public class CameraGLSurfaceView extends GLSurfaceView {

    // Scale type constants
    public static final int SCALE_TYPE_NONE = 0;
    public static final int SCALE_TYPE_CENTER_CROP = 1;
    private static final int[] mScaleTypeList = {
            SCALE_TYPE_NONE,
            SCALE_TYPE_CENTER_CROP
    };

    // Members
    private CameraGLRenderer mRenderer = null;
    private BaseCamera mCamera = null;
    private Matrix mDrawMatrix = null;
    private Rect mDrawRect = null;
    private int mScaleType = SCALE_TYPE_NONE;
    public final Map<Integer, Integer> mFPSTickList = new ConcurrentHashMap<Integer, Integer>();

    /**
     * Constructor
     *
     * @param context {@link android.content.Context}
     */
    public CameraGLSurfaceView(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context {@link android.content.Context}
     * @param attrs   {@link android.util.AttributeSet}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        mRenderer = new CameraGLRenderer(this);
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        if (this.getHolder() != null) {
            this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }

        this.setRenderer(mRenderer);
        this.setRenderMode(RENDERMODE_CONTINUOUSLY);

    }

    /**
     * Set the camera config
     *
     */
    public void setCamera(BaseCamera camera) {
        mCamera = camera;
        mRenderer.setCamera(mCamera);
    }

    /**
     * Pause the {@link CameraGLRenderer} from rendering frames
     *
     * @param paused {@link boolean}
     */
    public void setPaused(boolean paused) {
        if (mRenderer != null) {
            mRenderer.setPaused(paused);
            this.setRenderMode(paused ? RENDERMODE_WHEN_DIRTY : RENDERMODE_CONTINUOUSLY);
        }
    }

    /**
     * Get the view's renderer
     *
     * @return {@link CameraGLRenderer}
     */
    public CameraGLRenderer getRenderer() {
        return mRenderer;
    }

    /**
     * Shutdown the views
     */
    public void shutdown() {
        if (mRenderer != null) {
            mRenderer.shutdown();
        }
    }

    /**
     * Overridden to handle drawing view for HDPI and lower devices
     * <br/>
     * {@inheritDoc}
     */
    public void onDraw(Canvas c) {
        if (mDrawMatrix != null) {
            c.concat(mDrawMatrix);
        }
        super.onDraw(c);
    }

    /**
     * Set the scale type to use when drawing
     *
     * @param scaleType Integer
     * @throws IllegalArgumentException {@link IllegalArgumentException}
     */
    public void setScaleType(int scaleType) throws IllegalArgumentException {
        for (int scaleTypeItem : mScaleTypeList) {
            if (scaleType == scaleTypeItem) {
                updateScaleType(scaleType);
                return;
            }
        }
        throw new IllegalArgumentException("Invalid scaletype!");
    }

    /**
     * Update the scaletype and rebuild the matrix
     *
     * @param scaleType Integer
     */
    private void updateScaleType(int scaleType) {
        mScaleType = scaleType;
        switch (mScaleType) {
            case SCALE_TYPE_NONE:
                clearDrawMatrix();
                break;
            case SCALE_TYPE_CENTER_CROP:
                buildDrawMatrix();
                break;
        }
    }

    /**
     * Clear drawing items
     */
    private void clearDrawMatrix() {
        mDrawRect = null;
        mDrawMatrix = null;
        // Hand drawing solely back over to surfaceview drawing thread
        setWillNotDraw(true);
    }

    /**
     * Add an FPS tick
     *
     * @param tick
     */
    public void addFpsTick(int tick) {
        mFPSTickList.put(tick, tick);
    }

    /**
     * Build a draw matrix for center crop of inner views
     */
    private void buildDrawMatrix() {
        // Check if we don't already have a drawing matrix set
        if (mDrawMatrix == null) {
            // Take control of drawing also
            setWillNotDraw(false);
            // Setup rect
            mDrawRect = new Rect();
            // Make new matrix
            mDrawMatrix = new Matrix();
            // Set the drawing rect (bounds of image)
            getDrawingRect(mDrawRect);
            // Grab image w and h
            int pfWidth = mDrawRect.width();
            int pfHeight = mDrawRect.height();
            // Grab our w and h, don't count padding
            int myWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int myHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            // Setup scale
            float scale;
            float pfx = 0;
            float pfy = 0;
            if (pfWidth * myHeight > myWidth * pfHeight) {
                scale = (float) myHeight / (float) pfHeight;
                pfx = (myWidth - pfWidth * scale) * 0.5f;
            } else {
                scale = (float) myWidth / (float) pfWidth;
                pfx = (myHeight - pfHeight * scale) * 0.5f;
            }
            // Setup matrix
            mDrawMatrix.setScale(scale, scale);
            mDrawMatrix.postTranslate(pfx, pfy);
        }
    }

}
