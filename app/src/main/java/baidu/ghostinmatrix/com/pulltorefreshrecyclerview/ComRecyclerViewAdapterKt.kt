package baidu.ghostinmatrix.com.pulltorefreshrecyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ghostinmatrix on 2018/2/28.
 *
 * @see baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter.FantasticRecyclerviewAdapter
 */
@Deprecated("Try to use FantasticRecyclerviewAdapter, which is simpler to replace the combination of these adapters",
        ReplaceWith("FantasticRecyclerviewAdapter"), DeprecationLevel.WARNING)
abstract class ComRecyclerViewAdapterKt<E>(val context: Context, private val layoutId: Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    protected var mGroup: ArrayList<E> = ArrayList()
    
    constructor(context: Context, layoutId: Int, datas: List<E>?)
            : this(context, layoutId) {
        datas?.let {
            mGroup.addAll(datas)
        }
    }
    
    fun setGroup(group: ArrayList<E>) {
        mGroup = group
        notifyDataSetChanged()
    }
    
    fun appendGroup(append: List<E>?) {
        append?.let {
            mGroup.addAll(append)
            notifyDataSetChanged()
        }
    }
    
    override fun onItemTop(fromPosition: Int) {
        val data = mGroup[fromPosition]
        for (idx in fromPosition downTo 1)
            mGroup[idx] = mGroup[idx - 1]
        mGroup[0] = data
        notifyItemMoved(fromPosition, 0)
    }
    
    override fun onItemSwap(itemAPosition: Int, itemBPosition: Int) {
        Collections.swap(mGroup, itemAPosition, itemBPosition)
        notifyItemMoved(itemAPosition, itemBPosition)
    }
    
    override fun getItemId(position: Int): Long {
        return getItemIdFromData(mGroup[position])
    }
    
    fun getItemIdFromData(data: E): Long {
        return RecyclerView.NO_ID
    }
    
    override fun onItemDismiss(position: Int) {
        mGroup.removeAt(position)
        notifyItemRemoved(position)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return ComViewHolderKt.Companion.getComViewHolder(context, layoutId, parent)
    }
    
    override fun getItemCount(): Int {
        return mGroup.size
    }
    
    abstract fun convert(viewHolderKt: ComViewHolderKt, data: E, type: Int, position: Int)
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        convert(holder as ComViewHolderKt, mGroup[position], getItemViewType(position), position)
    }
}