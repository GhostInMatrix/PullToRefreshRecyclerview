package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;



/**
 * Created by shanjie on 2017/4/20.
 */
public class PullToRefreshRecyclerView extends PullToRefreshLayout {

    protected Context mContext;

    private boolean allowRefreshDefault;
    private boolean allowLoadMoreDefault;

    public PullToRefreshRecyclerView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public void setAllowRefresh(boolean allowRefresh) {
        ((PullableRecyclerView) pullableView).setAllowRefresh(allowRefresh);
        allowRefreshDefault = allowRefresh;
    }

    public void setAllowLoad(boolean allowLoad) {
        ((PullableRecyclerView) pullableView).setAllowLoad(allowLoad);
        allowLoadMoreDefault = allowLoad;
    }

    @Override
    public void refreshFinish(int refreshResult) {
        super.refreshFinish(refreshResult);
        switch (refreshResult){
            case EMPTY:
            case FAIL:
                setAllowLoad(false);
                setAllowRefresh(false);
                break;
            case SUCCEED:
                setAllowLoad(allowLoadMoreDefault);
                setAllowRefresh(allowRefreshDefault);
                break;
        }
    }

    private void init() {
        inflate(mContext, R.layout.pull_to_refresh_recyclerview, this);
        pullableView = findViewById(R.id.pullable_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ((PullableRecyclerView) pullableView).setLayoutManager(linearLayoutManager);
        View header = findViewById(R.id.recyclerview_header);
        View footer = findViewById(R.id.recyclerview_footer);
        View loadingHeader = header.findViewById(R.id.refreshing_icon);
        View loadingFooter = footer.findViewById(R.id.loading_icon);
        RotateAnimation refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.rotating);
        refreshingAnimation.setInterpolator(new LinearInterpolator());
        setHeaderAnimation(refreshingAnimation, loadingHeader);
        setFooterAnimation(refreshingAnimation, loadingFooter);

        allowRefreshDefault = ((PullableRecyclerView) pullableView).isAllowRefresh();
        allowLoadMoreDefault = ((PullableRecyclerView) pullableView).isAllowLoad();
    }


    public void setAdapter(RecyclerView.Adapter adapter) {
        ((PullableRecyclerView) pullableView).setAdapter(adapter);

    }

    public RecyclerView.Adapter getAdapter() {
        return ((PullableRecyclerView) pullableView).getAdapter();
    }

    public PullableRecyclerView getPullableRecyclerView() {
        return (PullableRecyclerView) pullableView;
    }

    public void customizeEmptyView(View view) {
        ((PullableRecyclerView) pullableView).customizeEmptyView(view);
    }

    public void customizeLoadingView(View view) {
        ((PullableRecyclerView) pullableView).customizeLoadingView(view);
    }

    public void customizeErrorView(View view) {
        ((PullableRecyclerView) pullableView).customizeErrorView(view);
    }



}
