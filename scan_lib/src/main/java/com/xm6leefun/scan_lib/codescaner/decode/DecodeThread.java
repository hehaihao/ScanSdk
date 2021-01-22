package com.xm6leefun.scan_lib.codescaner.decode;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xm6leefun.scan_lib.codescaner.camera.CameraManager;
import com.xm6leefun.scan_lib.zxing.BarcodeFormat;
import com.xm6leefun.scan_lib.zxing.DecodeHintType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public final class DecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

    private final CameraManager cameraManager;
    private final Handler scannerViewHandler;
    private final Map<DecodeHintType, Object> hints;
    private DecodeHandler handler;
    private final CountDownLatch handlerInitLatch;

    public DecodeThread(CameraManager cameraManager, Handler scannerViewHandler, Collection<BarcodeFormat> decodeFormats) {
        this.cameraManager = cameraManager;
        this.scannerViewHandler = scannerViewHandler;
        handlerInitLatch = new CountDownLatch(1);

        hints = new EnumMap<>(DecodeHintType.class);
        // The prefs can't change while the thread is running, so pick them up once here.
        if (decodeFormats == null || decodeFormats.isEmpty()) {

            decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
            // 一维码：商品
            boolean decode1DProduct = true;
            if (decode1DProduct) {
                decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
            }
            // 一维码：工业
            boolean decode1DIndustrial = true;
            if (decode1DIndustrial) {
                decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
            }
            // 二维码
            boolean decodeQR = true;
            if (decodeQR) {
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            }
            // Data Matrix
            boolean decodeDataMatrix = true;
            if (decodeDataMatrix) {
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            }
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        Log.i("DecodeThread", "Hints: " + hints);
    }

    public Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(cameraManager, scannerViewHandler, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
