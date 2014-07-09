/*
 * Copyright 2014 Martin Brabham
 * Copyright 2014 Daniel Velazco
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.holoyolostudios.colorvision;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import com.holoyolostudios.colorvision.colorlib.colors.ColorNameCache;
import com.holoyolostudios.colorvision.colorlib.util.ColorAnalyzerUtil;
import com.holoyolostudios.colorvision.colorlib.view.ColorProgressBar;
import com.holoyolostudios.colorvision.view.FlashButton;

import java.io.IOException;
import java.util.List;

/**
 * ColorVisionActivity
 * <p/>
 * Main activity for showing the camera and color detection UI
 * <p/>
 *
 * @author Martin Brabham
 * @author Daniel Velazco
 * @see {@link Activity}
 * @see {@link TextureView.SurfaceTextureListener}
 * @see {@link Camera.PreviewCallback}
 */
public class PhoneColorVisionActivity extends Activity
        implements TextureView.SurfaceTextureListener, Camera.PreviewCallback, View.OnTouchListener {

    // Constants
    private static final String TAG = "ColorVisionActivity";

    private static final String WB_AUTO = "auto";
    private static final String WB_DAYLIGHT = "daylight";
    private static final String WB_CLOUDY = "cloudy-daylight";
    private static final String WB_TUNGSTEN = "tungsten";
    private static final String WB_FLUORESCENT = "fluorescent";
    private static final String WB_INCANDESCENT = "incandescent";
    private static final String WB_HORIZON = "horizon";
    private static final String WB_SUNSET = "sunset";
    private static final String WB_SHADE = "shade";
    private static final String WB_TWILIGHT = "twilight";
    private static final String WB_WARM = "warm-fluorescent";

    // Intent actions
    private static final String ACTION_TAKE_PICTURE = "com.google.glass.action.TAKE_PICTURE";
    private static final String ACTION_TAKE_PICTURE_FROM_SCREEN_OFF = "com.google.glass.action.TAKE_PICTURE_FROM_SCREEN_OFF";

    private static String[] WHITE_BALANCE_LIST = {
            WB_AUTO,
    };

    // Members
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private ColorNameCache mColorNameCacheInstance = ColorNameCache.getInstance();
    private Camera mCamera = null;
    private Camera.Size mPreviewSize = null;
    private int mExpectedBytes = -1;
    private byte[] PREVIEW_BUFFER = null;
    private int mHalfWidth = 0;
    private int mHalfHeight = 0;
    private boolean mFlashTorchSupported = false;
    private boolean mFlashTorchActive = false;
    private AudioManager mAudioManager = null;
    private int mWhiteBalanceIndex = 0;
    private float mLastDistance = 0;
    private float mLastX = 0.0f;
    private float mLastY = 0.0f;
    private static final int mThresholdDistance = 150;
    private boolean mIsTouching = false;
    private boolean mIsClick = false;
    private Runnable mOnClickRunnable = new Runnable() {
        @Override
        public void run() {
            mIsClick = false;
        }
    };

    // Intent Members
    private IntentFilter mIntentFilter = new IntentFilter();
    private BroadcastReceiver mTakePictureReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("ColorVisionActivity", "onRecieve(" + context + ", " + intent + ")");

            // Glass specific
            // [TODO][MSB]: Make work on all devices at some point
            // This combats the winking a picture while app is running which was introduced with XE12
            if (intent != null) {
                Log.i("ColorVisionActivity", "\tIntent Action: " + intent.getAction());
                String action = intent.getAction();
                if (action != null && action.length() > 0) {
                    if (action.equals(ACTION_TAKE_PICTURE)) {
                        stopPreview();
                    }
                }
            }
        }
    };

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
    private TextView mWhiteBalanceLabel = null;
    private View mInputController = null;

    // Flags
    private boolean mIsPreviewing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(mTakePictureReciever, mIntentFilter);

        mIntentFilter.addAction(ACTION_TAKE_PICTURE);
        mIntentFilter.addAction(ACTION_TAKE_PICTURE_FROM_SCREEN_OFF);

        setContentView(R.layout.activity_main);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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

        // Sample preview of color
        mSampleView = findViewById(R.id.v_sample);

        // Reference the input controller view
        mInputController = findViewById(R.id.v_input_control);
        mInputController.setOnTouchListener(this);

        // Viewport
        mViewPort = findViewById(R.id.view_color_viewport);

        // Labels
        mColorNameLabel = (TextView) findViewById(R.id.tv_color_name);
        mColorHexLabel = (TextView) findViewById(R.id.tv_color_hex);
