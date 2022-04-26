package com.zhiting.clouddisk.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.DownLoadFileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.UrlUtil;
import com.zhiting.clouddisk.util.ViewSizeUtil;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UnitUtil;
import com.zhiting.networklib.utils.imageutil.GlideUtil;

public class DownloadFileCompleteAdapter extends BaseQuickAdapter<DownLoadFileBean, BaseViewHolder> {

    public DownloadFileCompleteAdapter() {
        super(R.layout.item_upload_complete);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownLoadFileBean item) {
        if (item != null) {
            ImageView ivLogo = helper.getView(R.id.ivLogo);
            ImageView ivVideo = helper.getView(R.id.ivVideo);
            ivVideo.setVisibility(View.GONE);

            if (item.getType().equalsIgnoreCase("dir")) {//文件夹
                helper.setImageResource(R.id.ivLogo, R.drawable.icon_file);
            } else {
                int fileType = FileTypeUtil.fileType(item.getName());
                ViewSizeUtil.setViewSize(ivLogo,fileType);
                String thumbUrl = UrlUtil.getUrl(item.getThumbnail_url());
                if (fileType == 5) {
                    GlideUtil.load(thumbUrl).error(R.drawable.icon_jpg).into(ivLogo);
                } else if (fileType == 7) {
                    GlideUtil.load(thumbUrl).error(R.drawable.icon_mp4).into(ivLogo);
                    if (!TextUtils.isEmpty(item.getThumbnail_url()))
                        ivVideo.setVisibility(View.VISIBLE);
                } else {
                    int drawableRes = FileTypeUtil.getFileLogo(fileType);
                    helper.setImageResource(R.id.ivLogo, drawableRes);
                }
            }
            helper.setText(R.id.tvName, item.getName());
            helper.setText(R.id.tvSize, UnitUtil.getFormatSize(item.getSize()));
            helper.setText(R.id.tvTime, TimeFormatUtil.long2String(item.getCreate_time(), TimeFormatUtil.DATE_FORMAT));

            helper.addOnClickListener(R.id.root);
            helper.addOnClickListener(R.id.tvDelete);
        }
    }
}
