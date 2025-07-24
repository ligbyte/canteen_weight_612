package com.stkj.cashier.home.helper;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.setting.model.PauseFacePassDetect;
import com.stkj.cashier.setting.model.ResumeFacePassDetect;
import com.stkj.common.camera.CameraHelper;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.toast.AppToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 旷视人脸检测帮助类
 */
public class CBGCameraHelper extends ActivityWeakRefHolder {

    public final static String TAG = "CBGCameraHelper";
    private SurfaceView irPreview;
    private SurfaceView preview;
    private CameraHelper cameraHelper;
    private CameraHelper irCameraHelper;
    private boolean isFaceDualCamera;
    private long beforeTime = 0;

    public CBGCameraHelper(@NonNull Activity activity) {
        super(activity);
        EventBusUtils.registerEventBus(this);
    }



    public void setPreviewView(SurfaceView surfaceView, SurfaceView irPreview, boolean isFaceDualCamera) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        this.preview = surfaceView;
        this.irPreview = irPreview;
        this.isFaceDualCamera = isFaceDualCamera;

    }

    /**
     * 开始人脸检测
     */
    public void prepareFacePassDetect() {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (this.preview == null) {
            return;
        }
        Log.i(TAG, "limegoToAllAuth: " + 89);
        int cameraDisplayOrientation = DeviceManager.INSTANCE.getDeviceInterface().getCameraDisplayOrientation();
        if (cameraHelper == null) {
            cameraHelper = new CameraHelper(activityWithCheck);
            int cameraId = DeviceManager.INSTANCE.getDeviceInterface().getBackCameraId();
            cameraHelper.setCameraId(cameraId);
            cameraHelper.setDisplayOrientation(cameraDisplayOrientation);
        }
        Log.i(TAG, "limegoToAllAuth: " + 97);
//        int irCameraDisplayOrientation = DeviceManager.INSTANCE.getDeviceInterface().getIRCameraDisplayOrientation();
//        if (irCameraHelper == null && isFaceDualCamera) {
//            irCameraHelper = new CameraHelper(activityWithCheck);
//            int irCameraId = DeviceManager.INSTANCE.getDeviceInterface().getIRCameraId();
//            cameraHelper.setCameraId(irCameraId);
//            irCameraHelper.setDisplayOrientation(irCameraDisplayOrientation);
//        }
        Log.i(TAG, "limegoToAllAuth: " + 105);
        if (cameraHelper != null) {
            if (cameraHelper.hasPreviewView()) {
//            cameraHelper.startPreview();
            } else {
                Log.i(TAG, "limegoToAllAuth: " + 111);
                cameraHelper.setNeedPreviewCallBack(true);
                cameraHelper.setCameraHelperCallback(new CameraHelper.OnCameraHelperCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera, int displayOrientation, int previewOrientation) {
                        try {


                            if (MainApplication.isUnLockFrame) {
                                MainApplication.isUnLockFrame = false;
                            }


                            if (System.currentTimeMillis() - beforeTime < 300){
                                return;
                            }

                            beforeTime = System.currentTimeMillis();



                            Camera.Parameters parameters = camera.getParameters();
                            int width = parameters.getPreviewSize().width;
                            int height = parameters.getPreviewSize().height;
                            MainApplication.width = width;
                            MainApplication.height = height;
                            Log.w(TAG, "limewidth width: " + width  + "   height: " + height);

                            int orientation = DeviceManager.INSTANCE.getDeviceInterface().needUseCameraPreviewOrientation() ? previewOrientation : displayOrientation;

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
                cameraHelper.prepare(preview);
            }
        }
        //双目识别
        if (irPreview != null && irCameraHelper != null && !Build.MODEL.equals("rk3568_h09")) {
            if (irCameraHelper.hasPreviewView()) {
//                irCameraHelper.startPreview();
            } else {
                irCameraHelper.setNeedPreviewCallBack(true);
                irCameraHelper.setCameraHelperCallback(new CameraHelper.OnCameraHelperCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera, int displayOrientation, int previewOrientation) {

                    }
                });
                irCameraHelper.prepare(irPreview, true);
            }
        }

    }

    private boolean needResumeFacePassDetect;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPauseFacePassDetect(PauseFacePassDetect eventBus) {

        LogHelper.print("--EventBusUtils-onPauseFacePassDetect");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResumeFacePassDetect(ResumeFacePassDetect eventBus) {
        if (needResumeFacePassDetect) {
            startFacePassDetect();
            AppToast.toastMsg("人脸检测功能已恢复");
        }
        needResumeFacePassDetect = false;
        LogHelper.print("--EventBusUtils-onResumeFacePassDetect");
    }

    /**
     * 开启人脸检测
     */
    public void startFacePassDetect() {

    }

    /**
     * 停止人脸检测
     */
    public void stopFacePassDetect() {

    }

    @Override
    public void onClear() {
        EventBusUtils.unRegisterEventBus(this);
        stopFacePassDetect();
    }

    /**
     * 释放相机
     */
    public void releaseCameraHelper() {
        stopFacePassDetect();

        if (cameraHelper != null) {
            cameraHelper.onClear();
            cameraHelper = null;
        }
        if (irCameraHelper != null) {
            irCameraHelper.onClear();
            irCameraHelper = null;
        }
    }

    public CameraHelper getCameraHelper() {
        return cameraHelper;
    }
}
