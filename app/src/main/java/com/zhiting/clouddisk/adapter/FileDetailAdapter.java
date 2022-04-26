package com.zhiting.clouddisk.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.FileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.UrlUtil;
import com.zhiting.clouddisk.util.ViewSizeUtil;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UnitUtil;
import com.zhiting.networklib.utils.imageutil.GlideUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件详情列表适配器
 */
public class FileDetailAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {
    /**
     * 0. 文件夹详情
     * 1. 上传列表
     */
    private int type;

    public FileDetailAdapter(int type) {
        super(R.layout.item_file_detail);
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, FileBean item) {
        helper.addOnClickListener(R.id.ivSelected);
        TextView tvSize = helper.getView(R.id.tvSize);
        ImageView imageView = helper.getView(R.id.ivLogo);
        ImageView ivVideo = helper.getView(R.id.ivVideo);
        ivVideo.setVisibility(View.GONE);

        if (item.getType() == 0) {
            imageView.setImageResource(R.drawable.icon_file);
            tvSize.setVisibility(View.GONE);
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
            String thumbUrl = UrlUtil.getUrl(item.getThumbnail_url());
            ViewSizeUtil.setViewSize(imageView,fileType);
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
            tvSize.setVisibility(View.VISIBLE);
        }

        long time = item.getMod_time() * 1000;
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvTime, TimeFormatUtil.long2String(time, TimeFormatUtil.DATE_FORMAT))
                .setText(R.id.tvSize, UnitUtil.getFormatSize(item.getSize()));
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ivSelected.setVisibility(type == 0 ? View.VISIBLE : View.GONE);
        ivSelected.setSelected(item.isSelected());
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
     * 获取选中的数据（单个）
     *
     * @return
     */
    public FileBean getSingleSelectedData() {
        for (FileBean fileBean : getData()) {
            if (fileBean.isSelected()) {
                return fileBean;
            }
        }
        return null;
    }

    /**
     * 获取所有选择数据
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

    /**
     * 获取选择的文件路径
     *
     * @return
     */
    public List<String> getSelectedPath() {
        List<String> paths = new ArrayList<>();
        for (FileBean fileBean : getSelectedData()) {
            paths.add(fileBean.getPath());
        }
        return paths;
    }
}
