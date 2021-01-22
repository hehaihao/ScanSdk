package com.xm6leefun.scan_lib.codescaner;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.zxing.Result;

/**
 * Created by hupei on 2016/7/1.
 */
public class ScannerView extends RelativeLayout {

    private static final String TAG = ScannerView.class.getSimpleName();
    //保存上一次点击时候的时间
    private long preTime;

    private CameraSurfaceView mSurfaceView;
    private ViewfinderView mViewfinderView;

    private BeepManager mBeepManager;
    private OnScannerCompletionListener mScannerCompletionListener;

    private int mediaResId = R.raw.qrcode_completed;//扫描成功音频资源文件

    private int cameraZoomRatio = 1;//相机变焦比率
    //是否连续点击的标志位
    private boolean isContinuousClick;

    public ScannerView(Context context) {
        this(context, null);
    }

    public ScannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mSurfaceView = new CameraSurfaceView(context, this);
        mViewfinderView = new ViewfinderView(context, attrs);
        mSurfaceView.setId(android.R.id.list);
        final LayoutParams layoutParams = new LayoutParams(context, attrs);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, mSurfaceView.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mSurfaceView.getId());
        //延迟150毫秒开始添加绘制view和surfaaceview，已加快Activity启动速度
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ScannerView.this.addView(mSurfaceView);
                ScannerView.this.addView(mViewfinderView, layoutParams);
            }
        }, 150);

        mSurfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - preTime < 1000) {
                    //连续点击的时候只触发一次
                    if (!isContinuousClick) {
                        //刷新相机
                        mSurfaceView.changeCameraZoomRatio(cameraZoomRatio = cameraZoomRatio == 1 ? 2 : 1);
                    }
                    isContinuousClick = true;
                } else {
                    isContinuousClick = false;
                }
                preTime = System.currentTimeMillis();
            }
        });
    }

    public void onInit() {
        mSurfaceView.onInit(cameraZoomRatio);
        mViewfinderView.setCameraManager(mSurfaceView.getCameraManager());
        mViewfinderView.onResume();
        if (mBeepManager != null) mBeepManager.updatePrefs();
    }

    public void onResume() {
        mSurfaceView.onResume();
        mViewfinderView.onResume();
        if (mBeepManager != null) mBeepManager.updatePrefs();
    }

    public void onPause() {
        mSurfaceView.onPause();
        if (mBeepManager != null) mBeepManager.close();
        mViewfinderView.laserLineBitmapRecycle();
        mViewfinderView.onPause();
    }

    public void onDestroy() {
        mSurfaceView.onDestroy();
        if (mBeepManager != null) mBeepManager.close();
        mViewfinderView.laserLineBitmapRecycle();
    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        //扫描成功
        if (mScannerCompletionListener != null) {
            //转换结果
            mScannerCompletionListener.onScannerCompletion(rawResult, ScannerUtils.parseResult(rawResult), barcode);
        }
        if (mBeepManager == null) {
            mBeepManager = new BeepManager(getContext());
            mBeepManager.setMediaResId(mediaResId);
        }
        mBeepManager.playBeepSoundAndVibrate();
    }

    /**
     * 设置提示文案
     * @param textStr
     */
    public void setText(String textStr){
        mViewfinderView.setTextStr(textStr);
    }

    /**
     * 设置扫描成功监听器
     * @param listener
     * @return
     */
    public ScannerView setOnScannerCompletionListener(OnScannerCompletionListener listener) {
        this.mScannerCompletionListener = listener;
        return this;
    }


    /**
     * 切换闪光灯
     * @param mode true开；false关
     */
    public ScannerView toggleLight(boolean mode) {
        mSurfaceView.setTorch(mode);
        return this;
    }

    /**
     * 在经过一段延迟后重置相机以进行下一次扫描。 成功扫描过后可调用此方法立刻准备进行下次扫描
     * @param delayMS 毫秒
     */
    public void restartPreviewAfterDelay(long delayMS) {
        mSurfaceView.restartPreviewAfterDelay(delayMS);
    }

    /**
     * 播放提示音
     */
    public void playBeepSoundAndVibrate(){
        if (mBeepManager == null) {
            mBeepManager = new BeepManager(getContext());
            mBeepManager.setMediaResId(mediaResId);
        }
        mBeepManager.playBeepSoundAndVibrate();
    }

}
