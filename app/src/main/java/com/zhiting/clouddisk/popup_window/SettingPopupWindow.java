package com.zhiting.clouddisk.popup_window;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.SettingAdapter;
import com.zhiting.clouddisk.entity.mine.SettingBean;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.popupwindow.BasePopupWindow;

import java.util.List;

/**
 * 设置功能
 */
public class SettingPopupWindow extends BasePopupWindow {

    private RecyclerView rvSetting;
    private SettingAdapter settingAdapter;

    public SettingPopupWindow(BaseActivity context) {
        super(context);
        rvSetting = view.findViewById(R.id.rvSetting);
        rvSetting.setLayoutManager(new LinearLayoutManager(context));
        settingAdapter = new SettingAdapter();
        rvSetting.setAdapter( settingAdapter);
        settingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dismiss();
                if (selectedSettingListener!=null){
                    selectedSettingListener.selectedSetting(settingAdapter.getItem(position));
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_setting;
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 设置设置的功能
     */
    public void setSettingData(List<SettingBean> settingData){
        settingAdapter.setNewData(settingData);
    }

    private OnSelectedSettingListener selectedSettingListener;

    public OnSelectedSettingListener getSelectedSettingListener() {
        return selectedSettingListener;
    }

    public void setSelectedSettingListener(OnSelectedSettingListener selectedSettingListener) {
        this.selectedSettingListener = selectedSettingListener;
    }

    public interface OnSelectedSettingListener{
        void selectedSetting(SettingBean settingBean);
    }
}
