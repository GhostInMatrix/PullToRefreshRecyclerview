package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by shanjie on 2017/3/29.
 */
public class NetStateView extends RelativeLayout {
    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;
    private View mInnerView;
    private Context mContext;
    public final static int DATA_STATUS_LOADING = -1;
    public final static int DATA_STATUS_EMPTY = 0;
    public final static int DATA_STATUS_NORMAL = 1;
    public final static int DATA_STATUS_ERROR = 2;
    protected int curState = DATA_STATUS_NORMAL;
    private boolean isTransparentMode = true;

    public NetStateView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public NetStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();

    }

    public NetStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        mEmptyView = inflate(mContext, R.layout.common_empty, null);
        RelativeLayout.LayoutParams emptyViewLp = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mEmptyView.setLayoutParams(emptyViewLp);

        mLoadingView = inflate(mContext, R.layout.common_loading, null);
        mErrorView = inflate(mContext, R.layout.common_error, null);
        mErrorView.setLayoutParams(emptyViewLp);
        mLoadingView.setLayoutParams(emptyViewLp);
        addView(mEmptyView);
        addView(mLoadingView);
        addView(mErrorView);
        Util.hideView(mEmptyView);
        Util.hideView(mLoadingView);
        Util.hideView(mErrorView);
    }

    public void setInnerView(View innerView) {
        mInnerView = innerView;
    }

    public void customizeEmptyView(View view) {
        removeView(mEmptyView);
        mEmptyView = view;
        addView(mEmptyView);
        requestLayout();
        setNetState(curState);
    }

    public void customizeLoadingView(View view) {
        removeView(mLoadingView);
        mLoadingView = view;
        addView(mLoadingView);
        requestLayout();
        setNetState(curState);
    }

    public void customizeErrorView(View view) {
        removeView(mErrorView);
        mErrorView = view;
        addView(mErrorView);
        requestLayout();
        setNetState(curState);
    }

    public int getNetState() {
        return curState;
    }


    public void setNetState(int status) {
        curState = status;
        switch (status) {
            case DATA_STATUS_NORMAL:
                Util.showView(mInnerView);
                Util.hideView(mEmptyView);
                Util.hideView(mErrorView);
                Util.hideView(mLoadingView);

                break;
            case DATA_STATUS_EMPTY:
                Util.showView(mEmptyView);
                Util.hideView(mInnerView);
                Util.hideView(mErrorView);
                Util.hideView(mLoadingView);

                break;
            case DATA_STATUS_ERROR:
                Util.showView(mErrorView);
                Util.hideView(mEmptyView);
                Util.hideView(mInnerView);
                Util.hideView(mLoadingView);

                break;
            case DATA_STATUS_LOADING:
                Util.showView(mLoadingView);
                if (isTransparentMode) {
                    mLoadingView.bringToFront();
                } else {
                    Util.hideView(mEmptyView);
                    Util.hideView(mInnerView);
                    Util.hideView(mErrorView);
                }
                break;

            default:
                Util.hideView(mErrorView);
                Util.hideView(mEmptyView);
                Util.hideView(mInnerView);
                Util.hideView(mLoadingView);
                break;
        }
    }

    public View getmLoadingView() {
        return mLoadingView;
    }

    public View getmEmptyView() {
        return mEmptyView;
    }

    public View getmErrorView() {
        return mErrorView;
    }

    public View getmInnerView() {
        return mInnerView;
    }

    public boolean isTransparentMode() {
        return isTransparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        isTransparentMode = transparentMode;
    }


}
