package com.zhiting.clouddisk.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.UnitUtil;

import java.util.List;

public class DownloadFailAdapter extends BaseQuickAdapter<DownLoadFileBean, BaseViewHolder> {

    public DownloadFailAdapter(List<DownLoadFileBean> list) {
        super(R.layout.item_download_fail, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownLoadFileBean item) {
        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvSize, UnitUtil.getFormatSize(item.getSize()));
        if (item.getType().equalsIgnoreCase("dir")) {
            helper.setImageResource(R.id.ivLogo, R.drawable.icon_file);
        } else {
            int fileType = FileTypeUtil.fileType(item.getName());
            int drawableRes = FileTypeUtil.getFileLogo(fileType);
            helper.setImageResource(R.id.ivLogo, drawableRes);
        }

        //文件状态下载状态 0未开始 1下载中 2已暂停 3 成功 4下载失败
        if (item.getStatus() == 0 || item.getStatus() == 2) {
            helper.setText(R.id.tvStatus, UiUtil.getString(R.string.home_wait_download));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_A2A7AE));
        } else if (item.getStatus() == 1) {
            helper.setText(R.id.tvStatus, UnitUtil.getFormatSize(item.getSpeeds()));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_A2A7AE));
        } else if (item.getStatus() == 4) {//下载失败
            helper.setText(R.id.tvStatus, UiUtil.getString(R.string.home_download_fail));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_ff0000));
        }
        helper.addOnClickListener(R.id.tvDelete);
    }
}
