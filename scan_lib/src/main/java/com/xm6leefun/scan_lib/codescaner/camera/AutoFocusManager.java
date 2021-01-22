package com.xm6leefun.scan_lib.codescaner.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * author : Liao JunJie
 * e-mail : 504868593@qq.com
 * time   : 2018/05/24
 * desc   : 这是一个相机自动对焦的管理类
 */
final class AutoFocusManager implements SensorEventListener, Camera.AutoFocusCallback {

    private static final String TAG = AutoFocusManager.class.getSimpleName();

    private static final int STATUS_NONE = 0;
    private static final int STATUS_STATIC = 1;
    private static final int STATUS_MOVE = 2;

    private static final int DELAY_DURATION = 500;

    private static final Collection<String> FOCUS_MODES_CALLING_AF;

    static {
        FOCUS_MODES_CALLING_AF = new ArrayList<>(2);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
    }

    private final Camera camera;
    private SensorManager sensorManager;

    private final boolean useAutoFocus;
    private boolean isFocusing;
    private boolean isStopped;

    private int STATUE = STATUS_NONE;
    private int mX, mY, mZ;
    private long lastStaticStamp = 0;
    private boolean canFocusIn = false;  //内部是否能够对焦控制机制

    AutoFocusManager(Context context, Camera camera) {
        this.camera = camera;
        String currentFocusMode = camera.getParameters().getFocusMode();
        useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode); // 自动对焦
        if (useAutoFocus) {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager != null) {
                sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
            }
        }
        Log.e(TAG, "useAutoFocus-->" + useAutoFocus);
    }

    synchronized void start() {
        if (!useAutoFocus) return;
        isStopped = false;
        if (!isFocusing) {
            try {
                camera.autoFocus(this);
                isFocusing = true;
            } catch (RuntimeException re) {
                // Have heard RuntimeException reported in Android 4.0.x+; continue?
                Log.w(TAG, "Unexpected exception while focusing", re);
            }
        }
    }

    synchronized void stop() {
        if (!useAutoFocus) return;
        isStopped = true;
        // Doesn't hurt to call this even if not focusing
        try {
            camera.cancelAutoFocus();
        } catch (RuntimeException re) {
            // Have heard RuntimeException reported in Android 4.0.x+; continue?
            Log.w(TAG, "Unexpected exception while cancelling focusing", re);
        }
    }

    private void restParams() {
        STATUE = STATUS_NONE;
        canFocusIn = false;
        mX = 0;
        mY = 0;
        mZ = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) return;
        if (isStopped || isFocusing) {
            restParams();
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //加速度传感器
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            long stamp = System.currentTimeMillis();
            if (STATUE != STATUS_NONE) {
                int px = Math.abs(mX - x);
                int py = Math.abs(mY - y);
                int pz = Math.abs(mZ - z);
                double value = Math.sqrt(px * px + py * py + pz * pz);
                if (value > 1.5) {
                    STATUE = STATUS_MOVE;
                } else {
                    //上一次状态是move，记录静态时间点
                    if (STATUE == STATUS_MOVE) {
                        lastStaticStamp = stamp;
                        canFocusIn = true;
                    }
                    if (canFocusIn) {
                        if (stamp - lastStaticStamp > DELAY_DURATION) {
                            //移动后静止一段时间，可以发生对焦行为
                            if (!isFocusing) {
                                canFocusIn = false;
                                start();
                            }
                        }
                    }
                    STATUE = STATUS_STATIC;
                }
            } else {
                lastStaticStamp = stamp;
                STATUE = STATUS_STATIC;
            }
            mX = x;
            mY = y;
            mZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public synchronized void onAutoFocus(boolean success, Camera theCamera) {
        isFocusing = false;
    }

}
