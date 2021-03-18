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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.xm6leefun.scan_lib.net.ApiConfig;
import com.xm6leefun.scan_lib.utils.ChipConstant;
import com.xm6leefun.scan_lib.utils.CircularAnimUtil;
import com.xm6leefun.scan_lib.utils.DealUrlDataUtils;
import com.xm6leefun.scan_lib.utils.SharePreferenceUtil;

import java.io.IOException;

public class HwScanApiActivity extends Activity {

    protected FrameLayout frameLayout;
    protected RemoteView remoteView;
    private ImageView scan_area;
    private ImageView iv_line;
    int mScreenWidth;
    int mScreenHeight;
    //The width and height of scan_view_finder is both 210 dp.
    final int SCAN_FRAME_SIZE = 210;

    //Declare the key. It is used to obtain the value returned from Scan Kit.
    public static final String SCAN_RESULT = "scanResult";
    public static final String IS_BACK_CODE = "isBackCode";
    private static final int REQUEST_CODE_PHOTO = 0X1113;
    public static final int SCAN_CODE = 0X1114;
    private TextView tvTitle;
    private ImageView imFlash;

    public static void jump(Activity activity,View triggerView){
        Intent intent = new Intent(activity,HwScanApiActivity.class);
        CircularAnimUtil.startActivity(activity, intent, triggerView,R.color.tran, 100);
    }
    public static void jumpForResult(Activity activity,View triggerView,boolean isBackCode){
        Intent intent = new Intent(activity,HwScanApiActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(IS_BACK_CODE,isBackCode);
        intent.putExtras(args);
        CircularAnimUtil.startActivityForResult(activity, intent, SCAN_CODE,triggerView,R.color.tran, 100);
    }

    private boolean isBackCode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_scan_api);
        Bundle args = getIntent().getExtras();
        if(args != null)isBackCode = args.getBoolean(IS_BACK_CODE,false);
        // Bind the camera preview screen.
        scan_area = findViewById(R.id.scan_area);
        iv_line = findViewById(R.id.iv_line);
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

        animateLine();
    }

    private String qrCode =null;
    /**
     * 处理扫描结果
     * @param result
     */
    protected void handleReulst(HmsScan[] result){
        //Check the result.
        if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
            Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            if(isBackCode){//回码
                Intent intent = new Intent();
                intent.putExtra(SCAN_RESULT, result[0]);
                setResult(RESULT_OK, intent);
                HwScanApiActivity.this.finish();
            }else {
                String resultScan = result[0].getOriginalValue();
                qrCode = resultScan;
                String belong2UsStr = DealUrlDataUtils.isBelong2Us(qrCode);
                // todo  测试  tq给进  部分注释
                if (!qrCode.startsWith(ApiConfig.URL_HEAD_QRCODE)/* && !qrCode.startsWith("http://tqrco.weecot.com")*/) {  // 不是以公司域名开头
                    Toast.makeText(getApplicationContext(), getString(R.string.no_me_link), Toast.LENGTH_SHORT).show();
                    remoteView.onResume();
                    return;
                }
                String userId = SharePreferenceUtil.getString(ChipConstant.User_ID);
                qrCode = qrCode + "&type=2&userUuid=" + userId;
                String resultCode = ApiConfig.URL_HEAD_QRCODE + "/backCode/#/?" + belong2UsStr;
                String[] temp = qrCode.split(belong2UsStr);
                switch (belong2UsStr) {
                    case "w=":
                    case "n=":
                    case "p=":
                        resultCode = resultCode + temp[1] + "&token=" + SharePreferenceUtil.getString(ChipConstant.TOKEN);
                        //跳转回码H5
                        WebApiActivity.jump(this, resultCode);
                        break;
                }
            }
        }else{
            Toast.makeText(getApplicationContext(),getString(R.string.scan_fail),Toast.LENGTH_SHORT).show();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        animateLine();
    }

    private void animateLine() {
        // 模拟的mPreviewView的左右上下坐标坐标
        int left = scan_area.getLeft() / 2;
        int top = scan_area.getTop() + 30;
        int bottom = scan_area.getBottom() - 50;

        TranslateAnimation tAnim = new TranslateAnimation(0, 0, top, bottom);//设置视图上下移动的位置
        tAnim .setDuration(1800);
        tAnim .setRepeatCount(Animation.INFINITE);
        tAnim .setRepeatMode(Animation.REVERSE);
        iv_line.setAnimation(tAnim);
        tAnim.startNow();
    }
}