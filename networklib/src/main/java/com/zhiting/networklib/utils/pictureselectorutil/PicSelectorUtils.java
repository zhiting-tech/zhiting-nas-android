package com.zhiting.networklib.utils.pictureselectorutil;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.zhiting.networklib.R;

import java.util.List;


/**
 * author       : jim
 * time         : 2021-04-14 10:51
 * desc         : 文件选择
 * version      : 1.0.0
 */
public class PicSelectorUtils {

    /**
     * 根据选择文件类型打开
     * @param activity
     * @param type  TYPE_ALL = 0;
     *              TYPE_IMAGE = 1;
     *              TYPE_VIDEO = 2;
     * @param listener
     */
    public static void openFileType(Activity activity, int type, OnResultCallbackListener listener){
        switch (type){
            case PictureConfig.TYPE_IMAGE:
                openAlbumPicSelector(activity, listener);
                break;

            case PictureConfig.TYPE_VIDEO:
                openVideoPicSelector(activity, listener);
                break;

            case PictureConfig.TYPE_ALL:
                openAllPicSelector(activity, listener);
                break;
        }
    }

    /**
     * 打开图片
     * @param activity
     * @param listener
     */
    public static void openAlbumPicSelector(Activity activity, OnResultCallbackListener listener){
        openAlbumPicSelector(activity, 3, 9, PictureConfig.MULTIPLE,PictureMimeType.ofImage(), listener);
    }

    /**
     * 打开视频
     * @param activity
     * @param listener
     */
    public static void openVideoPicSelector(Activity activity, OnResultCallbackListener listener){
        openAlbumPicSelector(activity, 3, 9, PictureConfig.MULTIPLE, PictureMimeType.ofVideo(), listener);
    }

    /**
     * 打开所有文件
     * @param activity
     * @param listener
     */
    public static void openAllPicSelector(Activity activity, OnResultCallbackListener listener){
        openAlbumPicSelector(activity, 3, 9, PictureConfig.MULTIPLE, PictureMimeType.ofAll(), listener);
    }

    public static void openAlbumPicSelector(Activity activity, int spanCount, int maxNum, int selectModel, int pictureMimeType,  OnResultCallbackListener listener) {
        PictureSelectionModel pictureSelectionModel = PictureSelector.create(activity)
                .openGallery(pictureMimeType)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .imageSpanCount(spanCount)// 每行显示个数
                .maxSelectNum(maxNum)
                .maxVideoSelectNum(maxNum)
                //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 设置相册Activity方向，不设置默认使用系统
                .selectionMode(selectModel)// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .cutOutQuality(60)// 裁剪输出质量 默认100
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                .isGif(true)// 是否显示gif图片
                .circleDimmedLayer(false)// 是否圆形裁剪
                .isOpenClickSound(false)// 是否开启点击声音
                .isCompress(true)// 是否压缩
                .compressQuality(60)// 图片压缩后输出质量 0~ 100
                .synOrAsy(false)//同步true或异步false 压缩 默认同步
                .isAndroidQTransform(true);
        pictureSelectionModel.forResult(listener);
    }

    /**
     * 预览图片
     *
     * @param activity
     * @param position
     * @param selectList
     */
    public static void openPreviewImages(Activity activity, int position, List<LocalMedia> selectList) {
        PictureSelector.create(activity)
                .themeStyle(R.style.picture_default_style) // xml设置主题
                //.setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                //.setPictureWindowAnimationStyle(animationStyle)// 自定义页面启动动画
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isNotPreviewDownload(true)// 预览图片长按是否可以下载
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义播放回调控制，用户可以使用自己的视频播放界面
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .openExternalPreview(position, selectList);
    }
}
