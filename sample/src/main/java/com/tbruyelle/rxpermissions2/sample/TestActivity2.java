package com.tbruyelle.rxpermissions2.sample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
 * 测试权限 多次调用情况
 *
 * @Author hanbo
 * @Since 2019/5/21
 */
public class TestActivity2 extends AppCompatActivity implements View.OnClickListener, IDisponsableController {


    public static void open(Context context) {
        Intent starter = new Intent(context, TestActivity2.class);
        context.startActivity(starter);
    }

    private TestActivity2 mSelf;
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

        setContentView(R.layout.act_main_test);
        mB1 = (Button) findViewById(R.id.b1);
        mB1.setOnClickListener(this);
        mB2 = (Button) findViewById(R.id.b2);
        mB2.setOnClickListener(this);
        mDesc = (TextView) findViewById(R.id.desc);

        mB1.setText("申请权限" + hashCode());
        mB2.setText("读取联系人");


    }

    private void requestPem2() {
        requestPems().subscribe(new RxRequestCallBack(this) {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                PLog.i("222222 : onSubscribe: ");
            }

            @Override
            public void onFailed() {
                PLog.i("222222 : onFailed: ");
            }

            @Override
            public void showRationale(String name) {
                PLog.i("222222 : showRationale: ");
            }

            @Override
            public void onSucc() {
                PLog.i("2222222 : onSucc: 获取所有权限");
            }
        });
    }

    private void requestPem3() {
        requestPems().subscribe(new RxRequestCallBack(this) {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                PLog.i("333333  : onSubscribe: ");
            }

            @Override
            public void onFailed() {
                PLog.i("3333333: onFailed: ");
            }

            @Override
            public void showRationale(String name) {
                PLog.i("3333333  : showRationale: ");
            }

            @Override
            public void onSucc() {
                PLog.i("333333  : onSucc: 获取所有权限");
            }
        });
    }

    private Observable<Permission> requestPems() {
        return rxPermissions.requestCombined(Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                requestPem2();
                requestPem2();
//                requestPem3();
                break;
            case R.id.b2:

                String[] contacts = getContacts();
                int length = contacts.length;
                break;
        }
    }

    private CompositeDisposable mDisposables = new CompositeDisposable();

    public void addDisponsable(Disposable... disposables) {
        mDisposables.addAll(disposables);
    }

    public void clearDisponsables() {
        mDisposables.clear();
    }


    private String[] getContacts() {
        //联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        String[] arr = new String[cursor.getCount()];
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(0);
                //获取姓名
                String name = cursor.getString(1);
                //指定获取NUMBER这一列数据
                String[] phoneProjection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                arr[i] = id + " , 姓名：" + name;

                //根据联系人的ID获取此人的电话号码
                Cursor phonesCusor = this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                        null,
                        null);

                //因为每个联系人可能有多个电话号码，所以需要遍历
                if (phonesCusor != null && phonesCusor.moveToFirst()) {
                    do {
                        String num = phonesCusor.getString(0);
                        arr[i] += " , 电话号码：" + num;
                    } while (phonesCusor.moveToNext());
                }
                i++;
            } while (cursor.moveToNext());
        }
        return arr;
    }
}
