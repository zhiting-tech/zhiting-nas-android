package com.zhiting.clouddisk.mine.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.event.RunnableEvent;
import com.zhiting.networklib.event.FileStatusEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 网络变化
 */
public class TrafficTipActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic2);
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
            EventBus.getDefault().post(new RunnableEvent());
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