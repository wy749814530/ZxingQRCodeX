package com.zxingx.library.activity;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zxingx.library.R;
import com.zxingx.library.camera.CameraManager;
import com.zxingx.library.decoding.CaptureActivityHandler;
import com.zxingx.library.decoding.InactivityTimer;
import com.zxingx.library.enumc.ScanFrequency;
import com.zxingx.library.linstener.ImageAnalyzeLinstener;
import com.zxingx.library.linstener.ScanQRcodeLinstener;
import com.zxingx.library.linstener.ScanResultLinstener;
import com.zxingx.library.utils.ImageUtil;
import com.zxingx.library.view.ViewfinderView;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Administrator on 2019/10/24 0024.
 */

public abstract class BaseScanActivity extends AppCompatActivity implements SurfaceHolder.Callback, ScanResultLinstener, View.OnClickListener {
    private ScanQRcodeLinstener mLinstener;
    private final int REQUEST_IMAGE = 112;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private boolean cameraIsShow = false;
    private SurfaceView previewView;
    private ViewfinderView viewfinderView;
    private ImageView ivFinder;
    private ImageView ivBack;
    private ImageView ivFlashlight;
    private ImageView ivPhotoAlbum;
    private TextView tvTitle;
    private TextView tvRight;
    private TextView tvDescription;
    private ScanFrequency mSpeed = ScanFrequency.MEDIUM_SPEED;
    private List<String> pressionList = new ArrayList<>();
    private List<String> deniedPressionList = new ArrayList<>();

    @DrawableRes
    int flash_Colse = R.mipmap.add_scan_btn_colse;
    @DrawableRes
    int flash_Open = R.mipmap.add_scan_btn_opne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置为无标题格式
        setContentView(R.layout.activity_base_scan);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Log.i("BaseScanActivity", "--- onCreate ---");

        initView();
        ZXingLibrary.initDisplayOpinion(this);
        CameraManager.init(this);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    private void initView() {

        tvTitle = findViewById(R.id.tv_title);
        tvRight = findViewById(R.id.tv_right);
        tvDescription = findViewById(R.id.tv_description);

        previewView = findViewById(R.id.preview_view);
        viewfinderView = findViewById(R.id.viewfinder_view);
        ivFinder = findViewById(R.id.iv_finder);
        ivBack = findViewById(R.id.iv_back);
        ivFlashlight = findViewById(R.id.iv_flashlight);
        ivPhotoAlbum = findViewById(R.id.iv_photo_album);
        Log.i("ScanActivity", "--- onCreate ---");

        surfaceHolder = previewView.getHolder();


        ivBack.setOnClickListener(this);
        ivFlashlight.setOnClickListener(this);
        ivPhotoAlbum.setOnClickListener(this);
        tvRight.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("BaseScanActivity", "--- onResume ---" + hasSurface);
        if (pressionList.size() == 0 && deniedPressionList.size() == 0) {
            requestPermission();
        }

        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        cameraIsShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("BaseScanActivity", "--- onPause ---");
        cameraIsShow = false;
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pressionList.clear();
        deniedPressionList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("BaseScanActivity", "--- onDestroy ---");
        inactivityTimer.shutdown();
    }

    public void cancelRequestPermission(String permission) {
        if (Manifest.permission.CAMERA.equals(permission)) {
            requestPermissionReadExternal();
        }
    }

    private void requestPermission() {
        if (requestPermissionCamera()) {
            requestPermissionReadExternal();
        }
    }

