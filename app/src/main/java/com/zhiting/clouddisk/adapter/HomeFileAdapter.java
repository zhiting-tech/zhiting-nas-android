package com.zhiting.clouddisk.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.networklib.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 首页文件列表
 */
public class HomeFileAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {

    private int type;
    private boolean showEncryptStyle;

    public HomeFileAdapter(int type, boolean showEncryptStyle) {
        super(R.layout.item_home_file);
        this.type = type;
        this.showEncryptStyle = showEncryptStyle;
    }

    public boolean isShowEncryptStyle() {
        return showEncryptStyle;
    }

    public void setShowEncryptStyle(boolean showEncryptStyle) {
        this.showEncryptStyle = showEncryptStyle;
    }

    @Override
    protected void convert(BaseViewHolder helper, FileBean item) {
        helper.addOnClickListener(R.id.ivSelected);
        int drawableRes = R.drawable.icon_gho;
        LinearLayout llParent = helper.getView(R.id.llParent);
        TextView tvSharer = helper.getView(R.id.tvSharer);
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ivSelected.setSelected(item.isSelected());
        llParent.setEnabled(item.isEnabled());
        llParent.setAlpha(item.isEnabled() ? 1 : 0.5f);
        if (item.getType() == 0){
            drawableRes = TextUtils.isEmpty(item.getFrom_user()) ? R.drawable.icon_file : R.drawable.icon_share_folder;
            tvSharer.setVisibility(TextUtils.isEmpty(item.getFrom_user()) ? View.GONE : View.VISIBLE);
            tvSharer.setText(item.getFrom_user()+ UiUtil.getString(R.string.home_share_to_me));
        }else {
            /**
             * 1. word
             * 2. excel
             * 3. ppt
             * 4. 压缩包
             * 5. 图片
             * 6. 音频
             * 7. 视频
             * 8. 文本
             *
             */
            int fileType = FileTypeUtil.fileType(item.getName());
            drawableRes = FileTypeUtil.getFileLogo(fileType);

        }

        ImageView imageView = helper.getView(R.id.ivLogo);
        ImageView ivEncrypt = helper.getView(R.id.ivEncrypt);
        imageView.setImageResource(drawableRes);
        if (showEncryptStyle) {
            ivEncrypt.setVisibility(item.getIs_encrypt() == 1 ? View.VISIBLE : View.GONE);
        }
        String name = item.getName();
        helper.setText(R.id.tvName, TextUtils.isEmpty(name) ? Constant.HOME_NAME : name);
        if (type == 0 && item.getType() == 0){
            ivSelected.setVisibility(!TextUtils.isEmpty(item.getFrom_user()) ? View.VISIBLE : View.GONE);
        }


    }

    /**
     * 获取选中的个数
     * @return
     */
    public int getSelectedSize(){
        int count = 0;
        for (FileBean fileBean : getData()){
            if (fileBean.isSelected()){
                count++;
            }
        }
        return count;
    }

    /**
     * 选中是否只有文件夹
     * @return
     */
    public boolean isOnlyFolder(){
        for (FileBean fileBean : getData()){
            if (fileBean.isSelected()){
                if (fileBean.getType() == 1){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取选中一个的实体
     * @return
     */
    public FileBean getOneSelectedData(){
        for (FileBean fileBean : getData()){
            if (fileBean.isSelected()){
               return fileBean;
            }
        }
        return null;
    }

    /**
     * 获取所有选中的数据
     * @return
     */
    public List<FileBean> getSelectedData(){
        List<FileBean> data = new ArrayList<>();
        for (FileBean fileBean : getData()){
            if (fileBean.isSelected()){
                data.add(fileBean);
            }
        }
        return data;
    }
}
