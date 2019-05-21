package com.tbruyelle.rxpermissions2;

import java.util.ArrayList;
import java.util.List;


/**
 * 全量结果
 *
 * @Author hanbo
 * @Since 2019/5/20
 */
public class RxPermissionFullResult {


    private boolean mIsAllGranted = true;           //是否全部获得

    private List<Permission> mGranted;              //获取到权限的集合
    private List<Permission> mUnGranted;            //未获取到权限的集合
    private List<Permission> mShouldShowRationale;  //需要展示说明的集合
    private List<Permission> mNeedGoSetting;        //彻底拒绝的，需要去设置里手动处理的集合，这里有多种情况，比如用户勾选不再提示，比如你未在manifest添加就直接申请了。


    RxPermissionFullResult(List<Permission> permissions) {
        for (Permission p : permissions) {
            if (!p.granted) {
                mIsAllGranted = false;
                if (mUnGranted == null) {
                    mUnGranted = new ArrayList<>(permissions.size());
                }
                mUnGranted.add(p);

                if (p.shouldShowRequestPermissionRationale) {
                    if (mShouldShowRationale == null) {
                        mShouldShowRationale = new ArrayList<>(permissions.size());
                    }
                    mShouldShowRationale.add(p);
                } else {
                    if (mNeedGoSetting == null) {
                        mNeedGoSetting = new ArrayList<>(permissions.size());
                    }
                    mNeedGoSetting.add(p);
                }
            } else {
                if (mGranted == null) {
                    mGranted = new ArrayList<>(permissions.size());
                }
                mGranted.add(p);
            }
        }
    }

    /**
     * 是否全部获取成功
     *
     * @return
     */
    public boolean isAllGranted() {
        return mIsAllGranted;
    }

    /**
     * 失败的列表，全部获得权限的话为空
     *
     * @return
     */
    public List<Permission> getUnGranted() {
        return mUnGranted;
    }

    /**
     * 成功的列表，可能为空
     *
     * @return
     */
    public List<Permission> getGranted() {
        return mGranted;
    }


    /**
     * 用户拒绝，但是可以展示提示框的列表
     *
     * @return
     */
    public List<Permission> getShouldShowRationale() {
        return mShouldShowRationale;
    }


    /**
     * 用户不再询问的列表,可能为空
     *
     * @return
     */
    public List<Permission> getNeedGoSetting() {
        return mNeedGoSetting;
    }

}
