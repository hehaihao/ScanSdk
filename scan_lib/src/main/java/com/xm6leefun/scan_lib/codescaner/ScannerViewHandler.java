package com.xm6leefun.scan_lib.codescaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xm6leefun.scan_lib.codescaner.camera.CameraManager;
import com.xm6leefun.scan_lib.codescaner.decode.DecodeThread;
import com.xm6leefun.scan_lib.zxing.Result;

/**
 * 针对扫描任务的Handler，可接收的message有启动扫描（restart_preview）、扫描成功（decode_succeeded）、扫描失败（decode_failed）等等
 * This class handles all the messaging which comprises the state machine for
 * capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class ScannerViewHandler extends Handler {

    public interface HandleDecodeListener {
        void decodeSucceeded(Result rawResult, Bitmap barcode, float scaleFactor);
    }

    private final DecodeThread decodeThread;
    private State state;
    private final CameraManager cameraManager;
    private HandleDecodeListener handleDecodeListener;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    ScannerViewHandler(CameraManager cameraManager
            , HandleDecodeListener handleDecodeListener) {
        this.cameraManager = cameraManager;
        this.handleDecodeListener = handleDecodeListener;
        //启动扫描线程
        decodeThread = new DecodeThread(cameraManager, this, null);

        decodeThread.start();
        state = State.SUCCESS;
        //开启相机预览界面
        cameraManager.startPreview();
        //将preview回调函数与decodeHandler绑定、调用viewfinderView
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case ScannerUtils.RESTART_PREVIEW:
                restartPreviewAndDecode();
                break;
            case ScannerUtils.DECODE_SUCCEEDED:
                state = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = null;
                float scaleFactor = 1.0f;
                if (bundle != null) {
                    byte[] compressedBitmap = bundle
                            .getByteArray(DecodeThread.BARCODE_BITMAP);
                    if (compressedBitmap != null && compressedBitmap.length > 0) {
                        barcode = BitmapFactory.decodeByteArray(compressedBitmap,
                                0, compressedBitmap.length, null);
                        barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                    }
                    scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
                }
                if (handleDecodeListener != null)
                    handleDecodeListener.decodeSucceeded((Result) message.obj, barcode, scaleFactor);
                break;
            case ScannerUtils.DECODE_FAILED:
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), ScannerUtils.DECODE);
                break;
            case ScannerUtils.RETURN_SCAN_RESULT:
                break;
            case ScannerUtils.LAUNCH_PRODUCT_QUERY:
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), ScannerUtils.QUIT);
        quit.sendToTarget();
        try {
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        removeMessages(ScannerUtils.DECODE_SUCCEEDED);
        removeMessages(ScannerUtils.DECODE_FAILED);
    }

    public void pauseSynchronously() {
        state = State.DONE;
        Message quit = Message.obtain(decodeThread.getHandler(), ScannerUtils.QUIT);
        quit.sendToTarget();
        try {
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        removeMessages(ScannerUtils.DECODE_SUCCEEDED);
        removeMessages(ScannerUtils.DECODE_FAILED);
    }

    public void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), ScannerUtils.DECODE);
        }
    }
}
