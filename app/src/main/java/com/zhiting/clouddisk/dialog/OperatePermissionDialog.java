package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.KeyValueAdapter;
import com.zhiting.clouddisk.entity.KeyValBean;
import com.zhiting.clouddisk.entity.PermissionUserBean;
import com.zhiting.clouddisk.entity.mine.AccessibleMemberBean;
import com.zhiting.clouddisk.entity.mine.OperatePermissionBean;
import com.zhiting.clouddisk.util.SpacesItemDecoration;
import com.zhiting.clouddisk.widget.PileAvertView;
import com.zhiting.networklib.dialog.BaseBottomDialog;
import com.zhiting.networklib.dialog.CommonBaseDialog;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.widget.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户操作权限弹窗
 */
public class OperatePermissionDialog extends CommonBaseDialog {

    private ImageView ivClose;
    private CircleImageView ciAvatar;
    private TextView tvName;
    private RecyclerView rvPermission;
    private TextView tvConfirm;
    private PileAvertView pav;
    private RecyclerView rvNote;

    private OperatePermissionAdapter operatePermissionAdapter;

    private int read;  // 读
    private int write; // 写
    private int del;  // 删

    private boolean originalWrite = true; // 共享者的写权限
    private boolean originalDel = true; // 共享者的删权限

    private boolean checkSaveEnabled;  // 是否需要设置保存按钮可点击/不可地那就

    private List<PermissionUserBean> users;

    public static OperatePermissionDialog getInstance(){
        OperatePermissionDialog operatePermissionDialog = new OperatePermissionDialog();
        return operatePermissionDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_operate_permission;
    }

    @Override
    protected int obtainWidth() {
        return UiUtil.getDimens(R.dimen.dp_345);
    }

    @Override
    protected int obtainHeight() {
        return UiUtil.getDimens(R.dimen.dp_500);
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        read = arguments.getInt("read", 0);
        write = arguments.getInt("write", 0);
        del = arguments.getInt("del", 0);
        originalWrite = arguments.getBoolean("originalWrite");
        originalDel = arguments.getBoolean("originalDel");
        checkSaveEnabled = arguments.getBoolean("checkSaveEnabled");
        users = (List<PermissionUserBean>) arguments.getSerializable("users");
    }

    @Override
    protected void initView(View view) {
        ivClose = view.findViewById(R.id.ivClose);
        ciAvatar = view.findViewById(R.id.ciAvatar);
        tvName = view.findViewById(R.id.tvName);
        rvPermission = view.findViewById(R.id.rvPermission);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        pav = view.findViewById(R.id.pav);
        rvNote = view.findViewById(R.id.rvNote);
        if (CollectionUtil.isNotEmpty(users)) {
            int size = users.size();
            pav.setPileGravity(size>1 ? Gravity.LEFT : Gravity.CENTER_HORIZONTAL);
            tvName.setGravity(size == 1 ? Gravity.CENTER : Gravity.LEFT);
            String name = users.get(0).getName();
            tvName.setText(size == 1 ? name : name + StringUtil.getStringFormat(UiUtil.getString(R.string.mine_people_count), size));
            List<String> data = new ArrayList<>();
            for (int i = 0; i < (size<6 ? size : 5); i++) { // 如果小于6个，展示全，否则只展示前5个
                data.add(users.get(i).getAvatar());
            }
            if (size>=6){ // 大于6的话补一个省略的头像
                data.add("");
            }
            pav.setAvertImages(data);
        }
        initRv();
        initRvNote();
        initListener();

    }


    /**
     * 初始化列表
     */
    private void initRv(){
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        rvPermission.addItemDecoration(spacesItemDecoration);
        rvPermission.setLayoutManager(new GridLayoutManager(getContext(), 3));
        operatePermissionAdapter = new OperatePermissionAdapter();
        rvPermission.setAdapter(operatePermissionAdapter);
        List<OperatePermissionBean> data = new ArrayList<>();
        data.add(new OperatePermissionBean(UiUtil.getString(R.string.mine_accessible_read), R.drawable.selector_read, read == 1));
        data.add(new OperatePermissionBean(UiUtil.getString(R.string.mine_accessible_write), R.drawable.selector_write, write == 1, originalWrite));
        data.add(new OperatePermissionBean(UiUtil.getString(R.string.mine_accessible_del), R.drawable.selector_del, del == 1, originalDel));
        operatePermissionAdapter.setNewData(data);
        operatePermissionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OperatePermissionBean operatePermissionBean = operatePermissionAdapter.getItem(position);
                boolean selected = operatePermissionBean.isSelected();
                operatePermissionBean.setSelected(!selected);
                List<OperatePermissionBean> permissionData = operatePermissionAdapter.getData();
                switch (position){
                    case 0:
                        read = selected ? 0 : 1;
                        if (read == 0){  // 如果没有读权限，也就没有写删权限
                            for (OperatePermissionBean opb : permissionData){
                                opb.setSelected(false);
                            }
                            write = 0;
                            del = 0;
                        }
                        break;

                    case 1:
                        write = selected ? 0 : 1;
                        if (write == 1){  // 如果选择了写权限，读权限也要一并选择
                            permissionData.get(0).setSelected(true);
                            read = 1;
                        }
                        break;

                    case 2:
                        del = selected ? 0 : 1;
                        if (del == 1){  // 如果选择了删权限，读权限也要一并选择
                            permissionData.get(0).setSelected(true);
                            read = 1;
                        }
                        break;
                }
                operatePermissionAdapter.notifyDataSetChanged();
                setSaveStatus();
            }
        });
    }

    /**
     * 备注说明
     */
    private void initRvNote(){
        rvNote.setLayoutManager(new LinearLayoutManager(getContext()));
        KeyValueAdapter keyValueAdapter = new KeyValueAdapter();
        rvNote.setAdapter(keyValueAdapter);
        List<KeyValBean> data = new ArrayList<>();
        data.add(new KeyValBean(UiUtil.getString(R.string.serial_number_1), UiUtil.getString(R.string.serial_number_1_content)));
        data.add(new KeyValBean(UiUtil.getString(R.string.serial_number_2), UiUtil.getString(R.string.serial_number_2_content)));
        data.add(new KeyValBean(UiUtil.getString(R.string.serial_number_3), UiUtil.getString(R.string.serial_number_3_content)));
        data.add(new KeyValBean(UiUtil.getString(R.string.serial_number_4), UiUtil.getString(R.string.serial_number_4_content)));
        data.add(new KeyValBean(UiUtil.getString(R.string.serial_number_5), UiUtil.getString(R.string.serial_number_5_content)));
        keyValueAdapter.setNewData(data);
    }

    /**
     * 设置保存按钮是否可点击
     */
    private void setSaveStatus(){
        if(checkSaveEnabled){ // 需要设置保存按钮可点击/不可点击
            tvConfirm.setEnabled(operatePermissionAdapter.checkOneSelected());
            tvConfirm.setAlpha(tvConfirm.isEnabled() ? 1 : 0.5f);
        }
    }

    /**
     * 监听事件
     */
    private void initListener(){
        // 关闭
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 确定
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener!=null){
                    confirmListener.onConfirm(read, write, del);
                }
            }
        });
    }

    private OnConfirmListener confirmListener;

    public OnConfirmListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener{
        void onConfirm(int read, int write, int del);
    }
}
