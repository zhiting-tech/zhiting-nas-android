package com.zhiting.clouddisk.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.MineFunctionBean;

/**
 * 我的功能入口
 */
public class MineFunctionAdapter extends BaseQuickAdapter<MineFunctionBean, BaseViewHolder> {

    public MineFunctionAdapter() {
        super(R.layout.item_mine_function);
    }

    @Override
    protected void convert(BaseViewHolder helper, MineFunctionBean item) {
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        View viewLine = helper.getView(R.id.viewLine);
        viewLine.setVisibility(helper.getAdapterPosition() == getData().size() - 1 ? View.INVISIBLE : View.VISIBLE);
        ivLogo.setImageResource(item.getLogo());
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setEnabled(item.isEnable());
        helper.setText(R.id.tvName, item.getName());
    }
}
