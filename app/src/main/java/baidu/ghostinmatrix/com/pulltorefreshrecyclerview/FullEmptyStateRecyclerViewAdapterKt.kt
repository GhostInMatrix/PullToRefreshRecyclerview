package com.sfexpress.commonui.widget.recyclerview

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.ItemTouchHelperAdapter

/**
 * Created by ghostinmatrix on 2018/2/28.
 */
class FullEmptyStateRecyclerViewAdapterKt<E>(private val innerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, private val emptyView: View)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    private val emptyType = -10000
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val viewHolder = (holder as ComViewHolderKt)
        if ("emptyView" != viewHolder.getTag()) {
            innerAdapter.onBindViewHolder(holder, position)
        }
    }
    
    override fun getItemCount(): Int {
        return if (innerAdapter.itemCount > 0)
            innerAdapter.itemCount
        else 1
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (innerAdapter.itemCount == 0)
            emptyType
        else
            0
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            emptyType -> ComViewHolderKt(emptyView).apply { setTag("emptyView") }
            else -> innerAdapter.onCreateViewHolder(parent, viewType)
        }
    }
    
    
    override fun onItemTop(fromPosition: Int) {
        //TODO:数据交换
    }
    
    override fun onItemDismiss(position: Int) {
        (innerAdapter as ItemTouchHelperAdapter).onItemDismiss(position)       // TODO("鉴别类型异常")
        notifyItemRemoved(position)
    }
    
    override fun onItemSwap(itemAPosition: Int, itemBPosition: Int) {
        //TODO:数据交换
    }
}