package com.zxingx.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxingx.library.activity.BaseScanActivity;


/**
 * Created by Administrator on 2020/5/15 0015.
 */

public class ScanQRcodeActivity extends BaseScanActivity {
    private String TAG = ScanQRcodeActivity.class.getSimpleName();
    private boolean flightIsOpen = false;
    private TextView ivFlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置标题
        setTitleLay(R.layout.qr_title_lay);
        setBottomLay(R.layout.qr_bottom_lay);

        findViewById(R.id.scan_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivFlight = findViewById(R.id.iv_flight);
        findViewById(R.id.iv_flight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flightIsOpen) {
                    flightIsOpen = false;
                    openFlashlight(false);
                    ivFlight.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.add_scan_btn_opne, 0, 0);
                } else {
                    flightIsOpen = true;
                    openFlashlight(true);
                    ivFlight.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.add_scan_btn_colse, 0, 0);
                }
            }
        });

        findViewById(R.id.qrcode_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanPictures();
            }
        });
    }


    @Override
    public void onQrAnalyzeFailed() {
        Log.i(TAG, "== 无法识别的二维码或条形码 ==");
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
