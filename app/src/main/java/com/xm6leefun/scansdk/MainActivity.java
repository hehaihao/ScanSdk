package com.xm6leefun.scansdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.xm6leefun.scan_lib.WebApiActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void open(View view) {
        WebApiActivity.jump(this,"file:///android_asset/index.html");
    }
}