package com.zhiting.clouddisk.adapter;


import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.networklib.utils.UiUtil;

/**
 * 磁盘
 */
public class DiskAdapter extends BaseQuickAdapter<DiskBean, BaseViewHolder> {

    public DiskAdapter() {
        super(R.layout.item_disk);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiskBean item) {
        helper.addOnClickListener(R.id.tvAdd);
        ConstraintLayout clParent = helper.getView(R.id.clParent);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) clParent.getLayoutParams();
        layoutParams.setMargins(UiUtil.getDimens(R.dimen.dp_15), 0, helper.getAdapterPosition() == getData().size()-1 ? UiUtil.getDimens(R.dimen.dp_15) : 0, 0);
        clParent.setLayoutParams(layoutParams);
        int drawableRes = R.drawable.icon_disk_blue_bg;
        int textColor = R.color.color_427AED;
        switch (helper.getAdapterPosition()%4){
            case 0:
                drawableRes = R.drawable.icon_disk_blue_bg;
                textColor = R.color.color_427AED;
                break;
            case 1:
                drawableRes = R.drawable.icon_disk_red_bg;
                textColor = R.color.color_FF7E6B;
                break;
            case 2:
                drawableRes = R.drawable.icon_disk_yellow;
                textColor = R.color.color_FEB447;
                break;
            case 3:
                drawableRes = R.drawable.icon_disk_green;
                textColor = R.color.color_47D4AE;
                break;

        }
        clParent.setBackgroundResource(drawableRes);
        TextView tvAdd = helper.getView(R.id.tvAdd);
        tvAdd.setTextColor(UiUtil.getColor(textColor));
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvSize, FileUtil.getReadableFileSize(item.getCapacity()));
    }
}
