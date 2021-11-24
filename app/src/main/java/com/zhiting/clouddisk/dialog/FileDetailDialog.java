package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FileDetailOperateAdapter;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.entity.home.FileOperateBean;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.SpacesItemDecoration;
import com.zhiting.networklib.dialog.BaseBottomDialog;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.UnitUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 文件详情弹窗
 */
public class FileDetailDialog extends BaseBottomDialog {


    private ImageView ivLogo;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvSize;
    private TextView tvCancel;
    private RecyclerView rvOperate;

    private FileDetailOperateAdapter fileDetailOperateAdapter;

    private FileBean fileBean;

    private List<FileOperateBean> mOperateData = new ArrayList<>();

    public static FileDetailDialog getInstance(FileBean fileBean){
        Bundle args = new Bundle();
        args.putSerializable(Constant.BEAN, fileBean);
        FileDetailDialog fileDetailDialog = new FileDetailDialog();
        fileDetailDialog.setArguments(args);
        return fileDetailDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_file_detail;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        fileBean = (FileBean) arguments.getSerializable(Constant.BEAN);
    }

    @Override
    protected void initView(View view) {
        ivLogo = view.findViewById(R.id.ivLogo);
        tvName = view.findViewById(R.id.tvName);
        tvTime = view.findViewById(R.id.tvTime);
        tvSize = view.findViewById(R.id.tvSize);
        tvCancel = view.findViewById(R.id.tvCancel);
        rvOperate = view.findViewById(R.id.rvOperate);
        int drawableRes = R.drawable.icon_gho;
        if (fileBean.getType() == 0){
            drawableRes = R.drawable.icon_file;
        }else {
            /**
             * 1. word
             * 2. excel
             * 3. ppt
             * 4. 压缩包
             * 5. 图片
             * 6. 音频
             * 7. 视频
             * 8. 文本
             *
             * 默认 文件夹
             */
            int fileType = FileTypeUtil.fileType(fileBean.getName());
            drawableRes = FileTypeUtil.getFileLogo(fileType);

        }
        ivLogo.setImageResource(drawableRes);
        tvName.setText(fileBean.getName());
        tvTime.setText(TimeFormatUtil.long2String(fileBean.getMod_time()*1000, TimeFormatUtil.DATE_FORMAT));
        tvSize.setText(UnitUtil.getFormatSize(fileBean.getSize()));
        initRv();
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    /**
     * 操作列表
     */
    private void initRv(){
        rvOperate.setLayoutManager(new GridLayoutManager(getContext(), 3));
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_10));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_10));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        rvOperate.addItemDecoration(spacesItemDecoration);
        fileDetailOperateAdapter = new FileDetailOperateAdapter();
        rvOperate.setAdapter(fileDetailOperateAdapter);
        if (fileBean.getWrite() == 1) // 写权限
        mOperateData.add(new FileOperateBean(R.drawable.icon_download_black, UiUtil.getString(R.string.home_download)));
        if (fileBean.getDeleted() == 1) // 删权限
        mOperateData.add(new FileOperateBean(R.drawable.icon_move_black, UiUtil.getString(R.string.home_move)));
        mOperateData.add(new FileOperateBean(R.drawable.icon_copy_black, UiUtil.getString(R.string.home_copy)));
        if (fileBean.getWrite() == 1) // 写权限
        mOperateData.add(new FileOperateBean(R.drawable.icon_rename_black, UiUtil.getString(R.string.home_rename)));
        if (fileBean.getDeleted() == 1) // 删权限
        mOperateData.add(new FileOperateBean(R.drawable.icon_remove_black, UiUtil.getString(R.string.home_remove)));
        fileDetailOperateAdapter.setNewData(mOperateData);
        fileDetailOperateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (operateListener!=null){
                    operateListener.onOperate(position, fileBean);
                }
            }
        });
    }

    private OnOperateListener operateListener;

    public OnOperateListener getOperateListener() {
        return operateListener;
    }

    public void setOperateListener(OnOperateListener operateListener) {
        this.operateListener = operateListener;
    }

    public interface OnOperateListener{
        void onOperate(int position, FileBean fileBean);
    }
}
