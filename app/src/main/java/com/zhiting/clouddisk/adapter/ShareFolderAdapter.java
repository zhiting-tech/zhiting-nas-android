package com.zhiting.clouddisk.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.FileBean;

/**
 * 共享文件夹
 */
public class ShareFolderAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {

    public ShareFolderAdapter() {
        super(R.layout.item_share_folder);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileBean item) {
        helper.setText(R.id.tvName, item.getName());
    }
}
