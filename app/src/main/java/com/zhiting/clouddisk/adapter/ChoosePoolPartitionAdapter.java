package com.zhiting.clouddisk.adapter;


import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.DiskBean;


public class ChoosePoolPartitionAdapter extends BaseQuickAdapter<DiskBean, BaseViewHolder> {

    public ChoosePoolPartitionAdapter() {
        super(R.layout.item_choose_pool_partition);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiskBean item) {
        helper.setText(R.id.tvName, item.getName());
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ivSelected.setSelected(item.isSelected());
    }
}
