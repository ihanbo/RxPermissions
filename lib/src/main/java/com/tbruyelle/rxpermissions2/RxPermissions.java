/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tbruyelle.rxpermissions2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

public class RxPermissions {

    static final String TAG = RxPermissions.class.getSimpleName();
    static final Object TRIGGER = new Object();

    @VisibleForTesting
    Lazy<RxPermissionsFragment> mRxPermissionsFragment;

    public RxPermissions(@NonNull final FragmentActivity activity) {
        mRxPermissionsFragment = getLazySingleton(activity.getSupportFragmentManager());
    }

    public RxPermissions(@NonNull final Fragment fragment) {
        mRxPermissionsFragment = getLazySingleton(fragment.getChildFragmentManager());
    }

    @NonNull
    private Lazy<RxPermissionsFragment> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new Lazy<RxPermissionsFragment>() {

            private RxPermissionsFragment rxPermissionsFragment;

            @Override
            public synchronized RxPermissionsFragment get() {
                if (rxPermissionsFragment == null) {
                    rxPermissionsFragment = getRxPermissionsFragment(fragmentManager);
                }
                return rxPermissionsFragment;
            }

        };
    }

    /**
     * ac/fragment插入权限申请Fragment
     *
     * @param fragmentManager
     * @return
     */
    private RxPermissionsFragment getRxPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        RxPermissionsFragment rxPermissionsFragment = findRxPermissionsFragment(fragmentManager);
        boolean isNewInstance = rxPermissionsFragment == null;
        if (isNewInstance) {
            rxPermissionsFragment = new RxPermissionsFragment();
            fragmentManager
                    .beginTransaction()
                    .add(rxPermissionsFragment, TAG)
                    .commitNow();
        }
        return rxPermissionsFragment;
    }

    private RxPermissionsFragment findRxPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        return (RxPermissionsFragment) fragmentManager.findFragmentByTag(TAG);
    }

    public void setLogging(boolean logging) {
        mRxPermissionsFragment.get().setLogging(logging);
    }


    /**
     * 每个请求发射一次结果，很少用
     *
     * @param permissions
     * @param <T>
     * @return
     */
    @SuppressWarnings("WeakerAccess")
    public <T> ObservableTransformer<T, Permission> ensureEach(final String... permissions) {
        return new ObservableTransformer<T, Permission>() {
            @Override
            public ObservableSource<Permission> apply(Observable<T> o) {
                return request(o, permissions);
            }
        };
    }

    /**
     * 全量结果，保留全部细节，参考{@link RxPermissionFullResult}
     *
     * @param permissions
     * @param <T>
     * @return
     */
    public <T> ObservableTransformer<T, RxPermissionFullResult> ensureEachFullResult(final String... permissions) {
        return new ObservableTransformer<T, RxPermissionFullResult>() {
            @Override
            public ObservableSource<RxPermissionFullResult> apply(Observable<T> o) {
                return request(o, permissions)
                        .buffer(permissions.length)
                        .flatMap(new Function<List<Permission>, ObservableSource<RxPermissionFullResult>>() {
                            @Override
                            public ObservableSource<RxPermissionFullResult> apply(List<Permission> permissions) {
                                if (permissions.isEmpty()) {
                                    //据说是弹出权限弹框然后按home键会出现，本人测试未发现
                                    return Observable.empty();
                                }
                                return Observable.just(new RxPermissionFullResult(permissions));
                            }
                        });
            }
        };
    }


    /**
     * 主要方法：合并结果
     *
     * @param permissions
     * @param <T>
     * @return
     */
    public <T> ObservableTransformer<T, Permission> ensureEachCombined(final String... permissions) {
        return new ObservableTransformer<T, Permission>() {
            @Override
            public ObservableSource<Permission> apply(Observable<T> o) {
                return request(o, permissions)
                        .doAfterNext(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                PLog.i("Permission  result： " + permission.toString());
                            }
                        })
                        .buffer(permissions.length)
                        .flatMap(new Function<List<Permission>, ObservableSource<Permission>>() {
                            @Override
                            public ObservableSource<Permission> apply(List<Permission> permissions) {
                                if (permissions.isEmpty()) {
                                    //据说是弹出权限弹框然后按home键会出现，本人测试未发现
                                    return Observable.empty();
                                }
                                return Observable.just(new Permission(permissions));
                            }
                        });
            }
        };
    }

    /**
     * 多个权限挨个回调
     *
     * @param permissions
     * @return
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Permission> requestEach(final String... permissions) {
        return Observable.just(TRIGGER).compose(ensureEach(permissions));
    }


    /**
     * 返回一个全量结果
     *
     * @param permissions
     * @return
     */
    public Observable<RxPermissionFullResult> requestEachFulled(final String... permissions) {
        return Observable.just(TRIGGER).compose(ensureEachFullResult(permissions));
    }

    public Observable<Permission> requestCombined(final String... permissions) {
        return Observable.just(TRIGGER).compose(ensureEachCombined(permissions));
    }

    /**
     * 检查权限
     *
     * @param permissions
     * @return
     */
    public boolean checkPermissions(final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        for (String perm : permissions) {
            if (!isGranted(perm)) {
                return false;
            }
        }
        return true;
    }


    private Observable<Permission> request(final Observable<?> trigger, final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("RxPermissions.request/requestEach requires at least one input permission");
        }
        //return oneOf(trigger, pending(permissions))  迷惑，屏幕切换会导致数据混乱
        return trigger.flatMap(new Function<Object, Observable<Permission>>() {
            @Override
            public Observable<Permission> apply(Object o) {
                return requestImplementation(permissions);
            }
        });
    }




    /*private Observable<?> pending(final String... permissions) {
        for (String p : permissions) {
            //不包含的时候
            if (!mRxPermissionsFragment.get().containsByPermission(p)) {
                return Observable.empty();
            }
        }
        return Observable.just(TRIGGER);
    }

    private Observable<?> oneOf(Observable<?> trigger, Observable<?> pending) {
        if (trigger == null) {
            return Observable.just(TRIGGER);
        }

        return Observable.merge(trigger, pending);
    }*/

    @TargetApi(Build.VERSION_CODES.M)
    private Observable<Permission> requestImplementation(final String... permissions) {
        List<Observable<Permission>> list = new ArrayList<>(permissions.length);
        List<String> unrequestedPermissions = new ArrayList<>();

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (String permission : permissions) {
            mRxPermissionsFragment.get().log("Requesting permission " + permission);
            if (isGranted(permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                list.add(Observable.just(new Permission(permission, true, false)));
                continue;
            }

            if (isRevoked(permission)) {
                // Revoked by a policy, return a denied Permission object.
                list.add(Observable.just(new Permission(permission, false, false)));
                continue;
            }

            PublishSubject<Permission> subject = mRxPermissionsFragment.get().getSubjectByPermission(permission);
            // Create a new subject if not exists
            if (subject == null) {
                unrequestedPermissions.add(permission);
                subject = PublishSubject.create();
                mRxPermissionsFragment.get().setSubjectForPermission(permission, subject);
            }

            list.add(subject);
        }

        if (!unrequestedPermissions.isEmpty()) {
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);
            requestPermissionsFromFragment(unrequestedPermissionsArray);
        }
        return Observable.concat(Observable.fromIterable(list));
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     * <p>
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     * <p>
     * You shouldn't call this method if all permissions have been granted.
     * <p>
     * For SDK &lt; 23, the observable will always emit false.
     */
    @SuppressWarnings("WeakerAccess")
    Observable<Boolean> shouldShowRequestPermissionRationale(final Activity activity, final String... permissions) {
        if (!isMarshmallow()) {
            return Observable.just(false);
        }
        return Observable.just(shouldShowRequestPermissionRationaleImplementation(activity, permissions));
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean shouldShowRequestPermissionRationaleImplementation(final Activity activity, final String... permissions) {
        for (String p : permissions) {
            if (!isGranted(p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions) {
        mRxPermissionsFragment.get().log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions));
        mRxPermissionsFragment.get().requestPermissions(permissions);
    }

    /**
     * sdk小于23或者已授权返回true
     */
    @SuppressWarnings("WeakerAccess")
    boolean isGranted(String permission) {
        return !isMarshmallow() || mRxPermissionsFragment.get().isGranted(permission);
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    boolean isRevoked(String permission) {
        return isMarshmallow() && mRxPermissionsFragment.get().isRevoked(permission);
    }

    boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 测试用
     *
     * @param permissions
     * @param grantResults
     */
    @Deprecated
    void onRequestPermissionsResult(String permissions[], int[] grantResults) {
        mRxPermissionsFragment.get().onRequestPermissionsResult(permissions, grantResults, new boolean[permissions.length]);
    }


    @FunctionalInterface
    public interface Lazy<V> {
        V get();
    }

}
