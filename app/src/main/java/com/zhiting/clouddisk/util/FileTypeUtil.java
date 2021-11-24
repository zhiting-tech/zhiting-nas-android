package com.zhiting.clouddisk.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.zhiting.clouddisk.R;
import com.zhiting.networklib.utils.LibLoader;
import com.zhiting.networklib.utils.UiUtil;

import java.io.File;

public class FileTypeUtil {

    /**
     * word文档
     *
     * @param fileName
     * @return
     */
    public static boolean isWord(String fileName) {
        return fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".rtf");
    }

    /**
     * excel
     *
     * @param fileName
     * @return
     */
    public static boolean isExcel(String fileName) {
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    /**
     * ppt
     *
     * @param fileName
     * @return
     */
    public static boolean isPPT(String fileName) {
        return fileName.endsWith(".ppt") || fileName.endsWith(".pptx");
    }

    /**
     * txt
     *
     * @param fileName
     * @return
     */
    public static boolean isTxt(String fileName) {
        return fileName.endsWith(".txt");
    }

    /**
     * 压缩包
     *
     * @param fileName
     * @return
     */
    public static boolean isCompressedPackage(String fileName) {
        return fileName.endsWith(".rar") || fileName.endsWith(".zip") || fileName.endsWith(".0") || fileName.endsWith(".000")
                || fileName.endsWith(".001") || fileName.endsWith(".7z") || fileName.endsWith(".ace") || fileName.endsWith(".ain")
                || fileName.endsWith(".alz") || fileName.endsWith(".apz") || fileName.endsWith(".ar") || fileName.endsWith(".arc")
                || fileName.endsWith(".ari") || fileName.endsWith(".arj") || fileName.endsWith(".axx") || fileName.endsWith(".bh")
                || fileName.endsWith(".bhx") || fileName.endsWith(".boo") || fileName.endsWith(".bz") || fileName.endsWith(".bza")
                || fileName.endsWith(".bz2") || fileName.endsWith(".c00") || fileName.endsWith(".c02") || fileName.endsWith(".cab")
                || fileName.endsWith(".car") || fileName.endsWith(".cbr") || fileName.endsWith(".cbz") || fileName.endsWith(".cp9")
                || fileName.endsWith(".cpgz") || fileName.endsWith(".cpt") || fileName.endsWith(".dar") || fileName.endsWith(".dd")
                || fileName.endsWith(".dgc") || fileName.endsWith(".efw") || fileName.endsWith(".f") || fileName.endsWith(".gca")
                || fileName.endsWith(".gz") || fileName.endsWith(".ha") || fileName.endsWith(".hbc") || fileName.endsWith(".hbc2")
                || fileName.endsWith(".hbe") || fileName.endsWith(".hki") || fileName.endsWith(".hki1") || fileName.endsWith(".hki2")
                || fileName.endsWith(".hki3") || fileName.endsWith(".hpk") || fileName.endsWith(".hyp") || fileName.endsWith(".ice")
                || fileName.endsWith(".imp") || fileName.endsWith(".ipk") || fileName.endsWith(".ish") || fileName.endsWith(".jar")
                || fileName.endsWith(".jgz") || fileName.endsWith(".jic") || fileName.endsWith(".kgb") || fileName.endsWith(".kz")
                || fileName.endsWith(".lbr") || fileName.endsWith(".lha") || fileName.endsWith(".lnx") || fileName.endsWith(".lqr")
                || fileName.endsWith(".lqr") || fileName.endsWith(".lz4") || fileName.endsWith(".lzh") || fileName.endsWith(".lzm")
                || fileName.endsWith(".lzma") || fileName.endsWith(".lzo") || fileName.endsWith(".lzx") || fileName.endsWith(".md")
                || fileName.endsWith(".mint") || fileName.endsWith(".mou") || fileName.endsWith(".mpkg") || fileName.endsWith(".mzp")
                || fileName.endsWith(".nz") || fileName.endsWith(".p7m") || fileName.endsWith(".package") || fileName.endsWith(".pae")
                || fileName.endsWith(".pak") || fileName.endsWith(".paq6") || fileName.endsWith(".paq7") || fileName.endsWith(".paq8")
                || fileName.endsWith(".par") || fileName.endsWith(".par2") || fileName.endsWith(".pbi") || fileName.endsWith(".pcv")
                || fileName.endsWith(".pea") || fileName.endsWith(".pf") || fileName.endsWith(".pim") || fileName.endsWith(".pit")
                || fileName.endsWith(".piz") || fileName.endsWith(".puz") || fileName.endsWith(".pwa") || fileName.endsWith(".qda")
                || fileName.endsWith(".r00") || fileName.endsWith(".r01") || fileName.endsWith(".r02") || fileName.endsWith(".r03")
                || fileName.endsWith(".rk") || fileName.endsWith(".rnc") || fileName.endsWith(".rpm") || fileName.endsWith(".rte")
                || fileName.endsWith(".rz") || fileName.endsWith(".rzs") || fileName.endsWith(".s00") || fileName.endsWith(".s01")
                || fileName.endsWith(".s02") || fileName.endsWith(".s7z") || fileName.endsWith(".sar") || fileName.endsWith(".sdn")
                || fileName.endsWith(".sea") || fileName.endsWith(".sfs") || fileName.endsWith(".sfx") || fileName.endsWith(".sh")
                || fileName.endsWith(".shar") || fileName.endsWith(".shk") || fileName.endsWith(".shr") || fileName.endsWith(".sit")
                || fileName.endsWith(".sitx") || fileName.endsWith(".spt") || fileName.endsWith(".sqx") || fileName.endsWith(".sqz")
                || fileName.endsWith(".tar") || fileName.endsWith(".taz") || fileName.endsWith(".tbz") || fileName.endsWith(".tbz2")
                || fileName.endsWith(".tgz") || fileName.endsWith(".tlz") || fileName.endsWith(".tlz4") || fileName.endsWith(".txz")
                || fileName.endsWith(".uc2");
    }

    /**
     * 图片
     *
     * @param fileName
     * @return
     */
    public static boolean isImage(String fileName) {
        return fileName.endsWith(".psd") || fileName.endsWith(".pdd") || fileName.endsWith(".psdt") || fileName.endsWith(".psb")
                || fileName.endsWith(".bmp") || fileName.endsWith(".rle") || fileName.endsWith(".dib") || fileName.endsWith(".gif")
                || fileName.endsWith(".dcm") || fileName.endsWith(".dc3") || fileName.endsWith(".dic") || fileName.endsWith(".eps")
                || fileName.endsWith(".iff") || fileName.endsWith(".tdi") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                || fileName.endsWith(".jpg") || fileName.endsWith(".jpf") || fileName.endsWith(".jpx") || fileName.endsWith(".jp2")
                || fileName.endsWith(".j2c") || fileName.endsWith(".j2k") || fileName.endsWith(".jpc") || fileName.endsWith(".jps")
                || fileName.endsWith(".pcx") || fileName.endsWith(".pdp") || fileName.endsWith(".raw") || fileName.endsWith(".pxr")
                || fileName.endsWith(".png") || fileName.endsWith(".pbm") || fileName.endsWith(".pgm") || fileName.endsWith(".ppm")
                || fileName.endsWith(".pnm") || fileName.endsWith(".pfm") || fileName.endsWith(".pam") || fileName.endsWith(".sct")
                || fileName.endsWith(".tga") || fileName.endsWith(".vda") || fileName.endsWith(".icb") || fileName.endsWith(".vst")
                || fileName.endsWith(".tif") || fileName.endsWith(".tiff") || fileName.endsWith(".mpo") || fileName.endsWith(".webp ")
                || fileName.endsWith(".ico");
    }

    /**
     * 音频
     *
     * @param fileName
     * @return
     */
    public static boolean isAudio(String fileName) {
        return fileName.endsWith(".aac") || fileName.endsWith(".ac3") || fileName.endsWith(".aif") || fileName.endsWith(".aifc")
                || fileName.endsWith(".aiff") || fileName.endsWith(".amr") || fileName.endsWith(".caf") || fileName.endsWith(".cda")
                || fileName.endsWith(".fiv") || fileName.endsWith(".flac") || fileName.endsWith(".m4a") || fileName.endsWith(".m4b")
                || fileName.endsWith(".oga") || fileName.endsWith(".ogg") || fileName.endsWith(".sf2") || fileName.endsWith(".sfark")
                || fileName.endsWith(".voc") || fileName.endsWith(".wav") || fileName.endsWith(".weba") || fileName.endsWith(".mp3")
                || fileName.endsWith(".mid") || fileName.endsWith(".wma") || fileName.endsWith(".ra");
    }

    /**
     * 音频
     *
     * @param fileName
     * @return
     */
    public static boolean isVideo(String fileName) {
        return fileName.endsWith(".mp4") || fileName.endsWith(".m4v") || fileName.endsWith(".avi") || fileName.endsWith(".mkv")
                || fileName.endsWith(".mov") || fileName.endsWith(".mpg") || fileName.endsWith(".mpeg") || fileName.endsWith(".vob")
                || fileName.endsWith(".ram") || fileName.endsWith(".rm") || fileName.endsWith(".rmvb") || fileName.endsWith(".asf")
                || fileName.endsWith(".wmv") || fileName.endsWith(".webm") || fileName.endsWith(".m2ts") || fileName.endsWith(".movie");
    }

    /**
     * 安装包
     *
     * @param fileName
     * @return
     */
    public static boolean isApk(String fileName) {
        return fileName.endsWith(".apk");
    }

    /**
     * pdf
     *
     * @param fileName
     * @return
     */
    public static boolean isPdf(String fileName) {
        return fileName.endsWith(".pdf");
    }

    /**
     * 文件类型
     *
     * @param mFileName
     * @return
     */
    public static int fileType(String mFileName) {
        String fileName = mFileName.toLowerCase();
        if (isWord(fileName)) { // word
            return 1;
        }
        if (isExcel(fileName)) { // excel
            return 2;
        }

        if (isPPT(fileName)) { // ppt
            return 3;
        }

        if (isCompressedPackage(fileName)) { // 压缩包
            return 4;
        }

        if (isImage(fileName)) { // 图片
            return 5;
        }

        if (isAudio(fileName)) { // 音频
            return 6;
        }

        if (isVideo(fileName)) {  // 视频
            return 7;
        }

        if (isTxt(fileName)) { // 文本
            return 8;
        }

        if (isPdf(fileName)) {
            return 9;
        }

        if (isApk(fileName)) {
            return 10;
        }
        return 0;
    }

    public static int getFileLogo(int type) {
        /**
         * 1. word
         * 2. excel
         * 3. ppt
         * 4. 压缩包
         * 5. 图片
         * 6. 音频
         * 7. 视频
         * 8. 文本
         *
         * 默认 文件夹
         */
        int drawableRes = R.drawable.icon_gho;
        switch (type) {

            case 1:
                drawableRes = R.drawable.icon_docx;
                break;

            case 2:
                drawableRes = R.drawable.icon_xls;
                break;


            case 3:
                drawableRes = R.drawable.icon_ppt;
                break;

            case 4:
                drawableRes = R.drawable.icon_zip;
                break;

            case 5:
                drawableRes = R.drawable.icon_jpg;
                break;

            case 6:
                drawableRes = R.drawable.icon_mp3;
                break;

            case 7:
                drawableRes = R.drawable.icon_mp4;
                break;

            case 8:
                drawableRes = R.drawable.icon_txt;
                break;

            case 9:
                drawableRes = R.drawable.icon_pdf;
                break;

            default:
                drawableRes = R.drawable.icon_gho;
        }
        return drawableRes;
    }

    public static int getFileBigLogo(int type) {
        /**
         * 1. word
         * 2. excel
         * 3. ppt
         * 4. 压缩包
         * 5. 图片
         * 6. 音频
         * 7. 视频
         * 8. 文本
         *
         * 默认 文件夹
         */
        int drawableRes = R.drawable.icon_gho;
        switch (type) {

            case 1:
                drawableRes = R.drawable.icon_docx_big;
                break;

            case 2:
                drawableRes = R.drawable.icon_xls_big;
                break;


            case 3:
                drawableRes = R.drawable.icon_ppt_big;
                break;

            case 4:
                drawableRes = R.drawable.icon_zip_big;
                break;

            case 5:
                drawableRes = R.drawable.icon_jpg_big;
                break;

            case 6:
                drawableRes = R.drawable.icon_mp3_big;
                break;

            case 7:
                drawableRes = R.drawable.icon_mp4_big;
                break;

            case 8:
                drawableRes = R.drawable.icon_txt_big;
                break;

            case 9:
                drawableRes = R.drawable.icon_pdf_big;
                break;

            default:
                drawableRes = R.drawable.icon_gho_big;
        }
        return drawableRes;
    }

    /**
     * 文件类型
     *
     * @param fileType
     * @return
     */
    public static String getFileUriType(int fileType) {
        String type = "*/*";
        switch (fileType) {
            case 1:
                type = "application/msword";
                break;

            case 2:
                type = "application/vnd.ms-excel";
                break;


            case 3:
                type = "application/vnd.ms-powerpoint";
                break;

            case 4:
                type = "application/x-gzip";
                break;

            case 5:
                type = "image/*";
                break;

            case 6:
                type = "audio/*";
                break;

            case 7:
                type = "video/*";
                break;

            case 8:
                type = "text/plain";
                break;

            case 9:
                type = "application/pdf";
                break;

            case 10:
                type = "application/vnd.android.package-archive";
                break;

            default:
                type = "*/*";
        }
        return type;
    }

    /**
     * 打开文件
     */
    public static void openFile(String filePth) {
        File file = new File(filePth);
        Uri imageUri = FileProvider.getUriForFile(LibLoader.getCurrentActivity(), "com.zhiting.clouddisk.provider", file);
        String type = FileTypeUtil.getFileUriType(FileTypeUtil.fileType(file.getName()));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(imageUri, type);
        LibLoader.getCurrentActivity().startActivity(Intent.createChooser(intent, UiUtil.getString(R.string.home_please_select_software_to_open)));
    }
}
