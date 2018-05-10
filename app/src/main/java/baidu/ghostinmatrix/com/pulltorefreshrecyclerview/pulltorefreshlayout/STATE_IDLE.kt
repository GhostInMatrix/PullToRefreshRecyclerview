package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.pulltorefreshlayout

/**
 * Created by ghostinmatrix on 2018/3/29.
 */
class STATE_IDLE(val listener: PullToRefreshLayoutKt, val moveSpeed: Float) : IState() {
    override fun onHandRelease(y: Float) {
    
    }
    
    override fun beginAt(y: Float) {
    
    }
    
    override var tag: String? = "state_idle"
    
    
    override fun onHandMoving() {
    }
    
    
    override fun stopAnim() {
    }
}