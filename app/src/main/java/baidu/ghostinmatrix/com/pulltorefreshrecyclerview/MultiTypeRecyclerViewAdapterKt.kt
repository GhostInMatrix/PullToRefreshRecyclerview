package baidu.ghostinmatrix.com.pulltorefreshrecyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by ghostinmatrix on 2018/2/28.
 */
abstract class MultiTypeRecyclerViewAdapterKt<E>(ctx: Context, multiTypeSupport: MultiTypeSupport<E>) : ComRecyclerViewAdapterKt<E>(ctx, -1) {
    private var mMultiTypeSupport: MultiTypeSupport<E> = multiTypeSupport
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val layoutid = mMultiTypeSupport.getLayoutId(viewType)
        return ComViewHolderKt.Companion.getComViewHolder(context, layoutid, parent)
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        super.onBindViewHolder(holder, position)
    }
    
    override fun getItemViewType(position: Int): Int {
        return mMultiTypeSupport.getItemViewType(position, mGroup[position])
    }
}