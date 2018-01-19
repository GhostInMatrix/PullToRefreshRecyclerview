package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


/**
 * Created by shanjie on 2017/3/29.
 */
public class NetStateView extends RelativeLayout {
    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;
    private View mInnerView;
    private LayoutParams mCommonViewLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

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
        mEmptyView.setLayoutParams(mCommonViewLp);

        mLoadingView = inflate(mContext, R.layout.pop_loading, null);
        mErrorView = inflate(mContext, R.layout.common_error, null);
        mErrorView.setLayoutParams(mCommonViewLp);
        mLoadingView.setLayoutParams(mCommonViewLp);
        addView(mEmptyView);
        addView(mLoadingView);
        addView(mErrorView);
        mEmptyView.setVisibility(GONE);
        mLoadingView.setVisibility(GONE);
        mErrorView.setVisibility(GONE);
    }

    public void setInnerView(View innerView) {
        mInnerView = innerView;
    }

    public void customizeEmptyView(View view) {
        removeView(mEmptyView);
        mEmptyView = view;
        mEmptyView.setLayoutParams(mCommonViewLp);
        addView(mEmptyView);
        requestLayout();
        setNetState(curState);
    }

    public void customizeLoadingView(View view) {
        removeView(mLoadingView);
        mLoadingView = view;
        mLoadingView.setLayoutParams(mCommonViewLp);
        addView(mLoadingView);
        requestLayout();
        setNetState(curState);
    }

    public void customizeErrorView(View view) {
        removeView(mErrorView);
        mErrorView = view;
        mErrorView.setLayoutParams(mCommonViewLp);
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
                mInnerView.setVisibility(VISIBLE);
                mEmptyView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
                mLoadingView.setVisibility(GONE);
                break;
            case DATA_STATUS_EMPTY:
                mEmptyView.setVisibility(VISIBLE);
                mInnerView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
                mLoadingView.setVisibility(GONE);
                break;
            case DATA_STATUS_ERROR:
                mErrorView.setVisibility(VISIBLE);
                mInnerView.setVisibility(GONE);
                mEmptyView.setVisibility(GONE);
                mLoadingView.setVisibility(GONE);

                break;
            case DATA_STATUS_LOADING:
                mLoadingView.setVisibility(VISIBLE);
                if (isTransparentMode) {
                    mLoadingView.bringToFront();
                } else {
                    mEmptyView.setVisibility(GONE);
                    mInnerView.setVisibility(GONE);
                    mErrorView.setVisibility(GONE);
                }
                break;

            default:
                mEmptyView.setVisibility(GONE);
                mInnerView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
                mLoadingView.setVisibility(GONE);
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

    public void setDefaultRetryClickListener(OnClickListener onClickListener) {
        Button btn = (Button) mErrorView.findViewById(R.id.error_retry_btn);
        btn.setOnClickListener(onClickListener);
    }
}
