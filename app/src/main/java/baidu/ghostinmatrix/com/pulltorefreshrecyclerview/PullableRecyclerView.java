package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by shanjie on 2017/3/15.
 */
public class PullableRecyclerView extends NetableRecyclerView implements Pullable {
    private boolean mCanRefresh = true;
    private boolean mCanLoad = true;

    private boolean mAllowRefresh = true;
    private boolean mAllowLoad = true;

    public PullableRecyclerView(Context context) {
        super(context);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAllowRefresh(boolean allowRefresh) {
        mAllowRefresh = allowRefresh;
    }

    public void setAllowLoad(boolean allowLoad) {
        mAllowLoad = allowLoad;
    }

    public boolean isAllowRefresh() {
        return mAllowRefresh;
    }

    public boolean isAllowLoad() {
        return mAllowLoad;
    }

    @Override
    public boolean canPullDown() {
        return mAllowRefresh && mCanRefresh;
    }


    @Override
    public boolean canPullUp() {
        return mAllowLoad && mCanLoad;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (mNetStateView.getNetState()) {
            case NetStateView.DATA_STATUS_EMPTY:
                mCanRefresh = true;
                mCanLoad = false;
                break;
            case NetStateView.DATA_STATUS_ERROR:
                mCanRefresh = false;
                mCanLoad = false;
                break;
            case NetStateView.DATA_STATUS_LOADING:
                mCanRefresh = false;
                mCanLoad = false;
                break;
            case NetStateView.DATA_STATUS_NORMAL:

                if (!mRecyclerView.canScrollVertically(-1)) {
                    mCanRefresh = true;
                } else {
                    mCanRefresh = false;
                }

                if (!mRecyclerView.canScrollVertically(1)) {
                    mCanLoad = true;
                } else {
                    mCanLoad = false;
                }
                break;
        }


        return super.dispatchTouchEvent(ev);
    }

}
