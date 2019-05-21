package com.tbruyelle.rxpermissions2.sample;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissionFullResult;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tbruyelle.rxpermissions2.sample.callback.RxRequestCallBack;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @Author hanbo
 * @Since 2019/5/21
 */
public class API {


    //示例代码
    public static void api(AppCompatActivity mself) {

        //一般用法
        RxPermissions rp = new RxPermissions(mself);
        rp.requestCombined(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {

                    }
                });

        //简易的回调
        rp.requestCombined(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new RxRequestCallBack() {
                    @Override
                    public void onFailed() {
                        //用户全部不再询问
                    }

                    @Override
                    public void showRationale(String name) {
                        // 需要展示弹窗的，就是用户拒绝了，但是没点“不再询问”，
                        // 请求的多个权限里只要有一个可以展示就会回调这个。
                        // 这里按需要处理，不一定非要弹权限说明窗
                    }

                    @Override
                    public void onSucc() {
                        //全部获取权限
                    }
                });

        //全量结果
        rp.requestEachFulled(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<RxPermissionFullResult>() {
                    @Override
                    public void accept(RxPermissionFullResult rxPermissionFullResult) throws Exception {

                    }
                });

    }
}
