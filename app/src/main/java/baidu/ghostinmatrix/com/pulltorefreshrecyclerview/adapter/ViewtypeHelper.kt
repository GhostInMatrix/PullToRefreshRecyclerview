package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter
import android.view.View
import android.view.ViewGroup

/**
 * Created by ghostinmatrix on 2018/5/14.
 */
interface ViewtypeHelper {
    fun getDataItemViewType(data: Any): Int {
        return 0
    }
    
    fun getLayoutId(dataItemViewType: Int): Int {
        return 0
    }
    
    fun getLayoutView(dataItemViewType: Int, parent: ViewGroup): View? {
        return null
    }
}