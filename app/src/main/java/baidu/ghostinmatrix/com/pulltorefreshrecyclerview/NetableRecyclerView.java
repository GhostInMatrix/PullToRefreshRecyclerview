package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * Created by shanjie on 2017/3/29.
 */
@Deprecated
public class NetableRecyclerView extends RelativeLayout {
    private Context mContext;
    protected RecyclerView mRecyclerView;
    protected NetStateView mNetStateView;

    public NetableRecyclerView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public NetableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public NetableRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NetableRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        mRecyclerView.setItemAnimator(itemAnimator);
    }

    public void smoothScrollToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    private void init() {

        View view = inflate(mContext, R.layout.widget_recycler_netable_view, this);
        mNetStateView = view.findViewById(R.id.net_state_view);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mNetStateView.setInnerView(mRecyclerView);
    }

    public void notifyNetState(int state) {
        mNetStateView.setNetState(state);
    }

    public void customizeEmptyView(View view) {
        mNetStateView.customizeEmptyView(view);
    }

    public void customizeLoadingView(View view) {
        mNetStateView.customizeLoadingView(view);
    }

    public void customizeErrorView(View view) {
        mNetStateView.customizeErrorView(view);
    }

    public void setDefaultRetryClickListener(OnClickListener onClickListener) {
        mNetStateView.setDefaultRetryClickListener(onClickListener);
    }

}
