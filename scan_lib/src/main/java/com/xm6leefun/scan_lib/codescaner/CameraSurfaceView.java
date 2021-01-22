package com.xm6leefun.scan_lib.codescaner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xm6leefun.scan_lib.codescaner.camera.CameraManager;
import com.xm6leefun.scan_lib.zxing.Result;

import java.io.IOException;

/**
 * Created by hupei on 2017/12/13.
 */

class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraSurfaceView.class.getSimpleName();

    private ScannerView mScannerView;
    private boolean hasSurface;
    private CameraManager mCameraManager;
    private ScannerViewHandler mScannerViewHandler;

    private SurfaceHolder surfaceHolder;

    private boolean lightMode = false;//闪光灯，默认关闭



     CameraSurfaceView(Context context, ScannerView scannerView) {
        super(context);
        this.mScannerView = scannerView;
        hasSurface = false;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    void onInit(int cameraZoomRatio) {
        this.mCameraManager = new CameraManager(getContext(), cameraZoomRatio);
        this.mScannerViewHandler = null;


        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        }
    }

    void onResume() {
        try {
            mCameraManager.setTorch(lightMode);
            if (mScannerViewHandler == null) {
                mScannerViewHandler = new ScannerViewHandler(mCameraManager, new ScannerViewHandler.HandleDecodeListener() {
                    @Override
                    public void decodeSucceeded(Result rawResult, Bitmap barcode, float scaleFactor) {
                        mScannerView.handleDecode(rawResult, barcode, scaleFactor);
                    }
                });
            }
        } catch (RuntimeException e) {
            // Barcode ScannerUtils has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
        }
    }

    /**
     * 初始化相机
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder,getContext());
            //requestLayout();
            mCameraManager.setTorch(lightMode);
            // Creating the mScannerViewHandler starts the preview, which can also throw a
            // RuntimeException.
            if (mScannerViewHandler == null ) {
                mScannerViewHandler = new ScannerViewHandler(mCameraManager, new ScannerViewHandler.HandleDecodeListener() {
                    @Override
                    public void decodeSucceeded(Result rawResult, Bitmap barcode, float scaleFactor) {
                        mScannerView.handleDecode(rawResult, barcode, scaleFactor);
                    }
                });
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
        } catch (RuntimeException e) {
            // Barcode ScannerUtils has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
        }
    }

    /**
     * 暂停
     */
    public void onDestroy() {
        if (mScannerViewHandler != null) {
            mScannerViewHandler.quitSynchronously();
            mScannerViewHandler = null;
        }
        mCameraManager.closeDriver();
    }

    public void onPause(){
        if (mScannerViewHandler != null) {
            mScannerViewHandler.pauseSynchronously();
            mScannerViewHandler = null;
        }
    }

    void setTorch(boolean mode) {
        this.lightMode = mode;
        if (mCameraManager != null) mCameraManager.setTorch(lightMode);
    }

    void restartPreviewAfterDelay(long delayMS) {
        if (mScannerViewHandler != null)
            mScannerViewHandler.sendEmptyMessageDelayed(ScannerUtils.RESTART_PREVIEW, delayMS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        hasSurface = false;
    }


    CameraManager getCameraManager() {
        return mCameraManager;
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        boolean portrait = true;
        if (mCameraManager != null) {
            if (mCameraManager.getCameraResolution() != null) {
                Point cameraResolution = mCameraManager.getCameraResolution();
                int cameraPreviewWidth = cameraResolution.y;
                int cameraPreviewHeight = cameraResolution.x;
                if (width * 1f / height < cameraPreviewWidth * 1f / cameraPreviewHeight) {
                    float ratio = cameraPreviewHeight * 1f / cameraPreviewWidth;
                    width = (int) (height / ratio + 0.5f);
                } else {
                    float ratio = cameraPreviewWidth * 1f / cameraPreviewHeight;
                    height = (int) (width / ratio + 0.5f);
                }
            }
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    /**
     * 修改了焦距之后要刷新相机
     */
    public void changeCameraZoomRatio(int cameraZoomRatio){
        this.mCameraManager = new CameraManager(getContext(), cameraZoomRatio);
        this.mScannerViewHandler = null;
        initCamera(surfaceHolder);
    }
}
