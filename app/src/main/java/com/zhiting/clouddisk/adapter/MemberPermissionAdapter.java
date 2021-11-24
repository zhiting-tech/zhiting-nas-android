package com.zhiting.clouddisk.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;


/**
 * 新增/修改文件 成员的权限
 */
public class MemberPermissionAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MemberPermissionAdapter() {
        super(R.layout.item_member_permission);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvName, item);
    }
}
