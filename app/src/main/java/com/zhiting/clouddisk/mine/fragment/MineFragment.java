package com.zhiting.clouddisk.mine.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.MineFunctionAdapter;
import com.zhiting.clouddisk.databinding.FragmentMineBinding;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.entity.mine.MemberDetailBean;
import com.zhiting.clouddisk.entity.mine.MineFunctionBean;
import com.zhiting.clouddisk.entity.mine.SettingBean;
import com.zhiting.clouddisk.event.RefreshAuthEvent;
import com.zhiting.clouddisk.main.activity.LoginActivity;
import com.zhiting.clouddisk.mine.activity.FolderActivity;
import com.zhiting.clouddisk.mine.activity.StoragePoolListActivity;
import com.zhiting.clouddisk.mine.contract.MineContract;
import com.zhiting.clouddisk.mine.presenter.MinePresenter;
import com.zhiting.clouddisk.main.fragment.BaseMVPDBFragment;
import com.zhiting.clouddisk.popup_window.SettingPopupWindow;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 */
public class MineFragment extends BaseMVPDBFragment<FragmentMineBinding, MineContract.View, MinePresenter> implements MineContract.View {

    private MineFunctionAdapter mineFunctionAdapter;
    private SettingPopupWindow settingPopupWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initUI(View view) {
        super.initUI(view);
        binding.setHandler(new OnClickHandler());
        initSettingView();
        initRv();
    }

    @Override
    protected void lazyLoad() {
        mPresenter.getUserDetail(Constant.scope_token, Constant.USER_ID);
    }

    @Override
    protected void refreshAuth() {
        super.refreshAuth();
        if (Constant.authBackBean!=null) {
            binding.tvName.setText(Constant.authBackBean.getUserName());
            if (Constant.authBackBean.getHomeCompanyBean()!=null) {
                binding.tvHome.setText(Constant.authBackBean.getHomeCompanyBean().getName());
            }
        }
    }

    /**
     * 设置弹窗的数据
     */
    private void initSettingView(){
        settingPopupWindow = new SettingPopupWindow((BaseActivity) mActivity);
        List<SettingBean> settingBeans = new ArrayList<>();
        settingBeans.add(new SettingBean(0, UiUtil.getString(R.string.mine_logout), R.drawable.icon_mine_logout));
        settingPopupWindow.setSelectedSettingListener(settingBean -> {
            SpUtil.put("loginInfo", "");
            switchToActivity(LoginActivity.class);
            mActivity.finish();
        });
        settingPopupWindow.setSettingData(settingBeans);
    }

    /**
     * 功能入口
     */
    private void initRv(){
        binding.rvFunction.setLayoutManager(new LinearLayoutManager(mContext));
        mineFunctionAdapter = new MineFunctionAdapter();
        binding.rvFunction.setAdapter(mineFunctionAdapter);

        mineFunctionAdapter.setOnItemClickListener((adapter, view, position) -> {
            MineFunctionBean mineFunctionBean = mineFunctionAdapter.getItem(position);
            switch (mineFunctionBean){
                case STORAGE_POOL_MANAGER:  // 存储池管理
                    switchToActivity(StoragePoolListActivity.class);
                    break;

                case FOLDER_MANAGER:  // 文件夹管理
                    switchToActivity(FolderActivity.class);
                    break;
            }
        });
    }

    @Override
    protected void selectedHome(HomeCompanyBean homeCompanyBean) {
        super.selectedHome(homeCompanyBean);
        binding.tvHome.setText(homeCompanyBean.getName());
    }

    @Override
    protected void familyPopupWindowDismiss() {
        super.familyPopupWindowDismiss();
        binding.tvHome.setSelected(false);
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    public void getUserDetailSuccess(MemberDetailBean memberBean) {
        if (memberBean!=null){
            String nickName = memberBean.getNickname();
            binding.tvName.setText(memberBean.getNickname());
            MemberDetailBean.AreaBean areaBean = memberBean.getArea();
            String homeName = "";
            if (areaBean!=null) {
                homeName = areaBean.getName();
                binding.tvHome.setText(homeName);
            }
            // 更新授权的信息（用户昵称和家庭名称
            if (Constant.authBackBean!=null){
                Constant.authBackBean.setUserName(nickName);
                if (!TextUtils.isEmpty(homeName)) {
                    Constant.authBackBean.getHomeCompanyBean().setName(homeName);
                }
                String authJson = new Gson().toJson(Constant.authBackBean);
                SpUtil.put("loginInfo", authJson);
            }
            EventBus.getDefault().post(new RefreshAuthEvent());
            List<MineFunctionBean> functionData = new ArrayList<>();
            if (memberBean.isIs_owner()) {  // 如果是拥有者，有存储池管理入口
                functionData.add(MineFunctionBean.STORAGE_POOL_MANAGER);
            }
            functionData.add(MineFunctionBean.FOLDER_MANAGER);
            mineFunctionAdapter.setNewData(functionData);
            ((SimpleItemAnimator)binding.rvFunction.getItemAnimator()).setSupportsChangeAnimations(false);
        }
    }

    @Override
    public void getUserDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 刷新授权信息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshAuthEvent event){
        refreshAuth();
    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tvHome){ // 我的家
                binding.tvHome.setSelected(true);
                familyPopupWindow.showAsDown(binding.tvHome);
            }else if (viewId == R.id.ivSetting){ // 设置
                settingPopupWindow.showAsDown(binding.ivSetting);
            }
        }
    }
}
