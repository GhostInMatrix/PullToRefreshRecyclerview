package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.R
import java.util.*

/**
 * Created by ghostinmatrix on 2018/5/9.
 */
const val NOTHING: Int = 0
const val EMPTY: Int = 10000
const val HEADER: Int = 20000
const val FOOTER: Int = 30000
const val DATA: Int = 40000

@IntDef(NOTHING, EMPTY, HEADER, FOOTER, DATA)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewType

const val Anim_NONE: Int = 0x0000
const val Anim_LEFTIN: Int = 0x0001
const val Anim_RIGHT: Int = 0x0002

@IntDef(Anim_NONE, Anim_LEFTIN, Anim_RIGHT)
@Retention(AnnotationRetention.SOURCE)
annotation class AnimType

abstract class FantasticRecyclerviewAdapter<E : Any>(private val context: Context, private val func: ((viewHolderKt: ComViewHolderKt, data: E, type: Int, position: Int) -> Unit)? = null, private val datas: List<E>? = null)
    : RecyclerView.Adapter<ComViewHolderKt>() {
    private var firstItemAnimPosition: Int = 0
    private var mData: ArrayList<E> = ArrayList()
    private val mHeader = ArrayList<Int>()
    private val mFooter = ArrayList<Int>()
    private var selectedAnim = Anim_NONE
    private var stickHeaderFooter = false
    private var emptyLayoutId = R.layout.common_empty
    private var viewTypeHelper: ViewtypeHelper? = null
    private var isAdapterInited: Boolean = false
    private var isNeedInitStatus: Boolean = true
    
    init {
        datas?.let {
            mData.addAll(datas)
            isAdapterInited = true
        }
    }
    
    /**
     * choose on of a @AnimType as each Data Item anim when first attached to Window
     * @see AnimType
     */
    fun setAnim(@AnimType animType: Int) {
        selectedAnim = animType
    }
    
    /**
     * 自定义headers的resources
     */
    fun setHeaders(headers: ArrayList<Int>) {
        mHeader.addAll(headers)
    }
    
    /**
     * 自定义footer的Resource
     */
    fun setFooters(footers: ArrayList<Int>) {
        mFooter.addAll(footers)
    }
    
    fun clearFooters() {
        mFooter.clear()
    }
    
    
    /**
     * choose if the EmptyView will cover the headers and footers or not
     * true is not cover
     */
    fun setStickHeaderFooter(isStick: Boolean) {
        stickHeaderFooter = isStick
    }
    
    /**
     * customize emptyView when adapter as no data
     */
    fun setEmptyView(emptyViewId: Int) {
        emptyLayoutId = emptyViewId
    }
    
    /**
     * support multiType itemViews if pass a MultitypeHelper in to adapter
     *  @see MultitypeHelper
     *  cannot use {singleTypeLayoutId} at same time
     */
    fun setViewTypeHelper(viewTypeHelper: ViewtypeHelper) {
        this.viewTypeHelper = viewTypeHelper
    }
    
    /**
     * 设置是否需要展示初始态的背景（全白，而非空页面）
     * @param need true,则初始态背景全白，需要调用数据相关API才会根据数据状态展示对应view
     * @see refreshData
     * @see appendData
     * @see appendOneData
     * @see init{}
     *
     * @param need false, 则初始态就是empty view
     */
    fun setNeedInitStatus(need: Boolean) {
        this.isNeedInitStatus = need
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
     * 一般用于下拉刷新，清空[mData]重新注入
     * [mData]是Adapter内部容器，data所在的外部列表不会被持有
     */
    fun refreshData(group: List<E>?) {
        isAdapterInited = true
        mData.clear()
        group?.let { mData.addAll(it) }
        firstItemAnimPosition = 0
        notifyDataSetChanged()
    }
    
    /**
     * 一般用于分页加载，追加即可。
     */
    fun appendData(append: List<E>?) {
        isAdapterInited = true
        append?.let {
            mData.addAll(append)
            notifyDataSetChanged()
        }
    }
    
    fun appendOneData(element: E) {
        isAdapterInited = true
        mData.add(element)
        notifyDataSetChanged()
    }
    
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComViewHolderKt {
        return when {
            viewType == EMPTY -> {
                ComViewHolderKt.getComViewHolder(context, emptyLayoutId, parent)
            }
            viewType > HEADER && viewType <= (HEADER + mHeader.size) -> {
                ComViewHolderKt.getComViewHolder(context, mHeader[viewType - HEADER - 1], parent)
            }
            viewType > FOOTER && viewType < DATA -> {
                ComViewHolderKt.getComViewHolder(context, mFooter[viewType - FOOTER - 1], parent)
            }
            else -> {
                if (viewTypeHelper?.getLayoutView(viewType, parent) != null) {
                    ComViewHolderKt.getComViewHolder(viewTypeHelper?.getLayoutView(viewType, parent))
                } else {
                    ComViewHolderKt.getComViewHolder(context, viewTypeHelper?.getLayoutId(viewType)
                            ?: 0, parent)
                }
            }
        }
    }
    
    override fun onBindViewHolder(holder: ComViewHolderKt, position: Int) {
        if (isData(holder)) {
            convert(holder, mData[position - mHeader.size], getItemViewType(position), position)
        }
    }
    
    open fun convert(viewHolderKt: ComViewHolderKt, data: E, type: Int, position: Int) {
        func?.invoke(viewHolderKt, data, type, position)
    }
    
    
    /**
     * 得到真实数据的总量，不包括header/footer/empty等
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
        var count = if (isNeedInitStatus) {
            if (!isAdapterInited)
                0
            else if (mData.isEmpty()) {
                if (stickHeaderFooter) {
                    mHeader.size + 1 + mFooter.size
                } else {
                    1
                }
            } else {
                mHeader.size + mData.size + mFooter.size
            }
        } else {
            if (mData.isEmpty()) {
                if (stickHeaderFooter) {
                    mHeader.size + 1 + mFooter.size
                } else {
                    1
                }
            } else {
                mHeader.size + mData.size + mFooter.size
            }
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
                    return viewTypeHelper?.getDataItemViewType(mData[position - mHeader.size])
                            ?: DATA
                }
            }
        }
    }
}