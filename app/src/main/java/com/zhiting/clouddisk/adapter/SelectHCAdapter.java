package com.zhiting.clouddisk.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.ExtensionTokenListBean;


public class SelectHCAdapter extends BaseQuickAdapter<ExtensionTokenListBean.ExtensionTokenBean, BaseViewHolder> {

    public SelectHCAdapter() {
        super(R.layout.item_selected_hc);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExtensionTokenListBean.ExtensionTokenBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        ImageView ivHead = helper.getView(R.id.ivHead);
        ImageView ivSel = helper.getView(R.id.ivSel);
        tvName.setText(item.getArea_name());
        tvName.setSelected(item.isSelected());
        ivHead.setSelected(item.isSelected());
        ivSel.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
    }

    /**
     * 选中的哪一项
     * @return
     */
    public ExtensionTokenListBean.ExtensionTokenBean getSelectedItem() {
        ExtensionTokenListBean.ExtensionTokenBean extensionTokenBean = null;
        for (ExtensionTokenListBean.ExtensionTokenBean etb : getData()) {
            if (etb.isSelected()) {
                extensionTokenBean = etb;
            }
        }
        return extensionTokenBean;
    }
}
