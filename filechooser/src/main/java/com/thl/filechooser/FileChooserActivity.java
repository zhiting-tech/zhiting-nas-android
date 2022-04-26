package com.thl.filechooser;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thl.filechooser.statusbarutil.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileChooserActivity extends AppCompatActivity {

    private boolean showFile = true;
    private boolean showHideFile = true;
    public static FileChooser mFileChooser;
    private String mChoosenFilePath;

    private int firstItemPosition = 0;
    private int lastItemPosition = 0;
    private FileTourController tourController;
    private FileAdapter adapter;
    private CurrentFileAdapter currentFileAdapter;
    private RecyclerView fileRv;
    private HashMap<Integer, Integer> firstItemPositionMap;
    private HashMap<Integer, Integer> lastItemPositionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);

        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        setContentView(R.layout.activity_file_chooser);
        initListener();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    private void initListener() {
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvOk).setOnClickListener(v -> clickRightText());

        this.showFile = getIntent().getBooleanExtra("showFile", true);
        this.showHideFile = getIntent().getBooleanExtra("showHideFile", true);
        this.mChoosenFilePath = getIntent().getStringExtra("currentPath");
        String chooseType = getIntent().getStringExtra("chooseType");
        int themeColorRes = getIntent().getIntExtra("themeColorRes", -1);

        tourController = new FileTourController(this, mChoosenFilePath);
        tourController.setShowFile(this.showFile);
        tourController.setShowHideFile(this.showHideFile);
        View bgView = (View) findViewById(R.id.bg_title);
        if (themeColorRes != -1) {
            bgView.setBackgroundResource(themeColorRes);
        }

        adapter = new FileAdapter(this, (ArrayList<FileInfo>) tourController.getCurrenFileInfoList(), R.layout.item_file, chooseType);
        fileRv = (RecyclerView) findViewById(R.id.fileRv);
        fileRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fileRv.setAdapter(adapter);

        final RecyclerView currentPath = (RecyclerView) findViewById(R.id.currentPath);
        currentFileAdapter = new CurrentFileAdapter(this, (ArrayList<File>) tourController.getCurrentFolderList(), R.layout.item_current_file);
        currentPath.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        currentPath.setAdapter(currentFileAdapter);
        currentPath.scrollToPosition(tourController.getCurrentFolderList().size() - 1);
        firstItemPositionMap = new HashMap<>();
        lastItemPositionMap = new HashMap<>();

        fileRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (recyclerView.getLayoutManager() != null && layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    lastItemPosition = linearManager.findLastVisibleItemPosition();
                }
            }
        });
        adapter.setItemClickListener((view, position, data) -> {
            File selectFile = new File(tourController.getCurrenFileInfoList().get(position).getFilePath());
            if (selectFile.isDirectory() && isDirectory(selectFile.getAbsolutePath())) {
                ArrayList<FileInfo> childFileInfoList = (ArrayList<FileInfo>) tourController.addCurrentFile(selectFile);
                adapter.setData(childFileInfoList);
                adapter.notifyData();

                currentFileAdapter.setData(tourController.getCurrentFolderList());
                currentFileAdapter.notifyDataSetChanged();
                int sign = tourController.getCurrentFolderList().size() - 1;
                currentPath.scrollToPosition(sign);
                firstItemPositionMap.put(sign, firstItemPosition);
                lastItemPositionMap.put(sign, lastItemPosition);
            } else {
                adapter.notifyClick(data, position);
            }
        });

        currentFileAdapter.setItemClickListener((view, position) -> {
            List<FileInfo> fileInfoList = tourController.resetCurrentFile(position);
            adapter.setData(fileInfoList);
            adapter.notifyData();

            currentFileAdapter.setData(tourController.getCurrentFolderList());
            currentFileAdapter.notifyDataSetChanged();

            currentPath.scrollToPosition(tourController.getCurrentFolderList().size() - 1);
        });

        findViewById(R.id.switchSdcard).setOnClickListener(v -> {
            final ListPopupWindow listPopupWindow = new ListPopupWindow(FileChooserActivity.this);
            listPopupWindow.setAnchorView(v);
            ArrayList<String> sdcardList = new ArrayList<>();
            sdcardList.add("手机存储");
            if (FileTourController.getStoragePath(FileChooserActivity.this, true) != null)
                sdcardList.add("SD卡");

            SdCardAdapter sdCardAdapter = new SdCardAdapter(FileChooserActivity.this, sdcardList);
            listPopupWindow.setAdapter(sdCardAdapter);
            listPopupWindow.setWidth(sdCardAdapter.getItemViewWidth());
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    firstItemPositionMap.clear();
                    lastItemPositionMap.clear();
                    firstItemPosition = 0;
                    lastItemPosition = 0;
                    if (tourController != null)
                        tourController.switchSdCard(position);
                    if (adapter != null) {
                        adapter.setData(tourController.getCurrenFileInfoList());
                        adapter.notifyDataSetChanged();
                    }
                    if (currentFileAdapter != null) {
                        currentFileAdapter.setData(tourController.getCurrentFolderList());
                        currentFileAdapter.notifyDataSetChanged();
                    }
                    listPopupWindow.dismiss();
                }
            });
            listPopupWindow.show();
        });
    }

    public boolean isDirectory(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files == null) {
            return false;
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                return true;
            }
        }
        return false;
    }

    public void clickRightText() {
        if (adapter != null && adapter.getSign() < 0) {
            Toast.makeText(this, "请选择文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tourController != null) {
            mChoosenFilePath = tourController.getCurrentFile().getAbsolutePath();
        }
        if (this.mFileChooser != null) {
            mFileChooser.finish(adapter.getChooseFilePath());
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFileChooser = null;
    }

    @Override
    public void onBackPressed() {
        if (!tourController.isRootFile()) {
            List<FileInfo> currentList = tourController.backToParent();
            adapter.setData(currentList);
            adapter.notifyDataSetChanged();
            currentFileAdapter.setData(tourController.getCurrentFolderList());
            currentFileAdapter.notifyDataSetChanged();

            int sign = tourController.getCurrentFolderList().size();
            Integer firstposition = firstItemPositionMap.get(sign);
            int first = firstposition == null ? 0 : firstposition.intValue();

            Integer lastItemPosition = lastItemPositionMap.get(sign);
            int last = lastItemPosition == null ? 0 : lastItemPosition.intValue();

            int rectification = dp2px(15); //纠偏
            if (fileRv.getLayoutManager() != null) {
                ((LinearLayoutManager) fileRv.getLayoutManager()).scrollToPositionWithOffset(first, last);
            }
        } else {
            super.onBackPressed();
        }
    }

    public int dp2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
