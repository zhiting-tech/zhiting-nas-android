package com.zhiting.clouddisk.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.SettingBean;

import java.util.List;

/**
 * 设置
 */
public class SettingAdapter extends BaseQuickAdapter<SettingBean, BaseViewHolder> {

    public SettingAdapter() {
        super(R.layout.item_setting);
    }

    @Override
    protected void convert(BaseViewHolder helper, SettingBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setCompoundDrawablesWithIntrinsicBounds(item.getDrawable(), 0, 0, 0);
    }
}
