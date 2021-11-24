package com.zhiting.clouddisk.adapter;

import android.widget.ImageView;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.FileOperateBean;


public class UploadFileAdapter extends BaseQuickAdapter<FileOperateBean, BaseViewHolder> {

    public UploadFileAdapter() {
        super(R.layout.item_upload_file);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileOperateBean item) {
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        ivLogo.setImageResource(item.getDrawable());
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
    }
}
