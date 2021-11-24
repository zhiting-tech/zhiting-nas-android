package com.zhiting.clouddisk.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.OperatePermissionBean;

public class OperatePermissionAdapter extends BaseQuickAdapter<OperatePermissionBean, BaseViewHolder> {

    public OperatePermissionAdapter() {
        super(R.layout.item_operate_permission);
    }

    @Override
    protected void convert(BaseViewHolder helper, OperatePermissionBean item) {

        ConstraintLayout clParent = helper.getView(R.id.clParent);
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        TextView tvName = helper.getView(R.id.tvName);

        clParent.setBackgroundResource(item.isSelected() ? R.drawable.shape_stroke_ff6d57_c4 : R.drawable.shape_stroke_a2a7ae_c4);
        ivSelected.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
        ivLogo.setImageResource(item.getDrawable());
        tvName.setText(item.getName());
        ivLogo.setSelected(item.isSelected());
        tvName.setSelected(item.isSelected());
        clParent.setEnabled(item.isEnabled());
        clParent.setAlpha(item.isEnabled() ? 1 : 0.5f);
    }

    /**
     * 检查是否有一个选中
     * @return
     */
    public boolean checkOneSelected(){
        for (OperatePermissionBean operatePermissionBean : getData()){
            if (operatePermissionBean.isSelected()){
                return true;
            }
        }
        return false;
    }
}
