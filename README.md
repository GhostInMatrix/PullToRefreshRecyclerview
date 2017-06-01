# PullToRefreshRecyclerview
原生RecyclerView无法支持下拉刷新及上拉加载等操作，需要封装才能支持。考虑到不仅仅是RecyclerView可能需要该操作，任何一个View都有可能需要，因此将上下拉设计为一个可容纳三个子View的容器（headerView，innerView和footerView）。

![PullToRefreshRecyclerView总体思路](http://upload-images.jianshu.io/upload_images/6070809-477df6ff3fd6e505.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##NetableView
封装了三个状态view（Loading、Empty、Error）并从外部传入一个innerView（可以是任意View，作为内容显示的view）。可通过```setNetState(int state)```控制状态页面的展示。状态类型如下：
-  DATA_STATUS_LOADING = -1;
-  DATA_STATUS_EMPTY = 0;
-  DATA_STATUS_NORMAL = 1;
-  DATA_STATUS_ERROR = 2;


##NetableRecyclerView
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
##Pullable接口
任何放入PullToRefreshLayout作为innerView的控件都需要实现Pullable接口，使得容器能够判断innerView是否能够进行pullDown和pullUp动作。innerView需要借此控制是否能够进行下拉或上拉操作，返回false则无法进行对应的操作。一般情况下，实现Pullable接口作为innerView的视图控件还要处理与PullToRefreshLayout的滑动事件分发，这个后面再说。
```
public interface Pullable {
    boolean canPullDown();
    boolean canPullUp();
}
```
##PullableRecyclerView
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

##PullToRefreshLayout
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

##PullToRefreshRecyclerView

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

