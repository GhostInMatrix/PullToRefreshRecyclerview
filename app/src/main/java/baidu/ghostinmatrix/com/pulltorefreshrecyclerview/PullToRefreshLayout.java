package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class PullToRefreshLayout extends RelativeLayout {
    protected Context mContext;
    public static final int INIT = 0;
    public static final int RELEASE_TO_REFRESH = 1;
    public static final int REFRESHING = 2;
    public static final int RELEASE_TO_LOAD = 3;
    public static final int LOADING = 4;
    public static final int DONE = 5;
    private int state = INIT;
    private OnRefreshListener mListener;
    public static final int SUCCEED = 0;
    public static final int FAIL = 1;
    private float downY, lastY;
    private float downX, lastX;

    public float pullDownY = 0;
    private float pullUpY = 0;

    private float refreshDist = 200;
    private float loadmoreDist = 200;

    private MyTimer timer;
    public float MOVE_SPEED = 8;
    private boolean isLayout = false;
    private boolean isTouch = false;
    private boolean canceled = false;
    private float radio = 2;
    private Animation mRefreshingAnimation;
    private Animation mLoadingAnimation;

    protected View refreshView;
    protected View mRefreshingView;

    protected View loadmoreView;
    protected View mLoadingView;

    protected View pullableView;
    private int mEvents;
    private boolean canPullDown = true;
    private boolean canPullUp = true;

    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.e("timer", "running");
            if (!canceled) {
                MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
                        / getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
                if (!isTouch) {
                    if (state == REFRESHING && pullDownY <= refreshDist) {
                        pullDownY = refreshDist;
                        timer.cancel();
                        Log.e("timer", "state == REFRESHING && pullDownY <= refreshDist");
                        canceled = true;
                    } else if (state == LOADING && -pullUpY <= loadmoreDist) {
                        pullUpY = -loadmoreDist;
                        timer.cancel();
                        Log.e("timer", "state == LOADING && -pullUpY <= loadmoreDist");

                        canceled = true;
                    }
                }
                if (pullDownY > 0) {
                    pullDownY -= MOVE_SPEED;

                } else if (pullUpY < 0)
                    pullUpY += MOVE_SPEED;

                if (pullDownY < 0) {
                    pullDownY = 0;
                    if (state != REFRESHING && state != LOADING) {
                        changeState(INIT);
                    }
                    timer.cancel();
                    Log.e("timer", "state != REFRESHING && state != LOADING");

                }
                if (pullUpY > 0) {
                    pullUpY = 0;
                    if (state != REFRESHING && state != LOADING) {
                        changeState(INIT);
                    }
                    timer.cancel();
                    Log.e("timer", "state != REFRESHING && state != LOADING");
                }

                if ((pullDownY == 0 && state == REFRESHING) || (pullUpY == 0 && state == LOADING)) {
                    changeState(INIT);
                    timer.cancel();
                    Log.e("timer", "(pullDownY == 0 && state == REFRESHING) || (pullUpY == 0 && state == LOADING)");

                }

                if (state == DONE) {
                    if (pullDownY == 0 && pullUpY == 0) {
                        changeState(INIT);
                        timer.cancel();
                        Log.e("timer", "pullDownY == 0 && pullUpY == 0");
                    }
                }
                requestLayout();
            }
        }

    };

    private void hide() {
        canceled = false;
        timer.schedule(5);
    }


    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public PullToRefreshLayout(Context context) {
        super(context);
        initView(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        timer = new MyTimer(updateHandler);
        mContext = context;
    }


    public void setHeaderAnimation(Animation headerAnimation, View refreshingView) {
        mRefreshingView = refreshingView;
        mRefreshingAnimation = headerAnimation;
    }

    public void setFooterAnimation(Animation footerAnimation, View loadingView) {
        mLoadingView = loadingView;
        mLoadingAnimation = footerAnimation;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isLayout) {
            refreshView = getChildAt(0);
            pullableView = getChildAt(1);
            loadmoreView = getChildAt(2);
            if (!(pullableView instanceof Pullable)) {
                try {
                    throw new ClassNotPullableException("pullableView is not a Class of Pullable");
                } catch (ClassNotPullableException e) {
                    e.printStackTrace();
                }
            }
            isLayout = true;

            refreshDist = ((ViewGroup) refreshView).getChildAt(0)
                    .getMeasuredHeight();
            loadmoreDist = ((ViewGroup) loadmoreView).getChildAt(0)
                    .getMeasuredHeight();
        }
        refreshView.layout(0,
                (int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
                refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
        pullableView.layout(0, (int) (pullDownY + pullUpY),
                pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
                        + pullableView.getMeasuredHeight());
        loadmoreView.layout(0,
                (int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
                loadmoreView.getMeasuredWidth(),
                (int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
                        + loadmoreView.getMeasuredHeight());
    }


    public void refreshFinish(int refreshResult) {
        if (mRefreshingView == null)
            return;
        switch (refreshResult) {
            case SUCCEED:
                break;
            case FAIL:
                break;
            default:
                break;
        }
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                changeState(DONE);
                hide();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    public void loadmoreFinish(int refreshResult) {
        if (mLoadingView == null)
            return;
//        mRefreshingView.clearAnimation();
//        mLoadingView.setVisibility(View.GONE);
        switch (refreshResult) {
            case SUCCEED:
                break;
            case FAIL:
                break;
            default:
                break;
        }
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                changeState(DONE);
                hide();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    private void changeState(int to) {
        state = to;
        switch (state) {
            case INIT:
                mRefreshingView.clearAnimation();
                mLoadingView.clearAnimation();
                break;
            case RELEASE_TO_REFRESH:
                if (mRefreshingAnimation != null)
                    mRefreshingView.startAnimation(mRefreshingAnimation);
                break;
            case REFRESHING:
                break;
            case RELEASE_TO_LOAD:
                if (mLoadingAnimation != null)
                    mLoadingView.startAnimation(mLoadingAnimation);
                break;
            case LOADING:
                if (mLoadingAnimation != null)
                    mLoadingView.startAnimation(mLoadingAnimation);
                break;
            case DONE:
                mRefreshingView.clearAnimation();
                mLoadingView.clearAnimation();
                break;
        }
    }

    private void releasePull() {
        canPullDown = true;
        canPullUp = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean rst = false;
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                lastY = downY;

                downX = ev.getX();
                lastX = downX;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - lastX) < Math.abs(ev.getY() - lastY)) {
                    if (ev.getY() > lastY) {
                        if (((Pullable) pullableView).canPullDown() || state == REFRESHING || state == LOADING)
                            rst = true;
                        else {
                            rst = false;
                        }
                    } else {
                        if (((Pullable) pullableView).canPullUp() || state == LOADING || state == REFRESHING) {
                            rst = true;
                        } else {
                            rst = false;
                        }
                    }
                } else return false;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return rst;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e("shanjie", "PullToRefreshLayout onTouchEvent");

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                lastY = downY;
                timer.cancel();
                Log.e("timer", "init");

                mEvents = 0;
                releasePull();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mEvents == 0) {
                    if (((Pullable) pullableView).canPullDown() && canPullDown
                            && state != LOADING) {
                        pullDownY = pullDownY + (ev.getY() - lastY) / radio;
                        if (pullDownY < 0) {
                            pullDownY = 0;
                            canPullDown = false;
                            canPullUp = true;
                        }
                        if (pullDownY > getMeasuredHeight())
                            pullDownY = getMeasuredHeight();
                        if (state == REFRESHING) {
                            isTouch = true;
                        }
                    } else if (((Pullable) pullableView).canPullUp() && canPullUp
                            && state != REFRESHING) {
                        pullUpY = pullUpY + (ev.getY() - lastY) / radio;
                        if (pullUpY > 0) {
                            pullUpY = 0;
                            canPullDown = true;
                            canPullUp = false;
                        }
                        if (pullUpY < -getMeasuredHeight())
                            pullUpY = -getMeasuredHeight();
                        if (state == LOADING) {
                            isTouch = true;
                        }
                    } else
                        releasePull();
                } else
                    mEvents = 0;
                lastY = ev.getY();
                radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                        * (pullDownY + Math.abs(pullUpY))));
                requestLayout();
                if (pullDownY <= refreshDist && state == RELEASE_TO_REFRESH) {
                    changeState(INIT);
                }
                if (pullDownY >= refreshDist && state == INIT) {
                    changeState(RELEASE_TO_REFRESH);
                }
                if (-pullUpY <= loadmoreDist && state == RELEASE_TO_LOAD) {
                    changeState(INIT);
                }
                if (-pullUpY >= loadmoreDist && state == INIT) {
                    changeState(RELEASE_TO_LOAD);
                }
                if ((pullDownY + Math.abs(pullUpY)) > 8) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pullDownY > refreshDist || -pullUpY > loadmoreDist) {
                    isTouch = false;
                    hide();
                } else if (pullDownY > 0 || -pullUpY > 0) {
                    hide();
                }
                if (state == RELEASE_TO_REFRESH) {
                    changeState(REFRESHING);
                    if (mListener != null)
                        mListener.onRefresh(this);
                    hide();
                } else if (state == RELEASE_TO_LOAD) {
                    changeState(LOADING);
                    if (mListener != null)
                        mListener.onLoadMore(this);
                    hide();
                }

//                hide();

            default:
                break;
        }
//        return super.onTouchEvent(ev);
        return true;
    }

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);

        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }
    }


    public interface OnRefreshListener {
        void onRefresh(PullToRefreshLayout pullToRefreshLayout);

        void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
    }

    public class ClassNotPullableException extends Exception {
        ClassNotPullableException(String errorMsg) {
            super(errorMsg);
        }
    }

}