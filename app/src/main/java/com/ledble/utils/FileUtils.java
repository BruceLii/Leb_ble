package com.ledble.utils;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
    /**
     * 删除指定文件目录下所有文件，这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理。
     *
     * @param directory 文件目录
     */
    public static void deleteFilesByDirectory(File directory) {
        if (isHasStorage()) {
            if (directory != null && directory.exists() && directory.isDirectory()) {
                for (File item : directory.listFiles()) {
                    item.delete();
                }
            }
        }
    }
    /**
     * 删除单个文件
     *
     * @param fileName 被删除文件的文件名
     * @return 单个文件删除成功返回true, 否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除目录
     *
     * @param dir
     * @return
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            return false;
        }

        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取某个文件夹下所有文件的大小（适合于没有子目录的文件夹）
     *
     * @param file
     * @return
     */
    public static long getDirectorySize(File file) {
        long j = 0;
        if (file.isDirectory()) {

            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                j = j + files[i].length();

            }
        }

        return j;
    }

    /**
     * 用第三方工具打开文件，支持doc、pdf、png、jpg/jpeg、txt
     *
     * @param path
     * @param context
     */
    public static void openFile(String path, Context context) {
        String str = path.substring(path.lastIndexOf(".") + 1);
        str = str.toLowerCase();
        if (str.equals("doc")) {
            openWordFile(path, context);
        } else if (str.equals("pdf")) {
            openPdfFile(path, context);
        } else if (str.equals("png")) {
            openPngFile(path, context);
        } else if (str.equals("jpeg") || str.equals("jpg")) {
            openJpegFile(path, context);
        } else if (str.equals("txt")) {
            openTextFile(path, false, context);
        } else {
            new AlertDialog.Builder(context).setTitle("提示")
                    .setMessage("请安装阅读软件后查看附件信息.")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }
    }

    /**
     * 用于打开Word文件
     *
     * @param path
     * @param context
     */
    public static void openWordFile(String path, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/msword");
        context.startActivity(intent);
    }

    /**
     * 用于打开JPEG文件
     *
     * @param path
     * @param context
     */
    public static void openJpegFile(String path, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "image/jpeg");
        context.startActivity(intent);
    }

    /**
     * 用于打开PNG文件
     *
     * @param path
     * @param context
     */
    public static void openPngFile(String path, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "image/png");
        context.startActivity(intent);
    }

    /**
     * 用于打开TXT文件
     *
     * @param path
     * @param paramBoolean
     * @param context
     */
    public static void openTextFile(String path, boolean paramBoolean, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(path);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(path));
            intent.setDataAndType(uri2, "text/plain");
        }
        context.startActivity(intent);
    }

    /**
     * 用于打开PDF文件
     *
     * @param path
     * @param context
     */
    public static void openPdfFile(String path, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/pdf");
        context.startActivity(intent);
    }

    /**
     * 判定是否已挂载SD卡
     *
     * @return true：已挂载；false：未挂载
     */
    public static boolean isHasStorage() {
        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(storageState)) {
            return true;
        }
        return false;
    }

    /**
     * 取得sd卡根目录
     *
     * @return
     */
    public static File getStorageFilePath() {
        if (isHasStorage()) {
            return Environment.getExternalStorageDirectory();
        } else {
            Log.d("FileUtils", "SD卡不存在或未挂载");
        }
        return null;
    }

    /**
     * 在sd卡上取得或创建文件目录
     *
     * @param derectoryPath 目标文件目录
     * @return 返回文件目录
     */
    public static File getStorageDerectory(String derectoryPath) {
        File file = getStorageFilePath();
        if (file != null) {
            File fileDerectory = new File(file.getAbsolutePath() + derectoryPath);
            if (!fileDerectory.exists()) {
                boolean flag = fileDerectory.mkdirs();
                if (flag) return fileDerectory;
            } else {
                return fileDerectory;
            }
        }
        return null;
    }

    /**
     * 取得指定目录下的指定文件，如果不存在则创建
     *
     * @param derectoryPath 目标文件目录
     * @param fileName      目标文件
     * @return 指定文件
     */
    public static File getStorageFile(String derectoryPath, String fileName) {
        File derectory = getStorageDerectory(derectoryPath);
        if (derectory != null) {
            String targetFilePath = derectory.getAbsolutePath() + "/" + fileName;
            Log.d("FileUtils", "targetFile=" + targetFilePath);
            File targetFile = new File(targetFilePath);
            if (!targetFile.exists()) {
                try {
                    boolean flag = targetFile.createNewFile();
                    return flag ? targetFile : null;
                } catch (IOException e) {
                    Log.d("FileUtils", "目标文件创建失败");
                }
            } else {
                return targetFile;
            }
        }
        return null;
    }

}
