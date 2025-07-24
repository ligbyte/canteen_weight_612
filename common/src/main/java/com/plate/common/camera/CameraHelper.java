package com.stkj.common.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;


import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.storage.StorageHelper;
import com.stkj.common.ui.toast.AppToast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * camera1帮助类
 */
@SuppressWarnings("deprecation")
public class CameraHelper extends ActivityWeakRefHolder implements SurfaceHolder.Callback {

    private static final String TAG = "CameraHelper";

    private boolean isPrepared;
    private boolean isCaptureStarted;
    private boolean isFrontCamera;
    private SurfaceView mSurfaceView;

    private Camera mCamera;
    private Camera.Parameters mParameters;
    private int mPreviewWidth = 320; // default 1440
    private int mPreviewHeight = 240; // default 1080
    private float mPreviewScale = mPreviewHeight * 1f / mPreviewWidth;
    private MediaRecorder mMediaRecorder;
    private OnCameraHelperCallback onCameraHelperCallback;

    private String mCameraOutputPath;
    private boolean isFirstInit = true;
    private int displayOrientation = -1;
    private int previewOrientation = 0;
    private boolean needPreviewCallBack;
    private int mCameraId = -1;
    private boolean flipMirrorH;
    private boolean flipMirrorV;
    private byte[] callbackBuffer;

    public CameraHelper(@NonNull Activity activity) {
        super(activity);
    }

    public void setNeedPreviewCallBack(boolean needPreviewCallBack) {
        this.needPreviewCallBack = needPreviewCallBack;
    }

    public void setFlipMirrorH(boolean flipMirrorH) {
        this.flipMirrorH = flipMirrorH;
    }

    public void setFlipMirrorV(boolean flipMirrorV) {
        this.flipMirrorV = flipMirrorV;
    }

    public void setDisplayOrientation(int displayOrientation) {
        this.displayOrientation = displayOrientation;
    }

    public void setCameraId(int cameraId) {
        this.mCameraId = cameraId;
    }

    @Override
    protected void onActivityPause() {
        Log.e(TAG, "limeopenCamera onActivityPause 80: ");
        isPrepared = false;
        releaseMediaRecorder();
        releaseCamera();
        Log.i(TAG, "onActivityPause");
    }

    @Override
    protected void onActivityResume() {
        if (!isFirstInit && !isPrepared && hasPreviewView()) {
            Log.i(TAG, "onActivityResume");
            prepare(mSurfaceView, isFrontCamera);
        }
    }

    public boolean hasPreviewView() {
        return mSurfaceView != null;
    }

    public void prepare(SurfaceView surfaceView) {
        prepare(surfaceView, false);
    }

    public void prepare(SurfaceView surfaceView, boolean isFrontCamera) {
        try {
            Log.d(TAG, "limeopenCamera prepare 105");

        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
            Log.d(TAG, "limeopenCamera prepare 110");
        if (mMediaRecorder != null) {
            releaseMediaRecorder();
        }
        int cameraId = mCameraId;
        if (mCameraId != -1) {
            mCamera = openCameraById(mCameraId);
        } else {
            this.isFrontCamera = isFrontCamera;
            Log.d(TAG, "limeopenCamera prepare 126   =============== isFrontCamera: " + isFrontCamera);
            if (isFrontCamera) {
                mCamera = openFrontCamera();
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                mCamera = openBackCamera();
            }
        }
            Log.d(TAG, "limeopenCamera prepare 127   + cameraId: " + cameraId);
        Camera.CameraInfo info =
                new Camera.CameraInfo();
            Log.d(TAG, "limeopenCamera prepare 130");
            if (cameraId == 1){
                return;
            }
        Camera.getCameraInfo(cameraId, info);
            Log.d(TAG, "limeopenCamera prepare 132");
        int rotation = activityWithCheck.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Log.i(TAG, "prepare degrees: " + degrees);
            Log.d(TAG, "limeopenCamera prepare 149");
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        //预览相机原始方向
        previewOrientation = result;
        //设置相机显示的方向
        if (displayOrientation == -1) {
            displayOrientation = result;
        } else {
            result = displayOrientation;
        }
        Log.i(TAG, "prepare displayOrientation: " + displayOrientation);
            Log.d(TAG, "limeopenCamera prepare 166");
        if (mCamera == null){
            return;
        }
            Log.d(TAG, "limeopenCamera prepare 170");
        mCamera.setDisplayOrientation(90);
        mSurfaceView = surfaceView;
        this.isFrontCamera = isFrontCamera;
        SurfaceHolder viewHolder = surfaceView.getHolder();
        viewHolder.addCallback(this);
            Log.d(TAG, "limeopenCamera prepare 176");
            if (!viewHolder.isCreating()) {
                Log.d(TAG, "limeopenCamera prepare 178");
                mCamera.setPreviewDisplay(viewHolder);
                Log.d(TAG, "limesetCameraInitParams : " + surfaceView.getWidth() + "  " + surfaceView.getHeight());
                setCameraInitParams();
                Log.i(TAG, "limeopenCamera prapre 181");
                mCamera.startPreview();
                handlePreviewCallback();
                Log.i(TAG, "limeopenCamera prapre 184");
                isPrepared = true;
                isFirstInit = false;
                Log.i(TAG, "prepare viewHolder created");
            }
        } catch (Throwable e) {
            Log.e(TAG, "limesetCameraInitParams openCameraById 189: " + e.getMessage());
//            AppToast.toastMsg("相机预览失败!" + e.getMessage());
        }
    }

