package com.zhiting.clouddisk.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;

public class ChoosePoolAdapter extends BaseQuickAdapter<StoragePoolDetailBean, BaseViewHolder> {

    public ChoosePoolAdapter() {
        super(R.layout.item_choose_pool);
    }

    @Override
    protected void convert(BaseViewHolder helper, StoragePoolDetailBean item) {
        helper.setText(R.id.tvName, item.getName());
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ivSelected.setSelected(item.isSelected());
    }
}
