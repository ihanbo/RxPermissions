package com.tbruyelle.rxpermissions2.sample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;

public class HomeActivity extends AppCompatActivity {

    private TextView mTv;

    public static void open(Context context) {
        Intent starter = new Intent(context, HomeActivity.class);
        context.startActivity(starter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        mTv = (TextView) findViewById(R.id.tv);
        RxPermissions rxPermissions = new RxPermissions(this);
        boolean has = rxPermissions.checkPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mTv.setText(has ? "已有权限" : "没有权限");
    }

}
