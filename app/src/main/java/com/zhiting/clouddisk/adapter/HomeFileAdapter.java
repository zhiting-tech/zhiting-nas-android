package com.zhiting.clouddisk.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.UrlUtil;
import com.zhiting.clouddisk.util.ViewSizeUtil;
import com.zhiting.networklib.utils.UiUtil;
import com.zhiting.networklib.utils.imageutil.GlideUtil;

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

    public void setShowEncryptStyle(boolean showEncryptStyle) {
        this.showEncryptStyle = showEncryptStyle;
    }

    @Override
    protected void convert(BaseViewHolder helper, FileBean item) {
        LinearLayout llParent = helper.getView(R.id.llParent);
        TextView tvSharer = helper.getView(R.id.tvSharer);
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ImageView imageView = helper.getView(R.id.ivLogo);
        ImageView ivEncrypt = helper.getView(R.id.ivEncrypt);
        ImageView ivVideo = helper.getView(R.id.ivVideo);
        helper.addOnClickListener(R.id.ivSelected);

        llParent.setEnabled(item.isEnabled());
        ivSelected.setSelected(item.isSelected());
        llParent.setAlpha(item.isEnabled() ? 1 : 0.5f);
        ivVideo.setVisibility(View.GONE);

        if (item.getType() == 0) {
            int drawableRes = TextUtils.isEmpty(item.getFrom_user()) ? R.drawable.icon_file : R.drawable.icon_share_folder;
            tvSharer.setVisibility(TextUtils.isEmpty(item.getFrom_user()) ? View.GONE : View.VISIBLE);
            tvSharer.setText(item.getFrom_user() + UiUtil.getString(R.string.home_share_to_me));
            imageView.setImageResource(drawableRes);
        } else {
            /**
             * 1. word
             * 2. excel
             * 3. ppt
             * 4. 压缩包
             * 5. 图片
             * 6. 音频
             * 7. 视频
             * 8. 文本
             */
            int fileType = FileTypeUtil.fileType(item.getName());
            ViewSizeUtil.setViewSize(imageView,fileType);
            String thumbUrl = UrlUtil.getUrl(item.getThumbnail_url());
            if (fileType == 5) {
                GlideUtil.load(thumbUrl).error(R.drawable.icon_jpg).into(imageView);
            } else if (fileType == 7) {
                GlideUtil.load(thumbUrl).error(R.drawable.icon_mp4).into(imageView);
                if (!TextUtils.isEmpty(item.getThumbnail_url()))
                    ivVideo.setVisibility(View.VISIBLE);
            } else {
                int drawableRes = FileTypeUtil.getFileLogo(fileType);
                imageView.setImageResource(drawableRes);
            }
        }

        if (showEncryptStyle) {
            ivEncrypt.setVisibility(item.getIs_encrypt() == 1 ? View.VISIBLE : View.GONE);
        }
        String name = item.getName();
        helper.setText(R.id.tvName, TextUtils.isEmpty(name) ? Constant.HOME_NAME : name);
        if (type == 0 && item.getType() == 0) {
            ivSelected.setVisibility(!TextUtils.isEmpty(item.getFrom_user()) ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 获取选中的个数
     *
     * @return
     */
    public int getSelectedSize() {
        int count = 0;
        for (FileBean fileBean : getData()) {
            if (fileBean.isSelected()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 选中是否只有文件夹
     *
     * @return
     */
    public boolean isOnlyFolder() {
        for (FileBean fileBean : getData()) {
            if (fileBean.isSelected()) {
                if (fileBean.getType() == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取所有选中的数据
     *
     * @return
     */
    public List<FileBean> getSelectedData() {
        List<FileBean> data = new ArrayList<>();
        for (FileBean fileBean : getData()) {
            if (fileBean.isSelected()) {
                data.add(fileBean);
            }
        }
        return data;
    }
}
