package com.zhiting.clouddisk.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiting.clouddisk.R;
import com.zhiting.clouddisk.entity.home.UploadFileBean;
import com.zhiting.clouddisk.util.FileTypeUtil;
import com.zhiting.clouddisk.util.UrlUtil;
import com.zhiting.clouddisk.util.ViewSizeUtil;
import com.zhiting.networklib.utils.TimeFormatUtil;
import com.zhiting.networklib.utils.UnitUtil;
import com.zhiting.networklib.utils.imageutil.GlideUtil;

public class UploadFileCompleteAdapter extends BaseQuickAdapter<UploadFileBean, BaseViewHolder> {

    public UploadFileCompleteAdapter() {
        super(R.layout.item_upload_complete);
    }

    @Override
    protected void convert(BaseViewHolder helper, UploadFileBean item) {
        if (item == null) return;
        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvSize, UnitUtil.getFormatSize(item.getSize()));
        helper.setText(R.id.tvTime, TimeFormatUtil.long2String(item.getCreate_time(), TimeFormatUtil.DATE_FORMAT));
        ImageView ivVideo = helper.getView(R.id.ivVideo);
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        ivVideo.setVisibility(View.GONE);
        helper.addOnClickListener(R.id.root);
        helper.addOnClickListener(R.id.tvDelete);

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
}
