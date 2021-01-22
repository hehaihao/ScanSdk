package com.xm6leefun.scan_lib.codescaner.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.xm6leefun.scan_lib.codescaner.camera.open.CameraFacing;
import com.xm6leefun.scan_lib.codescaner.camera.open.OpenCamera;

import static android.content.Context.WINDOW_SERVICE;

/**
 * 相机辅助类，主要用于设置相机的各类参数 2017/02/21 4:35
 * A class which deals with reading, parsing, and setting the camera parameters
 * which are used to configure the camera hardware.
 */
@SuppressWarnings("deprecation")
final class CameraConfigurationManager {

    private static final String TAG = "CameraConfiguration";
    private static final int FRONT_LIGHT_MODE_ON = 0;
    private static final int FRONT_LIGHT_MODE_OFF = 1;
    //    private int cwNeededRotation;
    private int cwRotationFromDisplayToCamera;
    // 屏幕分辨率
    private Point screenResolution;
    // 相机分辨率
    private Point cameraResolution;
    private Point bestPreviewSize;

    private int cameraZoomRatio = 1;//相机变焦比率

    CameraConfigurationManager(Context context, int cameraZoomRatio) {
        this.cameraZoomRatio = cameraZoomRatio;
    }

    /**
     * 计算了屏幕分辨率和当前最适合的相机像素
     * Reads, one time, values from the camera that are needed by the app.
     */
    void initFromCameraParameters(OpenCamera camera,Context context) {
        Camera.Parameters parameters = camera.getCamera().getParameters();
        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        int displayRotation = display.getRotation();
        int cwRotationFromNaturalToDisplay;
        switch (displayRotation) {
            case Surface.ROTATION_0:
                cwRotationFromNaturalToDisplay = 0;
                break;
            case Surface.ROTATION_90:
                cwRotationFromNaturalToDisplay = 90;
                break;
            case Surface.ROTATION_180:
                cwRotationFromNaturalToDisplay = 180;
                break;
            case Surface.ROTATION_270:
                cwRotationFromNaturalToDisplay = 270;
                break;
            default:
                // Have seen this return incorrect values like -90
                if (displayRotation % 90 == 0) {
                    cwRotationFromNaturalToDisplay = (360 + displayRotation) % 360;
                } else {
                    throw new IllegalArgumentException("Bad rotation: " + displayRotation);
                }
        }
        Log.i(TAG, "Display at: " + cwRotationFromNaturalToDisplay);

        int cwRotationFromNaturalToCamera = camera.getOrientation();
        Log.i(TAG, "Camera at: " + cwRotationFromNaturalToCamera);

        // Still not 100% sure about this. But acts like we need to flip this:
        if (camera.getFacing() == CameraFacing.FRONT) {
            cwRotationFromNaturalToCamera = (360 - cwRotationFromNaturalToCamera) % 360;
            Log.i(TAG, "Front camera overriden to: " + cwRotationFromNaturalToCamera);
        }

        cwRotationFromDisplayToCamera =
                (360 + cwRotationFromNaturalToCamera - cwRotationFromNaturalToDisplay) % 360;
        Log.i(TAG, "Final display orientation: " + cwRotationFromDisplayToCamera);
//        if (camera.getFacing() == CameraFacing.FRONT) {
//            Log.i(TAG, "Compensating rotation for front camera");
//            cwNeededRotation = (360 - cwRotationFromDisplayToCamera) % 360;
//        } else {
//            cwNeededRotation = cwRotationFromDisplayToCamera;
//        }
//        Log.i(TAG, "Clockwise rotation from display to camera: " + cwNeededRotation);

        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);
        screenResolution = theScreenResolution;
        Log.i(TAG, "Screen resolution in current orientation: " + screenResolution);

        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = screenResolution.x;
        screenResolutionForCamera.y = screenResolution.y;
        if (screenResolution.x < screenResolution.y) {
            screenResolutionForCamera.x = screenResolution.y;
            screenResolutionForCamera.y = screenResolution.x;
        }
        cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolutionForCamera);
        Log.i(TAG, "Camera resolution: " + cameraResolution);
        bestPreviewSize = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolutionForCamera);
        Log.i(TAG, "Best available preview size: " + bestPreviewSize);
    }

    /**
     * 读取配置设置相机的对焦模式、闪光灯模式等等
     * @param camera
     * @param safeMode
     */
    void setDesiredCameraParameters(OpenCamera camera, boolean safeMode) {

        Camera theCamera = camera.getCamera();
        Camera.Parameters parameters = theCamera.getParameters();

        if (parameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without " +
                    "configuration.");
            return;
        }

        if (safeMode) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }

        // 默认关闪光灯
        initializeTorch(parameters, FRONT_LIGHT_MODE_OFF, safeMode);
        // 自动对焦
        boolean autoFocus = true;
        // 持续对焦
        boolean disableContinuousFocus = false;
        CameraConfigurationUtils.setFocus(parameters, autoFocus, disableContinuousFocus, safeMode);

        if (!safeMode) {
            // 不进行条形码场景匹配
            boolean barCodeSceneMode = true;
            if (!barCodeSceneMode) {
                CameraConfigurationUtils.setBarcodeSceneMode(parameters);
            }

            // 不使用距离测量
            boolean disableMetering = true;
            if (!disableMetering) {
                CameraConfigurationUtils.setVideoStabilization(parameters);
                CameraConfigurationUtils.setFocusArea(parameters);
                CameraConfigurationUtils.setMetering(parameters);
            }
        }

        parameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y);

        if (cameraZoomRatio > 0) {
            CameraConfigurationUtils.setZoom(parameters, cameraZoomRatio);
        }

        theCamera.setParameters(parameters);
        theCamera.setDisplayOrientation(cwRotationFromDisplayToCamera);

        Camera.Parameters afterParameters = theCamera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null
                && (bestPreviewSize.x != afterSize.width || bestPreviewSize.y != afterSize.height)) {
            bestPreviewSize.x = afterSize.width;
            bestPreviewSize.y = afterSize.height;
        }
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    Point getScreenResolution() {
        return screenResolution;
    }

    boolean getTorchState(Camera camera) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters != null) {
                String flashMode = camera.getParameters().getFlashMode();
                return flashMode != null
                        && (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)
                        || Camera.Parameters.FLASH_MODE_TORCH
                        .equals(flashMode));
            }
        }
        return false;
    }

    void setTorch(Camera camera, boolean newSetting) {
        Camera.Parameters parameters = camera.getParameters();
        doSetTorch(parameters, newSetting, false);
        camera.setParameters(parameters);
    }

    private void initializeTorch(Camera.Parameters parameters,
                                 int frontLightMode, boolean safeMode) {
        boolean currentSetting = frontLightMode == FRONT_LIGHT_MODE_ON;
        doSetTorch(parameters, currentSetting, safeMode);
    }

    private void doSetTorch(Camera.Parameters parameters, boolean newSetting,
                            boolean safeMode) {
        CameraConfigurationUtils.setTorch(parameters, newSetting);
        // 不曝光
        boolean disableExposure = true;
        if (!safeMode && !disableExposure) {
            CameraConfigurationUtils.setBestExposure(parameters, newSetting);
        }
    }
}
