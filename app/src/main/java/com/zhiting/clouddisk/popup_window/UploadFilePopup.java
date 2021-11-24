package com.zhiting.clouddisk.popup_window;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.UploadFileAdapter;
import com.zhiting.clouddisk.entity.home.FileOperateBean;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.popupwindow.BasePopupWindow;
import com.zhiting.networklib.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

public class UploadFilePopup extends BasePopupWindow {

    private RecyclerView rvUpload;
    private UploadFileAdapter uploadFileAdapter;

    public UploadFilePopup(BaseActivity context) {
        super(context);
        rvUpload = view.findViewById(R.id.rvUpload);
        rvUpload.setLayoutManager(new GridLayoutManager(context, 3));
        uploadFileAdapter = new UploadFileAdapter();
        rvUpload.setAdapter(uploadFileAdapter);
        List<FileOperateBean> mOperateData = new ArrayList<>();
        mOperateData.add(new FileOperateBean(R.drawable.icon_upload_video, UiUtil.getString(R.string.home_video)));
        mOperateData.add(new FileOperateBean(R.drawable.icon_upload_picture, UiUtil.getString(R.string.home_picture)));
        mOperateData.add(new FileOperateBean(R.drawable.icon_upload_file, UiUtil.getString(R.string.home_other_file)));
        //mOperateData.add(new FileOperateBean(R.drawable.icon_upload_folder, UiUtil.getString(R.string.home_folder)));
        uploadFileAdapter.setNewData(mOperateData);
        uploadFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (uploadOperateListener != null) {
                    uploadOperateListener.onUploadOperate(position);
                }
                dismiss();
            }
        });
        setWidthHeight(LayoutStyle.WRAP_HEIGHT);//设置布局样式
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_upload_file;
    }

    @Override
    public void onClick(View v) {

    }

    private OnUploadOperateListener uploadOperateListener;

    public OnUploadOperateListener getUploadOperateListener() {
        return uploadOperateListener;
    }

    public void setUploadOperateListener(OnUploadOperateListener uploadOperateListener) {
        this.uploadOperateListener = uploadOperateListener;
    }

    public interface OnUploadOperateListener {
        void onUploadOperate(int position);
    }
}
