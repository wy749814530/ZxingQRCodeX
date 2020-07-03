package com.zxingx.library.linstener;

import android.graphics.Bitmap;

import com.google.zxing.Result;

/**
 * Created by Administrator on 2019/10/24 0024.
 */

public interface ImageAnalyzeLinstener {
    void onImageAnalyzeSuccess(Result result, Bitmap barcode);

    void onImageAnalyzeFailed();
}
