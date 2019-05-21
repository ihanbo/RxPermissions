package com.tbruyelle.rxpermissions2.sample;

import android.Manifest.permission;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.sample.callback.RxRequestCallBack;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;

/**
 * 闪屏页-不需要权限也能进。
 */
public class SplashActivity extends AppCompatActivity implements View.OnClickListener {


    public static void open(Context context) {
        Intent starter = new Intent(context, SplashActivity.class);
        context.startActivity(starter);
    }

    private SplashActivity mSelf;
    private Disposable disposable;
    private Button mB1;
    private Button mB2;
    private TextView mDesc;
    RxPermissions rxPermissions = new RxPermissions(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelf = this;
        rxPermissions.setLogging(true);

        setContentView(R.layout.act_main3);
        mB1 = (Button) findViewById(R.id.b1);
        mB1.setOnClickListener(this);
        mB2 = (Button) findViewById(R.id.b2);
        mB2.setOnClickListener(this);
        mDesc = (TextView) findViewById(R.id.desc);

        mB1.setText("申请权限" + hashCode());

        requestPem();
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
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

    void goHome() {
        HomeActivity.open(mSelf);
    }

    private void requestPem() {
        rxPermissions.requestCombined(permission.READ_PHONE_STATE, permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new RxRequestCallBack() {
                    @Override
                    public void onFailed() {
                        Toast.makeText(mSelf, "无权限，用户全部点了不在询问，3秒后去主界面", Toast.LENGTH_SHORT).show();
                        setText("没有获取到权限");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goHome();
                            }
                        }, 3000);

                    }

                    @Override
                    public void showRationale(String name) {
                        new AlertDialog.Builder(mSelf).setCancelable(false)
                                .setTitle("权限说明")
                                .setMessage("这些权限。。。。。。。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPem();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        goHome();
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onSucc() {
                        setText("获取到权限了");
                        Toast.makeText(mSelf, "获取了全部权限,3秒后去主界面", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goHome();
                            }
                        }, 3000);
                    }
                });
    }


    StringBuilder sb = new StringBuilder();

    int line;

    void setText(String s) {
        sb.append(line++).append("  : ").append(s).append("\n");
        mDesc.setText(sb.toString());
    }

}