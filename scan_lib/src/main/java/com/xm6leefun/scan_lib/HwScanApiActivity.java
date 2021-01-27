package com.xm6leefun.scan_lib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.xm6leefun.scan_lib.utils.CircularAnimUtil;

import java.io.IOException;

public class HwScanApiActivity extends Activity {

    protected FrameLayout frameLayout;
    protected RemoteView remoteView;
    int mScreenWidth;
    int mScreenHeight;
    //The width and height of scan_view_finder is both 240 dp.
    final int SCAN_FRAME_SIZE = 240;

    //Declare the key. It is used to obtain the value returned from Scan Kit.
    public static final String SCAN_RESULT = "scanResult";
    private static final int REQUEST_CODE_PHOTO = 0X1113;
    public static final int SCAN_CODE = 0X1114;
    private TextView tvTitle;
    private ImageView imFlash;

    public static void jump(Activity activity,View triggerView){
        Intent intent = new Intent(activity,HwScanApiActivity.class);
        CircularAnimUtil.startActivity(activity, intent, triggerView,R.color.tran, 100);
    }
    public static void jumpForResult(Activity activity,View triggerView){
        Intent intent = new Intent(activity,HwScanApiActivity.class);
        CircularAnimUtil.startActivityForResult(activity, intent, SCAN_CODE,triggerView,R.color.tran, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_scan_api);
        // Bind the camera preview screen.
        frameLayout = findViewById(R.id.rim);
        tvTitle = findViewById(R.id.base_topBar_tv_title);
        tvTitle.setText(getString(R.string.scan_api_title));
        imFlash = findViewById(R.id.base_topBar_iv_right);
        imFlash.setVisibility(View.VISIBLE);
        imFlash.setImageResource(R.mipmap.flash_sel);
        //1. Obtain the screen density to calculate the viewfinder's rectangle.
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        //2. Obtain the screen size.
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);

        //3. Calculate the viewfinder's rectangle, which in the middle of the layout.
        //Set the scanning area. (Optional. Rect can be null. If no settings are specified, it will be located in the middle of the layout.)
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;


        //Initialize the RemoteView instance, and set callback for the scanning result.
        remoteView = new RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE).build();
        // Subscribe to the scanning result callback event.
        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] result) {
                //Check the result.
                handleReulst(result);
            }
        });
        // Load the customized view to the activity.
        remoteView.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(remoteView, params);
    }

    /**
     * 处理扫描结果
     * @param result
     */
    protected void handleReulst(HmsScan[] result){
        //Check the result.
        if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
            Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            Intent intent = new Intent();
            intent.putExtra(SCAN_RESULT, result[0]);
            setResult(RESULT_OK, intent);
            HwScanApiActivity.this.finish();
        }
    }

    /**
     * Call the lifecycle management method of the remoteView activity.
     */
    protected void setPictureScanOperation() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        HwScanApiActivity.this.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
    }

    /**
     * Call the lifecycle management method of the remoteView activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        remoteView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }

    /**
     * Handle the return results from the album.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(HwScanApiActivity.this, bitmap, new HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create());
                handleReulst(hmsScans);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭当前界面
     * @param view
     */
    public void close(View view) {
        finish();
    }

    // 手电筒是否开启状态
    public void rightClick(View view) {
        remoteView.switchLight();
    }
}