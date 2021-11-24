package com.zhiting.clouddisk.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;

import java.io.File;

/**
 * 下载详情导航
 */
public class DownDetailNavigateAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    public DownDetailNavigateAdapter() {
        super(R.layout.item_file_navigation);
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        helper.setText(R.id.tvName, item.getName());
        ImageView imageView = helper.getView(R.id.ivNext);
        imageView.setVisibility(helper.getAdapterPosition() < getData().size()-1 ? View.VISIBLE : View.GONE);
    }
}
