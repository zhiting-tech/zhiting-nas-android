package com.zhiting.clouddisk.popup_window;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lihang.ShadowLayout;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.AreaCodeAdapter;
import com.zhiting.clouddisk.entity.AreaCodeBean;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.popupwindow.BasePopupWindow;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.util.List;

public class AreaCodePopupWindow extends BasePopupWindow {

    private RecyclerView rvAreaCode;
    private AreaCodeAdapter mAreaCodeAdapter;
    private ShadowLayout slParent;

    public AreaCodePopupWindow(BaseActivity context, int width) {
        this(context, width, null);
    }

    public AreaCodePopupWindow(BaseActivity context, int width, List<AreaCodeBean> areaCodeData) {
        super(context);

        rvAreaCode = view.findViewById(R.id.rvAreaCode);
        slParent = view.findViewById(R.id.slParent);
        rvAreaCode.setLayoutManager(new LinearLayoutManager(context));
        mAreaCodeAdapter = new AreaCodeAdapter();
        rvAreaCode.setAdapter(mAreaCodeAdapter);
        mAreaCodeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AreaCodeBean areaCodeBean = mAreaCodeAdapter.getItem(position);
                if (!areaCodeBean.getCode().equals("86")){
                    ToastUtil.show(UiUtil.getString(R.string.main_area_not_support));
                    return;
                }
                for (AreaCodeBean acb : mAreaCodeAdapter.getData()){
                    acb.setSelected(false);
                }
                areaCodeBean.setSelected(true);
                mAreaCodeAdapter.notifyDataSetChanged();
                if (selectedAreaCodeListener != null) {
                    selectedAreaCodeListener.selectedAreaCode(areaCodeBean);
                }
            }
        });
        mAreaCodeAdapter.setNewData(areaCodeData);
//        slParent.post(new Runnable() {
//            @Override
//            public void run() {
//                ViewGroup.LayoutParams layoutParams = slParent.getLayoutParams();
//                layoutParams.width = width+UiUtil.dip2px(10);
//                slParent.setLayoutParams(layoutParams);
//            }
//        });

        slParent.getViewTreeObserver().addOnPreDrawListener(() -> {
            ViewGroup.LayoutParams layoutParams = slParent.getLayoutParams();
            layoutParams.width = width + UiUtil.dip2px(10);
            slParent.setLayoutParams(layoutParams);
            return true;
        });
    }

    public void setAreaCodeData(List<AreaCodeBean> areaCodeData){
        if (CollectionUtil.isNotEmpty(areaCodeData)){
            mAreaCodeAdapter.setNewData(areaCodeData);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_area_code;
    }

    @Override
    public void onClick(View v) {

    }

    private OnSelectedAreaCodeListener selectedAreaCodeListener;

    public void setSelectedAreaCodeListener(OnSelectedAreaCodeListener selectedAreaCodeListener) {
        this.selectedAreaCodeListener = selectedAreaCodeListener;
    }

    public interface OnSelectedAreaCodeListener {
        void selectedAreaCode(AreaCodeBean areaCodeBean);
    }
}
