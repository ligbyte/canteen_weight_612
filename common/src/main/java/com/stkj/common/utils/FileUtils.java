package com.stkj.common.utils;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 文件存储工具类
 */
public class FileUtils {

    public final static String TAG = "FileUtils";
    private static final long KB_DIVIDE = 1024;
    private static final long MB_DIVIDE = 1048576;
    private static final long GB_DIVIDE = 1073741824;
    public final static String ROOT_PATH = Environment.getExternalStorageDirectory() + "/";
    public static final String MMKV_NAME = "file_settings";
    public static final String KEY_FACE_CACHE_PATHS = "key_face_cache_paths";

    /**
     * 创建文件夹
     *
     * @param dir
     * @return
     */
    public static boolean createDir(File dir) {
        try {
            if (!dir.exists()) {
                dir.mkdirs();
                if (TextUtils.isEmpty(getKeyFaceCachePathsValue())){
                    putKeyFaceCachePathsValue(dir.getName());
                }else {
                    putKeyFaceCachePathsValue(getKeyFaceCachePathsValue() + "," +dir.getName());
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "create dir error");
            return false;
        }
    }

    /**
     * 清除人脸缓存
     * 人脸最大存放天数 maxDay = 7;
     * @param folders 文件夹列表 日期字符串，格式为 "YYYYMMDD,YYYYMMDD,..."
     * @return
     */
    public static void clearFaceCache(String folders) {
        final int maxDay = 30;
        if (TextUtils.isEmpty(folders)){
            return;
        }
        List<String> dates =  Arrays.asList(folders.split(","));
        List<String> newFolders =  new ArrayList<>();
        for (int i = 0;i < dates.size();i++) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 使用 Java 8 的 LocalDate 和 DateTimeFormatter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate currentDate = LocalDate.now();
                LocalDate targetDate = LocalDate.parse(dates.get(i), formatter);
                long daysBetween = ChronoUnit.DAYS.between(targetDate, currentDate);
                if (daysBetween > maxDay) {
                    deleteFolder(FileUtils.ROOT_PATH + "face_cache" + "/" + dates.get(i) + File.separator);
                }else {
                    newFolders.add(dates.get(i));
                }
                }

            } catch (Exception e) {
            }
        }

        if (newFolders.size() == 0){
            putKeyFaceCachePathsValue("");
        }else {
            putKeyFaceCachePathsValue( String.join(",", newFolders));
        }


        return ;
    }



    public static void deleteFolder(String folderPath) {
        File folder = new File(folderPath);
        deleteFolder(folder);
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file); // 递归调用删除子文件夹
                } else {
                    file.delete(); // 删除文件
                }
            }
        }
        folder.delete(); // 删除文件夹
    }

    /**
     * 删除文件夹及其所有内容
     */
    public static void deleteFaceDirectory(File dirFile) {
        if (dirFile == null) {
            return;
        }
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) {
            dirFile.delete();
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        dirFile.delete();
    }



    public static String getFaceCachePath() {
        return FileUtils.ROOT_PATH + "face_cache" + "/" + TimeUtils.getCurrentYearMonthDay();
    }

    public static String getFaceCachePathParent() {
        return FileUtils.ROOT_PATH + "face_cache" + "/";
    }

    public static void saveImageCache(byte[] imageCache,String createOrderNumber) {
        // 检查外部存储是否可用
        if (!isExternalStorageWritable()) {
            Log.e(TAG, "External storage is not writable");
            return;
        }

        int width = 1920;
        int height = 1080;
        int quality = 50;
        if (Build.MODEL.equals("rk3568_h09")){
             width = 352;
             height = 288;
            quality = 100;
        }

        YuvImage im = new YuvImage(imageCache, ImageFormat.NV21, width,
                height, null);
        Rect r = new Rect(0, 0, width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        im.compressToJpeg(r, quality, baos);

        try {
            FileOutputStream output = new FileOutputStream(getFaceCachePath() + File.separator + createOrderNumber + ".jpg");
            output.write(baos.toByteArray());
            output.flush();
            output.close();

        } catch (Exception e) {

            System.out.println("Saving to file failed");

        }


    }

    /**
     * 获取指定文件夹下第一层所有文件夹的名字
     *
     * @param directoryPath 指定文件夹路径
     * @return 文件夹名字列表
     */
    public static List<String> getSubDirectoryNames(String directoryPath) {
        List<String> subDirectoryNames = new ArrayList<>();
        File directory = new File(directoryPath);

        // 检查目录是否存在并且是一个目录
        if (!directory.exists() || !directory.isDirectory()) {
            Log.e(TAG, "Directory does not exist or is not a directory: " + directoryPath);
            return subDirectoryNames;
        }

        // 获取目录下的所有文件和文件夹
        File[] files = directory.listFiles();
        if (files == null) {
            Log.e(TAG, "Failed to list files in directory: " + directoryPath);
            return subDirectoryNames;
        }

        // 遍历文件和文件夹
        for (File file : files) {
            if (file.isDirectory()) {
                subDirectoryNames.add(file.getName());
            }
        }

        return subDirectoryNames;
    }


    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 删除file
     *
     * @param cameraFilePath 指定文件路径
     */
    public static boolean deleteFileByPath(String cameraFilePath) {
        if (TextUtils.isEmpty(cameraFilePath)) {
            return false;
        }
        File file = new File(cameraFilePath);
        if (file.exists()) {
            file.delete();
            Log.d("delete file by path %s", cameraFilePath);
            return true;
        }
        return false;
    }

    /**
     * 删除文件夹
     */
    public static void deleteDirectory(File dirFile) {
        if (dirFile == null) {
            return;
        }
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        dirFile.delete();

    }

    /**
     * 删除文件夹
     */
    public static void deleteDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }
        if (dirFile.isFile()) {
            dirFile.delete();
            return;
        }
        deleteDirectory(dirFile);
    }


    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    /**
     * 获取指定文件夹
     */
    public static long getDirFileSize(File f) {
        if (f == null) {
            return 0;
        }
        long size = 0;
        File fList[] = f.listFiles();
        if (fList == null) {
            return 0;
        }
        for (File aFList : fList) {
            if (aFList.isDirectory()) {
                size = size + getDirFileSize(aFList);
            } else {
                size = size + getFileSize(aFList);
            }
        }
        return size;
    }

    /**
     * 转换文件大小(b,kb,mb,g)
     */
    public static String formatFileSize(long fileS) {
        String fileSizeString;
        if (fileS < KB_DIVIDE) {
            fileSizeString = fileS + "B";
        } else if (fileS < MB_DIVIDE) {
            fileSizeString = fileS / KB_DIVIDE + "KB";
        } else if (fileS < GB_DIVIDE) {
            fileSizeString = fileS / MB_DIVIDE + "MB";
        } else {
            DecimalFormat df = new DecimalFormat("#0.00");
            fileSizeString = df.format(fileS * 1.0f / GB_DIVIDE) + "G";
        }
        return fileSizeString;
    }

    public static boolean mergeFiles(File outFile, List<File> fileList) {
        if (outFile == null || outFile.isDirectory()) {
            Log.d("FileUtils", "--mergeFiles  outFile is null--");
            return false;
        }
        if (fileList == null || fileList.isEmpty()) {
            Log.d("FileUtils", "--mergeFiles  fileList is null--");
            return false;
        }
        if (outFile.exists()) {
            outFile.delete();
        }
        FileChannel outChannel = null;
        try {
            int BUFF_SIZE = 1024 * 128;
            outChannel = new FileOutputStream(outFile).getChannel();
            for (File f : fileList) {
                FileChannel fc = new FileInputStream(f).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUFF_SIZE);
                while (fc.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
            Log.d("FileUtils", "--mergeFiles success--");
            return true;
        } catch (IOException ioe) {
            Log.d("FileUtils", "--mergeFiles error--");
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                Log.d("FileUtils", "--mergeFiles error--");
            }
        }
        return false;
    }

    public static boolean mergeFiles(FileOutputStream outputStream, List<File> fileList) {
        if (outputStream == null) {
            Log.d("FileUtils", "--mergeFiles  outFile is null--");
            return false;
        }
        if (fileList == null || fileList.isEmpty()) {
            Log.d("FileUtils", "--mergeFiles  fileList is null--");
            return false;
        }
        FileChannel outChannel = null;
        try {
            int BUFF_SIZE = 1024 * 128;
            outChannel = outputStream.getChannel();
            for (File f : fileList) {
                FileChannel fc = new FileInputStream(f).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUFF_SIZE);
                while (fc.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
            Log.d("FileUtils", "--mergeFiles success--");
            return true;
        } catch (IOException ioe) {
            Log.d("FileUtils", "--mergeFiles error--");
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                Log.d("FileUtils", "--mergeFiles error--");
            }
        }
        return false;
    }

    /**
     * 复制文件到指定路径
     */
    public static boolean copyFileToOtherFile(File file, File otherFile) {
        if (file == null || !file.exists()) {
            Log.d("FileUtils", "--file not exist--");
            return false;
        }
        if (file.isDirectory()) {
            Log.d("FileUtils", "--file is a dir--");
            return false;
        }
        if (otherFile == null || otherFile.isDirectory()) {
            Log.d("FileUtils", "--other file is null--");
            return false;
        }
        if (otherFile.exists()) {
            otherFile.delete();
        }

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(file);
            fo = new FileOutputStream(otherFile);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
            Log.d("FileUtils", "--copyFileToOtherFile success--");
        } catch (IOException e) {
            Log.d("FileUtils", "--copyFileToOtherFile error--");
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.d("FileUtils", "--copyFileToOtherFile error--");
            }
        }
        return true;
    }

    public static boolean copyFileToOtherFile(File file, FileOutputStream outputStream) {
        if (file == null || !file.exists()) {
            Log.i("FileUtils", "--file not exist--");
            return false;
        }
        if (file.isDirectory()) {
            Log.i("FileUtils", "--file is a dir--");
            return false;
        }

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(file);
            fo = outputStream;
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
            Log.i("FileUtils", "--copyFileToOtherFile success--");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("FileUtils", "--copyFileToOtherFile error--");
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("FileUtils", "--copyFileToOtherFile error--");
            }
        }
        return true;
    }

    public static boolean copyFileToOtherFile(FileInputStream inputStream, File file) {
        if (file == null) {
            Log.i("FileUtils", "--file not exist--");
            return false;
        }
        if (file.isDirectory()) {
            Log.i("FileUtils", "--file is a dir--");
            return false;
        }

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = inputStream;
            fo = new FileOutputStream(file);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
            Log.i("FileUtils", "--copyFileToOtherFile success--");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("FileUtils", "--copyFileToOtherFile error--");
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("FileUtils", "--copyFileToOtherFile error--");
            }
        }
        return true;
    }

    public static byte[] file2Bytes(String filePath) {
        int byte_size = 1024;
        byte[] b = new byte[byte_size];
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                    byte_size);
            for (int length; (length = fileInputStream.read(b)) != -1; ) {
                outputStream.write(b, 0, length);
            }
            fileInputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            Log.d("FileUtils", "--file2Bytes error--");
        }
        return null;
    }


    /**
     * 保存文件到sd
     *
     * @param filename
     * @param content
     * @return
     */
    public static boolean saveContentToSdcard(String filename, String content) {
        FileWriter writer = null;
        boolean flag = false;
//        FileOutputStream fos = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/", filename);
            writer = new FileWriter(file, true);
            writer.write(content);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return flag;
    }


    public static String getKeyFaceCachePathsValue() {
        return getMMKV().getString(KEY_FACE_CACHE_PATHS, "");
    }

    public static void putKeyFaceCachePathsValue(String keyFaceCachePathsValue) {
        getMMKV().putString(KEY_FACE_CACHE_PATHS, keyFaceCachePathsValue);
    }

    public static MMKV getMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }

}
