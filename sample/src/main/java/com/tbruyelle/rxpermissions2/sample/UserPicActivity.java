package com.tbruyelle.rxpermissions2.sample;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.sample.callback.RxRequestCallBack;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;

/**
 * 拍照页-必须要权限
 */
public class UserPicActivity extends AppCompatActivity implements View.OnClickListener {


    public static void open(Context context) {
        Intent starter = new Intent(context, UserPicActivity.class);
        context.startActivity(starter);
    }

    private UserPicActivity mSelf;
    private Disposable disposable;
    private Button mB1;
    private Button mB2;
    private TextView mDesc;
    RxPermissions rxPermissions = new RxPermissions(this);


    private static final int REQ_2_SETTING = 521;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelf = this;
        rxPermissions.setLogging(true);

        setContentView(R.layout.act_main2);
        mB1 = (Button) findViewById(R.id.b1);
        mB1.setOnClickListener(this);
        mB2 = (Button) findViewById(R.id.b2);
        mB2.setOnClickListener(this);
        mDesc = (TextView) findViewById(R.id.desc);

        mB1.setText("申请权限" + hashCode());

        requestPems();
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
//            disposable.dispose();
        }
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.b1:

                break;
            case R.id.b2:
                break;
        }
    }

    private void requestPems() {
        rxPermissions.requestCombined(permission.READ_PHONE_STATE, permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new RxRequestCallBack() {
                    @Override
                    public void onFailed() {
                        PermissionDialog.showSettingDialog(mSelf);
                    }

                    @Override
                    public void showRationale(String name) {
                        PermissionDialog.showRationaleDialog(mSelf, name, mRequestRun, mFinish);
                    }

                    @Override
                    public void onSucc() {
                        setText("获取了全部权限可以拍照了");
                        Toast.makeText(mSelf, "获取了全部权限可以拍照了", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private Runnable mRequestRun = new Runnable() {
        @Override
        public void run() {
            requestPems();
        }
    };

    private Runnable mFinish = new Runnable() {

        @Override
        public void run() {
            finish();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_2_SETTING) {
            requestPems();
        }
    }

    StringBuilder sb = new StringBuilder();

    void setText(String s) {
        sb.append(s).append("\n");
        mDesc.setText(sb.toString());
    }

}