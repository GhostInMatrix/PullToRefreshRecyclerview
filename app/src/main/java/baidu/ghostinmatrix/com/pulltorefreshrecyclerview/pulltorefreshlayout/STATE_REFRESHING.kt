package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.pulltorefreshlayout

import android.view.View
import android.view.animation.Animation

/**
 * Created by ghostinmatrix on 2018/3/29.
 */
class STATE_REFRESHING(val listener: PullToRefreshLayoutKt, val refreshingView: View?, val refreshingAnim: Animation?) : IState() {
    override fun onHandRelease(y: Float) {
        refreshingView?.startAnimation(refreshingAnim)
    
    }
    
    override fun beginAt(y: Float) {
        refreshingView?.startAnimation(refreshingAnim)
    
    }
    
    override var tag: String? = "state_refreshing"
    
    
    override fun onHandMoving() {
        stopAnim()
    }
    
    
    override fun stopAnim() {
        refreshingView?.clearAnimation()
    }
    
    
}