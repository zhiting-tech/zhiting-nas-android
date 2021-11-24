package com.zhiting.clouddisk.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FileOperateAdapter;
import com.zhiting.clouddisk.entity.home.FileOperateBean;
import com.zhiting.networklib.dialog.BaseBottomDialog;

import java.util.List;

/**
 * 操作文件弹窗
 */
public class OperateFileDialog extends BaseBottomDialog {

    private RecyclerView rvOperate;
    private FileOperateAdapter fileOperateAdapter;
    private List<FileOperateBean> mOperateData;


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setBackgroundDrawableResource(com.zhiting.networklib.R.color.transparent);
        dialog.getWindow().setDimAmount(0);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setCanceledOnTouchOutside(false);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_operate_file;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        rvOperate = view.findViewById(R.id.rvOperate);
        rvOperate.setLayoutManager(new GridLayoutManager(getContext(), 6));
        fileOperateAdapter = new FileOperateAdapter();
        rvOperate.setAdapter(fileOperateAdapter);
        fileOperateAdapter.setNewData(mOperateData);
    }

    /**
     * 设置操作数据
     * @param operateData
     */
    public void setOperateData(List<FileOperateBean> operateData){
        mOperateData = operateData;
    }

    public void notifyDataChange(){
        if (fileOperateAdapter!=null){
            fileOperateAdapter.notifyDataSetChanged();
        }
    }
}
