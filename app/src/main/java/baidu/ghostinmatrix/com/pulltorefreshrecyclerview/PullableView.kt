package baidu.ghostinmatrix.com.pulltorefreshrecyclerview

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView

/**
 * Created by ghostinmatrix on 2018/3/30.
 */
class PullableView : ScrollView, Pullable {
    var canPullDown = true
    override fun canPullDown(): Boolean {
        return canPullDown
    }
    
    override fun canPullUp(): Boolean {
        return false
    }
    
    constructor(context: Context) : super(context) {
        initView(context)
    }
    
    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        initView(context)
    }
    
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    fun initView(context: Context) {
        
        setOnScrollChangeListener(object : OnScrollChangeListener {
            override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                Log.e("pullableview", "" + scrollY)
                if (scrollY == 0) {
                    Log.e("pullableview", "顶部")
                    canPullDown = true
                } else {
                    canPullDown = false
                }
                
            }
            
        })
        
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val su = super.onTouchEvent(event)
        Log.e("pullableview",""+su)
        return su
//
//        if (su) {
//
//        }
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                Log.e("pullableView", "action_down")
//            }
//            MotionEvent.ACTION_MOVE ->
//                Log.e("pullableView", "action_move")
//            MotionEvent.ACTION_UP ->
//                Log.e("pullableView", "action_up")
//
//        }
//        return true
    }
}