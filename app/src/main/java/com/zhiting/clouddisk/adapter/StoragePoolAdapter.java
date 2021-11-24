package com.zhiting.clouddisk.adapter;


import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;


/**
 * 存储池
 */
public class StoragePoolAdapter extends BaseQuickAdapter<StoragePoolDetailBean, BaseViewHolder> {

    public StoragePoolAdapter() {
        super(R.layout.item_storage_pool);
    }

    @Override
    protected void convert(BaseViewHolder helper, StoragePoolDetailBean item) {
        helper.addOnClickListener(R.id.ivDot);
        helper.addOnClickListener(R.id.tvRetry);
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvSize, FileUtil.getReadableFileSize(item.getCapacity()));

        ImageView ivDot = helper.getView(R.id.ivDot);
        LinearLayout llResult = helper.getView(R.id.llResult);
        TextView tvTips= helper.getView(R.id.tvTips);
        ImageView ivStatus = helper.getView(R.id.ivStatus);
        llResult.setVisibility(View.GONE);
        ivStatus.setVisibility(View.GONE);
        String status = item.getStatus();
        ivDot.setVisibility(TextUtils.isEmpty(status) ? View.VISIBLE : View.GONE);
        if (status!=null){
            if (status.equals(Constant.STORAGE_POOL_DELETING)){  // 删除中
                ivStatus.setVisibility(View.VISIBLE);
            }else if (status.equals(Constant.STORAGE_POOL_DELETE_FAIL)){  // 删除失败
                tvTips.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_del_storage_pool_fail), item.getName()));
                llResult.setVisibility(View.VISIBLE);
            }
        }
    }
}
