package com.zhiting.clouddisk.adapter;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.BackupSettingBean;
import com.zhiting.networklib.utils.SpUtil;

/**
 * 备份
 */
public class BackupSettingAdapter extends BaseQuickAdapter<BackupSettingBean, BaseViewHolder> {

    public BackupSettingAdapter() {
        super(R.layout.item_backup_setting);
    }

    @Override
    protected void convert(BaseViewHolder helper, BackupSettingBean item) {
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        TextView tvName = helper.getView(R.id.tvName);
        Switch sw = helper.getView(R.id.sw);
        sw.setChecked(item.isOpen());
        View viewLine = helper.getView(R.id.viewLine);
        ivLogo.setImageResource(item.getLogo());
        tvName.setText(item.getName());
        viewLine.setVisibility(helper.getPosition() < getData().size() - 1 ? View.VISIBLE : View.INVISIBLE);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                SpUtil.put(item.getKey(), isChecked);
                if (switchListener != null) {
                    switchListener.onSwitch(item, isChecked);
                }
            }
        });
    }

    private OnSwitchListener switchListener;

    public void setSwitchListener(OnSwitchListener switchListener) {
        this.switchListener = switchListener;
    }

    public interface OnSwitchListener {
        void onSwitch(BackupSettingBean backupSettingBean, boolean open);
    }
}
