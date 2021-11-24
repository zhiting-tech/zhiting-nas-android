package com.zhiting.clouddisk.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.constant.Constant;

public class FileNavigationAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {

    public FileNavigationAdapter() {
        super(R.layout.item_file_navigation);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileBean item) {
        String name =  item.getName();
        helper.setText(R.id.tvName, TextUtils.isEmpty(name) ? Constant.HOME_NAME :  name);
        ImageView imageView = helper.getView(R.id.ivNext);
        imageView.setVisibility(helper.getAdapterPosition() < getData().size()-1 ? View.VISIBLE : View.GONE);
    }
}
