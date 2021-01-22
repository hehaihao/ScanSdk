package com.xm6leefun.scansdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xm6leefun.scan_lib.ScanApiActivity;
import com.xm6leefun.scan_lib.utils.CircularAnimUtil;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);
    }

    public void open(View view) {
        Intent intent = new Intent(this, ScanApiActivity.class);
        CircularAnimUtil.startActivity(this, intent, textView,R.color.tran, 100);
    }
}