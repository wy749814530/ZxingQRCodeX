package com.zxingx.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.zxingx.library.activity.BaseScanActivity;


/**
 * Created by Administrator on 2020/5/15 0015.
 */

public class ScanQRcodeActivity extends BaseScanActivity {
    private String TAG = ScanQRcodeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置标题
        setTitle("二维码识别");
        //不显示右侧菜单
        setMenuVisibility(false);

        setDescriptionText();
    }


    @Override
    public void onQrAnalyzeFailed() {
        Log.i(TAG, "== 无法识别的二维码或条形码 ==");
    }

    @Override
    public void onClickMenuItem() {
        Log.i(TAG, "== onClickMenuItem ==");
    }

    @Override
    protected void onDeniedPermission(String permission) {
        Log.i(TAG, "== 权限被拒绝 ==" + permission);
        new RuleAlertDialog(this).builder().setCancelable(false).
                setTitle(getString(R.string.add_wifi_tip)).
                setMsg(getString(R.string.add_camera_per)).
                setPositiveButton(getString(R.string.go_to_settings), v1 -> {
                    toPermissionSetting();
                }).setNegativeButton(getString(R.string.label_cancel), v2 -> {
            cancelRequestPermission(permission);
        }).show();
    }

    @Override
    public void onQrAnalyzeSuccess(String result, Bitmap barcode) {
        Log.i(TAG, "== 识别的二维码或条形码成功 ==" + result);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("ScanActivity", "--- onResume ---");
    }

    /**
     * 跳转到权限设置界面
     */
    public void toPermissionSetting() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