    private boolean requestPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
                return false;
            } else {
                if (pressionList.contains(Manifest.permission.CAMERA)) {
                    pressionList.remove(Manifest.permission.CAMERA);
                }
                return true;
            }
        }
        return true;
    }

    private boolean requestPermissionReadExternal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
                return false;
            } else {
                if (pressionList.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    pressionList.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(this.getClass().getSimpleName(), "requestCode == 1000 1");
                if (pressionList.contains(Manifest.permission.CAMERA)) {
                    pressionList.remove(Manifest.permission.CAMERA);
                }
                if (deniedPressionList.contains(Manifest.permission.CAMERA)) {
                    deniedPressionList.remove(Manifest.permission.CAMERA);
                }
                requestPermissionReadExternal();
            } else {
                boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                if (showRequestPermission) {
                    // 普通拒绝
                    if (!pressionList.contains(Manifest.permission.CAMERA)) {
                        pressionList.add(Manifest.permission.CAMERA);
                    }
                    requestPermissionReadExternal();
                } else {
                    //被禁止且点了不再询问按钮
                    if (!deniedPressionList.contains(Manifest.permission.CAMERA)) {
                        deniedPressionList.add(Manifest.permission.CAMERA);
                    }
                    onDeniedPermission(Manifest.permission.CAMERA);
                }
            }
        }

        if (requestCode == 2000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(this.getClass().getSimpleName(), "requestCode == 2000 1");
                if (pressionList.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    pressionList.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (deniedPressionList.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    deniedPressionList.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                if (showRequestPermission) {
                    // 普通拒绝
                    if (!pressionList.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        pressionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    //被禁止且点了不再询问按钮
                    if (!deniedPressionList.contains(Manifest.permission.CAMERA)) {
                        deniedPressionList.add(Manifest.permission.CAMERA);
                    }
                    onDeniedPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        }
    }


    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (result == null || TextUtils.isEmpty(result.getText())) {
            onQrAnalyzeFailed();
        } else {
            onQrAnalyzeSuccess(result.getText(), barcode);
        }

        if (handler != null) {
            int speed = 1000;
            if (mSpeed == ScanFrequency.HIGHT_SPEED) {
                speed = 1000;
            } else if (mSpeed == ScanFrequency.MEDIUM_SPEED) {
                speed = 2000;
            } else if (mSpeed == ScanFrequency.LOW_SPEED) {
                speed = 2500;
            }
            handler.postDelayed(() -> {
                restartScanCode();
            }, speed);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            camera = CameraManager.get().getCamera();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet, viewfinderView);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        if (camera != null) {
            if (camera != null && CameraManager.get().isPreviewing()) {
                if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                    camera.setPreviewCallback(null);
                }
                camera.stopPreview();
                CameraManager.get().getPreviewCallback().setHandler(null, 0);
                CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
                CameraManager.get().setPreviewing(false);
            }
        }
    }

    protected void scanLocalPictures() {
        Intent innerIntent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            innerIntent.setAction(Intent.ACTION_PICK);
        }
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
        startActivityForResult(wrapperIntent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 选择系统图片并解析
         */
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new ImageAnalyzeLinstener() {
                        @Override
                        public void onImageAnalyzeSuccess(Result result, Bitmap barcode) {
                            handleDecode(result, barcode);
                        }

                        @Override
                        public void onImageAnalyzeFailed() {
                            onQrAnalyzeFailed();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //震动
    private void playBeepSoundAndVibrate() {
        Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(200);
    }

    /**
     * 设置title
     *
     * @param title
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 设置是否显示右侧菜单栏
     *
     * @param visibility
     */
    public void setMenuVisibility(boolean visibility) {
        tvRight.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置右侧Menu
     *
     * @param text
     */
    public void setMenuText(String text) {
        tvRight.setText(text);
    }

    /**
     * 设置右侧menu图片
     *
     * @param imageRes
     */
    public void setMenuImage(@DrawableRes int imageRes) {
        tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, imageRes, 0);
    }

    /**
     * 设置底部描述
     *
     * @param text
     */
    public void setDescriptionText(String text) {
        tvDescription.setText(text);
    }

    /**
     * 设置底部描述配对图片
     *
     * @param imageRes
     */
    public void setDescriptionImage(@DrawableRes int imageRes) {
        tvDescription.setCompoundDrawablesWithIntrinsicBounds(0, imageRes, 0, 0);
    }

    /**
     * 设置手电筒图片
     *
     * @param closeRes
     * @param openRes
     */
    public void setFlashlightImage(@DrawableRes int closeRes, @DrawableRes int openRes) {
        flash_Colse = closeRes;
        flash_Open = openRes;
        if (isOpen) {
            ivFlashlight.setImageResource(flash_Colse);
        } else {
            ivFlashlight.setImageResource(flash_Open);
        }
    }

    /**
     * 设置手电筒图片
     *
     * @param imageRes
     */
    public void setPhotoAlbumImage(@DrawableRes int imageRes) {
        ivPhotoAlbum.setImageResource(imageRes);
    }

    /**
     * 重启开启扫码
     */
    public void restartScanCode() {
        if (cameraIsShow) {
            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            if (surfaceHolder != null) {
                initCamera(surfaceHolder);
            }
        }
    }


    /**
     * 设置扫码频率
     *
     * @param speed
     */
    public void setScanFrequency(ScanFrequency speed) {
        mSpeed = speed;
    }

    public boolean isOpen = false;

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back) {
            finish();
        } else if (i == R.id.iv_flashlight) {
            if (requestPermissionCamera()) {
                if (!isOpen) {
                    CodeUtils.isLightEnable(true);
                    isOpen = true;
                    ivFlashlight.setImageResource(flash_Colse);
                } else {
                    CodeUtils.isLightEnable(false);
                    isOpen = false;
                    ivFlashlight.setImageResource(flash_Open);
                }
            }
        } else if (i == R.id.iv_photo_album) {
            if (requestPermissionReadExternal()) {
                scanLocalPictures();
            }
        } else if (i == R.id.tv_right) {
            onClickMenuItem();
        }
    }

    protected abstract void onQrAnalyzeFailed();

    protected abstract void onQrAnalyzeSuccess(String result, Bitmap barcode);

    protected abstract void onClickMenuItem();

    protected abstract void onDeniedPermission(String permission);
}
