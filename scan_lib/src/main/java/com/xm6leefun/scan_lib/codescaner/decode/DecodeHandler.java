package com.xm6leefun.scan_lib.codescaner.decode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xm6leefun.scan_lib.codescaner.ScannerUtils;
import com.xm6leefun.scan_lib.codescaner.camera.CameraManager;
import com.xm6leefun.scan_lib.zxing.BinaryBitmap;
import com.xm6leefun.scan_lib.zxing.DecodeHintType;
import com.xm6leefun.scan_lib.zxing.MultiFormatReader;
import com.xm6leefun.scan_lib.zxing.PlanarYUVLuminanceSource;
import com.xm6leefun.scan_lib.zxing.ReaderException;
import com.xm6leefun.scan_lib.zxing.Result;
import com.xm6leefun.scan_lib.zxing.common.GlobalHistogramBinarizer;

import java.util.Map;

final class DecodeHandler extends Handler {

    private final CameraManager cameraManager;
    private final Handler scannerViewHandler;
    private final MultiFormatReader multiFormatReader;
    private boolean running = true;

    DecodeHandler(CameraManager cameraManager, Handler scannerViewHandler, Map<DecodeHintType, Object> hints) {
        this.cameraManager = cameraManager;
        this.scannerViewHandler = scannerViewHandler;
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null || !running) {
            return;
        }
        switch (message.what) {
            case ScannerUtils.DECODE:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case ScannerUtils.QUIT:
                running = false;
                Looper.myLooper().quit();
                break;
        }
    }

    /**
     * 捕捉画面并解码<br/>
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one decode to
     * the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        //竖屏识别一维

        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;
        width = height;
        height = tmp;
        data = rotatedData;
        Result rawResult = null;
        PlanarYUVLuminanceSource source = cameraManager.buildLuminanceSource(data, width, height);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }

        Handler handler = scannerViewHandler;
        if (rawResult != null) {
            if (handler != null) {
                //会向 ScannerViewHandler 发消息
                Message message = Message.obtain(handler, ScannerUtils.DECODE_SUCCEEDED, rawResult);
                Bundle bundle = new Bundle();
                message.setData(bundle);
                message.sendToTarget();
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, ScannerUtils.DECODE_FAILED);
                message.sendToTarget();
            }
        }
    }
}
