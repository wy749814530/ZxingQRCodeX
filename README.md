# ZxingQRCodeX
# Android 二维码扫描框架 基于Zxing3.3的封装

# Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```java
  allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
  }
```
# Step 2. Add the dependency
```java
  dependencies {
     implementation 'com.github.wy749814530:ZxingQRCodeX:4.0.1'
  }
```

# Step 3. Examples
```java
public class ScanQRcodeActivity extends BaseScanActivity {
    private String TAG = ScanQRcodeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置扫码框顶部布局
        setTitleLay(R.layout.qr_title_lay);
        // 设备扫码框底部布局
        setBottomLay(R.layout.qr_bottom_lay);
    }

    @Override
    public void onQrAnalyzeFailed() {
        Log.i(TAG, "== 无法识别的二维码或条形码 ==");
    }

    @Override
    public void onQrAnalyzeSuccess(String result, Bitmap barcode) {
        Log.i(TAG, "== 识别的二维码或条形码成功 ==" + result);
    }

    @Override
    protected void onDeniedPermission(String permission) {
        Log.i(TAG, "== 权限被拒绝 ==" + permission);
    }
}
```
# Step 4. Required permissions
```java
<!--摄像机权限-->
<uses-permission android:name="android.permission.CAMERA" />
<!--手机震动权限-->
<uses-permission android:name="android.permission.VIBRATE" />
<!--读取本地图片权限-->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

# Step 4. Demo地址
https://github.com/wy749814530/ZxingQRCodeX
