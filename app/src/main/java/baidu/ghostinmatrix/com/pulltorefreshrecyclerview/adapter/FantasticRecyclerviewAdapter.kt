package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.ComViewHolderKt
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.R
import java.util.*

/**
 * Created by ghostinmatrix on 2018/5/9.
 */
const val EMPTY: Int = 10000
const val HEADER: Int = 20000
const val FOOTER: Int = 30000
const val DATA: Int = 40000

@IntDef(EMPTY, HEADER, FOOTER, DATA)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewType

const val Anim_NONE: Int = 0x0000
const val Anim_LEFTIN: Int = 0x0001
const val Anim_RIGHT: Int = 0x0002

@IntDef(Anim_NONE, Anim_LEFTIN, Anim_RIGHT)
@Retention(AnnotationRetention.SOURCE)
annotation class AnimType

abstract class FantasticRecyclerviewAdapter<E>(private val context: Context, private val layoutId: Int,
                                               private val datas: List<E>? = null) : RecyclerView.Adapter<ComViewHolderKt>() {
    private var firstItemAnimPosition: Int = 0
    private var mData: ArrayList<E> = ArrayList()
    private val mHeader = ArrayList<Int>()
    private val mFooter = ArrayList<Int>()
    private var selectedAnim = Anim_NONE
    private var stickHeaderFooter = false
    private var emptyLayoutId = R.layout.common_empty
    
    init {
        datas?.let {
            mData.addAll(datas)
        }
        
    }
    
    fun anim(@AnimType animType: Int): FantasticRecyclerviewAdapter<E> {
        selectedAnim = animType
        return this
    }
    
    fun headers(headers: ArrayList<Int>): FantasticRecyclerviewAdapter<E> {
        mHeader.addAll(headers)
        return this
    }
    
    
    fun footers(footers: ArrayList<Int>): FantasticRecyclerviewAdapter<E> {
        mFooter.addAll(footers)
        return this
    }
    
    fun stickHeaderFooter(isStick: Boolean): FantasticRecyclerviewAdapter<E> {
        stickHeaderFooter = isStick
        return this
    }
    
    fun emptyView(emptyViewId: Int): FantasticRecyclerviewAdapter<E> {
        emptyLayoutId = emptyViewId
        return this
    }
    
    
    override fun onViewAttachedToWindow(holder: ComViewHolderKt) {
        if (holder.layoutPosition >= firstItemAnimPosition) {
            firstItemAnimPosition++
            if (isData(holder))
                startAnim(holder.itemView)
        }
    }
    
    private fun isData(holder: ComViewHolderKt): Boolean {
        return getItemViewType(holder.layoutPosition) == DATA
    }
    
    fun startAnim(view: View) {
        when (selectedAnim) {
            Anim_LEFTIN -> {
                val anim = ObjectAnimator.ofFloat(view, "translationX", -view.rootView.width.toFloat(), 0f).setDuration(200)
                anim.interpolator = AccelerateDecelerateInterpolator()
                anim.start()
            }
            Anim_RIGHT -> {
                val anim = ObjectAnimator.ofFloat(view, "translationX", view.rootView.width.toFloat(), 0f).setDuration(200)
                anim.interpolator = AccelerateDecelerateInterpolator()
                anim.start()
            }
        }
    }
    
    fun refreshData(group: ArrayList<E>) {
        mData.clear()
        mData.addAll(group)
        firstItemAnimPosition = 0
        notifyDataSetChanged()
    }
    
    fun appendData(append: List<E>?) {
        append?.let {
            mData.addAll(append)
            notifyDataSetChanged()
        }
    }
    
    override fun getItemId(position: Int): Long {
        return getItemIdFromData(mData[position - mHeader.size])
    }
    
    fun getItemIdFromData(data: E): Long {
        return RecyclerView.NO_ID
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ComViewHolderKt {
        return when {
            viewType == EMPTY -> ComViewHolderKt.getComViewHolder(context, emptyLayoutId, parent)
            viewType > HEADER && viewType <= (HEADER + mHeader.size) -> {
                ComViewHolderKt.getComViewHolder(context, mHeader[viewType - HEADER - 1], parent)
            }
            viewType > FOOTER && viewType < DATA -> {
                ComViewHolderKt.getComViewHolder(context, mFooter[viewType - FOOTER - 1], parent)
            }
            else -> {
                ComViewHolderKt.getComViewHolder(context, layoutId, parent)
            }
        }
    }
    
    override fun onBindViewHolder(holder: ComViewHolderKt, position: Int) {
        if (getItemViewType(position) == DATA) {
            convert(holder, mData[position - mHeader.size], getItemViewType(position), position)
        }
    }
    
    abstract fun convert(viewHolderKt: ComViewHolderKt, data: E, type: Int, position: Int)
    
    //这个地方，如果mData.isEmpty，有两宗可能，1：删除光，这样会导致数组越界；2.压根就是空。所以要考虑昨天的crash场景。
    //详见recyclerView 5419行。
    override fun getItemCount(): Int {
        var count = 0
        if (mData.isEmpty()) {
            count = if (stickHeaderFooter) {
                mHeader.size + 1 + mFooter.size
            } else {
                1
            }
        } else {
            count = mHeader.size + mData.size + mFooter.size
        }
        return count
    }
    
    fun removeItem(holder: ComViewHolderKt) {
        val position = holder.layoutPosition
        if (mData.isNotEmpty() && position >= mHeader.size && position < mHeader.size + mData.size) {
            mData.removeAt(position - mHeader.size)
            if (mData.isEmpty() && !stickHeaderFooter) {
                notifyItemRangeRemoved(0, mHeader.size + 1 + mFooter.size)
            } else
                notifyItemRemoved(position)
        }
    }
    
    fun topItem(holder: ComViewHolderKt) {
        val position = holder.layoutPosition
        if (mData.isNotEmpty() && position >= mHeader.size && position < mHeader.size + mData.size) {
            val theData = mData.removeAt(position - mHeader.size)
            mData.add(0, theData)
            notifyItemMoved(position, mHeader.size)
        }
    }
    
    override fun getItemViewType(position: Int): Int {
        
        return if (mData.isEmpty()) {
            if (stickHeaderFooter) {
                when {
                    position < mHeader.size -> HEADER + position + 1
                    position > mHeader.size -> FOOTER + (position + 1 - mHeader.size - 1)
                    else -> EMPTY
                }
            } else {
                EMPTY
            }
        } else {
            when {
                position < mHeader.size -> HEADER + position + 1
                position >= mHeader.size + mData.size -> FOOTER + position + 1 - mHeader.size - mData.size
                else -> DATA
            }
        }
    }
    
}