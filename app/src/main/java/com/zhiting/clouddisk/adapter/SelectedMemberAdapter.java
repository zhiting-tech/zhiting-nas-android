package com.zhiting.clouddisk.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.AccessibleMemberBean;
import com.zhiting.networklib.widget.CircleImageView;

import java.util.List;

public class SelectedMemberAdapter extends BaseQuickAdapter<AccessibleMemberBean, BaseViewHolder> {

    public SelectedMemberAdapter() {
        super(R.layout.item_selected_member);
    }

    @Override
    protected void convert(BaseViewHolder helper, AccessibleMemberBean item) {
        helper.addOnClickListener(R.id.tvRead);
        helper.addOnClickListener(R.id.tvWrite);
        helper.addOnClickListener(R.id.tvDel);
        helper.addOnClickListener(R.id.ivClose);
        CircleImageView ivLogo = helper.getView(R.id.ivLogo);
        TextView tvName = helper.getView(R.id.tvName);
        TextView tvRead = helper.getView(R.id.tvRead);
        TextView tvWrite = helper.getView(R.id.tvWrite);
        TextView tvDel = helper.getView(R.id.tvDel);
        tvName.setText(item.getNickname());
        tvRead.setSelected(item.getRead() == 1);
        tvWrite.setSelected(item.getWrite() == 1);
        tvDel.setSelected(item.getDeleted() == 1);

    }
}
