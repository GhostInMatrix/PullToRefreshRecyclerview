package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.pulltorefreshlayout

/**
 * Created by ghostinmatrix on 2018/3/29.
 */
abstract class IState {
    open var tag: String? = null
    
    override fun equals(other: Any?): Boolean {
        return if (other is IState) {
            tag == other.tag
        } else
            false
    }
    
    abstract fun onHandRelease(y: Float)
    
    abstract fun onHandMoving()

//    abstract fun startAnim()
    
    abstract fun stopAnim()
    
    abstract fun beginAt(y: Float)
    
}