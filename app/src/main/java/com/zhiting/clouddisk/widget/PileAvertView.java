package com.zhiting.clouddisk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import com.zhiting.clouddisk.R;
import com.zhiting.networklib.utils.imageutil.GlideUtil;
import com.zhiting.networklib.widget.CircleImageView;

import java.util.List;

public class PileAvertView extends LinearLayout {

    private LinearLayout llParent;
    private PileView pileView;

    private Context context = null;
    public static final int VISIBLE_COUNT = 10;//默认显示个数

    public PileAvertView(Context context) {
        this(context, null);
        this.context = context;
    }

    public PileAvertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_group_pile_avert, this);
        pileView = view.findViewById(R.id.pile_view);
        llParent = view.findViewById(R.id.llParent);
    }

    public void setPileGravity(int gravity){
        llParent.setGravity(gravity);
    }

    public void setAvertImages(List<String> imageList) {
        setAvertImages(imageList,VISIBLE_COUNT);
    }

    //如果imageList>visiableCount,显示List最上面的几个
    public void setAvertImages(List<String> imageList, int visibleCount) {
        List<String> visibleList = null;
        if (imageList.size() > visibleCount) {
            visibleList = imageList.subList(imageList.size() - 1 - visibleCount, imageList.size() - 1);
        }
        pileView.removeAllViews();
        for (int i = 0; i < imageList.size(); i++) {
            CircleImageView image= (CircleImageView) LayoutInflater.from(context).inflate(R.layout.item_group_round_avert, pileView, false);
//            GlideUtil.load(imageList.get(i)).override(R.drawable.icon_mine_avatar).into(image);
            image.setImageResource(i<5 ? R.drawable.icon_mine_avatar : R.drawable.icon_more_avatar);
            pileView.addView(image);
        }
    }

}
