package com.zhiting.clouddisk.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.DownloadFailAdapter;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.util.GonetUtil;
import com.zhiting.networklib.dialog.BaseBottomDialog;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.UiUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载失败列表
 */
public class DownFailListDialog extends BaseBottomDialog {

    private static final String LIST = "list";
    private static final String FILE_NAME = "file_name";

    private TextView tvNote, tvName;
    private RecyclerView rvFail;
    private DownloadFailAdapter downloadFailAdapter;
    private List<DownLoadFileBean> mErrorlist = new ArrayList<>();
    private List<DownLoadFileBean> mAlllist = new ArrayList<>();
    private String fileName;

    public static DownFailListDialog getInstance(String dirName, List<DownLoadFileBean> list) {
        Bundle args = new Bundle();
        args.putSerializable(LIST, (Serializable) list);
        args.putString(FILE_NAME, dirName);
        DownFailListDialog downFailListDialog = new DownFailListDialog();
        downFailListDialog.setArguments(args);
        return downFailListDialog;
    }

    @Override
    protected int obtainHeight() {
        return UiUtil.getDimens(R.dimen.dp_400);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_down_fail_list;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        fileName = arguments.getString(FILE_NAME);
        mAlllist = (List<DownLoadFileBean>) arguments.getSerializable(LIST);
    }

    @Override
    protected void initView(View view) {
        rvFail = view.findViewById(R.id.rvFail);
        tvNote = view.findViewById(R.id.tvNote);
        tvName = view.findViewById(R.id.tvName);

        setTitle();
        rvFail.setLayoutManager(new LinearLayoutManager(getContext()));
        downloadFailAdapter = new DownloadFailAdapter(mErrorlist);
        rvFail.setAdapter(downloadFailAdapter);

        downloadFailAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.tvDelete) {
                DownLoadFileBean fileBean = downloadFailAdapter.getItem(position);
                GonetUtil.deleteDownload(fileBean.getId());

                mAlllist.remove(fileBean);
                setTitle();
                downloadFailAdapter.setNewData(mErrorlist);
            }
        });
    }

    public void setTitle() {
        if (CollectionUtil.isNotEmpty(mAlllist)) {
            mErrorlist.clear();
            for (DownLoadFileBean bean : mAlllist) {
                if (bean.getStatus() == 4) {
                    mErrorlist.add(bean);
                }
            }
        }

        String format = getActivity().getString(R.string.common_upload_file_failed);
        tvNote.setText(String.format(format, mAlllist.size(), mErrorlist.size()));
        tvName.setText(fileName);
    }
}
