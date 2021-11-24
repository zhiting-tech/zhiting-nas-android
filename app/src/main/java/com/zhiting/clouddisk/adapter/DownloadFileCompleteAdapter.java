package com.zhiting.clouddisk.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UnitUtil;

public class DownloadFileCompleteAdapter extends BaseQuickAdapter<DownLoadFileBean, BaseViewHolder> {

    public DownloadFileCompleteAdapter() {
        super(R.layout.item_upload_complete);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownLoadFileBean item) {
        if (item != null) {
            if (item.getType().equalsIgnoreCase("dir")) {
                helper.setImageResource(R.id.ivLogo, R.drawable.icon_file);
            } else {
                int fileType = FileTypeUtil.fileType(item.getName());
                int drawableRes = FileTypeUtil.getFileLogo(fileType);
                helper.setImageResource(R.id.ivLogo, drawableRes);
            }
            helper.setText(R.id.tvName, item.getName());
            helper.setText(R.id.tvSize, UnitUtil.getFormatSize(item.getSize()));
            helper.setText(R.id.tvTime, TimeFormatUtil.long2String(item.getCreate_time(), TimeFormatUtil.DATE_FORMAT));
        }
    }
}
