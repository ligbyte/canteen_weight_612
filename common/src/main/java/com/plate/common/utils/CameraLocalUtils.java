package com.stkj.common.utils;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Camera相机工具
 */
public class CameraLocalUtils {
    /**
     * 旋转YUV数据
     *
     * @param size
     * @param buffer
     * @param data
     */
    public static byte[] rotationAngle(Camera.Size size, byte[] buffer, byte[] data) {
        int width = size.width;
        int height = size.height;
//        旋转y
        int y_len = width * height;
//u   y/4   v  y/4
        int uvHeight = height / 2;

        int k = 0;
        for (int j = 0; j < width; j++) {
            for (int i = height - 1; i >= 0; i--) {
//                存值  k++  0          取值  width * i + j
                buffer[k++] = data[width * i + j];
            }
        }
//        旋转uv
        for (int j = 0; j < width; j += 2) {
            for (int i = uvHeight - 1; i >= 0; i--) {
                buffer[k++] = data[y_len + width * i + j];
                buffer[k++] = data[y_len + width * i + j + 1];
            }
        }
        return buffer;
    }

    private static int index = 0;

    /**
     * 保存一帧图片
     */
    public static void capture(int width, int height, byte[] temp) {
        //保存一张照片
        String fileName = "IMG_" + String.valueOf(index++) + ".jpg";  //jpeg文件名定义
        Log.e("图片保存路径：",fileName);
        File sdRoot = Environment.getExternalStorageDirectory();    //系统路径

        File pictureFile = new File(sdRoot, fileName);
        if (!pictureFile.exists()) {
            try {
                pictureFile.createNewFile();

                FileOutputStream filecon = new FileOutputStream(pictureFile);
                //ImageFormat.NV21 and ImageFormat.YUY2 for now
                YuvImage image = new YuvImage(temp, ImageFormat.NV21, height, width, null);   //将NV21 data保存成YuvImage
                //图像压缩
                image.compressToJpeg(
                        new Rect(0, 0, image.getWidth(), image.getHeight()),
                        100, filecon);   // 将NV21格式图片，以质量70压缩成Jpeg，并得到JPEG数据流

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] rotateYUVDegree270AndMirror(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate and mirror the Y luma
        int i = 0;
        int maxY = 0;
        for (int x = imageWidth - 1; x >= 0; x--) {
            maxY = imageWidth * (imageHeight - 1) + x * 2;
            for (int y = 0; y < imageHeight; y++) {
                yuv[i] = data[maxY - (y * imageWidth + x)];
                i++;
            }
        }
        // Rotate and mirror the U and V color components
        int uvSize = imageWidth * imageHeight;
        i = uvSize;
        int maxUV = 0;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            maxUV = imageWidth * (imageHeight / 2 - 1) + x * 2 + uvSize;
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[maxUV - 2 - (y * imageWidth + x - 1)];
                i++;
                yuv[i] = data[maxUV - (y * imageWidth + x)];
                i++;
            }
        }
        return yuv;
    }

}