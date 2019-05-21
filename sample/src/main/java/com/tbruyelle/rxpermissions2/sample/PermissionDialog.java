package com.tbruyelle.rxpermissions2.sample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;


/**
 * 权限弹窗通用处理
 *
 * @Author hanbo
 * @Since 2019/5/20
 */
public class PermissionDialog {
    private static final int REQ_2_SETTING = 521;

    public static void showRationaleDialog(Activity activity, String name, final Runnable requestRun, final Runnable finish) {
        new AlertDialog.Builder(activity).setCancelable(false)
                .setTitle("权限说明")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("这是权限说明文档，这些事必要的请同意")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        requestRun.run();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish.run();
                    }
                })
                .show();
    }

    public static void showSettingDialog(final Activity self) {
        new AlertDialog.Builder(self).setCancelable(false)
                .setTitle("去设置")
                .setMessage("用户点击了不在询问，请去设置里手动开启")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", self.getPackageName(), null);
                        intent.setData(uri);
                        self.startActivityForResult(intent, REQ_2_SETTING);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //结束界面
                        self.finish();
                    }
                })
                .show();
    }
}
