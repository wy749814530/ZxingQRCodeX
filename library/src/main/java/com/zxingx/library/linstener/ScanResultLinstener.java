package com.zxingx.library.linstener;

import android.graphics.Bitmap;

import com.google.zxing.Result;

/**
 * Created by Administrator on 2019/10/23 0023.
 */

public interface ScanResultLinstener {
    void handleDecode(Result result, Bitmap barcode);

    void drawViewfinder();
}
