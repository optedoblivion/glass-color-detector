package com.holoyolostudios.colorblind.detector;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;
import com.holoyolostudios.colorblind.detector.util.ColorAnalyzerUtil;
import com.holoyolostudios.colorblind.detector.view.ColorProgressBar;
import com.holoyolostudios.colorblind.detector.view.FlashButton;

import java.io.IOException;
import java.util.List;

/**
 * ColorDetectorActivity
 * <p/>
 * Main activity for showing the camera and color detection UI
 * <p/>
 *
 * @see {@link android.app.Activity}
 * @see {@link android.view.TextureView.SurfaceTextureListener}
 * @see {@link android.hardware.Camera.PreviewCallback}
 */
public class ColorDetectorActivity extends Activity
        implements TextureView.SurfaceTextureListener, Camera.PreviewCallback, View.OnTouchListener {

    // Constants
    private static final String TAG = "ColorDetectorActivity";

    // Members
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private ColorNameCache mColorNameCacheInstance = ColorNameCache.getInstance();
    private Camera mCamera = null;
    private Camera.Size mPreviewSize = null;
    private int mExpectedBytes = -1;
    private byte[] PREVIEW_BUFFER = null;
    private int mHalfWidth = 0;
    private int mHalfHeight = 0;
    private boolean mFlashTorchSupported = false;
    private boolean mFlashTorchActive = false;

    // Views
    private TextureView mTextureView = null;
    private SurfaceTexture mSurfaceTexture = null;
    private ColorProgressBar mRBar = null;
    private ColorProgressBar mGBar = null;
    private ColorProgressBar mBBar = null;
    private TextView mColorNameLabel = null;
    private TextView mColorHexLabel = null;
    private TextView mInfoRGBLabel = null;
    private View mSampleView = null;
    private FlashButton mBtnFlashTorch = null;
    private View mViewPort = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the views
        mTextureView = (TextureView) findViewById(R.id.tv_camera_preview);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOnTouchListener(this);

        // Progress bars
        mRBar = (ColorProgressBar) findViewById(R.id.cpb_r);
        mRBar.setLabelText("R");
        mGBar = (ColorProgressBar) findViewById(R.id.cpb_g);
        mGBar.setLabelText("G");
        mBBar = (ColorProgressBar) findViewById(R.id.cpb_b);
        mBBar.setLabelText("B");

        // Sample preview of color
        mSampleView = findViewById(R.id.v_sample);

        // Viewport
        mViewPort = findViewById(R.id.view_color_viewport);

        // Labels
        mColorNameLabel = (TextView) findViewById(R.id.tv_color_name);
        mColorHexLabel = (TextView) findViewById(R.id.tv_color_hex);
        mInfoRGBLabel = (TextView) findViewById(R.id.tv_info_rgb);

        // Flash torch button
        mBtnFlashTorch = (FlashButton) findViewById(R.id.btn_flash_torch);
        mBtnFlashTorch.setVisibility(mFlashTorchSupported ? View.VISIBLE : View.GONE);
        mBtnFlashTorch.setTorchFlashOn(mFlashTorchActive);
        mBtnFlashTorch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    Camera.Parameters params = mCamera.getParameters();
                    if (mFlashTorchActive) {
                        mFlashTorchActive = false;
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    } else {
                        mFlashTorchActive = true;
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    }
                    mBtnFlashTorch.setTorchFlashOn(mFlashTorchActive);
                    mCamera.setParameters(params);
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (mSurfaceTexture != null) {
            startPreview(mSurfaceTexture);
        }

    }

    public void onPause() {
        stopPreview();
        super.onPause();
    }

    private String getColorName(int r, int g, int b) {
        String colorName = null;
        if (mColorNameCacheInstance != null && mColorNameCacheInstance.isInitialized()) {
            colorName = mColorNameCacheInstance.getColorName(r, g, b);
        }
        return colorName;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("TEST", "Event: " + event.getAction());
        return true;
    }

    private Camera.Parameters setCameraParametersForPreview(Camera.Parameters params) {
        params.setPreviewFormat(ImageFormat.NV21);

        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes != null) {
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
        }

        List<String> whiteBalanceModes = params.getSupportedWhiteBalance();
        if (whiteBalanceModes != null) {
            if (whiteBalanceModes.contains(Camera.Parameters.WHITE_BALANCE_AUTO)) {
                params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            }
        }

        List<String> sceneModes = params.getSupportedSceneModes();
        if (sceneModes != null) {
            if (sceneModes.contains(Camera.Parameters.SCENE_MODE_AUTO)) {
                params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            }
        }

        mFlashTorchActive = params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH);
        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes != null) {
            mFlashTorchSupported = flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH);
            mBtnFlashTorch.setVisibility(mFlashTorchSupported ? View.VISIBLE : View.GONE);
        }

        // Hack for Google glass
        if (Build.MODEL.contains("Glass")) {
            params.setPreviewSize(640, 360);
            params.setPreviewFpsRange(30000, 30000);
        }

        return params;
    }

    private void startPreview(SurfaceTexture surface) {
        if (mCamera == null) {
            // Rear-facing camera only
            mCamera = Camera.open();
        }
        try {
            if (mCamera != null && surface != null) {
                Camera.Parameters p = mCamera.getParameters();
                p = setCameraParametersForPreview(p);
                mPreviewSize = p.getPreviewSize();
                Log.d(TAG, "mPreviewSize.width: " + mPreviewSize.width);
                Log.d(TAG, "mPreviewSize.height: " + mPreviewSize.height);
                mExpectedBytes = mPreviewSize.width * mPreviewSize.height * 3 / 2;
                ColorAnalyzerUtil.FRAME_WIDTH = mPreviewSize.width;
                ColorAnalyzerUtil.FRAME_HEIGHT = mPreviewSize.height;
                mHalfWidth = mPreviewSize.width / 2;
                mHalfHeight = mPreviewSize.height / 2;
                mCamera.setParameters(p);
                mCamera.setPreviewCallbackWithBuffer(this);
                PREVIEW_BUFFER = new byte[mExpectedBytes];
                mCamera.addCallbackBuffer(PREVIEW_BUFFER);
                mCamera.setPreviewTexture(surface);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "SurfaceTexture available!");
        mSurfaceTexture = surface;
        stopPreview();
        startPreview(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "SurfaceTexture changed!");
        mSurfaceTexture = surface;
        stopPreview();
        startPreview(surface);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "SurfaceTexture destroyed!");
        mSurfaceTexture = surface;
        stopPreview();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        mSurfaceTexture = surface;
        Log.i(TAG, "SurfaceTexture updated!");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            if (mCamera != null) {
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        final ColorAnalyzerUtil.RGBColor color = ColorAnalyzerUtil.getAverageColor(data,
                mHalfWidth - (mViewPort.getWidth() / 2), mHalfHeight - (mViewPort.getHeight() / 2), mHalfWidth + (mViewPort.getWidth() / 2), mHalfHeight + (mViewPort.getHeight() / 2));
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRBar.setColorProgress(color.getRed());
                mGBar.setColorProgress(color.getGreen());
                mBBar.setColorProgress(color.getBlue());
                mColorHexLabel.setText("#" + color.getHexCode().substring(2).toUpperCase());
                mColorNameLabel.setText(getColorName(color.getRed(), color.getGreen(), color.getBlue()));
                mInfoRGBLabel.setText("R: " + color.getRed() + " G: " + color.getGreen() + " B: " + color.getBlue());
                mSampleView.setBackgroundColor(color.getPixel());
            }
        });
        camera.addCallbackBuffer(PREVIEW_BUFFER);
    }

}
