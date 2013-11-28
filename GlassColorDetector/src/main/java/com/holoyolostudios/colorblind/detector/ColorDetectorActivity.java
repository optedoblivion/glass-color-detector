package com.holoyolostudios.colorblind.detector;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;

import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;
import com.holoyolostudios.colorblind.detector.view.ColorProgressBar;
import com.holoyolostudios.colorblind.detector.util.ColorAnalyzerUtil;

import java.io.IOException;

/**
 * ColorDetectorActivity
 * <p/>
 * <p/>
 *
 * @see {@link android.app.Activity}
 * @see {@link android.view.TextureView.SurfaceTextureListener}
 * @see {@link android.hardware.Camera.PreviewCallback}
 */
public class ColorDetectorActivity extends Activity
        implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    // Constants
    private static final String LOG_TAG = "ColorDetectorActivity";

    // Members
    private ColorNameCache mColorNameCacheInstance = ColorNameCache.getInstance();
    private Camera mCamera = null;
    private Camera.Size mPreviewSize = null;
    private int mExpectedBytes = -1;
    private byte[] PREVIEW_BUFFER = null;

    // Views
    private TextureView mTextureView = null;
    private ColorProgressBar mRBar = null;
    private ColorProgressBar mGBar = null;
    private ColorProgressBar mBBar = null;
    private TextView mColorNameLabel = null;
    private TextView mColorHexLabel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the views
        mTextureView = (TextureView) findViewById(R.id.tv_camera_preview);
        mTextureView.setSurfaceTextureListener(this);

        // Progress bars
        mRBar = (ColorProgressBar) findViewById(R.id.cpb_r);
        mRBar.setLabelText("R");
        mGBar = (ColorProgressBar) findViewById(R.id.cpb_g);
        mGBar.setLabelText("G");
        mBBar = (ColorProgressBar) findViewById(R.id.cpb_b);
        mBBar.setLabelText("B");

        // Labels
        mColorNameLabel = (TextView) findViewById(R.id.tv_color_name);
//        mColorHexLabel = (TextView) findViewById(R.id.tv_color_hex);

        // Execute the AsyncTask and see if the ColorNameCache needs to be initialized
        (new CacheInitTask()).execute();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        if (mCamera != null) {
            mCamera.release();
        }
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private String getColorName(int r, int g, int b) {
        String colorName = null;
        if (mColorNameCacheInstance != null && mColorNameCacheInstance.isInitialized()) {
            colorName = mColorNameCacheInstance.getColorName(r, g, b);
        }
        return colorName;
    }

    /**
     * AsyncTask to initialize the color cache in case it hasn't been initialized yet.
     */
    private class CacheInitTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return mColorNameCacheInstance.init();
        }

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();
        try {
            if (mCamera != null && surface != null) {
                Camera.Parameters p = mCamera.getParameters();
//                p.setPreviewSize(640, 360); // TODO: why?
                mPreviewSize = p.getPreviewSize();
                if (mPreviewSize != null) {
                    Log.e("PIXELS", "mPreviewSize.width: " + mPreviewSize.width);
                    Log.e("PIXELS", "mPreviewSize.height: " + mPreviewSize.height);
                    mExpectedBytes = mPreviewSize.width * mPreviewSize.height * 3 / 2;
                }
                p.setPreviewFpsRange(30000, 30000);
                p.setPreviewFormat(ImageFormat.NV21);
                p.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                p.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
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

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        new ProcessPreviewDataTask().execute(data);
        camera.addCallbackBuffer(PREVIEW_BUFFER);
    }

    private boolean mImageSaved = false;

    private class ProcessPreviewDataTask extends AsyncTask<byte[], Void, String> {

        protected String doInBackground(byte[]... datas) {
            byte[] data = datas[0];

            String colorName = null;

//            // Convert YUV image to ARGB
            int[] argb = ColorAnalyzerUtil.convertYUV420_NV21toRGB8888(data, mPreviewSize.width, mPreviewSize.height);

            int index = argb.length / 4;
            index = (int) ((float) (index / 2) + .5f);
            index *= 4;

            int pixel = 0xFF000000;
            pixel |= argb[index];
            pixel |= argb[index + 1];
            pixel |= argb[mPreviewSize.width + index];
            pixel |= argb[mPreviewSize.width + index + 1];

            int alpha = Color.alpha(pixel);
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            Log.e("PIXEL COLOR: ", "" + red + ", " + green + ", " + blue);

            colorName = getColorName(red, green, blue);

            if (mCamera != null) {
                mCamera.addCallbackBuffer(data);
            }
            return colorName;
        }

        protected void onPostExecute(String colorName) {

        }

    }

}
