package com.holoyolostudios.colordetector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.holoyolostudios.colordetector.colors.ColorNameCache;
import com.holoyolostudios.colordetector.util.ColorAnalyzerUtil;
import com.holoyolostudios.colordetector.util.TrialPeriodManager;
import com.holoyolostudios.colordetector.view.ColorProgressBar;
import com.holoyolostudios.colordetector.view.FlashButton;

import java.io.IOException;
import java.util.List;

/**
 * ColorDetectorActivity
 * <p/>
 * Main activity for showing the camera and color detection UI
 * <p/>
 *
 * @author Martin Brabham
 * @see {@link android.app.Activity}
 * @see {@link android.view.TextureView.SurfaceTextureListener}
 * @see {@link android.hardware.Camera.PreviewCallback}
 */
public class ColorDetectorActivity extends Activity
        implements TextureView.SurfaceTextureListener, Camera.PreviewCallback,
        GestureDetector.BaseListener, GestureDetector.ScrollListener, GestureDetector.FingerListener {

    // Constants
    private static final String TAG = "ColorDetectorActivity";

    // White balance options available for Google Glass as of XE11
    /*
        White Balance Mode 'auto' available!
        White Balance Mode 'daylight' available!
        White Balance Mode 'cloudy-daylight' available!
        White Balance Mode 'tungsten' available!
        White Balance Mode 'fluorescent' available!
        White Balance Mode 'incandescent' available!
        White Balance Mode 'horizon' available!
        White Balance Mode 'sunset' available!
        White Balance Mode 'shade' available!
        White Balance Mode 'twilight' available!
        White Balance Mode 'warm-fluorescent' available!
    */
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

    private static final String[] WHITE_BALANCE_LIST = {
            WB_AUTO,
            WB_DAYLIGHT,
            WB_CLOUDY,
            WB_TUNGSTEN,
            WB_FLUORESCENT,
            WB_INCANDESCENT,
            WB_HORIZON,
            WB_SUNSET,
            WB_SHADE,
            WB_TWILIGHT,
            WB_WARM
    };

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
    private AudioManager mAudioManager = null;
    private GestureDetector mGestureDetector = null;
    private int mWhiteBalanceIndex = 0;

    // Intent Members
    private IntentFilter mIntentFilter = new IntentFilter();
    private BroadcastReceiver mTakePictureReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("ColorDetectorActivity", "onRecieve(" + context + ", " + intent + ")");

            // Glass specific
            // [TODO][MSB]: Make work on all devices at some point
            // This combats the winking a picture while app is running which was introduced with XE12
            if (intent != null) {
                Log.i("ColorDetectorActivity", "\tIntent Action: " + intent.getAction());
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

    // Flags
    private boolean mIsPreviewing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long now = System.currentTimeMillis() / 1000;
        if (now > TrialPeriodManager.EXPIRATION_TIMESTAMP) {
            Toast.makeText(this, "Trial period has expired!", Toast.LENGTH_LONG).show();
            Uri packageURI = Uri.fromParts("package", "com.holoyolostudios.colordetector", null);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            startActivity(uninstallIntent);
            finish();
        }

        registerReceiver(mTakePictureReciever, mIntentFilter);

        mIntentFilter.addAction(ACTION_TAKE_PICTURE);
        mIntentFilter.addAction(ACTION_TAKE_PICTURE_FROM_SCREEN_OFF);

        setContentView(R.layout.activity_main);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(this).setScrollListener(this).setFingerListener(this);


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

        // Viewport
        mViewPort = findViewById(R.id.view_color_viewport);

        // Labels
        mColorNameLabel = (TextView) findViewById(R.id.tv_color_name);
        mColorHexLabel = (TextView) findViewById(R.id.tv_color_hex);
        mInfoRGBLabel = (TextView) findViewById(R.id.tv_info_rgb);
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
        Log.i("ColorDetectorActivity", "New Intent");
    }

    private void setWhiteBalanceLabelText() {
        mHandler.post(new Runnable() {
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
            // Need to propogate
            return false;
//        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//            openOptionsMenu();
//            // Don't need to propogate
//            return true;
        } else {
            // Let super deal with it
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
//                mInfoRGBLabel.setText("R: " + color.getRed() + " G: " + color.getGreen() + " B: " + color.getBlue());
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

    @Override
    public boolean onGesture(Gesture gesture) {
        if (gesture == Gesture.TAP) {
            playClickSoundEffect();
            if (mIsPreviewing) {
                stopPreview();
            } else {
                startPreview(mSurfaceTexture);
            }
            return true;
        }

        return false;
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

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
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

    private float mLastDistance = 0;
    private int mThresholdDistance = 150;

    @Override
    public boolean onScroll(float distance, float delta, float velocity) {
        if (!mIsPreviewing) {
            return false;
        }
        if (distance > 0) {
            if (distance - mLastDistance > mThresholdDistance) {
                // Scroll Right
                setNextWhiteBalance();
                mLastDistance = distance;
            }
        } else if (distance < 0) {
            if (distance - mLastDistance < -mThresholdDistance) {
                // Scroll Left
                setLastWhiteBalance();
                mLastDistance = distance;
            }
        }
        return true;
    }

    @Override
    public void onFingerCountChanged(int previewCount, int currentCount) {
        if (currentCount == 0) {
            mLastDistance = 0;
        }
    }
}
