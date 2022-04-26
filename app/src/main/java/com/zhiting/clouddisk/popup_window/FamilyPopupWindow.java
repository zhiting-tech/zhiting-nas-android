package com.zhiting.clouddisk.popup_window;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.FamilyAdapter;
import com.zhiting.clouddisk.constant.Config;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.entity.AuthBackBean;
import com.zhiting.clouddisk.entity.HomeCompanyBean;
import com.zhiting.clouddisk.entity.UpdateHomeNameEvent;
import com.zhiting.clouddisk.event.ChangeHomeEvent;
import com.zhiting.clouddisk.util.ChannelUtil;
import com.zhiting.clouddisk.util.HttpUrlParams;
import com.zhiting.networklib.base.activity.BaseActivity;
import com.zhiting.networklib.constant.BaseConstant;
import com.zhiting.networklib.constant.SpConstant;
import com.zhiting.networklib.http.HttpConfig;
import com.zhiting.networklib.popupwindow.BasePopupWindow;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.SpUtil;
import com.zhiting.networklib.utils.gsonutils.GsonConverter;

import org.greenrobot.eventbus.EventBus;

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
                for (AuthBackBean authBackBean : familyAdapter.getData()){
                    authBackBean.setSelected(false);
                }
                familyAdapter.getItem(position).setSelected(true);
                familyAdapter.notifyDataSetChanged();
                if (selectFamilyListener!=null){
                    selectFamilyListener.selectedFamily(familyAdapter.getItem(position));
                }
            }
        });
        List<AuthBackBean> authBackList = GsonConverter.getGson().fromJson(SpUtil.getString(Config.KEY_AUTH_INFO), new TypeToken<List<AuthBackBean>>() {
        }.getType());
        authBackList.get(0).setSelected(true);
        familyAdapter.setNewData(authBackList);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_family;
    }

    @Override
    public void onClick(View v) {

    }

    public void setFamilyData(List<AuthBackBean> familyData){
        if (CollectionUtil.isNotEmpty(familyData)){
            familyAdapter.setNewData(familyData);
        }
    }

    /**
     * 移除家庭
     */
    public void removeHome(){
        if (familyAdapter!=null && CollectionUtil.isNotEmpty(familyAdapter.getData())){
            for (int i=0; i<familyAdapter.getData().size(); i++) {
                AuthBackBean authBackBean = familyAdapter.getData().get(i);
                HomeCompanyBean homeCompanyBean = authBackBean.getHomeCompanyBean();
                if (Constant.AREA_ID == homeCompanyBean.getId()) {
                    familyAdapter.getData().remove(i);
                    familyAdapter.getData().get(0).setSelected(true);
                    Constant.authBackBean = familyAdapter.getData().get(0);
                    Constant.cookies = Constant.authBackBean.getCookies();
                    Constant.scope_token = Constant.authBackBean.getStBean().getToken();//scopeToken
                    Constant.USER_ID = Constant.authBackBean.getUserId();//用户 id
                    Constant.userName = Constant.authBackBean.getUserName();//用户名称
                    Constant.currentHome = Constant.authBackBean.getHomeCompanyBean();//家庭
                    Constant.AREA_ID = Constant.currentHome.getId();
                    BaseConstant.AREA_ID = Constant.AREA_ID;
                    BaseConstant.SCOPE_TOKEN = Constant.scope_token;
                    Constant.HOME_NAME = Constant.currentHome.getName();
                    SpUtil.put(SpConstant.HOME_ID, String.valueOf(Constant.AREA_ID));
                    SpUtil.put(SpConstant.SA_TOKEN, Constant.currentHome.getSa_user_token());
                    String saLanAddress = Constant.currentHome.getSa_lan_address();
                    HttpConfig.baseTestUrl = TextUtils.isEmpty(saLanAddress) ? HttpUrlParams.SC_URL : saLanAddress;
                    ChannelUtil.resetApiServiceFactory( HttpConfig.baseTestUrl);
                    EventBus.getDefault().post(new UpdateHomeNameEvent());
                    familyAdapter.notifyDataSetChanged();
                    break;
                }
            }
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
        void selectedFamily(AuthBackBean authBackBean);
    }
}
