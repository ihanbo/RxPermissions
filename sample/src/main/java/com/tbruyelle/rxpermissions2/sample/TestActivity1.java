package com.tbruyelle.rxpermissions2.sample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.sample.callback.IDisponsableController;
import com.tbruyelle.rxpermissions2.PLog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tbruyelle.rxpermissions2.sample.callback.RxRequestCallBack;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 测试旋转屏幕 多次调用及 销毁问题
 *
 * @Author hanbo
 * @Since 2019/5/21
 */
public class TestActivity1 extends AppCompatActivity implements View.OnClickListener, IDisponsableController {


    public static void open(Context context) {
        Intent starter = new Intent(context, TestActivity1.class);
        context.startActivity(starter);
    }

    private TestActivity1 mSelf;
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


        requestPem2();
    }

    private void requestPem2() {
        requestPems().subscribe(new RxRequestCallBack(this) {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                PLog.i("TestActivity1-" + mSelf.hashCode() + "  : onSubscribe: ");
            }

            @Override
            public void onFailed() {
                PLog.i("TestActivity1-" + mSelf.hashCode() + "  : onFailed: ");
            }

            @Override
            public void showRationale(String name) {
                PLog.i("TestActivity1-" + mSelf.hashCode() + "  : showRationale: ");
            }

            @Override
            public void onSucc() {
                PLog.i("TestActivity1-" + mSelf.hashCode() + "  : onSucc: 获取所有权限");
            }
        });
    }

    private Observable<Permission> requestPems() {
        return rxPermissions.requestCombined(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onDestroy() {
        clearDisponsables();
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

    private CompositeDisposable mDisposables = new CompositeDisposable();

    public void addDisponsable(Disposable... disposables) {
        mDisposables.addAll(disposables);
    }

    public void clearDisponsables() {
        PLog.i("TestActivity1" + hashCode() + "销毁了");
        mDisposables.clear();
    }
}
