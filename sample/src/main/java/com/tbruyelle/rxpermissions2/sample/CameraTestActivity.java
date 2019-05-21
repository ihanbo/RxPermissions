package com.tbruyelle.rxpermissions2.sample;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tbruyelle.rxpermissions2.sample.callback.RxRequestCallBack;

import java.io.IOException;

import io.reactivex.disposables.Disposable;

public class CameraTestActivity extends AppCompatActivity {

    public static void open(Context context) {
        Intent starter = new Intent(context, CameraTestActivity.class);
        context.startActivity(starter);
    }

    private static final String TAG = "RxPermissionsSample";

    private Camera camera;
    private SurfaceView surfaceView;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);

        setContentView(R.layout.act_main);
        surfaceView = findViewById(R.id.surfaceView);

        RxView.clicks(findViewById(R.id.enableCamera))
                // Ask for permissions when button is clicked
                .compose(rxPermissions.ensureEachCombined(permission.CAMERA))
                .subscribe(new RxRequestCallBack() {
                    @Override
                    public void onFailed() {
                        Toast.makeText(CameraTestActivity.this,
                                "需要去设置",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void showRationale(String name) {
                        Toast.makeText(CameraTestActivity.this,
                                "需要弹窗",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSucc() {
                        releaseCamera();
                        camera = Camera.open(0);
                        try {
                            camera.setPreviewDisplay(surfaceView.getHolder());
                            camera.startPreview();
                        } catch (IOException e) {
                            Log.e(TAG, "Error while trying to display the camera preview", e);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}