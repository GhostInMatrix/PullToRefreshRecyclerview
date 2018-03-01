package com.sfexpress.commonui.widget.recyclerview

import android.content.Context
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by ghostinmatrix on 2018/2/28.
 */
class ComViewHolderKt(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mViews: SparseArrayCompat<View> = SparseArrayCompat()
    private val convertView: View = itemView
    private var tag: String = ""
    
    fun setTag(tag: String) {
        this.tag = tag
    }
    
    fun getTag(): String {
        return this.tag
    }
    
    companion object {
        fun getComViewHolder(context: Context, layoutId: Int, parent: ViewGroup?): ComViewHolderKt {
            val itemView = LayoutInflater.from(context).inflate(layoutId, parent, false)
            return ComViewHolderKt(itemView)
        }
        
        fun getComViewHolder(context: Context, itemView: View, parent: ViewGroup): ComViewHolderKt {
            return ComViewHolderKt(itemView)
        }
    }
    
    fun <T : View> getView(layoutId: Int): T {
        var view = mViews.get(layoutId)
        if (view == null) {
            view = convertView.findViewById(layoutId)
            mViews.put(layoutId, view)
        }
        return view as T
    }
}