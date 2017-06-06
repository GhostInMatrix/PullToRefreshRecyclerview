# PullToRefreshRecyclerview
原生RecyclerView无法支持下拉刷新及上拉加载等操作，需要封装才能支持。考虑到不仅仅是RecyclerView可能需要该操作，任何一个View都有可能需要，因此将上下拉设计为一个可容纳三个子View的容器（headerView，innerView和footerView）。

![PullToRefreshRecyclerView总体思路](http://upload-images.jianshu.io/upload_images/6070809-477df6ff3fd6e505.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

NetableView
-----------
封装了三个状态view（Loading、Empty、Error）并从外部传入一个innerView（可以是任意View，作为内容显示的view）。可通过```setNetState(int state)```控制状态页面的展示。状态类型如下：
-  DATA_STATUS_LOADING = -1;
-  DATA_STATUS_EMPTY = 0;
-  DATA_STATUS_NORMAL = 1;
-  DATA_STATUS_ERROR = 2;


NetableRecyclerView
-----------
组合了RecyclerView及NetStateView，并将RecyclerView传入NetStateView以进行状态统一管控。通过提供的``` notifyNetState(int state) ```可直接更新页面数据状态。```setDefaultRetryClickListener（）```可设置默认Error页面的重试监听器。
通过以下三方法可以自定义各状态页面，并且调用立刻生效且不会影响当前数据显示状态：
```
    public void customizeEmptyView(View view) {
        mNetStateView.customizeEmptyView(view);
    }
    public void customizeLoadingView(View view) {
        mNetStateView.customizeLoadingView(view);
    }
    public void customizeErrorView(View view) {
        mNetStateView.customizeErrorView(view);
    }
```
Pullable接口
-----------
任何放入PullToRefreshLayout作为innerView的控件都需要实现Pullable接口，使得容器能够判断innerView是否能够进行pullDown和pullUp动作。innerView需要借此控制是否能够进行下拉或上拉操作，返回false则无法进行对应的操作。一般情况下，实现Pullable接口作为innerView的视图控件还要处理与PullToRefreshLayout的滑动事件分发，这个后面再说。
```
public interface Pullable {
    boolean canPullDown();
    boolean canPullUp();
}
```
PullableRecyclerView
-----------
介绍了Pullable接口，下面介绍主要成员——PullableRecyclerView。类图如下：
![PullableRecyclerView继承关系](http://upload-images.jianshu.io/upload_images/6070809-b8ea8fe065347255.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

作为下拉刷新的主体View，它需要具备的功能包含：显示数据不同状态页面（Empty、Error、Loading及Normal）；在Normal状态下，RecyclerView上拉至顶部的下拉刷新及下拉至分页处的上拉加载；Empty状态下的下拉刷新。
1.  为做到以上几点，PullableRecyclerView继承NetableRecyclerView，实现Pullable接口。
2.  功能管理。


```
    //初始化
    private boolean mCanRefresh = true;
    private boolean mCanLoad = true;

    private boolean mAllowRefresh = true;
    private boolean mAllowLoad = true;
```

为了适应多种场景下的使用，设置了```setAllowRefresh(boolean allowRefresh)```和```setAllowLoad(boolean allowLoad)```方法，用来控制**是否启用上拉下拉的能力**，即只有（allowRefresh&&mCanRefresh）为true才能够进入下拉状态，Load同理。
3.  重写了```dispatchTouchEvent(MotionEvent ev)```，但没有影响任何触摸事件传递，只不过是在MotionEvent为MOVE_DOWN的时候进行了是否进入上拉或下拉状态的判断（mCanRefresh和mCanLoad）。

```
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
                if ((((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition()) == 0) {
                    mCanRefresh = true;
                } else {
                    mCanRefresh = false;
                }

                if ((((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition()) == getAdapter().getItemCount() - 1) {
                    mCanLoad = true;
                } else {
                    mCanLoad = false;
                }
                break;
        }


        return super.dispatchTouchEvent(ev);
    }
```

这就需要保证在MOVE_DOWN事件发生时，ViewGroup不能拦截，而要允许其透传到子View的dispatchTouchEvent中。至于PullToRefreshLayout中如何做到，详见__PullToRefreshLayout__。

PullToRefreshLayout
-----------
最后介绍最最重要的一个ViewGroup——封装了下拉和上拉的操作的PullToRefreshLayout。作为一个容器，可在xml中按顺序加入三个子view（headerView，innerView及footerView）。使用如下，示例中加入了按照上述原理封装好的WebView作为innerView：

```
	<com.baidu.lbs.widget.PullToRefreshLayout
		android:id="@+id/pull_to_refresh_layout"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		>
		<include
			android:id="@+id/pull_header"
			layout="@layout/refresh_head"/>
		<com.baidu.lbs.commercialism.bridge.WMWebView
			android:id="@+id/common_webview"
			android:layout_width="match_parent"
			android:layout_height="match_parent"/>
		<include
			android:id="@+id/pull_footer"
			layout="@layout/load_more"/>
	</com.baidu.lbs.widget.PullToRefreshLayout>
	
```

在PullToRefreshLayout首次onLayout渲染的时候通过getChildAt（）获取内部View，依次得到headerView，innerView及footerView。
在PullToRefreshLayout中实现了如下功能：
-  判断是否需要拦截触摸事件
-  拦截触摸事件后，处理下拉或上拉视图
-  下拉、上拉过程的状态和动画效果
为做到第一点，需要重写```onInterceptTouchEvent（）```方法，MotionEvent.ACTION_DOWN时，不进行任何拦截，使得动作能够透传至子View中（PullableRecyclerView的dispatchTouchEvent方法能够得到调用）如下：

```

@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean rst = false;   //  默认不拦截
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: //  按下事件，不拦截
                downY = ev.getY();
                lastY = downY;
                downX = ev.getX();
                lastX = downX;
                break;
            case MotionEvent.ACTION_MOVE: 
                //若纵向滑动偏移量大于横向滑动偏移量，忽略横向滑动；解决了既有纵向滑动又有横向滑动的过敏问题（比如：item的横向滑动删除效果，如果没有该判断，将会很容易在斜滑的时候触发横向逻辑）
                if (Math.abs(ev.getX() - lastX) < Math.abs(ev.getY() - lastY)) {
                    if (ev.getY() > lastY) {
                        //若innerView处于canPullDown状态、或当前状态为刷新中或加载中，则触摸事件被拦截下来，由该类自行控制，不再分发给子view。
                        if (((Pullable) pullableView).canPullDown() || state == REFRESHING || state == LOADING)
                            rst = true;
                        else {
                            rst = false;
                        }
                    } else {
                        //同下拉刷新
                        if (((Pullable) pullableView).canPullUp() || state == LOADING || state == REFRESHING) {
                            rst = true;
                        } else {
                            rst = false;
                        }
                    }
                } else 
                      return false;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return rst;
    }
    
```

第二点和第三点其实是一回事，即在触摸事件拦截下来后，控制权掌握在了ViewGroup自己手里，如何处理滑动动效及当前视图状态的问题。
1.  重写```onTouchEvent（）```处理触摸态下视图更改。
2.  处理手松开后，视图的更改，借助Timer、Handler、Task实现。（具体实现方式以后再讲）

PullToRefreshRecyclerView
-----------

![PullToRefreshRecyclerView类图](http://upload-images.jianshu.io/upload_images/6070809-28fd7ba3c34dc1b0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

继承PullToRefreshLayout，封装了一套默认header和footer布局，并以PullableRecyclerView为innerView。布局如下：

```
<merge
	xmlns:android="http://schemas.android.com/apk/res/android"
	android:layout_width="match_parent"
	android:layout_height="match_parent"
	>
	<include
		android:id="@+id/recyclerview_header"
		layout="@layout/refresh_head"/>

	<com.baidu.lbs.widget.recyclerview.PullableRecyclerView
		android:id="@+id/pullable_recycler_view"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:background="@color/transparent"
		android:divider="@null"
		android:dividerPadding="0dp"
		android:showDividers="none"
		>

	</com.baidu.lbs.widget.recyclerview.PullableRecyclerView>
	<include
		android:id="@+id/recyclerview_footer"
		layout="@layout/load_more_2"/>

</merge>
```

PullToRefreshRecyclerView 初始化直接使用的是xml布局渲染的方式，定制了一套header和footer布局。merge之后，该类本身即为xml布局文件中三个子view的父布局。因此在PullToRefreshLayout首次onLayout获取子view的时候即可拿到对应内容。



打造高效通用RecyclerView.Adapter及ViewHolder
--------------------------------------------

在使用RecyclerView的过程中，每一个列表Adapter，每一个样式都要重新编写对应的ViewHolder，去重新实现一遍onCreateViewHolder、onBindViewHolder等方法，这样做实在是劳心费神，非常麻烦。况且，当你希望在RecyclerView中加入动画的时候，（如：左右横滑，长按拖动，置顶，删除等）会发现还需要继承实现ItemTouchHelper.Callback（如果你知道要用它的话），再自行处理其中的dataset相关的变更逻辑，那简直是雪上加霜啊。为解决这些痛点，我们要对RecyclerView.ViewHolder、RecyclerView.Adapter进行封装，做到在绝大多数情况下，一次编写，到处复用。

-	通用ViewHolder

如何能做到通用呢？即不再需要定制ViewHolder，甚至不再需要在你的代码中new出任何ViewHolder，在用到ViewHolder中的任何一个控件都不用再每次都使用```convertView.findViewById(id)```，因为即便ViewHolder中的布局很简单，也会有性能损耗（想想findViewById的原理）。但做到这两点优化并不难：ViewHolder的设计初衷就是缓存布局文件的各个控件，方便查找和设置内容。因此我们在遵循该初衷的基础上更进一步，传入**layoutId**，在ViewHolder初始化时渲染好ItemView；设置缓存机制（使用SparseArray，int-Obj 对应的映射表），即在ViewHolder内部就能直接获取到并返回所需要用到的每一个控件，这样就完成了ComViewHolder的封装，代码如下。


```
public class ComViewHolder extends RecyclerView.ViewHolder {
    private SparseArrayCompat<View> mViews;
    private View mConvertView;

    public ComViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArrayCompat<>();
    }

    public static ComViewHolder getComViewHolder(Context context, int layoutId, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ComViewHolder(context, itemView, parent);
    }

    /**
     * 缓存+提取
     * @param layoutId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int layoutId) {
        View view = mViews.get(layoutId);
        if (view == null) {
            view = mConvertView.findViewById(layoutId);
            mViews.put(layoutId, view);
        }
        return (T) view;
    }
}
```


-	ComRecyclerViewAdapter


通用的Adapter，在使用上述ComViewHolder之后，就避免了手写onCreateViewHolder（）和onBindViewHolder（）方法的处境，取而代之的是暴露一个虚方法convert（）给业务代码，在convert（）方法中进行对应item的控件操作。由于ComViewHolder提供了静态方法```getComViewHolder（context，layoutId，viewGroup）```并返回ComViewHolder实例，因此在Adapter在任何需要初始化ViewHolder场景的情况下，都能直接使用getComViewHolder。相关方法实现如下：


```
    //init
    protected List<E> mGroup;
    protected Context mContext;
    protected int mLayoutId;

    public ComRecyclerViewAdapter(Context context, int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
        mGroup = new ArrayList<>();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ComViewHolder.getComViewHolder(mContext, mLayoutId, parent);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        convert((ComViewHolder) holder, mGroup.get(position), getItemViewType(position), position);
    }

    public abstract void convert(ComViewHolder viewHolder, E data, int type, int position);

```


当然，一开始的时候说过，除了布局渲染和复用方面有所优化，在动画效果方面也有相关的封装。在讲完动效相关封装后再回来说明。

-	SimpleItemTouchHelperCallback


RecyclerView最突出的特性之一就在于它提供了比ListView更友好跟方便的动画效果辅助类：ItemTouchHelper及ItemTouchHelper.Callback。基于这个好用而方便的特性当然要加以利用，我们封装了一套SimpleItemTouchHelperCallback，继承ItemTouchHelper.Callback，将常用操作：长按拖动、左右横扫删除等操作及相关的衍生操作（如：置顶）封装进去，并获得手势松开时的回调用来执行后续操作。此外，该类还需要能够在初始化时控制上述两种手势开关。


```

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    //一个实现了ItemTouchHelperAdapter接口协议的任意Adapter    
    private ItemTouchHelperAdapter mAdapter; 
    
    //控制两种手势的开关
    private boolean mCanDrag;
    private boolean mCanSwipe;
    
    //手势松开释放的监听
    private OnSelectEndListener mOnSelectEndListener;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, boolean canDrag, boolean canSwipe) {
        mAdapter = adapter;
        mCanDrag = canDrag;
        mCanSwipe = canSwipe;
    }

    public void setOnSelectEndListener(OnSelectEndListener onSelectEndListener) {
        mOnSelectEndListener = onSelectEndListener;
    }

    
    //默认支持竖直方向上下,水平方向左右动作
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    //用于两个item交换位置，若两item属于纵向列表方向，则为上下交换。
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onItemSwap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
     //两个item交换位置后回调（注意跟手势释放的回调加以区分）
    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }


    //用于扫动某个item,可根据direction自行定制,这里未区分（若列表为纵向，则为横扫）
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    //控制能否长按拖动
    @Override
    public boolean isLongPressDragEnabled() {
        return mCanDrag;
    }

    //控制能否扫动
    @Override
    public boolean isItemViewSwipeEnabled() {
        return mCanSwipe;
    }
   
    
    //判断手势是否放开对应item的ViewHolder,一般用于拖动情况中
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (viewHolder == null) {
            if (mOnSelectEndListener != null) {
                mOnSelectEndListener.onSelectEnd();
            }
        } else {
            Log.e("ss","start");
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    public interface OnSelectEndListener {
        void onSelectEnd();
    }
}

```

-	再谈ComRecyclerViewAdapter


为了能够使用SimpleItemTouchHelper，令ComRecyclerViewAdapter实现接口```ItemTouchHelperAdapter```及相关协议：

```
public interface ItemTouchHelperAdapter {
    void onItemTop(int fromPosition);

    void onItemDismiss(int position);

    void onItemSwap(int itemAPosition, int itemBPosition);
}

```

最终得到可支持通用动效的高度复用的ComRecyclerViewAdapter，这其中无非就是两项，ViewHolder及ItemTouchHelperAdapter协议，是不是非常简单？

```

public abstract class ComRecyclerViewAdapter<E> extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    protected List<E> mGroup;
    protected Context mContext;
    protected int mLayoutId;

    public ComRecyclerViewAdapter(Context context, int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
        mGroup = new ArrayList<>();
    }

    public ComRecyclerViewAdapter(Context context, int layoutId, List<E> datas) {
        this(context, layoutId);
        if (datas == null)
            mGroup = new ArrayList<>();
        else
            mGroup = datas;
    }

    public void setGroup(List<E> group) {
        mGroup = group;
        notifyDataSetChanged();
    }

    /**
     * 置顶
     * @param fromPosition
     */
    @Override
    public void onItemTop(int fromPosition) {
        E data = mGroup.get(fromPosition);
        for (int i = fromPosition; i > 0; i--) {
            mGroup.set(i, mGroup.get(i - 1));
        }
        mGroup.set(0, data);
        notifyItemMoved(fromPosition, 0);
    }


    /**
     * 拖动交换
     * @param itemAPosition
     * @param itemBPosition
     */
    @Override
    public void onItemSwap(int itemAPosition, int itemBPosition) {
        Collections.swap(mGroup, itemAPosition, itemBPosition);
        notifyItemMoved(itemAPosition, itemBPosition);
    }

    /**
     * 删除
     * @param position
     */
    @Override
    public void onItemDismiss(int position) {
        mGroup.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ComViewHolder.getComViewHolder(mContext, mLayoutId, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        convert((ComViewHolder) holder, mGroup.get(position), getItemViewType(position), position);
    }

    public abstract void convert(ComViewHolder viewHolder, E data, int type, int position);


    @Override
    public int getItemCount() {
        return mGroup.size();
    }

    @Override
    public long getItemId(int position) {
        return getItemIdFromData(mGroup.get(position));
    }

    public long getItemIdFromData(E data) {
        return RecyclerView.NO_ID;
    }

}
```

后续还会继续对多类型及涉及到Header和Footer的Adapter如何在现有的控件上封装。
