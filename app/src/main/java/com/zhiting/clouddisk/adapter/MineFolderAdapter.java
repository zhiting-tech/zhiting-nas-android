package com.zhiting.clouddisk.adapter;


import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.FolderBean;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;

/**
 * 文件夹列表适配器
 */
public class MineFolderAdapter extends BaseQuickAdapter<FolderBean, BaseViewHolder> {

    public MineFolderAdapter() {
        super(R.layout.item_folder);
    }

    @Override
    protected void convert(BaseViewHolder helper, FolderBean item) {
        helper.addOnClickListener(R.id.ivThreeDot);
        helper.addOnClickListener(R.id.tvOperate);
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvDistrict, item.getPool_name())
                .setText(R.id.tvMember, item.getPersons());
        LinearLayout llResult = helper.getView(R.id.llResult);
        ImageView ivEncrypt = helper.getView(R.id.ivEncrypt);
        ImageView ivStatus = helper.getView(R.id.ivStatus);
        ImageView ivThreeDot = helper.getView(R.id.ivThreeDot);
        TextView tvTips = helper.getView(R.id.tvTips);
        TextView tvOperate = helper.getView(R.id.tvOperate);
        int is_encrypt = item.getIs_encrypt();
        ivEncrypt.setVisibility(is_encrypt == 1 ? View.VISIBLE : View.GONE);
        ivThreeDot.setSelected(is_encrypt == 1);
        llResult.setVisibility(View.GONE);
        ivStatus.setVisibility(View.GONE);
        ivThreeDot.setVisibility(View.GONE);
        String status = item.getStatus();
        if (TextUtils.isEmpty(status)){  // 空则代表没有异步状态
            ivThreeDot.setVisibility(View.VISIBLE);
        }else {
            String name = item.getName();
            name = name.length()<4 ? name : (name.substring(0, 2)+"...");
            switch (status){
                case Constant
                        .FOLDER_UPDATING:  // 修改中
                    ivStatus.setVisibility(View.VISIBLE);
                    ivStatus.setImageResource(R.drawable.icon_folder_updating);
                    break;

                case Constant
                        .FOLDER_UPDATE_FAIL:  // 修改失败
                    llResult.setVisibility(View.VISIBLE);
                    tvTips.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_update_fail), name));
                    tvOperate.setText(UiUtil.getString(R.string.common_confirm));
                    break;


                case Constant
                        .FOLDER_DELETING:  //  删除中
                    ivStatus.setVisibility(View.VISIBLE);
                    ivStatus.setImageResource(R.drawable.icon_folder_deleting);
                    break;

                case Constant
                        .FOLDER_DELETE_FAIL:  // 删除失败
                    llResult.setVisibility(View.VISIBLE);
                    tvTips.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_del_fail), name));
                    tvOperate.setText(UiUtil.getString(R.string.mine_retry));
                    break;
            }
        }
    }
}
