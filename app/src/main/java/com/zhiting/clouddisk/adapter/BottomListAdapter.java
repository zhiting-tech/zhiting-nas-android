package com.zhiting.clouddisk.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.BottomListBean;
import com.zhiting.networklib.utils.UiUtil;

import java.util.List;

public class BottomListAdapter extends BaseQuickAdapter<BottomListBean, BaseViewHolder> {

    public BottomListAdapter() {
        super(R.layout.item_bottom_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, BottomListBean item) {
        View viewLine = helper.getView(R.id.viewLine);
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setPadding(0, helper.getAdapterPosition() == 0 ? UiUtil.getDimens(R.dimen.dp_11) : UiUtil.getDimens(R.dimen.dp_21), 0, UiUtil.getDimens(R.dimen.dp_21));
        tvName.setSelected(item.isSelected());
        viewLine.setVisibility(helper.getAdapterPosition() < getData().size()-1 ? View.VISIBLE : View.GONE);
    }
}
