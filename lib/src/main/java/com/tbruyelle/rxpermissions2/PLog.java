package com.tbruyelle.rxpermissions2;

/**
 * 日志处理
 *
 * @Author hanbo
 * @Since 2019/5/21
 */
public class PLog {
    private static ILogPrinter mProxy;

    public static void setLogProxy(ILogPrinter proxy) {
        mProxy = proxy;
    }

    static long num;

    public static void i(String msg) {
        if (mProxy != null) {
            mProxy.i(msg);
            return;
        }
    }

    public static void e(String msg) {
        if (mProxy != null) {
            mProxy.e(msg);
            return;
        }
    }


    interface ILogPrinter {
        void i(String msg);

        void e(String msg);
    }


}
