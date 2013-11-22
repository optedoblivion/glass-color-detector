package com.holoyolostudios.colorblind.detector;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holoyolostudios.colorblind.detector.camera.CameraController;
import com.holoyolostudios.colorblind.detector.camera.view.PreviewFrameWidget;
import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;

import java.io.IOException;

/**
 * Color Detector Activity
 */
public class ColorDetectorActivity extends Activity {

    // Members
    private ColorNameCache mColorNameCacheInstance = ColorNameCache.getInstance();
    private CameraController mCameraController = null;

    // Views
    private TextView mTvColorName = null;
    private View mViewOverlay = null;
    private ProgressBar mProgressBar = null;
    private PreviewFrameWidget mPreviewFrameWidget = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In case we are running on a device that has an ActionBar, hide it
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Setup the views
        mTvColorName = (TextView) findViewById(R.id.tv_color_name);
        mViewOverlay = findViewById(R.id.view_overlay);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mPreviewFrameWidget = (PreviewFrameWidget) findViewById(R.id.pfl_camera_preview);
        mCameraController = ColorDetectorApplication.getCameraController();
        mCameraController.getCameraAccessor().getCurrentCamera().setIsPortrait(false);
        mPreviewFrameWidget.setCamera(mCameraController.getCameraAccessor().getRearFacingCamera());
        mPreviewFrameWidget.setIsPortrait(false);
        try {
            mCameraController.getCameraAccessor().getCurrentCamera().startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Execute the AsyncTask and see if the ColorNameCache needs to be initialized
        (new CacheInitTask()).execute();
    }

    public void onResume() {
        super.onResume();
        mPreviewFrameWidget.onResume();
        mCameraController.onResume();
    }

    public void onPause() {
        mCameraController.onPause();
        mPreviewFrameWidget.onPause();
        super.onPause();
    }

    public void onDestroy() {
        mCameraController.shutdown();
        super.onDestroy();
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
        if (mColorNameCacheInstance != null && mColorNameCacheInstance.isInitialized()) {
            String colorName = mColorNameCacheInstance.getColorName(r, g, b);
            mTvColorName.setText(colorName);
            mTvColorName.setVisibility(View.VISIBLE);
        }
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

            setColorName(0xFF, 0x00, 0xFF);
        }

    }

}
