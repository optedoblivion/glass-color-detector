package com.holoyolostudios.colorblind.detector;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.holoyolostudios.colorblind.detector.colors.ColorNameCache;

public class ColorDetectorImmersionCard extends Activity {

    // Views
    private View mViewCameraOverlayMockup = null;
    private TextView mTvColorName = null;
    private View mViewOverlay = null;
    private ProgressBar mProgressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewCameraOverlayMockup = findViewById(R.id.view_camera_preview_mock);
        mTvColorName = (TextView) findViewById(R.id.tv_color_name);
        mViewOverlay = findViewById(R.id.view_overlay);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        (new CacheInitTask()).execute();
    }

    private class CacheInitTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mTvColorName.setVisibility(View.INVISIBLE);
            mViewOverlay.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return ColorNameCache.getInstance().init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mTvColorName.setVisibility(View.VISIBLE);
            mViewOverlay.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }

    }

}
