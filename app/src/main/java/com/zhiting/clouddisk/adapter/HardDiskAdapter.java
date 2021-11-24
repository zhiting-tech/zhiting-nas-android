package com.zhiting.clouddisk.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.networklib.utils.UiUtil;

import java.util.List;

/**
 * 物理硬盘
 */
public class HardDiskAdapter extends BaseQuickAdapter<DiskBean, BaseViewHolder> {

    public HardDiskAdapter() {
        super(R.layout.item_hard_disk);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiskBean item) {
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvSize, FileUtil.getReadableFileSize(item.getCapacity()));
    }
}
