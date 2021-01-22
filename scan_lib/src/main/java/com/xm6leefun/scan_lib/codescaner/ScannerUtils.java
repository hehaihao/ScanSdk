package com.xm6leefun.scan_lib.codescaner;

import android.content.Context;
import android.util.TypedValue;

import com.xm6leefun.scan_lib.zxing.Result;
import com.xm6leefun.scan_lib.zxing.client.result.ParsedResult;
import com.xm6leefun.scan_lib.zxing.client.result.ResultParser;

/**
 * Created by hupei on 2016/7/1.
 */
public final class ScannerUtils {
    public static final int RESTART_PREVIEW = 0;
    public static final int DECODE_SUCCEEDED = 1;
    public static final int DECODE_FAILED = 2;
    public static final int RETURN_SCAN_RESULT = 3;
    public static final int LAUNCH_PRODUCT_QUERY = 4;
    public static final int DECODE = 5;
    public static final int QUIT = 6;

    public static class Scan {

        public static final String RESULT = "SCAN_RESULT";
    }

    public static ParsedResult parseResult(Result rawResult) {
        if (rawResult == null) return null;
        return ResultParser.parseResult(rawResult);
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue
                , context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue
                , context.getResources().getDisplayMetrics());
    }
}
