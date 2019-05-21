package com.tbruyelle.rxpermissions2.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RxPermissionsSample";
    private Button mB1;
    private Button mB2;
    private TextView mDesc;
    private Button mB3;
    private Button mB4;
    private Button mB5;
    private Button mB6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_main1);
        findViews();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.b1:
                //流程1
                SplashActivity.open(this);
                break;
            case R.id.b2:
                //流程2
                UserPicActivity.open(this);
                break;
            case R.id.b3:
                //测试横竖屏切换
                TestActivity1.open(this);
                break;
            case R.id.b4:
                //测试多次调用
                TestActivity2.open(this);
                break;
            case R.id.b5:
                CameraTestActivity.open(this);
                break;
            case R.id.b6:
                break;
        }
    }


    private void findViews() {
        mB1 = (Button) findViewById(R.id.b1);
        mB1.setOnClickListener(this);
        mB2 = (Button) findViewById(R.id.b2);
        mB2.setOnClickListener(this);
        mB3 = (Button) findViewById(R.id.b3);
        mB3.setOnClickListener(this);
        mB4 = (Button) findViewById(R.id.b4);
        mB4.setOnClickListener(this);
        mB5 = (Button) findViewById(R.id.b5);
        mB5.setOnClickListener(this);
        mB6 = (Button) findViewById(R.id.b6);
        mB6.setOnClickListener(this);
    }
}