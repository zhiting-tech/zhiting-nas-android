package com.zhiting.clouddisk.adapter;

import android.widget.ImageView;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.networklib.utils.TimeFormatUtil;

import java.io.File;

public class DownDetailAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    public DownDetailAdapter() {
        super(R.layout.item_down_detail);
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        int drawableRes = R.drawable.icon_gho;
        if (FileUtil.isFile(item)){
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
        }else {
            drawableRes = R.drawable.icon_file;
        }

        ImageView imageView = helper.getView(R.id.ivLogo);
        imageView.setImageResource(drawableRes);
        long size = FileUtil.getFileLength(item);
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvTime, TimeFormatUtil.long2String(item.lastModified(), TimeFormatUtil.DATE_FORMAT))
                .setText(R.id.tvSize, FileUtil.isFile(item) ? FileUtil.getReadableFileSize(size) : "");
    }
}
