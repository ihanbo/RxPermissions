
基于[RxPermissions](https://github.com/tbruyelle/RxPermissions)做了部分改动。
测试了屏幕旋转、多次调用等边缘情况。测试了部分国产手机。


## 动态权限相关的API：
1. ActivityCompat.checkSelfPermission(权限名)
检查是否有权限
2. ActivityCompat.shouldShowRequestPermissionRationale(权限名) 
返回true表示用户未选择 不要再询问，反之则已选择不要再询问。只能去设置里收东开启，**注意假如manifest未配置权限而去申请则为false，且不会弹权限申请框。**
3. ActivityCompat.requestPermissions
请求授权
4. onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
回调获取授权结果，判断是否授权。



## RxPermissions：

### 1.原理说明：
RxPermissions会在请求的Activity或者Fragment里添加一个无视图的Fragment-RxPermissionsFragment来申请权限、接收结果。

### 2.基础上做的改动：
#### 改动1：
去掉了oneof、pending方法的处理，这俩方法貌似没用，而且在申请权限屏幕切换过程中会导致数据混乱，会导致两个trigger，然后发送数据两次。
![源代码](https://upload-images.jianshu.io/upload_images/2288693-121963a7d6853e5b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
改动后如下：
![改动后](https://upload-images.jianshu.io/upload_images/2288693-fc41ab467c7c866b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 改动2：
增加了全量结果处理：
![全量结果](https://upload-images.jianshu.io/upload_images/2288693-4b03d94303fab5f6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
一些特殊需求需要完整结果的，或则调试用
#### 改动3：收紧了api,隐藏了一些无用的。

### 3.如何使用：
Activity或者Fragment里：
```java
RxPermissions rxPermissions = new RxPermissions(this);//参数为ac/fragment

rxPermissions.requestCombined(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
 .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        //permission说明：
                    }
                });
```
> 结果说明,Permission本身只是描述一个权限结果，包含三个字段：名字、是否已授权、是否可以展示说明框。但是此处最后拿到的结果permission其实是个复合的结果，将用户申请的多个权限结果合到一个Permission里去了。名字：多个名字用逗号隔开，是否已授权：多个权限全部授权才为true，否则为false；是否可以展示说明框：多个里有一个权限可以展示就为true，隐含意思为假如这个值为false，说明申请的多个权限用户都选了不在询问。

可以对结果进行进一步处理，返回如下结果：
```java
rxPermissions.requestCombined(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
 .subscribe(new RxRequestCallBack(this) {
  
    @Override
    public void onFailed() {
        //用户全部不再询问
    }

    @Override
    public void showRationale(String name) {
        // 需要展示弹窗的，就是用户拒绝了，但是没点“不再询问”，
        // 请求的多个权限里只要有一个可以展示就会回调这个。
        // 这里按需要处理，不一定非要弹权限说明窗
    }

    @Override
    public void onSucc() {
        //全部获取权限
    }
});

```

**请求权限的可能的几种结果：**
![请求权限的结果](https://upload-images.jianshu.io/upload_images/2288693-76e2fcc62f3fafff.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 典型流程：
### 1.闪屏界面申请多个权限，不管用户怎么选择，都要进入主界面（参考竞品）。
![闪屏界面流程](https://upload-images.jianshu.io/upload_images/2288693-b16ca9801c098462.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 2.用户修改照片界面，这里必须开启权限，没权限结束界面，获取成功则初始化。
![修改照片流程](https://upload-images.jianshu.io/upload_images/2288693-d6074d0833807c5e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 3.其他请根据业务需求处理。

## 备注：
1.关于WRITE_EXTERNAL_STORAGE，**使用getExternalCacheDir()获取外置缓存添加读写写文件不需要这个权限**，但通过Environment.getExternalStorageDirectory()获取存储卡目录然后操作文件就需要权限。
getExternalCacheDir()目录为sd卡/Androdi/data/包名/cache下。

2.**权限申请最好在onCreate生命周期里申请**，这样Activity旋转重建不影响结果，假如点击按钮后申请权限，假如此Activity旋转重建了，权限申请的弹窗系统会自动处理，但是申请权限结果新界面是收不到的。

3.**动态申请的权限必须先在manifest里配置**，不然不会弹该权限的窗口，结果永远返回false，切在应用权限设置里也看不到该权限。



