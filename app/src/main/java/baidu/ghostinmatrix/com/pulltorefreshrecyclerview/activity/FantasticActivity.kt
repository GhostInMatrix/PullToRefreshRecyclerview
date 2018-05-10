package baidu.ghostinmatrix.com.pulltorefreshrecyclerview.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.ComViewHolderKt
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.PullToRefreshRecyclerView
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.R
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter.Anim_NONE
import baidu.ghostinmatrix.com.pulltorefreshrecyclerview.adapter.FantasticRecyclerviewAdapter

/**
 * Created by ghostinmatrix on 2018/5/9.
 */
class FantasticActivity : Activity() {
    
    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, FantasticActivity::class.java)
            context.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fantastic)
        val recyclerView = findViewById<PullToRefreshRecyclerView>(R.id.recyclerView)
        val adapter = object : FantasticRecyclerviewAdapter<String>(this, R.layout.test_item_content_list) {
            override fun convert(viewHolderKt: ComViewHolderKt, data: String, type: Int, position: Int) {
                val titleTv = viewHolderKt.getView<TextView>(R.id.title)
                titleTv.text = data
            }
        }.anim(Anim_NONE).headers(arrayListOf(R.layout.header_view)).footers(arrayListOf(R.layout.header_view))
        
        recyclerView.adapter = adapter
        val data = arrayListOf("aaa", "bb", "cc", "dd", "ee", "ff", "gg", "hh", "ii")
        adapter.setData(data)
    }
}