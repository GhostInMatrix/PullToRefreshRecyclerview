package baidu.ghostinmatrix.com.pulltorefreshrecyclerview

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by ghostinmatrix on 2018/2/28.
 * @see baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter.FantasticRecyclerviewAdapter
 
 */

@Deprecated("Try to use FantasticRecyclerviewAdapter, which is simpler to replace the combination of these adapters",
        ReplaceWith("FantasticRecyclerviewAdapter"), DeprecationLevel.ERROR)
class HeaderFooterRecyclerViewAdapterKt(private val innerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    private val HEADER_BASE_TYPE = 10000
    private val FOOTER_BASE_TYPE = 20000
    private val mHeaderViews = SparseArrayCompat<View>()
    private val mFooterViews = SparseArrayCompat<View>()
    
    fun addHeaderView(headerView: View) {
        mHeaderViews.put(mHeaderViews.size() + HEADER_BASE_TYPE, headerView)
    }
    
    fun addFooterView(footerView: View) {
        mFooterViews.put(mFooterViews.size() + FOOTER_BASE_TYPE, footerView)
    }
    
    fun isHeaderPosition(position: Int): Boolean {
        return position < mHeaderViews.size()
    }
    
    fun isFooterPosition(position: Int): Boolean {
        return position >= innerAdapter.itemCount + mHeaderViews.size()
    }
    
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when {
            mHeaderViews.get(viewType) != null -> ComViewHolderKt(mHeaderViews.get(viewType))
            mFooterViews.get(viewType) != null -> ComViewHolderKt(mFooterViews.get(viewType))
            else -> innerAdapter.onCreateViewHolder(parent, viewType)
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (isHeaderPosition(position) || isFooterPosition(position))
            return
        innerAdapter.onBindViewHolder(holder, position - mHeaderViews.size())
    }
    
    override fun getItemCount(): Int {
        return mHeaderViews.size() + innerAdapter.itemCount + mFooterViews.size()
    }
    
    override fun getItemViewType(position: Int): Int {
        return when {
            isHeaderPosition(position) -> mHeaderViews.keyAt(position)
            isFooterPosition(position) -> mFooterViews.keyAt(position - mHeaderViews.size() - innerAdapter.itemCount)
            else -> innerAdapter.getItemViewType(position - mHeaderViews.size())
        }
    }
    
    override fun onItemTop(fromPosition: Int) {
        if (isHeaderPosition(fromPosition) || isFooterPosition(fromPosition))
            return
        (innerAdapter as ItemTouchHelperAdapter).onItemTop(fromPosition - mHeaderViews.size())
        notifyItemMoved(fromPosition, mHeaderViews.size())
    }
    
    override fun onItemDismiss(position: Int) {
        if (isHeaderPosition(position) || isFooterPosition(position))
            return
        (innerAdapter as ItemTouchHelperAdapter).onItemDismiss(position - mHeaderViews.size())
        notifyItemRemoved(position)
    }
    
    override fun onItemSwap(itemAPosition: Int, itemBPosition: Int) {
        if (isHeaderPosition(itemAPosition) || isFooterPosition(itemBPosition)) {
            return
        }
        (innerAdapter as ItemTouchHelperAdapter).onItemSwap(itemAPosition - mHeaderViews.size(), itemBPosition - mHeaderViews.size())
        notifyItemMoved(itemAPosition, itemBPosition)
    }
}

