package com.tbruyelle.rxpermissions2.sample.callback;

import io.reactivex.disposables.Disposable;

/**
 * 控制rx请求断开
 *
 * @Author hanbo
 * @Since 2019/5/21
 */
public interface IDisponsableController {
    void addDisponsable(Disposable... disposables);

    void clearDisponsables();

}
