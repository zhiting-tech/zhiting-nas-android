package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.HardDiskAdapter;
import com.zhiting.clouddisk.entity.mine.DiskBean;
import com.zhiting.clouddisk.util.SpacesItemDecoration;
import com.zhiting.networklib.dialog.CommonBaseDialog;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.UiUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 物理硬盘弹窗
 */
public class HardDiskDialog extends CommonBaseDialog {

    private TextView tvTitle;
    private RecyclerView rvDisk;
    private ImageView ivClose;

    private HardDiskAdapter hardDiskAdapter;

    private List<DiskBean> diskData;

    public static HardDiskDialog getInstance(List<DiskBean> diskData){
        HardDiskDialog hardDiskDialog = new HardDiskDialog();
        Bundle args = new Bundle();
        args.putSerializable("diskData", (Serializable) diskData);
        hardDiskDialog.setArguments(args);
        return hardDiskDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_hard_disk;
    }

    @Override
    protected int obtainWidth() {
        return UiUtil.getScreenWidth()-UiUtil.getDimens(R.dimen.dp_30);
    }

    @Override
    protected int obtainHeight() {
        return UiUtil.getScreenHeight()-UiUtil.getDimens(R.dimen.dp_167);
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        diskData = (List<DiskBean>) arguments.getSerializable("diskData");
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        rvDisk = view.findViewById(R.id.rvDisk);
        ivClose = view.findViewById(R.id.ivClose);
        if (rvDisk.getItemDecorationCount()<1) {
            HashMap<String, Integer> spaceValue = new HashMap<>();
            spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
            spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
            spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
            spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7_dot_5));
            SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
            rvDisk.addItemDecoration(spacesItemDecoration);
        }
        tvTitle.setText(String.format(UiUtil.getString(R.string.mine_hard_disk_count_with_brackets), CollectionUtil.isNotEmpty(diskData) ? diskData.size() : 0));
        rvDisk.setLayoutManager(new GridLayoutManager(getContext(), 2));
        hardDiskAdapter = new HardDiskAdapter();
        rvDisk.setAdapter(hardDiskAdapter);

        hardDiskAdapter.setNewData(diskData);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
