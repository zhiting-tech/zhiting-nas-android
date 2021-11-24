package com.zhiting.clouddisk.popup_window;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FamilyAdapter;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.popupwindow.BasePopupWindow;
import com.zhiting.networklib.utils.CollectionUtil;

import java.util.List;

/**
 * 家庭弹窗
 */
public class FamilyPopupWindow extends BasePopupWindow {

    private RecyclerView rvFamily;
    private FamilyAdapter familyAdapter;

    public FamilyPopupWindow(BaseActivity context) {
        super(context);
        rvFamily = view.findViewById(R.id.rvFamily);
        rvFamily.setLayoutManager(new LinearLayoutManager(context));
        familyAdapter = new FamilyAdapter();
        rvFamily.setAdapter(familyAdapter);
        familyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (HomeCompanyBean homeCompanyBean : familyAdapter.getData()){
                    homeCompanyBean.setSelected(false);
                }
                familyAdapter.getItem(position).setSelected(true);
                familyAdapter.notifyDataSetChanged();
                if (selectFamilyListener!=null){
                    selectFamilyListener.selectedFamily(familyAdapter.getItem(position));
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_family;
    }

    @Override
    public void onClick(View v) {

    }

    public void setFamilyData(List<HomeCompanyBean> familyData){
        if (CollectionUtil.isNotEmpty(familyData)){
            familyAdapter.setNewData(familyData);
        }
    }

    private OnSelectFamilyListener selectFamilyListener;

    public OnSelectFamilyListener getSelectFamilyListener() {
        return selectFamilyListener;
    }

    public void setSelectFamilyListener(OnSelectFamilyListener selectFamilyListener) {
        this.selectFamilyListener = selectFamilyListener;
    }

    public interface OnSelectFamilyListener {
        void selectedFamily(HomeCompanyBean homeCompanyBean);
    }
}
