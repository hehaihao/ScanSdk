package com.xm6leefun.scan_lib;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.xm6leefun.scan_lib.codescaner.OnScannerCompletionListener;
import com.xm6leefun.scan_lib.codescaner.ScannerView;
import com.xm6leefun.scan_lib.zxing.Result;
import com.xm6leefun.scan_lib.zxing.client.result.CalendarParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.EmailAddressParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.GeoParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.ISBNParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.ParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.ParsedResultType;
import com.xm6leefun.scan_lib.zxing.client.result.ProductParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.SMSParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.TelParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.TextParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.URIParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.VINParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.WifiParsedResult;

/**
 * @Description:
 * @Author: hhh
 * @CreateDate: 2021/1/18 13:43
 */
public class ScanApiActivity extends Activity implements OnScannerCompletionListener {
    protected ScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_api);
        mScannerView = findViewById(R.id.scanner_view);
        mScannerView.setOnScannerCompletionListener(this);
    }

    @Override
    protected void onResume() {
        initScan();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mScannerView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mScannerView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onScannerCompletion(final Result rawResult, final ParsedResult parsedResult, Bitmap barcode) {
        mScannerView.playBeepSoundAndVibrate();
        if (rawResult == null) {
            pauseScan();
            Toast.makeText(this,"未发现二维码",Toast.LENGTH_LONG).show();
            return;
        }
        //延迟500ms开始发送消息
        new Handler().postDelayed(new Runnable(){
            public void run() {
                successGetScan(parsedResult, rawResult);
            }
        }, 100);
    }

    //成功获取到二维码
    private void successGetScan(ParsedResult parsedResult, Result rawResult) {
        ParsedResultType type = parsedResult.getType();
        String url = "";
        switch (type) {
            case URI:
                URIParsedResult uri = (URIParsedResult) parsedResult;
                url = uri.getURI();
                break;
            case TEXT:
                TextParsedResult textParsedResult = (TextParsedResult) parsedResult;
                url = textParsedResult.getText();
                break;
            case PRODUCT:
                ProductParsedResult productParsedResult = (ProductParsedResult) parsedResult;
                url = productParsedResult.getProductID();
                break;
            case ISBN:
                ISBNParsedResult isbnParsedResult = (ISBNParsedResult) parsedResult;
                url = isbnParsedResult.getISBN();
                break;
            case GEO:
                GeoParsedResult geoParsedResult = (GeoParsedResult) parsedResult;
                url = geoParsedResult.getGeoURI();
                break;
            case SMS:
                SMSParsedResult smsParsedResult = (SMSParsedResult) parsedResult;
                url = smsParsedResult.getSMSURI();
                break;
            case TEL:
                TelParsedResult telParsedResult = (TelParsedResult) parsedResult;
                url = telParsedResult.getTelURI();
                break;
            case VIN:
                VINParsedResult vinParsedResult = (VINParsedResult) parsedResult;
                url = vinParsedResult.getVIN();
                break;
            case WIFI:
                WifiParsedResult wifiParsedResult = (WifiParsedResult) parsedResult;
                url = wifiParsedResult.getPassword();
                break;
            case CALENDAR:
                CalendarParsedResult calendarParsedResult = (CalendarParsedResult) parsedResult;
                url = calendarParsedResult.getDescription();
                break;
            case EMAIL_ADDRESS:
                EmailAddressParsedResult emailAddressParsedResult = (EmailAddressParsedResult) parsedResult;
                url = emailAddressParsedResult.getBody();
                break;
            default:
                break;
        }
        //处理扫描结果
        Toast.makeText(this,url,Toast.LENGTH_LONG).show();
        finish();
    }


    private void initScan() {
        mScannerView.onInit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    protected void pauseScan() {
        mScannerView.onPause();
    }

    private void refreshScan() {
        mScannerView.onResume();
    }

    /**
     * 关闭当前界面
     * @param view
     */
    public void close(View view) {
        finish();
    }
}
