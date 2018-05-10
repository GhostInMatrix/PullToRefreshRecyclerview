package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.pulltorefreshlayout

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by ghostinmatrix on 2018/3/29.
 */
class STATE_BELOW(val listener: PullToRefreshLayoutKt, val totalHeight: Int) : IState() {
    override fun beginAt(y: Float) {
        currentY = y
        autoHide()
    }
    
    override var tag: String? = "state_below"
    
    override fun onHandMoving() {
        stopAnim()
    }
    
    override fun onHandRelease(y: Float) {
        currentY = y
        autoHide()
    }
    
    
    var currentY: Float = 0f
    var MOVE_SPEED: Float = 0f
    var disposable: Disposable? = null
    
    
    override fun stopAnim() {
        stopHide()
    }
    
    
    fun autoHide() {
        if (disposable == null || disposable!!.isDisposed) {
            disposable = Flowable.interval(5, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Consumer<Long> {
                        override fun accept(t: Long) {
                            doHide()
                        }
                    })
        }
    }
    
    fun doHide() {
        Log.e("currY", "before speed:" + currentY)
        
        MOVE_SPEED = (8 + 5 * Math.tan(
                Math.PI / 2.0 / totalHeight * currentY)).toFloat()
        Log.e("currY", "speed:" + MOVE_SPEED)
        currentY -= MOVE_SPEED
        Log.e("currY", "speed:::" + currentY)
        
        if (currentY < 0)
            currentY = 0f
        listener.onCurrentYCome(currentY)
    }
    
    
    fun stopHide() {
        disposable?.dispose()
    }
}