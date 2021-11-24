package com.zhiting.clouddisk.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;

/**
 * 存储池详情的存储池分区列表
 */
public class StoragePoolDetailAdapter extends BaseQuickAdapter<DiskBean, BaseViewHolder> {

    public StoragePoolDetailAdapter() {
        super(R.layout.item_storage_pool_detail);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiskBean item) {

        helper.addOnClickListener(R.id.tvRetry);
        helper.addOnClickListener( R.id.tvCancel);
        String capacity = FileUtil.getReadableFileSize(item.getUse_capacity())+"/"+FileUtil.getReadableFileSize(item.getCapacity());
        String allCapacity = UiUtil.getString(R.string.mine_all_size)+FileUtil.getReadableFileSizeSaveTow(item.getCapacity());
        String availableCapacity = UiUtil.getString(R.string.mine_available_capacity)+FileUtil.getReadableFileSizeSaveTow(item.getCapacity() - item.getUse_capacity());
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvAllSize, allCapacity)
                .setText(R.id.tvAvailable, availableCapacity);

        ProgressBar rb = helper.getView(R.id.rb);
        int progress = item.getCapacity() == 0 ? 100 : (int) (StringUtil.long2Double(item.getUse_capacity())/StringUtil.long2Double(item.getCapacity())*100);
        rb.setProgress(progress);
        rb.setVisibility(View.VISIBLE);

        ImageView ivDot = helper.getView(R.id.ivDot);
        LinearLayout llResult = helper.getView(R.id.llResult);
        TextView tvRetry= helper.getView(R.id.tvRetry);
        TextView tvCancel = helper.getView(R.id.tvCancel);
        TextView tvTips = helper.getView(R.id.tvTips);
        ImageView ivStatus = helper.getView(R.id.ivStatus);
        llResult.setVisibility(View.GONE);
        ivStatus.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
        String status = item.getStatus();
        ivDot.setVisibility(TextUtils.isEmpty(status) ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(status)){
            switch (status){
                case Constant.PARTITION_ADDING:   // 添加中
                case Constant.PARTITION_UPDATING: // 修改中
                case Constant.PARTITION_DELETING: // 删除中
                    ivStatus.setVisibility(View.VISIBLE);
                    llResult.setVisibility(View.GONE);
                    if (status.equals(Constant.PARTITION_DELETING)) {  // 删除中
                        ivStatus.setImageResource(R.drawable.icon_folder_deleting);
                    }else  if (status.equals(Constant.PARTITION_ADDING)) {  // 添加中
                        ivStatus.setImageResource(R.drawable.icon_adding);
                    }else  if (status.equals(Constant.PARTITION_UPDATING)) {  // 修改中
                        ivStatus.setImageResource(R.drawable.icon_folder_updating);
                    }

                    break;

                case Constant.PARTITION_ADD_FAIL:  // 添加失败
                case Constant.PARTITION_UPDATE_FAIL:  // 修改失败
                case Constant.PARTITION_DELETE_FAIL:  // 删除失败
                    ivStatus.setVisibility(View.GONE);
                    tvRetry.setVisibility(View.VISIBLE);
                    llResult.setVisibility(View.VISIBLE);
                    if (status.equals(Constant.PARTITION_DELETE_FAIL)){  // 删除状态
                        tvCancel.setVisibility(View.GONE);
                        tvTips.setText(UiUtil.getString(R.string.mine_partition_del_fail));
                    }else {
                        tvCancel.setVisibility(View.VISIBLE);
                        rb.setVisibility(View.GONE);
                        if (status.equals(Constant.PARTITION_ADD_FAIL)){  // 添加失败
                            tvCancel.setText(UiUtil.getString(R.string.mine_cancel_add));
                            tvTips.setText(UiUtil.getString(R.string.mine_partition_add_fail));
                        }else  if (status.equals(Constant.PARTITION_UPDATE_FAIL)){  // 修改失败
                            tvCancel.setText(UiUtil.getString(R.string.mine_cancel_update));
                            tvTips.setText(UiUtil.getString(R.string.mine_partition_update_fail));
                        }
                    }

                    break;

            }
        }
    }
}