//        mInfoRGBLabel = (TextView) findViewById(R.id.tv_info_rgb);
        mWhiteBalanceLabel = (TextView) findViewById(R.id.tv_wb_label);

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

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("ColorVisionActivity", "New Intent");
    }

    private void setWhiteBalanceLabelText() {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                String wbName = WHITE_BALANCE_LIST[mWhiteBalanceIndex];
                char[] chars = wbName.toCharArray();
                StringBuilder wbNameBuilder = new StringBuilder();
                String charStr = String.valueOf(chars[0]);
                wbNameBuilder.append(charStr.toUpperCase());
                for (int i = 1; i < chars.length; i++) {
                    wbNameBuilder.append(chars[i]);
                }
                mWhiteBalanceLabel.setText(wbNameBuilder.toString());
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

    public void onDestroy() {
        unregisterReceiver(mTakePictureReciever);
        super.onDestroy();
    }

    private String getColorName(int r, int g, int b) {
        String colorName = null;
        if (mColorNameCacheInstance != null && mColorNameCacheInstance.isInitialized()) {
            colorName = mColorNameCacheInstance.getColorName(r, g, b);
        }
        return colorName;
    }

    private Camera.Parameters setCameraParametersForPreview(Camera.Parameters params) {
        params.setPreviewFormat(ImageFormat.NV21);

        // Load up whie balance list
        List<String> wbList = params.getSupportedWhiteBalance();
        if (wbList != null) {
            WHITE_BALANCE_LIST = new String[wbList.size()];
            for (int i = 0; i < wbList.size(); i++) {
                WHITE_BALANCE_LIST[i] = wbList.get(i);
            }
        }

        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes != null) {
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
        }

        List<String> whiteBalanceModes = params.getSupportedWhiteBalance();
        if (whiteBalanceModes != null) {
            if (whiteBalanceModes.contains(Camera.Parameters.WHITE_BALANCE_AUTO)) {
                try {
                    params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        List<String> sceneModes = params.getSupportedSceneModes();
        if (sceneModes != null) {
            if (sceneModes.contains(Camera.Parameters.SCENE_MODE_AUTO)) {
                try {
                    params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private void listAllWhiteBalances() {
        if (mCamera != null) {
            List<String> whiteBalances = mCamera.getParameters().getSupportedWhiteBalance();
            for (String wb : whiteBalances) {
                Log.i("CAMERA", "White Balance Mode '" + wb + "' available!");
            }
        }
    }

    private void startPreview(SurfaceTexture surface) {
        if (mCamera == null) {
            // Rear-facing camera only
            mCamera = Camera.open();

            // [DEBUG][MSB]: This is used to list all white balances
            //listAllWhiteBalanaces();

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
                setWhiteBalanceLabelText();
                mCamera.setPreviewTexture(surface);
                mCamera.startPreview();
                mIsPreviewing = true;
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
            mIsPreviewing = false;
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            stopPreview();
            // Need to propagate
            return false;
        } else {
            // Let super deal with it
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        final ColorAnalyzerUtil.RGBColor color = ColorAnalyzerUtil.getAverageColor(data,
                mHalfWidth - (mViewPort.getWidth() / 2), mHalfHeight - (mViewPort.getHeight() / 2), mHalfWidth + (mViewPort.getWidth() / 2), mHalfHeight + (mViewPort.getHeight() / 2));
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                mRBar.setColorProgress(color.getRed());
                mGBar.setColorProgress(color.getGreen());
                mBBar.setColorProgress(color.getBlue());
                mColorHexLabel.setText("#" + color.getHexCode().substring(2).toUpperCase());
                mColorNameLabel.setText(getColorName(color.getRed(), color.getGreen(), color.getBlue()));
                mSampleView.setBackgroundColor(color.getPixel());
            }
        });
        camera.addCallbackBuffer(PREVIEW_BUFFER);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_white_balance, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem wbMiAuto = menu.findItem(R.id.wb_mi_auto);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setWhiteBalance(String whiteBalance) {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            params.setWhiteBalance(whiteBalance);
            mCamera.setParameters(params);
            setWhiteBalanceLabelText();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.wb_mi_auto:
                setWhiteBalance(WB_AUTO);
                break;
            case R.id.wb_mi_daylight:
                setWhiteBalance(WB_DAYLIGHT);
                break;
            case R.id.wb_mi_cloudy:
                setWhiteBalance(WB_CLOUDY);
                break;
            case R.id.wb_mi_tungsten:
                setWhiteBalance(WB_TUNGSTEN);
                break;
            case R.id.wb_mi_fluorescent:
                setWhiteBalance(WB_FLUORESCENT);
                break;
            case R.id.wb_mi_incandescent:
                setWhiteBalance(WB_INCANDESCENT);
                break;
            case R.id.wb_mi_horizon:
                setWhiteBalance(WB_HORIZON);
                break;
            case R.id.wb_mi_sunset:
                setWhiteBalance(WB_SUNSET);
                break;
            case R.id.wb_mi_shade:
                setWhiteBalance(WB_SHADE);
                break;
            case R.id.wb_mi_twilight:
                setWhiteBalance(WB_TWILIGHT);
                break;
            case R.id.wb_mi_warm:
                setWhiteBalance(WB_WARM);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        invalidateOptionsMenu();
        return true;
    }

    private void setLastWhiteBalance() {
        // Decrement
        mWhiteBalanceIndex--;

        // Normalize
        if (mWhiteBalanceIndex < 0) {
            mWhiteBalanceIndex = 0;
            playClickSoundEffect();
        } else {
            playNavigationLeft();
        }

        // Set the white balance
        setWhiteBalance(WHITE_BALANCE_LIST[mWhiteBalanceIndex]);
    }

    private void setNextWhiteBalance() {
        // Increment
        mWhiteBalanceIndex++;

        // Normalize
        if (mWhiteBalanceIndex >= WHITE_BALANCE_LIST.length) {
            mWhiteBalanceIndex = WHITE_BALANCE_LIST.length - 1;
            playClickSoundEffect();
        } else {
            playNavigationRight();
        }

        // Set the white balance
        setWhiteBalance(WHITE_BALANCE_LIST[mWhiteBalanceIndex]);
    }

    /**
     * Play the click sound effect. Use whenever a menu item is clicked.
     */
    private void playClickSoundEffect() {
        mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
    }

    private void playNavigationLeft() {
        mAudioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_LEFT);
    }

    private void playNavigationRight() {
        mAudioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_RIGHT);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouching = true;
                mIsClick = true;
                sHandler.removeCallbacks(mOnClickRunnable);
                sHandler.postDelayed(mOnClickRunnable, 500);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mIsTouching) {

                    float ydiff = mLastY - event.getY();
                    if (Math.abs(ydiff) > 500) {
                        sHandler.removeCallbacks(mOnClickRunnable);
                    }

                    // Calculate the difference
                    float distance = event.getX() - mLastX;

                    if (distance - mLastDistance > mThresholdDistance) {
                        setNextWhiteBalance();
                        mLastDistance = distance;
                    }

                    if (distance - mLastDistance < -mThresholdDistance) {
                        setLastWhiteBalance();
                        mLastDistance = distance;
                    }

                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsClick) {
                    playClickSoundEffect();
                    if (mIsPreviewing) {
                        stopPreview();
                    } else {
                        startPreview(mSurfaceTexture);
                    }
                    mIsClick = false;
                }
                mLastX = event.getX();
                mIsTouching = false;
                mLastX = 0.0f;
                mLastY = 0.0f;
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

}
