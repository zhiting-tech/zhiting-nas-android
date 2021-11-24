package com.zhiting.clouddisk.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.mine.AccessibleMemberBean;
import com.zhiting.networklib.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

public class FolderMemberAdapter extends BaseQuickAdapter<AccessibleMemberBean, BaseViewHolder> {

    public FolderMemberAdapter() {
        super(R.layout.item_folder_member);
    }

    @Override
    protected void convert(BaseViewHolder helper, AccessibleMemberBean item) {
        helper.addOnClickListener(R.id.ivEdit);
        helper.addOnClickListener(R.id.ivDel);
        helper.setText(R.id.tvName, item.getNickname());
        RecyclerView rvPermission = helper.getView(R.id.rvPermission);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvPermission.setLayoutManager(layoutManager);
        MemberPermissionAdapter memberPermissionAdapter = new MemberPermissionAdapter();
        rvPermission.setAdapter(memberPermissionAdapter);
        List<String> data = new ArrayList<>();
        if (item.getRead() == 1){
            data.add(UiUtil.getString(R.string.mine_accessible_read));
        }
        if (item.getWrite() == 1){
            data.add(UiUtil.getString(R.string.mine_accessible_write));
        }
        if (item.getDeleted() == 1){
            data.add(UiUtil.getString(R.string.mine_accessible_del));
        }
        memberPermissionAdapter.setNewData(data);
    }
}
