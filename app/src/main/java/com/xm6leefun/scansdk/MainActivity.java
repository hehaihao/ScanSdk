package com.xm6leefun.scansdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xm6leefun.scan_lib.HwScanApiActivity;
import com.xm6leefun.scan_lib.WebApiActivity;
import com.xm6leefun.scan_lib.listener.AuthListener;
import com.xm6leefun.scan_lib.login.ZWDLoginActivity;
import com.xm6leefun.scan_lib.nfc.NfcActivity;

public class MainActivity extends AppCompatActivity implements AuthListener {

    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = findViewById(R.id.textview);
    }

    public void open(View view) {
//        WebApiActivity.jump(this,"file:///android_asset/index.html");
//        HwScanApiActivity.jump(this,view);
        NfcActivity.jump(this);
//        ZWDLoginActivity.jumpForData(this,this);
    }

    @Override
    public void onData(String resultJson) {
        textview.setText(resultJson);
    }
}