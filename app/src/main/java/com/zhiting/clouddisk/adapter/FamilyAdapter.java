package com.zhiting.clouddisk.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.HomeCompanyBean;


/**
 * 家庭列表
 */
public class FamilyAdapter extends BaseQuickAdapter<HomeCompanyBean, BaseViewHolder> {

    public FamilyAdapter() {
        super(R.layout.item_family);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeCompanyBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setSelected(item.isSelected());
        ImageView ivChecked = helper.getView(R.id.ivChecked);
        ivChecked.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
        View viewLine = helper.getView(R.id.viewLine);
        viewLine.setVisibility(helper.getAdapterPosition() == getData().size()-1 ? View.GONE : View.VISIBLE);

    }
}
