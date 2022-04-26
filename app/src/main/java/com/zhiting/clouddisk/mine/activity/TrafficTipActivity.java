package com.zhiting.clouddisk.mine.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.databinding.ActivityBackupSettingBinding;
import com.zhiting.clouddisk.databinding.ActivityTrafficBinding;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.clouddisk.mine.contract.DownloadSettingContract;
import com.zhiting.clouddisk.mine.presenter.DownloadSettingPresenter;
import com.zhiting.networklib.event.FileStatusEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 网络变化
 */
public class TrafficTipActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        initUI();
        overridePendingTransition(R.anim.m_dialog_defalut_enter_anim, R.anim.m_dialog_defalut_exit_anim);
    }

    protected void initUI() {
        TextView tvCancel = findViewById(R.id.tvCancel);
        TextView tvConfirm = findViewById(R.id.tvConfirm);

        EventBus.getDefault().postSticky(new FileStatusEvent(0));
        tvCancel.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new FileStatusEvent(0));
            finish();
        });

        tvConfirm.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new FileStatusEvent(1));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.m_dialog_defalut_enter_anim, R.anim.m_dialog_defalut_exit_anim);
    }

    @Override
    public void onBackPressed() {
    }
}