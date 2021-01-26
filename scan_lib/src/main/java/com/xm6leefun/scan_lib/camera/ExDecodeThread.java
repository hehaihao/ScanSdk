
/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xm6leefun.scan_lib.camera;

import android.os.Handler;
import android.os.Looper;

import com.xm6leefun.scan_lib.ScanApiActivity;
import com.xm6leefun.scan_lib.zxing.BarcodeFormat;
import com.xm6leefun.scan_lib.zxing.DecodeHintType;
import com.xm6leefun.scan_lib.zxing.ResultPointCallback;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 * 解码线程
 */
final class ExDecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    private final ScanApiActivity activity;
    private final Hashtable<DecodeHintType, Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    ExDecodeThread(ScanApiActivity activity,
                   Vector<BarcodeFormat> decodeFormats,
                   String characterSet,
                   ResultPointCallback resultPointCallback) {

        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);

        hints = new Hashtable<DecodeHintType, Object>(3);

        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();
            decodeFormats.addAll(ExDecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(ExDecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(ExDecodeFormatManager.DATA_MATRIX_FORMATS);
        }

        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }

        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
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
        handler = new ExDecodeHandler(activity, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
