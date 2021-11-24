package com.zhiting.clouddisk.adapter;


import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.MemberBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员列表适配器
 */
public class MemberAdapter extends BaseQuickAdapter<MemberBean.UsersBean, BaseViewHolder> {

    public MemberAdapter() {
        super(R.layout.item_member);
    }

    @Override
    protected void convert(BaseViewHolder helper, MemberBean.UsersBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getNickname());
        tvName.setSelected(item.isSelected());
    }

    /**
     * 设置全选/全不选
     * @param selected
     */
    public void setAllSelected(boolean selected){
        for (MemberBean.UsersBean usersBean : getData()){
            usersBean.setSelected(selected);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取选中的数据
     * @return
     */
    public List<MemberBean.UsersBean> getSelectedUsers(){
        List<MemberBean.UsersBean> data = new ArrayList<>();
        for (MemberBean.UsersBean usersBean : getData()){
            if (usersBean.isSelected()){
                data.add(usersBean);
            }
        }
        return data;
    }

    /**
     * 是否全部选择
     * @return
     */
    public boolean isAllSelected(){
        for (MemberBean.UsersBean usersBean : getData()){
            if (!usersBean.isSelected()){
                return false;
            }
        }
        return true;
    }

    /**
     * 是否全部未选择
     * @return
     */
    public boolean isAllUnselected(){
        for (MemberBean.UsersBean usersBean : getData()){
            if (usersBean.isSelected()){
                return false;
            }
        }
        return true;
    }
}
