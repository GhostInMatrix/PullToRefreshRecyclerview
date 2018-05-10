package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.pulltorefreshlayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.RelativeLayout
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.PullToRefreshLayout
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.Pullable

/**
 * Created by ghostinmatrix on 2018/3/29.
 */

class PullToRefreshLayoutKt : RelativeLayout {
    var mContext: Context? = null
    private var downY: Float = 0F
    private var lastY: Float = 0F
    private var downX: Float = 0F
    private var lastX: Float = 0F
    
    
    var pullDownY = 0f
    private var pullUpY = 0f
    
    private var refreshDist = 0f
    private var loadmoreDist = 0f
    
    var moveSpeed = 8f
    private var isLayoutInit = false
    private var radio = 2f
    private var mRefreshingAnimation: Animation? = null
    
    protected var refreshView: View? = null
    protected var mRefreshingView: View? = null
    
    protected var loadmoreView: View? = null
    
    protected var pullableView: View? = null
    private var canPullDown = true
    private var isHandMoving = false
    
    private var curState: IState = STATE_IDLE(this, moveSpeed)
    
    constructor(context: Context) : super(context) {
        mContext = context
    }
    
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
    }
    
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mContext = context
    }
    
    fun setState(state: IState) {
        curState.stopAnim()
        curState = state
    }
    
    fun setRefreshingAnim(refreshView: View, anim: Animation) {
        mRefreshingView = refreshView
        mRefreshingAnimation = anim
    }
    
    
    fun onCurrentYCome(curY: Float) {
        pullDownY = curY
        requestLayout()
        val isChange = mayChangeState(curY)
        if (isChange) {
            curState.beginAt(pullDownY)
        }
    }
    
    
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!isLayoutInit) {
            refreshView = getChildAt(0)
            pullableView = getChildAt(1)
            loadmoreView = getChildAt(2)
            if (pullableView !is Pullable) {
                try {
                    throw ClassNotPullableException("pullableView is not a Class of Pullable")
                } catch (e: ClassNotPullableException) {
                    e.printStackTrace()
                }
            }
            isLayoutInit = true
            
            refreshDist = (refreshView as ViewGroup).getChildAt(0)
                    .measuredHeight.toFloat()
            loadmoreDist = (loadmoreView as ViewGroup).getChildAt(0)
                    .measuredHeight.toFloat()
            
        }
        
        
        refreshView?.let {
            it.layout(0,
                    pullDownY.toInt() - it.measuredHeight,
                    it.measuredWidth, pullDownY.toInt())
        }
        pullableView?.let {
            it.layout(0, (pullDownY + pullUpY).toInt(),
                    it.measuredWidth, (pullDownY + pullUpY).toInt() + it.measuredHeight)
        }
        
        loadmoreView?.let {
            it.layout(0,
                    (pullDownY + pullUpY).toInt() + it.measuredHeight,
                    it.measuredWidth,
                    (pullDownY + pullUpY).toInt() + it.measuredHeight
                            + it.measuredHeight)
        }
    }
    
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y
                lastY = downY
                
                downX = ev.x
                lastX = downX
                Log.e("viewgroupicpt", "action_down")
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("viewgroupicpt", "action_move")
                
                if (Math.abs(ev.x - lastX) < Math.abs(ev.y - lastY)) {
                    
                    if ((pullableView as Pullable).canPullDown()) {
                        if (ev.y > lastY)//往下拉
                            return true
                        else {
                            if (curState.tag != "state_idle") {
                                return true
                            } else
                                return false
                        }
                    } else {
                        return false
                    }
                    
                } else
                    return false
            }
            MotionEvent.ACTION_UP -> {
                Log.e("viewgroupicpt", "action_up")
                return false
            }
        }
        return false
    }
    
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y
                lastY = downY
                Log.e("viewgroup", "action_down")
                
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("viewgroup", "action_move")
                
                if ((pullableView as Pullable).canPullDown()) {
                    
                    pullDownY += (ev.y - lastY) / radio
                    Log.e("viewgroup", "" + pullDownY)
                    if (pullDownY < 0) {
                        pullDownY = 0f
                        canPullDown = false
                    }
                    requestLayout()
                    mayChangeState(pullDownY)
                    curState.onHandMoving()
                    isHandMoving = true
                }
                
                lastY = ev.y
                radio = (2 + 2 * Math.tan(Math.PI / 2.0 / measuredHeight.toDouble() * (pullDownY + Math.abs(pullUpY)))).toFloat()
                
            }
            MotionEvent.ACTION_UP -> {
                Log.e("viewgroup", "action_up")
                
                isHandMoving = false
                curState.onHandRelease(pullDownY)
            }
        }
        return true
    }
    
    
    private fun mayChangeState(y: Float): Boolean {
        if (y <= 0f) {
            val target = STATE_IDLE(this, moveSpeed)
            if (target != curState) {
                setState(target)
                return true
            } else {
                return false
            }
        } else if (y > 0 && y < refreshDist) {
            if (curState.tag == "state_below") {
                val target = STATE_REFRESHING(this, mRefreshingView, mRefreshingAnimation)
                setState(target)
                
                return true
            } else {
                val target = STATE_ABOVE(this, measuredHeight)
                if (target != curState) {
                    setState(target)
                    return true
                } else {
                    return false
                }
            }
            
        } else if (y == refreshDist) {
            val target = STATE_REFRESHING(this, mRefreshingView, mRefreshingAnimation)
            if (target != curState) {
                setState(target)
                return true
            } else {
                return false
            }
        } else {
            val target = STATE_BELOW(this, measuredHeight)
            if (target != curState) {
                setState(target)
                return true
            } else {
                return false
            }
        }
    }
    
    interface OnRefreshListener {
        fun onRefresh(pullToRefreshLayout: PullToRefreshLayout)
        
        fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout)
    }
    
    fun refreshFinish() {
        if (!isHandMoving) {
            val target = STATE_ABOVE(this, measuredHeight)
            setState(target)
        }
    }
    
    inner class ClassNotPullableException internal constructor(errorMsg: String) : Exception(errorMsg)
}