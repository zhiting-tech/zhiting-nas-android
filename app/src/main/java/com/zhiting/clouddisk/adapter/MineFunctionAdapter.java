package com.zhiting.clouddisk.adapter;

import static com.zhiting.clouddisk.entity.mine.MineFunctionBean.CLEAR_CACHE;

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
        ImageView ivArrow = helper.getView(R.id.ivArrow);
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        View viewLine = helper.getView(R.id.viewLine);
        viewLine.setVisibility(helper.getAdapterPosition() == getData().size() - 1 ? View.INVISIBLE : View.VISIBLE);
        TextView tvName = helper.getView(R.id.tvName);
        TextView tvCacheSize = helper.getView(R.id.tvCacheSize);

        tvName.setEnabled(item.isEnable());
        ivLogo.setImageResource(item.getLogo());
        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvCacheSize, item.getSize());

        if (item == CLEAR_CACHE) {
            tvCacheSize.setVisibility(View.VISIBLE);
            ivArrow.setVisibility(View.GONE);
        } else {
            tvCacheSize.setVisibility(View.GONE);
            ivArrow.setVisibility(View.VISIBLE);
        }
    }
}
