package com.zhiting.networklib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhiting.networklib.R;
import com.zhiting.networklib.utils.UiUtil;

public class RefreshRecyclerView extends LinearLayout {

    private Context mContext;
    private SmartRefreshLayout mRefreshLayout; // 刷新布局
    private RecyclerView mRvData; // 数据展示
    private View mViewEmpty; // 空视图
    private ImageView mIvEmpty;
    private TextView mTvEmpty;
    private ImageView ivLocker;

    private boolean enabledRefresh; // 是否可刷新
    private boolean enableLoadable; // 是否可加载更多
    private int managerType;  // 布局管理器；类型
    private int spanCount;  // 如果是网格布局，每行显示几个
    private int imgEmpty;
    private String strEmpty;
    private boolean mShowLocker;

    private BaseQuickAdapter mBaseQuickAdapter; // 适配器

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_refresh_recycler, this, true);
        initAttr(context, attrs);
        initUI();
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RefreshRecyclerView);
            enabledRefresh = array.getBoolean(R.styleable.RefreshRecyclerView_enabledRefresh, true);
            enableLoadable = array.getBoolean(R.styleable.RefreshRecyclerView_enabledLoadable, true);
            mShowLocker = array.getBoolean(R.styleable.RefreshRecyclerView_showLocker, false);
            managerType = array.getInt(R.styleable.RefreshRecyclerView_managerType, 0);
            spanCount = array.getInt(R.styleable.RefreshRecyclerView_spanCount, 2);
            imgEmpty = array.getInt(R.styleable.RefreshRecyclerView_imgEmpty, R.drawable.img_no_data);
            strEmpty = array.getString(R.styleable.RefreshRecyclerView_strEmpty);
            array.recycle();
        }
    }

    /**
     * 初始化视图
     */
    private void initUI() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRvData = findViewById(R.id.rvData);
        mViewEmpty = findViewById(R.id.viewEmpty);
        mIvEmpty = mViewEmpty.findViewById(R.id.ivEmpty);
        mTvEmpty = mViewEmpty.findViewById(R.id.tvEmpty);
        mRefreshLayout.setEnableRefresh(enabledRefresh);
        mRefreshLayout.setEnableLoadMore(enableLoadable);
        mIvEmpty.setImageResource(imgEmpty);
        mTvEmpty.setText(TextUtils.isEmpty(strEmpty) ? UiUtil.getString(R.string.no_data) : strEmpty);
        ivLocker = findViewById(R.id.ivLocker);
        ivLocker.setVisibility(mShowLocker ? VISIBLE : GONE);
        initRvData();
    }

    /**
     * 初始化数据列表
     */
    private void initRvData() {
        mRvData.setLayoutManager(managerType == 0 ? new LinearLayoutManager(mContext) : new GridLayoutManager(mContext, spanCount));
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public RefreshRecyclerView setAdapter(BaseQuickAdapter adapter) {
        mBaseQuickAdapter = adapter;
        mRvData.setAdapter(adapter);
        return this;
    }

    /**
     * 设置空图
     *
     * @param drawable
     */
    public RefreshRecyclerView setIvEmptyRes(@DrawableRes int drawable) {
        mIvEmpty.setImageResource(drawable);
        return this;
    }

    /**
     * 设置空提示
     *
     * @param tips
     */
    public RefreshRecyclerView setTvEmptyStr(String tips) {
        mTvEmpty.setText(tips);
        return this;
    }

    /**
     * 设置锁图片是否可见
     * @param show
     * @return
     */
    public RefreshRecyclerView showLockerVisible(boolean show){
        mShowLocker = show;
        return this;
    }

    /**
     * 刷新和加更多监听事件
     *
     * @param refreshAndLoadMoreListener
     * @return
     */
    public RefreshRecyclerView setOnRefreshAndLoadMoreListener(OnRefreshLoadMoreListener refreshAndLoadMoreListener) {
        mRefreshLayout.setOnRefreshLoadMoreListener(refreshAndLoadMoreListener);
        return this;
    }

    /**
     * 刷新监听事件
     *
     * @param refreshListener
     * @return
     */
    public RefreshRecyclerView setOnRefreshListener(OnRefreshListener refreshListener) {
        mRefreshLayout.setOnRefreshListener(refreshListener);
        return this;
    }

    /**
     * 加载更多监听事件
     *
     * @param loadMoreListener
     * @return
     */
    public RefreshRecyclerView setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mRefreshLayout.setOnLoadMoreListener(loadMoreListener);
        return this;
    }

    /**
     * 视图控制
     *
     * @param visible true 显示空视图
     */
    public void showEmptyView(boolean visible) {
        mRvData.setVisibility(visible ? GONE : VISIBLE);
        if (mShowLocker){
            ivLocker.setVisibility(visible ? GONE : VISIBLE);
        }
        mViewEmpty.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * 设置是否可以刷新
     *
     * @param refresh
     */
    public void setEnabledRefresh(boolean refresh) {
        mRefreshLayout.setEnableRefresh(refresh);
    }

    /**
     * 设置是否可以加载更多
     *
     * @param loadMore
     */
    public void setEnableLoadable(boolean loadMore) {
        mRefreshLayout.setEnableLoadMore(loadMore);
    }

    /**
     * 设置是否可以刷新和加载更多
     *
     * @param operate
     */
    public void setRefreshAndLoadMore(boolean operate) {
        setEnabledRefresh(operate);
        setEnableLoadable(operate);
    }

    /**
     * 完成刷新和加载更多
     *
     * @param noMore 是否还有更多 true 没有更多
     */
    public void finishRefresh(boolean noMore) {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();

        if (noMore) {
            finishLoadMoreData();
        }
    }

    /**
     * 重新设置是否有更多数据
     */
    public void resetNoMoreData(){
        mRefreshLayout.resetNoMoreData();
    }

    /**
     * 没有更多数据
     */
    public void finishLoadMoreData() {
        mRefreshLayout.finishLoadMoreWithNoMoreData();
    }

    /**
     * 获取适配器
     *
     * @return
     */
    public BaseQuickAdapter getAdapter() {
        return mBaseQuickAdapter;
    }

    /**
     * 设置分割
     *
     * @param itemDecoration
     */
    public RefreshRecyclerView setItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRvData.addItemDecoration(itemDecoration);
        return this;
    }
}
