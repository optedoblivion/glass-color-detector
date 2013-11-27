package com.holoyolostudios.colorblind.detector;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;

import java.io.IOException;

/**
 * Color Detector Activity
 */
public class ColorDetectorActivity extends Activity
        implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    // Constants
    private static final String LOG_TAG = "ColorDetectorActivity";
    private static final int PROCESS_FRAME_DELAY = 750;
    private static final int STATE_OFF = 0;
    private static final int STATE_PREVIEW = 1;
    private static final int STATE_NO_CALLBACKS = 2;

    // Members
    private ColorNameCache mColorNameCacheInstance = ColorNameCache.getInstance();
    private Camera mCamera = null;
    private Camera.Size mPreviewSize = null;
    private int mExpectedBytes = -1;
    private boolean mColorProcessInProgress = false;
    private int mState = STATE_OFF;
    private long mLastProcessedFrameTimestamp = -1;
    private byte[] PREVIEW_BUFFER = null;

    // Views
    private TextView mTvColorName = null;
    private View mViewOverlay = null;
    private ProgressBar mProgressBar = null;
    private TextureView mTextureView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the views
        mTextureView = (TextureView) findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(this);

        mTvColorName = (TextView) findViewById(R.id.tv_color_name);
        mViewOverlay = findViewById(R.id.view_overlay);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

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

    private void setColorName(String colorName) {
        if (mTvColorName != null) {
            if (!TextUtils.isEmpty(colorName)) {
                mTvColorName.setText(colorName);
                mTvColorName.setVisibility(View.VISIBLE);
            } else {
                mTvColorName.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Helper method to get the name of the color based on the passed RGB values.
     * This will set it to the {@link android.widget.TextView}
     *
     * @param r {@link int}
     * @param g {@link int}
     * @param b {@link int}
     */
    private void setColorName(int r, int g, int b) {
        setColorName(getColorName(r, g, b));
    }

    /**
     * AsyncTask to initialize the color cache in case it hasn't been initialized yet.
     */
    private class CacheInitTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mTvColorName.setVisibility(View.INVISIBLE);
            mViewOverlay.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return mColorNameCacheInstance.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mViewOverlay.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
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
                mState = STATE_PREVIEW;
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
            mState = STATE_OFF;
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
        if (mColorProcessInProgress || mState != STATE_PREVIEW) {
            mCamera.addCallbackBuffer(data);
            return;
        }

        long currentTime = System.currentTimeMillis();
        if ((currentTime - mLastProcessedFrameTimestamp) < PROCESS_FRAME_DELAY) {
            mCamera.addCallbackBuffer(data);
            return;
        }
        mLastProcessedFrameTimestamp = currentTime;

        if (data == null) {
            return;
        }

        // An error has occurred, ABORT
        if (mExpectedBytes != data.length) {
            Log.e(LOG_TAG, "Mismatched size of buffer! (" + data.length + ") Expected: " + mExpectedBytes);
            mState = STATE_NO_CALLBACKS;
            mCamera.setPreviewCallbackWithBuffer(null);
            return;
        }

        mColorProcessInProgress = true;

        new ProcessPreviewDataTask().execute(data);
    }

    private boolean mImageSaved = false;

    private class ProcessPreviewDataTask extends AsyncTask<byte[], Void, String> {

        protected String doInBackground(byte[]... datas) {
            byte[] data = datas[0];
            String colorName = null;

//            // Convert YUV image to ARGB
            int[] argb = convertYUV420_NV21toARGB8888(data, mPreviewSize.width, mPreviewSize.height);

//            Bitmap b = mTextureView.getBitmap();
//            int argb[] = new int[b.getByteCount()];
//            b.getPixels(argb, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
//            b.recycle();
//            b = null;

            int index = argb.length / 4;
            index = (int) ((float) (index / 2) + .5f);
            index *= 4;

            int pixel = 0x00000000;
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
            mColorProcessInProgress = false;
            return colorName;
        }

        protected void onPostExecute(String colorName) {
            setColorName(colorName);
        }

    }

    /**
     * Converts YUV420 NV21 to ARGB8888
     *
     * @param data   byte array on YUV420 NV21 format.
     * @param width  pixels width
     * @param height pixels height
     * @return a ARGB8888 pixels int array. Where each int is a pixels ARGB.
     */
    public static int[] convertYUV420_NV21toARGB8888(byte[] data, int width, int height) {
        int size = width * height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;

        // i along Y and the final pixels
        // k along pixels U and V
        for (int i = 0, k = 0; i < size; i += 2, k += 1) {
            y1 = data[i] & 0xff;
            y2 = data[i + 1] & 0xff;
            y3 = data[width + i] & 0xff;
            y4 = data[width + i + 1] & 0xff;

            v = data[offset + k] & 0xff;
            u = data[offset + k + 1] & 0xff;
            v = v - 128;
            u = u - 128;

            pixels[i] = convertYUVtoARGB(y1, u, v);
            pixels[i + 1] = convertYUVtoARGB(y2, u, v);
            pixels[width + i] = convertYUVtoARGB(y3, u, v);
            pixels[width + i + 1] = convertYUVtoARGB(y4, u, v);

            if (i != 0 && (i + 2) % width == 0)
                i += width;
        }

        return pixels;
    }

    private static int convertYUVtoARGB(int y, int u, int v) {
        int r, g, b;
        r = y + (int) (1.402f * u);
        g = y - (int) (0.344f * v + 0.714f * u);
        b = y + (int) (1.772f * v);
        r = r > 255 ? 255 : r < 0 ? 0 : r;
        g = g > 255 ? 255 : g < 0 ? 0 : g;
        b = b > 255 ? 255 : b < 0 ? 0 : b;
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

}
