package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.BottomListAdapter;
import com.zhiting.clouddisk.entity.BottomListBean;
import com.zhiting.networklib.dialog.BaseBottomDialog;

import java.util.ArrayList;
import java.util.List;

public class BottomListDialog extends BaseBottomDialog {

    private RecyclerView rvData;
    private BottomListAdapter bottomListAdapter;

    private List<BottomListBean> bottomData;

    public static BottomListDialog getInstance(List<BottomListBean> bottomData){
        Bundle args = new Bundle();
        args.putParcelableArrayList("bottomData", (ArrayList<? extends Parcelable>) bottomData);
        BottomListDialog bottomListDialog = new BottomListDialog();
        bottomListDialog.setArguments(args);
        return bottomListDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_bottom_list;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        bottomData = arguments.getParcelableArrayList("bottomData");
    }

    @Override
    protected void initView(View view) {
        rvData = view.findViewById(R.id.rvData);
        rvData.setLayoutManager(new LinearLayoutManager(getContext()));
        bottomListAdapter = new BottomListAdapter();
        rvData.setAdapter(bottomListAdapter);
        bottomListAdapter.setNewData(bottomData);
        bottomListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BottomListBean bottomListBean = bottomListAdapter.getItem(position);
                for (BottomListBean blb : bottomData){
                    blb.setSelected(false);
                }
                bottomListBean.setSelected(true);
                bottomListAdapter.notifyDataSetChanged();
                if (itemSelectedListener!=null){
                    itemSelectedListener.onSelected(bottomListBean, position);
                }
                dismiss();
            }
        });
    }

    private OnItemSelectedListener itemSelectedListener;

    public void setItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public interface OnItemSelectedListener{
        void onSelected(BottomListBean bottomListBean, int position);
    }
}
