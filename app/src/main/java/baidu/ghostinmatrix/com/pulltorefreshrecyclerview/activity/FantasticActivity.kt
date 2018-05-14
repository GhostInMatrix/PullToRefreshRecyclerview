package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.ComViewHolderKt
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.PullToRefreshLayout
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.PullToRefreshLayout.SUCCEED
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.PullToRefreshRecyclerView
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.R
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter.Anim_LEFTIN
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter.FantasticRecyclerviewAdapter
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter.MultitypeHelper

/**
 * Created by ghostinmatrix on 2018/5/9.
 */
class FantasticActivity : Activity(), PullToRefreshLayout.OnRefreshListener {
    private lateinit var adapter: FantasticRecyclerviewAdapter<Any>
    private lateinit var recyclerView: PullToRefreshRecyclerView
    override fun onRefresh(pullToRefreshLayout: PullToRefreshLayout?) {
        adapter.refreshData(arrayListOf("1", "2", "3", "4", "5", "6"))
        recyclerView.refreshFinish(SUCCEED)
    }
    
    override fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout?) {
        adapter.appendData(arrayListOf("1", "2", "3", "4", "5", "6"))
        recyclerView.loadmoreFinish(SUCCEED)
    }
    
    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, FantasticActivity::class.java)
            context.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fantastic)
        recyclerView = findViewById<PullToRefreshRecyclerView>(R.id.recyclerView)
        recyclerView.setOnRefreshListener(this)
        adapter = object : FantasticRecyclerviewAdapter<Any>(this) {
            override fun convert(viewHolderKt: ComViewHolderKt, data: Any, type: Int, position: Int) {
                val titleTv = viewHolderKt.getView<TextView>(R.id.title)
                if (type == 1) {
                    titleTv.text = data as String
                } else {
                    titleTv.text = "数字：" + data as Int
                }
                
                val delBtn = viewHolderKt.getView<Button>(R.id.delete)
                delBtn.setOnClickListener { _ ->
                    removeItem(viewHolderKt)
                }
                
                val topBtn = viewHolderKt.getView<Button>(R.id.on_top)
                topBtn.setOnClickListener {
                    topItem(viewHolderKt)
                }
            }
        }
//                .singleTypeLayoutId(R.layout.test_item_content_list)
                .multiTypeHelper(object : MultitypeHelper {
                    override fun getDataItemViewType(position: Int, data: Any): Int {
                        if (data is String) {
                            return 1
                        } else
                            return 2
                    }
                    
                    override fun getLayoutId(dataItemViewType: Int): Int {
                        if (dataItemViewType == 1) {
                            return R.layout.test_item_content_list
                        } else
                            return R.layout.test_item_content_list2
                    }
                })
                .anim(Anim_LEFTIN)
                .headers(arrayListOf(R.layout.test_header, R.layout.test_header, R.layout.header_search_view))
                .footers(arrayListOf(R.layout.test_footer, R.layout.test_footer))
                .stickHeaderFooter(false)
                .emptyView(R.layout.common_error)
        
        recyclerView.adapter = adapter
        val data = arrayListOf("aaa", "bb", "cc", "dd", "ee", "ff", "gg", 1, 2, 3, 4, 5, 65)
        adapter.refreshData(data)
    }
}