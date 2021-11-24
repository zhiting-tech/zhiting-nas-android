package com.zhiting.clouddisk.mine.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.adapter.AllMemberAdapter;
import com.zhiting.clouddisk.adapter.SelectedMemberAdapter;
import com.zhiting.clouddisk.databinding.ActivityAddMemberBinding;
import com.zhiting.clouddisk.entity.MemberBean;
import com.zhiting.clouddisk.entity.mine.AccessibleMemberBean;
import com.zhiting.clouddisk.mine.contract.AddMemberContract;
import com.zhiting.clouddisk.mine.presenter.AddMemberPresenter;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.main.activity.BaseMVPDBActivity;
import com.zhiting.networklib.utils.CollectionUtil;
import com.zhiting.networklib.utils.StringUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.toast.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加成员
 */
public class AddMemberActivity extends BaseMVPDBActivity<ActivityAddMemberBinding, AddMemberContract.View, AddMemberPresenter> implements AddMemberContract.View {

    private SelectedMemberAdapter mSelectedMemberAdapter;
    private AllMemberAdapter mAllMemberAdapter;

    private List<AccessibleMemberBean> selectedMember = new ArrayList<>();
    private List<AccessibleMemberBean> members;

    private int mode; // 文件夹类型：1私人文件夹 2共享文件夹

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_member;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding.setHandler(new OnClickHandler());
        initRvSelected();
        initRvAll();

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mode = intent.getIntExtra("mode", 2);
        members = (List<AccessibleMemberBean>) intent.getSerializableExtra("members");
        mPresenter.getMember(Constant.scope_token);
    }

    /**
     * 选择的成员
     */
    private void initRvSelected(){
        binding.rvSelected.setLayoutManager(new LinearLayoutManager(this));
        mSelectedMemberAdapter = new SelectedMemberAdapter();
        binding.rvSelected.setAdapter(mSelectedMemberAdapter);
        mSelectedMemberAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                AccessibleMemberBean accessibleMemberBean = mSelectedMemberAdapter.getItem(position);
                if (viewId == R.id.tvRead){  // 可读
                    int read = accessibleMemberBean.getRead();
                    accessibleMemberBean.setRead(read == 1 ? 0 : 1);
                    if (read == 1){
                        accessibleMemberBean.setWrite(0);
                        accessibleMemberBean.setDeleted(0);
                    }
                    mSelectedMemberAdapter.notifyItemChanged(position);
                }else  if (viewId == R.id.tvWrite){  // 可写
                    int write = accessibleMemberBean.getWrite();
                    accessibleMemberBean.setWrite(write == 1 ? 0 : 1);
                    if (write == 0){
                        accessibleMemberBean.setRead(1);
                    }
                    mSelectedMemberAdapter.notifyItemChanged(position);
                }else  if (viewId == R.id.tvDel){  // 可删
                    int del = accessibleMemberBean.getDeleted();
                    accessibleMemberBean.setDeleted(del == 1 ? 0 : 1);
                    if (del == 0){
                        accessibleMemberBean.setRead(1);
                    }
                    mSelectedMemberAdapter.notifyItemChanged(position);
                }else  if (viewId == R.id.ivClose){  // 删除
                    selectedMember.remove(position);
                    mSelectedMemberAdapter.notifyItemRemoved(position);
                    setAddMemberNullView();
                    setAllMemberUnselected(accessibleMemberBean.getU_id());
                    setMemberCount();
                }

            }
        });
        mSelectedMemberAdapter.setNewData(selectedMember);
        setAddMemberNullView();
    }

    /**
     * 设置空视图
     */
    private void setAddMemberNullView(){
        boolean noMember = CollectionUtil.isEmpty(mSelectedMemberAdapter.getData());
        binding.ivLogo.setVisibility(noMember ? View.VISIBLE : View.GONE);
        binding.tvTips.setVisibility(noMember ? View.VISIBLE : View.GONE);
        binding.ivDown.setVisibility(noMember ? View.VISIBLE : View.GONE);
        binding.tvCount.setVisibility(noMember ? View.GONE : View.VISIBLE);
        binding.rvSelected.setVisibility(noMember ? View.GONE : View.VISIBLE);
    }

    /**
     * 所有成员
     */
    private void initRvAll(){
        binding.rvAll.setLayoutManager(new LinearLayoutManager(this));
        mAllMemberAdapter = new AllMemberAdapter();
        binding.rvAll.setAdapter(mAllMemberAdapter);
        mAllMemberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MemberBean.UsersBean usersBean = mAllMemberAdapter.getItem(position);
                boolean selected = usersBean.isSelected();
                if (!selected){
                    if (mode == 1 && (mAllMemberAdapter.hasSelectedMember()||CollectionUtil.isNotEmpty(selectedMember))){
                        ToastUtil.show(UiUtil.getString(R.string.mine_only_one));
                        return;
                    }
                }
                usersBean.setSelected(!selected);
                mAllMemberAdapter.notifyItemChanged(position);
                if (selected) {
                    removeSelectedMember(usersBean.getUser_id());
                }else {
                    AccessibleMemberBean accessibleMemberBean = new AccessibleMemberBean(usersBean.getUser_id(), usersBean.getNickname());
                    accessibleMemberBean.setRead(1);
                    if (mode == 1){
                        accessibleMemberBean.setWrite(1);
                        accessibleMemberBean.setDeleted(1);
                    }
                    selectedMember.add(accessibleMemberBean);
                    mSelectedMemberAdapter.notifyItemInserted(selectedMember.size());
                    binding.rvSelected.smoothScrollToPosition(selectedMember.size()-1);
                }
                setMemberCount();
                setAddMemberNullView();

            }
        });
    }

    /**
     * 已选删除后， 更新全部成员对应的选中状态
     * @param user_id
     */
    private void setAllMemberUnselected(int user_id){
        for (int i=0; i<mAllMemberAdapter.getData().size(); i++){
            MemberBean.UsersBean usersBean = mAllMemberAdapter.getData().get(i);
            if (user_id == usersBean.getUser_id()){
                usersBean.setSelected(false);
                mAllMemberAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * 移除已选
     * @param user_id
     */
    private void removeSelectedMember(int user_id){
        for (int i=0; i<mSelectedMemberAdapter.getData().size(); i++){
            AccessibleMemberBean accessibleMemberBean = mSelectedMemberAdapter.getData().get(i);
            if (user_id == accessibleMemberBean.getU_id()){
                mSelectedMemberAdapter.getData().remove(i);
                mSelectedMemberAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    /**
     * 显示成员数量
     */
    private void setMemberCount(){
        binding.tvCount.setText(StringUtil.getStringFormat(UiUtil.getString(R.string.mine_selected_member_list), selectedMember.size()));
    }

    /**
     * 获取成员列表成功
     * @param memberBean
     */
    @Override
    public void getMemberSuccess(MemberBean memberBean) {
        if (memberBean!=null){
            List<MemberBean.UsersBean> users = memberBean.getUsers();
            for (MemberBean.UsersBean usersBean : users){
                if (CollectionUtil.isNotEmpty(members)){
                    for (AccessibleMemberBean accessibleMemberBean : members){
                        if (usersBean.getUser_id() == accessibleMemberBean.getU_id()){
                            usersBean.setSelected(true);
                            usersBean.setEnabled(false);
                        }
                    }
                }

            }
            mAllMemberAdapter.setNewData(memberBean.getUsers());
        }
    }

    /**
     * 获取成员列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getMemberFail(int errorCode, String msg) {

    }

    /**
     * 点击事件
     */
    public class OnClickHandler {
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.ivBack) { // 返回
                finish();
            }else if (viewId == R.id.tvSave){  // 保存
                if (CollectionUtil.isEmpty(selectedMember)){
                    ToastUtil.show(UiUtil.getString(R.string.mine_please_choose_member));
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("selectedMember", (Serializable) selectedMember);
                setResult(RESULT_OK, intent);
                ToastUtil.show(UiUtil.getString(R.string.home_save_success));
                finish();
            }
        }
    }
}