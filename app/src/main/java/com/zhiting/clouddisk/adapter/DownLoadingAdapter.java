package com.zhiting.clouddisk.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.UnitUtil;

import java.util.List;

public class DownLoadingAdapter extends BaseQuickAdapter<DownLoadFileBean, BaseViewHolder> {

    public DownLoadingAdapter() {
        super(R.layout.item_downloading);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder helper, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(helper, position, payloads);
        if (payloads.isEmpty()) {
            onBindViewHolder(helper, position);
        } else if (helper != null) {
            DownLoadFileBean fileBean = (DownLoadFileBean) payloads.get(0);
            if (fileBean != null) {
                setStatus(helper, fileBean);
            }
        }
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

        setStatus(helper, item);
        helper.addOnClickListener(R.id.ivStatus);
        helper.addOnClickListener(R.id.ivError);
        helper.addOnClickListener(R.id.tvDelete);
    }

    //文件状态
    private void setStatus(BaseViewHolder helper, DownLoadFileBean item) {
        String uploaded = UnitUtil.getFormatSize(item.getDownloaded()) + "/" + UnitUtil.getFormatSize(item.getSize());
        helper.setText(R.id.tvSpeed, uploaded);

        int progress = (int) ((item.getDownloaded() * 1.0 / item.getSize()) * 100);
        helper.setProgress(R.id.rb, progress);
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
