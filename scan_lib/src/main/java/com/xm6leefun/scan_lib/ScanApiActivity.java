package com.xm6leefun.scan_lib;

import android.app.Activity;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/1/18 13:43
 */
public class ScanApiActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_api);
        BarcodeFormat barcodeFormat = null;
    }
}
