package com.zhiting.clouddisk.adapter;

import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.imageutil.GlideUtil;

import java.io.File;

public class DownDetailAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    public DownDetailAdapter() {
        super(R.layout.item_down_detail);
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        ImageView imageView = helper.getView(R.id.ivLogo);
        int drawableRes = R.drawable.icon_file;
        if (FileUtil.isFile(item)) {
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
             */
            int fileType = FileTypeUtil.fileType(item.getName());
            drawableRes = FileTypeUtil.getFileLogo(fileType);
            if (fileType == 5 || fileType == 7) {
                Glide.with(UiUtil.getContext()).load(item.getAbsolutePath()).into(imageView);
            } else {
                imageView.setImageResource(drawableRes);
            }
        } else {
            imageView.setImageResource(drawableRes);
        }

        long size = FileUtil.getFileLength(item);
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvTime, TimeFormatUtil.long2String(item.lastModified(), TimeFormatUtil.DATE_FORMAT))
                .setText(R.id.tvSize, FileUtil.isFile(item) ? FileUtil.getReadableFileSize(size) : "");
    }
}
