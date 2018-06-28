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

abstract class FantasticRecyclerviewAdapter<E : Any>(private val context: Context, private val datas: List<E>? = null) : RecyclerView.Adapter<ComViewHolderKt>() {
    private var firstItemAnimPosition: Int = 0
    private var mData: ArrayList<E> = ArrayList()
    private val mHeader = ArrayList<Int>()
    private val mFooter = ArrayList<Int>()
    private var selectedAnim = Anim_NONE
    private var stickHeaderFooter = false
    private var emptyLayoutId = R.layout.common_empty
    private var multiTypeHelper: MultitypeHelper? = null
    private var layoutId: Int = -1
    
    init {
        datas?.let {
            mData.addAll(datas)
        }
    }
    
    /**
     * choose on of a @AnimType as each Data Item anim when first attached to Window
     * @see AnimType
     * 选择一个itemView的入场动画
     */
    fun anim(@AnimType animType: Int): FantasticRecyclerviewAdapter<E> {
        selectedAnim = animType
        return this
    }
    
    /**
     * 自定义headers的resources
     */
    fun headers(headers: ArrayList<Int>): FantasticRecyclerviewAdapter<E> {
        mHeader.addAll(headers)
        return this
    }
    
    /**
     * 自定义footer的Resource
     */
    fun footers(footers: ArrayList<Int>): FantasticRecyclerviewAdapter<E> {
        mFooter.addAll(footers)
        return this
    }
    
    
    /**
     * choose if the EmptyView will cover the headers and footers or not
     * true if not cover
     * 可选择，空页面盖住header和footer，还是空页面在header和footer之间。
     * true 为之间。
     */
    fun stickHeaderFooter(isStick: Boolean): FantasticRecyclerviewAdapter<E> {
        stickHeaderFooter = isStick
        return this
    }
    
    /**
     * customize emptyView when adapter has no data
     * 自定义空页面，如果adapter内没有数据时，会自动展示
     */
    fun emptyView(emptyViewId: Int): FantasticRecyclerviewAdapter<E> {
        emptyLayoutId = emptyViewId
        return this
    }
    
    /**
     * support multiType itemViews if pass a MultitypeHelper in to adapter
     *  @see MultitypeHelper
     *  cannot use {singleTypeLayoutId} at same time
     */
    fun multiTypeHelper(multiTypeHelper: MultitypeHelper): FantasticRecyclerviewAdapter<E> {
        if (layoutId != -1) {
            throw Exception("already set singleTypeLayoutId, cannot turn it into a MultitypeAdapter")
        }
        this.multiTypeHelper = multiTypeHelper
        return this
    }
    
    /**
     * if itemView has only one layout, just use it!
     * cannot use {multiTypeHelper} at same time
     */
    fun singleTypeLayoutId(layoutId: Int): FantasticRecyclerviewAdapter<E> {
        if (multiTypeHelper != null) {
            throw Exception("already set multiTypeHelper, cannot turn it into a SingleTypeAdapter")
        }
        this.layoutId = layoutId
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
        return mData.isNotEmpty() && holder.layoutPosition >= mHeader.size && holder.layoutPosition < mHeader.size + mData.size
    }
    
    private fun startAnim(view: View) {
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
    
    /**
     * usually used when pull down to refresh
     * 一般用于下拉刷新，清空[mData]重新注入
     * [mData]是Adapter内部容器，data所在的外部列表不会被持有
     */
    fun refreshData(group: List<E>) {
        mData.clear()
        mData.addAll(group)
        firstItemAnimPosition = 0
        notifyDataSetChanged()
    }
    
    /**
     * append a list of data,usually used when pull up to load
     * 一般用于分页加载，追加即可。
     */
    fun appendData(append: List<E>?) {
        append?.let {
            mData.addAll(append)
            notifyDataSetChanged()
        }
    }
    
    /**
     * append a single data element
     * 新增单一数据
     */
    fun appendAData(element: E) {
        mData.add(element)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComViewHolderKt {
        return when {
            viewType == EMPTY -> ComViewHolderKt.getComViewHolder(context, emptyLayoutId, parent)
            viewType > HEADER && viewType <= (HEADER + mHeader.size) -> {
                ComViewHolderKt.getComViewHolder(context, mHeader[viewType - HEADER - 1], parent)
            }
            viewType > FOOTER && viewType < DATA -> {
                ComViewHolderKt.getComViewHolder(context, mFooter[viewType - FOOTER - 1], parent)
            }
            else -> {
                if (multiTypeHelper != null) {
                    ComViewHolderKt.getComViewHolder(context, multiTypeHelper!!.getLayoutId(viewType), parent)
                } else {
                    ComViewHolderKt.getComViewHolder(context, layoutId, parent)
                }
            }
        }
    }
    
    override fun onBindViewHolder(holder: ComViewHolderKt, position: Int) {
        if (isData(holder)) {
            convert(holder, mData[position - mHeader.size], getItemViewType(position), position)
        }
    }
    
    abstract fun convert(viewHolderKt: ComViewHolderKt, data: E, type: Int, position: Int)
    
    /**
     * 得到真实数据的数量，不包括header/footer/empty等
     */
    public fun getDataCount(): Int {
        return mData.size
    }
    
    /**
     * adapter内部使用判断各个item数量，并非外部数据的总量。一般在没有header、footer且不为空的情况下，二者是相同的
     * 否则数量大于等于 getDataCount()
     * @see getDataCount
     */
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
    
    
    /**
     * 删除单个item
     */
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
    
    /**
     * 置顶单个Item
     */
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
                else -> {
                    return multiTypeHelper?.getDataItemViewType(position, mData[position - mHeader.size])
                            ?: DATA
                }
            }
        }
    }
}