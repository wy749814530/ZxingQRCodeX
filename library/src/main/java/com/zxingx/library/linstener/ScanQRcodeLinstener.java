package com.zxingx.library.linstener;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2020/5/15 0015.
 */

public interface ScanQRcodeLinstener {
    void onQrAnalyzeFailed();

    void onQrAnalyzeSuccess(String result, Bitmap barcode);

    void onClickMenuItem();
}
