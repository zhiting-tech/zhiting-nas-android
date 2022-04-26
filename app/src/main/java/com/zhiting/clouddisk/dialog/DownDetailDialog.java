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
import com.zhiting.clouddisk.entity.home.FileOperateBean;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.FileUtil;
import com.zhiting.clouddisk.util.SpacesItemDecoration;
import com.zhiting.networklib.dialog.BaseBottomDialog;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UiUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 下载详情
 */
public class DownDetailDialog extends BaseBottomDialog {

    private ImageView ivLogo;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvSize;
    private TextView tvCancel;
    private RecyclerView rvOperate;

    private FileDetailOperateAdapter fileDetailOperateAdapter;

    private File file;

    private List<FileOperateBean> mOperateData = new ArrayList<>();

    public static DownDetailDialog getInstance(File file){
        Bundle args = new Bundle();
        args.putSerializable(Constant.BEAN, file);
        DownDetailDialog downDetailDialog = new DownDetailDialog();
        downDetailDialog.setArguments(args);
        return downDetailDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_file_detail;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        file = (File) arguments.getSerializable(Constant.BEAN);
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
        if (FileUtil.isFile(file)){
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
             */
            int fileType = FileTypeUtil.fileType(file.getName());
            drawableRes = FileTypeUtil.getFileLogo(fileType);
        }else {
            drawableRes = R.drawable.icon_file;
        }
        ivLogo.setImageResource(drawableRes);
        tvName.setText(file.getName());
        tvTime.setText(TimeFormatUtil.long2String(file.lastModified(), TimeFormatUtil.DATE_FORMAT));
        long size = FileUtil.getFileLength(file);
        tvSize.setText(FileUtil.getReadableFileSize(size));

        initRv();
        tvCancel.setOnClickListener(v -> dismiss());
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
        mOperateData.add(new FileOperateBean(R.drawable.icon_review_black, UiUtil.getString(R.string.common_look)));
        mOperateData.add(new FileOperateBean(R.drawable.icon_other_app_open, UiUtil.getString(R.string.home_other_app_open)));
        fileDetailOperateAdapter.setNewData(mOperateData);
        fileDetailOperateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (operateListener!=null){
                    operateListener.onOperate(position, file);
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
        void onOperate(int position, File file);
    }
}
