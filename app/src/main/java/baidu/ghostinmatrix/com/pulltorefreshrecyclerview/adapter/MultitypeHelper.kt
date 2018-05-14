package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter

/**
 * Created by ghostinmatrix on 2018/5/14.
 */
interface MultitypeHelper {
    fun getDataItemViewType(position: Int, data: Any):Int
    
    fun getLayoutId(dataItemViewType: Int):Int
}