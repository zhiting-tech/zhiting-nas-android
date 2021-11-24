package com.zhiting.clouddisk.adapter;

import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolMulBean;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;

import java.util.List;

public class StoragePoolMulAdapter extends BaseMultiItemQuickAdapter<StoragePoolDetailBean, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public StoragePoolMulAdapter(List<StoragePoolDetailBean> data) {
        super(data);
        addItemType(StoragePoolMulBean.POOL, R.layout.item_storage_pool_mul);
        addItemType(StoragePoolMulBean.ADD, R.layout.item_mine_pool_add);
    }

    @Override
    protected void convert(BaseViewHolder helper, StoragePoolDetailBean item) {
        switch (helper.getItemViewType()) {
            case StoragePoolMulBean.POOL:
                ConstraintLayout clParent = helper.getView(R.id.clParent);
                ImageView ivSelected = helper.getView(R.id.ivSelected);
                clParent.setSelected(item.isSelected());
                ivSelected.setSelected(ivSelected.isSelected());
                helper.setText(R.id.tvName, item.getName())
                        .setText(R.id.tvSize, StringUtil.getStringFormat(UiUtil.getString(R.string.mine_used_all_capacity), FileUtil.getReadableFileSize(item.getUse_capacity()), FileUtil.getReadableFileSize(item.getCapacity())));
                break;

            case StoragePoolMulBean.ADD:

                break;
        }
    }

    /**
     * 获取选择的数据
     * @return
     */
    public StoragePoolDetailBean getSelectedData(){
        StoragePoolDetailBean storagePoolDetailBean = null;
        for (StoragePoolDetailBean spdb : getData()){
            if (spdb.isSelected()){
                storagePoolDetailBean = spdb;
                break;
            }
        }
        return storagePoolDetailBean;
    }
}
