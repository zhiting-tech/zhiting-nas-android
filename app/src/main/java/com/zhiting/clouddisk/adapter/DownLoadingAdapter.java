package com.zhiting.clouddisk.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.networklib.utils.LogUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.UnitUtil;

public class DownLoadingAdapter extends BaseQuickAdapter<DownLoadFileBean, BaseViewHolder> {

    public DownLoadingAdapter() {
        super(R.layout.item_downloading);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownLoadFileBean item) {
        if (item.getType().equalsIgnoreCase("dir")) {
            helper.setImageResource(R.id.ivLogo, R.drawable.icon_file);
        } else {
            int fileType = FileTypeUtil.fileType(item.getName());
            int drawableRes = FileTypeUtil.getFileLogo(fileType);
            helper.setImageResource(R.id.ivLogo, drawableRes);
        }
        helper.setGone(R.id.ivError, false);
        helper.setText(R.id.tvName, item.getName());
        String uploaded = UnitUtil.getFormatSize(item.getDownloaded()) + "/" + UnitUtil.getFormatSize(item.getSize());
        helper.setText(R.id.tvSpeed, uploaded);
        helper.addOnClickListener(R.id.ivStatus);
        helper.addOnClickListener(R.id.ivError);
        helper.addOnClickListener(R.id.tvStatus);
        int progress = (int) ((item.getDownloaded() * 1.0 / item.getSize())*100);
        helper.setProgress(R.id.rb, progress);

        //文件状态
        if (item.getStatus() == 0 || item.getStatus() == 2) {
            helper.setText(R.id.tvStatus, UiUtil.getString(R.string.home_wait_download));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_A2A7AE));
            helper.setImageResource(R.id.ivStatus, R.drawable.icon_download_blue);
        } else if (item.getStatus() == 1) {
            helper.setText(R.id.tvStatus, UnitUtil.getFormatsSize(item.getSpeeds()));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_A2A7AE));
            helper.setImageResource(R.id.ivStatus, R.drawable.icon_pause);
        } else if (item.getStatus() == 4) {//下载失败
            helper.setText(R.id.tvStatus, UiUtil.getString(R.string.home_download_fail));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_ff0000));
            helper.setImageResource(R.id.ivStatus, R.drawable.icon_refresh_blue);
            if (item.getType().equalsIgnoreCase("dir"))
                helper.setGone(R.id.ivError, true);
        } else {
            helper.setText(R.id.tvStatus, UiUtil.getString(R.string.home_wait_download));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_A2A7AE));
            helper.setImageResource(R.id.ivStatus, R.drawable.icon_download_blue);
        }
    }
}
