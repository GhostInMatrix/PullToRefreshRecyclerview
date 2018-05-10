package baidu.ghostinmatrix.com.pulltorefreshrecyclerview

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.pulltorefreshlayout.PullToRefreshLayoutKt
import com.sf.lib_android_activity.BaseActivity

/**
 * Created by ghostinmatrix on 2018/3/29.
 */
class ThirdActivity : BaseActivity() {
    override fun getContentLayoutId(): Int {
        return R.layout.activity_pulltorefreshlayout_kt
    }
    
    override fun initView() {
        val pulltorefreshView = findViewById<PullToRefreshLayoutKt>(R.id.mPulltorefreshLayout)
        val refreshingView = pulltorefreshView.findViewById<View>(R.id.refreshing_icon)
        val animation:RotateAnimation = AnimationUtils.loadAnimation(this@ThirdActivity, R.anim.rotating) as RotateAnimation
        animation.interpolator = LinearInterpolator()
        pulltorefreshView.setRefreshingAnim(refreshingView,animation)
    }
    
    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, ThirdActivity::class.java)
            context.startActivity(intent)
        }
    }
}