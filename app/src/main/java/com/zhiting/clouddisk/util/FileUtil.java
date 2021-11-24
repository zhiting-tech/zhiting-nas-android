package com.zhiting.clouddisk.util;

import com.zhiting.clouddisk.entity.home.FileBean;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<File> getFileListByDirPath(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        List<File> result = new ArrayList<>();
        if (files == null) {
            return new ArrayList<>();
        }

        for (int i = 0; i < files.length; i++) {
            result.add(files[i]);
        }

        return result;
    }

    public static String cutLastSegmentOfPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB", "NB", "DB", "CB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getReadableFileSizeSaveTow(long size) {
        if (size <= 0) return "0B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB", "NB", "DB", "CB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#.00").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getReadableFileSizeWithoutUnit(long size) {
        if (size <= 0) return "0";
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups));
    }

    public static String getReadableFileSize(double size) {
        if (size <= 0) return "0B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB", "NB", "DB", "CB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#.00").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static long convert2B(long size, String unit){
        if (unit.equalsIgnoreCase("B")){
            return size;
        }
        if (unit.equalsIgnoreCase("KB")){
            return size*1024;
        }
        if (unit.equalsIgnoreCase("MB")){
            return size*1024*1024;
        }
        if (unit.equalsIgnoreCase("GB")){
            return size*1024*1024*1024;
        }
        if (unit.equalsIgnoreCase("TB")){
            return size*1024*1024*1024*1024;
        }
        if (unit.equalsIgnoreCase("PB")){
            return size*1024*1024*1024*1024*1024;
        }
        if (unit.equalsIgnoreCase("ZB")){
            return size*1024*1024*1024*1024*1024*1024;
        }
        if (unit.equalsIgnoreCase("YB")){
            return size*1024*024*1024*1024*1024*1024*1024;
        }
        if (unit.equalsIgnoreCase("BB")){
            return size*1024*1024*1024*1024*1024*1024*1024*1024;
        }
        if (unit.equalsIgnoreCase("NB")){
            return size*1024*1024*1024*1024*1024*1024*1024*1024*1024;
        }
        if (unit.equalsIgnoreCase("DB")){
            return size*1024*1024*1024*1024*1024*1024*1024*1024*1024*1024;
        }
        if (unit.equalsIgnoreCase("CB")){
            return size*1024*1024*1024*1024*1024*1024*1024*1024*1024*1024*1024;
        }
        return 0;
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件长度
     */
    public static long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件
     *
     * @param path
     * @return List<File>
     */
    public static List<File> getFileList(String path) {
        List<File> list = getFileListByDirPath(path);
        return list;
    }

    /**
     * 判断所选文件是否有读全写
     * @param fileBeans
     * @return
     */
    public static boolean hasReadPermission(List<FileBean> fileBeans){
        for (FileBean fileBean : fileBeans){
            if (fileBean.getRead() == 0){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断所选文件是否有写权限
     * @param fileBeans
     * @return
     */
    public static boolean hasWritePermission(List<FileBean> fileBeans){
        for (FileBean fileBean : fileBeans){
            if (fileBean.getWrite() == 0){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断所选文件是否有删权限
     * @param fileBeans
     * @return
     */
    public static boolean hasDelPermission(List<FileBean> fileBeans){
        for (FileBean fileBean : fileBeans){
            if (fileBean.getDeleted() == 0){
                return false;
            }
        }
        return true;
    }
}
