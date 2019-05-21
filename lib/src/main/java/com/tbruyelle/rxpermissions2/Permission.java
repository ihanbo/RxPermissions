package com.tbruyelle.rxpermissions2;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class Permission {
    public final String name;
    public final boolean granted;
    public final boolean shouldShowRequestPermissionRationale;

    public Permission(String name, boolean granted) {
        this(name, granted, false);
    }

    public Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationale) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
    }

    /**
     * 合并处理结果：
     * 1.granted为true表示全部获得
     * 2.shouldShowRequestPermissionRationale：有任意一个权限为true则为true，表示可以展示弹窗说明
     * 3.name：为所申请的所有权限的name拼接，逗号隔开。
     *
     * @param permissions
     */
    public Permission(List<Permission> permissions) {
        name = combineName(permissions);
        PLog.i("Permission 合并的名称: " + name);
        granted = combineGranted(permissions);
        shouldShowRequestPermissionRationale = combineShouldShowRequestPermissionRationale(permissions);
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Permission that = (Permission) o;

        if (granted != that.granted) return false;
        if (shouldShowRequestPermissionRationale != that.shouldShowRequestPermissionRationale)
            return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (granted ? 1 : 0);
        result = 31 * result + (shouldShowRequestPermissionRationale ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationale +
                '}';
    }

    private String combineName(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .map(new Function<Permission, String>() {
                    @Override
                    public String apply(Permission permission) throws Exception {
                        return permission.name;
                    }
                }).collectInto(new StringBuilder(), new BiConsumer<StringBuilder, String>() {
                    @Override
                    public void accept(StringBuilder s, String s2) throws Exception {
                        if (s.length() == 0) {
                            s.append(s2);
                        } else {
                            s.append(", ").append(s2);
                        }
                    }
                }).blockingGet().toString();
    }

    private Boolean combineGranted(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .all(new Predicate<Permission>() {
                    @Override
                    public boolean test(Permission permission) throws Exception {
                        return permission.granted;
                    }
                }).blockingGet();
    }

    private Boolean combineShouldShowRequestPermissionRationale(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .any(new Predicate<Permission>() {
                    @Override
                    public boolean test(Permission permission) throws Exception {
                        return permission.shouldShowRequestPermissionRationale;
                    }
                }).blockingGet();
    }

}
