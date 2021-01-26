package com.xm6leefun.scan_lib;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.xm6leefun.scan_lib.camera.CameraManager;
import com.xm6leefun.scan_lib.camera.ExScanActivityHandler;
import com.xm6leefun.scan_lib.camera.InactivityTimer;
import com.xm6leefun.scan_lib.camera.ViewfinderView;
import com.xm6leefun.scan_lib.utils.CircularAnimUtil;
import com.xm6leefun.scan_lib.zxing.BarcodeFormat;
import com.xm6leefun.scan_lib.zxing.Result;
import java.io.IOException;
import java.util.Vector;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/1/18 13:43
 */
public class ScanApiActivity extends Activity implements SurfaceHolder.Callback {
    public static final int SCAN_CODE = 0x1024;
    public static final String SCAN_RESULT = "scan_result";
    public static final String RESULT_TYPE = "result_type";
    protected TextView tvTitle;
    protected ImageView imFlash;
    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;

    private ExScanActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    public static void jump(Activity activity,View triggerView){
        Intent intent = new Intent(activity,ScanApiActivity.class);
        CircularAnimUtil.startActivity(activity, intent, triggerView,R.color.tran, 100);
    }
    public static void jumpForResult(Activity activity,View triggerView){
        Intent intent = new Intent(activity,ScanApiActivity.class);
        CircularAnimUtil.startActivityForResult(activity, intent, SCAN_CODE,triggerView,R.color.tran, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_api);
        surfaceView = findViewById(R.id.preview_view);
        viewfinderView = findViewById(R.id.viewfinder_view);
        tvTitle = findViewById(R.id.base_topBar_tv_title);
        imFlash = findViewById(R.id.base_topBar_iv_right);
        imFlash.setVisibility(View.VISIBLE);
        imFlash.setImageResource(R.mipmap.flash_sel);
        tvTitle.setText("扫一扫");
        initScan();
    }


    private CameraManager cameraManager;
    public CameraManager getCameraManager() {
        return cameraManager;
    }
    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplicationContext());
        initOnResume1();
    }

    private void initOnResume1() {
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        initOnPause2();
    }

    private void initOnPause2() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        try {
            CameraManager.get().closeDriver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            inactivityTimer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理扫描结果
     * @param result
     * @param barcode
     */
    String qrCode =null;
    String resultScan =null;
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        resultScan = result.getText();
        if (TextUtils.isEmpty(resultScan)) {
            Toast.makeText(this,"扫码失败",Toast.LENGTH_SHORT).show();
        } else {
            viewfinderView.isRefresh = false;
            playBeepSoundAndVibrate();
            qrCode = resultScan;
            //处理扫描结果
            Intent intent = new Intent();
            Bundle args = new Bundle();
            args.putString(SCAN_RESULT,qrCode);
            args.putString(RESULT_TYPE,result.getBarcodeFormat().toString());
            intent.putExtras(args);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }

    /**
     * 初始化声音资源
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.qrcode_completed);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);//设置音量
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }
    public Handler getHandler() {
        return handler;
    }
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    private void reScan() {
        viewfinderView.isRefresh = true;
        try {
            handler.initrequestAutoFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            handler.restartPreviewAndDecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭当前界面
     * @param view
     */
    public void close(View view) {
        finish();
    }
    private boolean isOpen = false;  // 手电筒是否开启状态
    public void rightClick(View view) {
        isOpen = !isOpen;
        if (camera==null) {
            Toast.makeText(this,"摄像头未初始化，请稍后",Toast.LENGTH_SHORT).show();
            return;
        }
        if (isOpen) {
            try {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// 开启
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
            statusFlag = false;
            //看得见
        } else {
            try {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);// 关闭
                camera.setParameters(parameters);
                statusFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initScan() {
        viewfinderView.isRefresh = true;
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

    }
    private Camera camera = null;
    private Camera.Parameters parameters = null;
    public static boolean statusFlag = true;
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            camera = CameraManager.get().getC();
            parameters = camera.getParameters();
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new ExScanActivityHandler(ScanApiActivity.this, decodeFormats, characterSet);
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private static final long VIBRATE_DURATION = 200L;
    /**
     * 响铃和震动
     */
    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
}
