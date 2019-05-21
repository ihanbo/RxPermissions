package com.tbruyelle.rxpermissions2.sample.callback;

import com.tbruyelle.rxpermissions2.Permission;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 包装的请求的回调
 *
 * @Author hanbo
 * @Since 2019/5/20
 */
public abstract class RxRequestCallBack implements Observer<Permission> {
    private WeakReference<IDisponsableController> mReference;

    public RxRequestCallBack(IDisponsableController d) {
        mReference = new WeakReference<>(d);
    }

    public RxRequestCallBack() {
    }


    @Override
    public void onSubscribe(Disposable d) {
        if (mReference != null && mReference.get() != null) {
            mReference.get().addDisponsable(d);
        }
    }

    @Override
    public final void onNext(Permission permission) {
        if (permission.granted) {
            onSucc();
        } else if (permission.shouldShowRequestPermissionRationale) {
            showRationale(permission.name);
        } else {
            onFailed();
        }
    }

    /**
     * 用户拒绝，需要去设置里处理
     */
    public abstract void onFailed();

    /**
     * 用户拒绝，但是没有点“不在询问”
     *
     * @param name
     */
    public abstract void showRationale(String name);

    /**
     * 全部获得
     */
    public abstract void onSucc();

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }


}
