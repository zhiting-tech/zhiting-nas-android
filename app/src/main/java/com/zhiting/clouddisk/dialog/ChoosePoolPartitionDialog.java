package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.os.strictmode.Violation;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.ChoosePoolAdapter;
import com.zhiting.clouddisk.adapter.ChoosePoolPartitionAdapter;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.entity.mine.StoragePoolDetailBean;
import com.zhiting.networklib.dialog.BaseBottomDialog;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.UnitUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChoosePoolPartitionDialog extends BaseBottomDialog {

    private TextView tvPool;
    private TextView tvPartition;
    private TextView tvCancel;
    private RecyclerView rvPool;
    private RecyclerView rvPartition;

    private View viewEmpty;
    private TextView tvEmpty;
    private RelativeLayout rlData;

    private ChoosePoolAdapter mChoosePoolAdapter; // 存储池
    private ChoosePoolPartitionAdapter choosePoolPartitionAdapter; // 存储池分区
    private List<StoragePoolDetailBean> mPoolData;
    private String pool_name; // 存储名称
    private String partition_name; // 存储分区名称
    private String originalPoolName;
    private String originalPartitionName;

    public static ChoosePoolPartitionDialog getInstance(List<StoragePoolDetailBean> poolData){
        ChoosePoolPartitionDialog choosePoolPartitionDialog = new ChoosePoolPartitionDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("pool", (Serializable) poolData);
        choosePoolPartitionDialog.setArguments(bundle);
        return choosePoolPartitionDialog;
    }

    @Override
    protected int obtainHeight() {
        return UiUtil.getDimens(R.dimen.dp_400);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_choose_pool_partition;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        mPoolData = (List<StoragePoolDetailBean>) arguments.getSerializable("pool");
    }

    @Override
    protected void initView(View view) {
        tvPool = view.findViewById(R.id.tvPool);
        tvPartition = view.findViewById(R.id.tvPartition);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvPool.setSelected(true);
        rvPool = view.findViewById(R.id.rvPool);
        rvPartition = view.findViewById(R.id.rvPartition);
        viewEmpty = view.findViewById(R.id.viewEmpty);
        rlData = view.findViewById(R.id.rlData);
        tvEmpty = viewEmpty.findViewById(R.id.tvEmpty);
        tvEmpty.setText(UiUtil.getString(R.string.mine_no_storage_pool_partition));
        tvPool.post(new Runnable() {
            @Override
            public void run() {
                int maxWidth = (UiUtil.getScreenWidth()-tvCancel.getWidth()-UiUtil.dip2px(42))/2;
                tvPool.setMaxWidth(maxWidth);
                tvPartition.setMaxWidth(maxWidth);
            }
        });
        initRvPool();
        initRvPartitionData();
        setNullView(CollectionUtil.isEmpty(mPoolData));
        mChoosePoolAdapter.setNewData(mPoolData);
        if (CollectionUtil.isNotEmpty(mPoolData)){
            for (StoragePoolDetailBean storagePoolDetailBean : mPoolData){
                if (storagePoolDetailBean.isSelected()){
                    originalPoolName = storagePoolDetailBean.getName();
                    tvPool.setText(originalPoolName);
                    List<DiskBean> disks = storagePoolDetailBean.getLv();
                    if (CollectionUtil.isNotEmpty(disks)){
                        for (DiskBean diskBean : disks){
                            if (diskBean.isSelected()){
                                originalPartitionName  = diskBean.getName();
                                tvPartition.setText(originalPartitionName);
                                break;
                            }
                        }
                        choosePoolPartitionAdapter.setNewData(disks);
                    }
                    break;
                }
            }
        }
        // 取消
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取消操作需要把数据置回上一次选中状态
                if (!TextUtils.isEmpty(originalPoolName)){
                    // 遍历存储池
                    for (StoragePoolDetailBean storagePoolDetailBean : mPoolData){
                        String spdbName = storagePoolDetailBean.getName();
                        // 如果是上一次选中的数据，设置为选中状态
                        if (spdbName!=null && spdbName.equals(originalPoolName)){
                            storagePoolDetailBean.setSelected(true);
                            List<DiskBean> diskBeans = storagePoolDetailBean.getLv();
                            // 选中的存储池的存储分区
                            if (CollectionUtil.isNotEmpty(diskBeans)){
                                for (DiskBean diskBean : diskBeans){
                                    // 上一次选中的存储池分区
                                    String diskName = diskBean.getName();
                                    if (diskName!=null && diskName.equals(originalPartitionName)){
                                        diskBean.setSelected(true);
                                    }else {
                                        diskBean.setSelected(false);
                                    }
                                }
                            }
                        }else {
                            storagePoolDetailBean.setSelected(false);
                        }
                    }
                }
                dismiss();
            }
        });

        // 选择存储池
        tvPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPool.setSelected(true);
                tvPartition.setSelected(false);
                setWhichShow(rvPool);
                setNullView(CollectionUtil.isEmpty(mPoolData));
            }
        });

        // 选择分区
        tvPartition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPartitionSelected();
            }
        });
    }

    /**
     * 设置分区选择状态
     */
    private void setPartitionSelected(){
        tvPool.setSelected(false);
        tvPartition.setSelected(true);
        setWhichShow(rvPartition);
        setNullView(CollectionUtil.isEmpty(choosePoolPartitionAdapter.getData()));
    }

    /**
     * 初始化存储池列表
     */
    private void initRvPool(){
        rvPool.setLayoutManager(new LinearLayoutManager(getContext()));
        mChoosePoolAdapter = new ChoosePoolAdapter();
        rvPool.setAdapter(mChoosePoolAdapter);
        mChoosePoolAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                StoragePoolDetailBean storagePoolDetailBean = mChoosePoolAdapter.getItem(position);
                pool_name = storagePoolDetailBean.getName();
                if (!storagePoolDetailBean.isSelected()) {
                    for (int i = 0; i < mChoosePoolAdapter.getData().size(); i++) {
                        mChoosePoolAdapter.getData().get(i).setSelected(false);
                    }
                    storagePoolDetailBean.setSelected(true);
                    choosePoolPartitionAdapter.setNewData(storagePoolDetailBean.getLv());
                    mChoosePoolAdapter.notifyDataSetChanged();
                    tvPool.setText(pool_name);
                    tvPartition.setText(UiUtil.getString(R.string.mine_please_choose_partition));

                    // 需要把之前选中的存储池分区置空
                    for (StoragePoolDetailBean spdb : mPoolData){
                        if (spdb!=storagePoolDetailBean){
                            List<DiskBean> diskList = spdb.getLv();
                            if (CollectionUtil.isNotEmpty(diskList)){
                                for (DiskBean diskBean : diskList){
                                    diskBean.setSelected(false);
                                }
                            }
                        }
                    }
                }
                setPartitionSelected();
            }
        });
    }

    /**
     * 展现存储池还是存储池分区
     * @param recyclerView
     */
    private void setWhichShow(RecyclerView recyclerView){
        rvPool.setVisibility(View.GONE);
        rvPartition.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化存储池分区列表
     */
    private void initRvPartitionData(){
        rvPartition.setLayoutManager(new LinearLayoutManager(getContext()));
        choosePoolPartitionAdapter = new ChoosePoolPartitionAdapter();
        rvPartition.setAdapter(choosePoolPartitionAdapter);
        choosePoolPartitionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DiskBean diskBean = choosePoolPartitionAdapter.getItem(position);
                partition_name = diskBean.getName();
                for (int i=0; i<choosePoolPartitionAdapter.getData().size(); i++){
                    choosePoolPartitionAdapter.getData().get(i).setSelected(false);
                }
                diskBean.setSelected(true);
                choosePoolPartitionAdapter.notifyDataSetChanged();
                if (poolsPartitionListener!=null){
                    poolsPartitionListener.onSelected(pool_name, partition_name);
                }
                dismiss();
                tvPartition.setText(partition_name);
            }
        });
    }

    /**
     * 设置空视图展示
     * @param visible
     */
    private void setNullView(boolean visible){
        viewEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
        rlData.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    private OnPoolsPartitionListener poolsPartitionListener;

    public OnPoolsPartitionListener getPoolsPartitionListener() {
        return poolsPartitionListener;
    }

    public void setPoolsPartitionListener(OnPoolsPartitionListener poolsPartitionListener) {
        this.poolsPartitionListener = poolsPartitionListener;
    }

    public interface OnPoolsPartitionListener{
        void onSelected(String poolName, String partitionName);
    }
}