    private void setCameraInitParams() {


        initCameraParameters();


    }


    private void initCameraParameters() {
        Log.v(TAG, "initConfig");
        try {
            mParameters = mCamera.getParameters();
            // 如果摄像头不支持这些参数都会出错的，所以设置的时候一定要判断是否支持
            List<String> supportedFlashModes = mParameters.getSupportedFlashModes();
            if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF); // 设置闪光模式
            }
            List<String> supportedFocusModes = mParameters.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
            }
            mParameters.setPreviewFormat(ImageFormat.NV21); // 设置预览图片格式
//            mParameters.setPictureFormat(ImageFormat.JPEG); // 设置拍照图片格式
            mParameters.setExposureCompensation(0); // 设置曝光强度
            Camera.Size previewSize = getSuitableSize(mParameters.getSupportedPreviewSizes());
            mPreviewWidth = previewSize.width;
            mPreviewHeight = previewSize.height;
            mParameters.setPreviewSize(mPreviewWidth, mPreviewHeight); // 设置预览图片大小
            Log.d(TAG, "limepreviewWidth: " + mPreviewWidth + ", previewHeight: " + mPreviewHeight);
            Camera.Size pictureSize = getSuitableSize(mParameters.getSupportedPictureSizes());
            mParameters.setPictureSize(pictureSize.width, pictureSize.height);
            Log.d(TAG, "pictureWidth: " + pictureSize.width + ", pictureHeight: " + pictureSize.height);
            mCamera.setParameters(mParameters); // 将设置好的parameters添加到相机里
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Camera.Size getSuitableSize(List<Camera.Size> sizes) {
        int minDelta = Integer.MAX_VALUE; // 最小的差值，初始值应该设置大点保证之后的计算中会被重置
        int index = 0; // 最小的差值对应的索引坐标
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            Log.d(TAG, "limeSupportedSize, width: " + size.width + ", height: " + size.height);
            // 先判断比例是否相等
            if (size.width * mPreviewScale == size.height) {
                int delta = Math.abs(mPreviewWidth - size.width);
                if (delta == 0) {
                    return size;
                }
                if (minDelta > delta) {
                    minDelta = delta;
                    index = i;
                }
            }
        }
        return sizes.get(index);
    }

    public int getPreviewOrientation() {
        return previewOrientation;
    }

    public void setCameraHelperCallback(OnCameraHelperCallback onCameraHelperCallback) {
        this.onCameraHelperCallback = onCameraHelperCallback;
    }

    public String getCameraOutputPath() {
        return mCameraOutputPath;
    }

    public void takePicture() {
        if (!isPrepared) {
            AppToast.toastMsg("未准备就绪");
            return;
        }
        try {
            if (mCamera != null) {
                mCameraOutputPath = null;
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            File cacheFile = StorageHelper.createCacheFile("takePicture_cache_" + System.currentTimeMillis() + ".jpg");
                            mCameraOutputPath = cacheFile.getAbsolutePath();
                            FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
                            fileOutputStream.write(data);
                            fileOutputStream.close();
                            //fix pic orientation
                            ExifInterface exifInterface = new ExifInterface(mCameraOutputPath);
                            if (flipMirrorH) {
                                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_FLIP_HORIZONTAL));
                            } else if (flipMirrorV) {
                                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_FLIP_VERTICAL));
                            } else {
                                String picOrientation;
                                if (displayOrientation != -1) {
                                    picOrientation = String.valueOf(displayOrientation);
                                } else {
                                    // 修正图片的旋转角度，设置其不旋转。这里也可以设置其旋转的角度，可以传值过去，
                                    // 例如旋转90度，传值ExifInterface.ORIENTATION_ROTATE_90，需要将这个值转换为String类型的
                                    if (isFrontCamera) {
                                        picOrientation = String.valueOf(ExifInterface.ORIENTATION_ROTATE_270);
                                    } else {
                                        picOrientation = String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);
                                    }
                                }
                                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, picOrientation);
                            }
                            exifInterface.saveAttributes();
                            if (onCameraHelperCallback != null) {
                                onCameraHelperCallback.onTakePictureSuccess(mCameraOutputPath);
                            }
                            Log.i(TAG, "takePicture cache file: " + mCameraOutputPath);
                        } catch (Throwable e) {
                            Log.e("TAG", "limeException 286: " + e.getMessage());
                            if (onCameraHelperCallback != null) {
                                onCameraHelperCallback.onTakePictureError(e.getMessage());
                            }
                            Log.i(TAG, "takePicture write output error: " + e.getMessage());
                        }
                    }
                });
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 296: " + e.getMessage());
            if (onCameraHelperCallback != null) {
                onCameraHelperCallback.onTakePictureError(e.getMessage());
            }
            Log.i(TAG, "takePicture error: " + e.getMessage());
        }
    }

    public void startCaptureVideo() {
        if (!isPrepared) {
            AppToast.toastMsg("未准备就绪");
            return;
        }
        if (isCaptureStarted) {
            AppToast.toastMsg("正在录制中");
            return;
        }
        try {
            if (mCamera != null) {
                mCameraOutputPath = null;
                //output cache file
                File captureVideoFile = StorageHelper.createCacheFile("captureVideo_cache_" + System.currentTimeMillis() + ".mp4");
                mCameraOutputPath = captureVideoFile.getAbsolutePath();
                if (mMediaRecorder == null) {
                    mMediaRecorder = new MediaRecorder();
                }

                // Step 1: Unlock and set camera to MediaRecorder
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);

                if (isFrontCamera) {
                    mMediaRecorder.setOrientationHint(270);
                } else {
                    mMediaRecorder.setOrientationHint(90);
                }

                // Step 2: Set sources
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
                mMediaRecorder.setProfile(getBestCamcorderProfile());

                // Step 4: Set output file
                mMediaRecorder.setOutputFile(captureVideoFile.getAbsolutePath());

                // Step 5: Set the preview output
                mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

                // Step 6: Prepare configured MediaRecorder
                mMediaRecorder.prepare();

                mMediaRecorder.start();

                isCaptureStarted = true;
                Log.i(TAG, "startCaptureVideo MediaRecorder.start");
                if (onCameraHelperCallback != null) {
                    onCameraHelperCallback.onCaptureVideoStart();
                }
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 358: " + e.getMessage());
            releaseMediaRecorder();
            try {
                if (mCamera != null) {
                    mCamera.lock();
                    Log.i(TAG, "startCaptureVideo error lock camera");
                }
            } catch (Throwable error) {
                error.printStackTrace();
            }
            isCaptureStarted = false;
            Log.i(TAG, "startCaptureVideo error: " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseCaptureVideo() {
        if (!isPrepared) {
            AppToast.toastMsg("未准备就绪");
            return;
        }
        if (!isCaptureStarted) {
            AppToast.toastMsg("拍摄未开始");
            return;
        }
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.pause();
                Log.i(TAG, "pauseCaptureVideo MediaRecorder pause");
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 389: " + e.getMessage());
            Log.i(TAG, "pauseCaptureVideo error: " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeCaptureVideo() {
        if (!isPrepared) {
            AppToast.toastMsg("未准备就绪");
            return;
        }
        if (!isCaptureStarted) {
            AppToast.toastMsg("拍摄未开始");
            return;
        }
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.resume();
                Log.i(TAG, "resumeCaptureVideo MediaRecorder resume");
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 410: " + e.getMessage());
            Log.i(TAG, "resumeCaptureVideo error: " + e.getMessage());
        }
    }

    public void stopCaptureVideo() {
        if (!isPrepared) {
            AppToast.toastMsg("未准备就绪");
            return;
        }
        if (!isCaptureStarted) {
            AppToast.toastMsg("拍摄未开始");
            return;
        }
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                if (onCameraHelperCallback != null) {
                    onCameraHelperCallback.onCaptureVideoSuccess(mCameraOutputPath);
                }
                Log.i(TAG, "stopCaptureVideo MediaRecorder stop and reset");
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 434: " + e.getMessage());
            if (onCameraHelperCallback != null) {
                onCameraHelperCallback.onCaptureVideoError(e.getMessage());
            }
            Log.i(TAG, "stopCaptureVideo error: " + e.getMessage());
        }
        isCaptureStarted = false;
    }

    public void switchCamera() {
        if (!isPrepared) {
            AppToast.toastMsg("未准备就绪");
            return;
        }
        if (isCaptureStarted) {
            AppToast.toastMsg("正在录制中,请先停止拍摄!");
            return;
        }
        prepare(mSurfaceView, !isFrontCamera);
    }

    public boolean isCaptureStarted() {
        return isCaptureStarted;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void stopPreview() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                Log.i(TAG, "stopPreview");
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 470: " + e.getMessage());
            Log.i(TAG, "stopPreview error: " + e.getMessage());
        }
        isPrepared = false;
    }

    public void startPreview() {
        if (!isPrepared && !hasPreviewView()) {
            AppToast.toastMsg("未准备就绪");
            return;
        }
        try {
            if (mCamera != null) {
                mCamera.startPreview();
                handlePreviewCallback();
                Log.i(TAG, "limeopenCamera startPreview 470");
                Log.i(TAG, "startPreview");
                isPrepared = true;
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 490: " + e.getMessage());
            Log.i(TAG, "startPreview error: " + e.getMessage());
        }
    }

    private void handlePreviewCallback() {
        Log.i(TAG, "limeopenCamera handlePreviewCallback 479");
        if (needPreviewCallBack) {
            Log.i(TAG, "startPreview handlePreviewCallback" + this);

            if (callbackBuffer == null) {
                Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
                callbackBuffer = new byte[((previewSize.width * previewSize.height) *
                        ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
                mCamera.addCallbackBuffer(callbackBuffer);
            }
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    camera.addCallbackBuffer(data);
                    if (onCameraHelperCallback != null) {
                        onCameraHelperCallback.onPreviewFrame(data, camera, displayOrientation, previewOrientation);
                    }
                }
            });
        }
    }

    private void releaseMediaRecorder() {
        try {
            if (isCaptureStarted) {
                if (onCameraHelperCallback != null) {
                    onCameraHelperCallback.onCaptureVideoError("releaseMediaRecorder");
                }
            }
            if (mMediaRecorder != null) {
                mMediaRecorder.reset();   // clear recorder configuration
                mMediaRecorder.release(); // release the recorder object
                mMediaRecorder = null;
                Log.i(TAG, "releaseMediaRecorder");
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 528: " + e.getMessage());
            Log.i(TAG, "releaseMediaRecorder error: " + e.getMessage());
        }
        mMediaRecorder = null;
        isCaptureStarted = false;
    }

    private void releaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
                Log.i(TAG, "releaseCamera");
            }
        } catch (Throwable e) {
            Log.e("TAG", "limeException 543: " + e.getMessage());
            Log.i(TAG, "releaseCamera error: " + e.getMessage());
        }
        mCamera = null;
    }

    @Override
    public void onClear() {
        onCameraHelperCallback = null;
        releaseCamera();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            handlePreviewCallback();
            Log.i(TAG, "limeopenCamera surfaceCreated 546");
            isPrepared = true;
            Log.i(TAG, "surfaceCreated prepared");
        } catch (Throwable e) {
            Log.e("TAG", "limeException 565: " + e.getMessage());
            AppToast.toastMsg("相机预览失败!" + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            // preview surface does not exist
            Log.i(TAG, "surfaceChanged surface does not exist");
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Throwable e) {
            Log.e("TAG", "limeException 582: " + e.getMessage());
            Log.i(TAG, "surfaceChanged mCamera.stopPreview error: " + e.getMessage());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(holder);
            setCameraInitParams();
            startPreview();
            isPrepared = true;
            isFirstInit = false;
            Log.i(TAG, "surfaceChanged prepared");
        } catch (Throwable e) {
            Log.e("TAG", "limeException 598: " + e.getMessage());
            Log.i(TAG, "surfaceChanged mCamera.setPreviewDisplay error:" + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
    }

    private CamcorderProfile getBestCamcorderProfile() {
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_CIF);
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        }
        return profile;
    }

    private int[] getBestPreviewSize(int width, int height) {
        Log.i(TAG, "getBestPreviewSize  init width: " + width + " height: " + height);
        int[] result = new int[]{width, height};
        if (mCamera != null) {
            final Camera.Parameters p = mCamera.getParameters();
            //特别注意此处需要规定rate的比是大的比小的，不然有可能出现rate = height/width，但是后面遍历的时候，current_rate = width/height,所以我们限定都为大的比小的。
            float rate = (float) Math.max(width, height) / (float) Math.min(width, height);
            float tmp_diff;
            float min_diff = -1f;
            for (Camera.Size size : p.getSupportedPreviewSizes()) {
                float current_rate = (float) Math.max(size.width, size.height) / (float) Math.min(size.width, size.height);
                tmp_diff = Math.abs(current_rate - rate);
                if (min_diff < 0) {
                    min_diff = tmp_diff;
                    result[0] = size.width;
                    result[1] = size.height;
                }
                if (tmp_diff < min_diff) {
                    min_diff = tmp_diff;
                    result[0] = size.width;
                    result[1] = size.height;
                }
            }
        }
        Log.i(TAG, "limesetCameraInitParams getBestPreviewSize width: " + width + " height: " + height + " result: " + Arrays.toString(result));
        return result;
    }

    public static Camera openCameraById(int cameraId) {
        Camera camera = null;
        try {
            Log.i(TAG, "limeopenCamera openBackCamera 629------------------------------------");// attempt to get a Camera instance
            camera = Camera.open(cameraId);

        } catch (Throwable e) {
            Log.e("TAG", "limeException 655: " + e.getMessage());
            AppToast.toastMsg("打开相机失败" + e.getMessage());
            Log.e(TAG, "limeopenCamera openCameraById 632: " + e.getMessage());
        }
        return camera;
    }



    public static Camera openBackCamera() {
        Camera camera = null;
        try {
            Log.i(TAG, "limeopenCamera openBackCamera 641------------------------------------");// attempt to get a Camera instance
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            Log.i(TAG, "limeopenCamera openBackCamera 645------------------------------------");

        } catch (Throwable e) {
            Log.e("TAG", "limeException 670: " + e.getMessage());
            AppToast.toastMsg("打开相机失败" + e.getMessage());
            Log.e(TAG, "limeopenCamera openCameraById 650: " + e.getMessage());
        }
        return camera;
    }

    public static Camera openFrontCamera() {
        Camera camera = null;
        try {
            Log.i(TAG, "limeopenCamera openBackCamera 655------------------------------------");// attempt to get a Camera instance
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

        } catch (Throwable e) {
            Log.e("TAG", "limeException 684: " + e.getMessage());
            AppToast.toastMsg("打开相机失败" + e.getMessage());
            Log.e(TAG, "limeopenCamera openCameraById 655: " + e.getMessage());
        }

        return camera;
    }

    public interface OnCameraHelperCallback {
        default void onTakePictureSuccess(String picPath) {
        }

        default void onTakePictureError(String message) {
        }

        default void onCaptureVideoSuccess(String videoPath) {
        }

        default void onCaptureVideoError(String message) {
        }

        default void onCaptureVideoStart() {
        }

        default void onPreviewFrame(byte[] data, Camera camera, int displayRotation, int previewRotation) {

        }
    }
}
