package com.zhiting.clouddisk.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.BackupBean;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.UnitUtil;

import java.util.List;

/**
 * 备份失败和备份中
 */
public class BackupAdapter extends BaseQuickAdapter<UploadFileBean, BaseViewHolder> {

    public BackupAdapter() {
        super(R.layout.item_back);
    }

//    @Override
//    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
//        super.onBindViewHolder(holder, position, payloads);
//        if (payloads.isEmpty()) {
//            onBindViewHolder(holder, position);
//        } else if (holder != null) {
//            UploadFileBean fileBean = (UploadFileBean) payloads.get(0);
//            if (fileBean != null) {
//                setStatus(holder, fileBean);
//            }
//        }
//    }

    @Override
    protected void convert(BaseViewHolder helper, UploadFileBean item) {
        if (item == null) return;
        int fileType = FileTypeUtil.fileType(item.getName());
        int drawableRes = FileTypeUtil.getFileLogo(fileType);
        helper.setImageResource(R.id.ivLogo, drawableRes);
        helper.setText(R.id.tvName, item.getName());

        setStatus(helper,item);

        helper.addOnClickListener(R.id.root);
        helper.addOnClickListener(R.id.ivStatus);
        helper.addOnClickListener(R.id.tvDelete);
    }

    //文件状态0未开始 1上传中 2暂停 3已完成 4上传失败 5生成临时文件中
    private void setStatus(BaseViewHolder helper, UploadFileBean item) {
        String uploaded = UnitUtil.getFormatSize(item.getUpload()) + "/" + UnitUtil.getFormatSize(item.getSize());
        helper.setText(R.id.tvSpeed, uploaded);
        int progress = (int) ((item.getUpload() * 1.0 / item.getSize()) * 100);
        helper.setProgress(R.id.rb, progress);
        helper.setText(R.id.tvName, item.getName());
        if (item.getStatus() == 2) {
            helper.setText(R.id.tvStatus, UiUtil.getString(R.string.home_wait_upload));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_A2A7AE));
            helper.setImageResource(R.id.ivStatus, R.drawable.icon_upload_blue);
        } else if (item.getStatus() == 0 || item.getStatus() == 1) {
            helper.setText(R.id.tvStatus, UnitUtil.getFormatsSize(item.getSpeeds()));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_A2A7AE));
            helper.setImageResource(R.id.ivStatus, R.drawable.icon_pause);
        } else if (item.getStatus() == 4) {
            helper.setText(R.id.tvStatus, UiUtil.getString(R.string.home_wait_upload_error));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_ff0000));
            helper.setImageResource(R.id.ivStatus, R.drawable.icon_refresh_blue);
        } else {
            helper.setText(R.id.tvStatus, UiUtil.getString(R.string.home_wait_upload));
            helper.setTextColor(R.id.tvStatus, UiUtil.getColor(R.color.color_A2A7AE));
            helper.setImageResource(R.id.ivStatus, R.drawable.icon_upload_blue);
        }
    }
}
