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

/**
 * Created by ghostinmatrix on 2018/5/9.
 */
class FantasticActivity : Activity(), PullToRefreshLayout.OnRefreshListener {
    private lateinit var adapter: FantasticRecyclerviewAdapter<String>
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
        adapter = object : FantasticRecyclerviewAdapter<String>(this, R.layout.test_item_content_list) {
            override fun convert(viewHolderKt: ComViewHolderKt, data: String, type: Int, position: Int) {
                val titleTv = viewHolderKt.getView<TextView>(R.id.title)
                titleTv.text = data
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
                .anim(Anim_LEFTIN)
                .headers(arrayListOf(R.layout.test_header, R.layout.test_header, R.layout.header_search_view))
                .footers(arrayListOf(R.layout.test_footer, R.layout.test_footer))
                .stickHeaderFooter(true)
                .emptyView(R.layout.common_error)
        
        recyclerView.adapter = adapter
        val data = arrayListOf("aaa", "bb", "cc", "dd", "ee", "ff", "gg")
        adapter.refreshData(data)
    }
}