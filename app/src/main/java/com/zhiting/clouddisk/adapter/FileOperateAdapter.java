package com.zhiting.clouddisk.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.FileOperateBean;

import java.util.List;

/**
 * 文件夹详情列表适配器
 */
public class FileOperateAdapter extends BaseQuickAdapter<FileOperateBean, BaseViewHolder> {

    public FileOperateAdapter() {
        super(R.layout.item_file_operate);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileOperateBean item) {
        LinearLayout llParent = helper.getView(R.id.llParent);
        llParent.setAlpha(item.isEnabled() ? 1 : 0.5f);
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        ivLogo.setImageResource(item.getDrawable());
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
    }
}
