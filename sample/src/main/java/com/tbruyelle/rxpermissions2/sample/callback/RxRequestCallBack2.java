package com.tbruyelle.rxpermissions2.sample.callback;

/**
 * @Author hanbo
 * @Since 2019/5/20
 */
public abstract class RxRequestCallBack2 extends RxRequestCallBack {

    @Override
    public final void showRationale(String name) {
        onFailed();
    }
}
