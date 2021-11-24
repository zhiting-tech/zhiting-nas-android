package com.zhiting.clouddisk.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.KeyValBean;


public class KeyValueAdapter extends BaseQuickAdapter<KeyValBean, BaseViewHolder> {

    public KeyValueAdapter() {
        super(R.layout.item_key_value);
    }

    @Override
    protected void convert(BaseViewHolder helper, KeyValBean item) {
        helper.setText(R.id.tvKey, item.getKey())
                .setText(R.id.tvValue, item.getValue());
    }
}
