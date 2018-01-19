package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghostinmatrix on 2018/1/19.
 */

public class HeaderFullEmptyStateActivity extends Activity implements PullToRefreshRecyclerView.OnRefreshListener {
    PullToRefreshRecyclerView recyclerview;
    ComRecyclerViewAdapter originAdapetr;
    FullEmptyStateRecyclerViewAdapter fullemptyStateAdapter;
    HeaderFooterRecyclerViewAdapter headerFooterRecyclerViewAdapter;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, HeaderFullEmptyStateActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headerfullemptystate);
        recyclerview = findViewById(R.id.recycler_view);
        originAdapetr = new ComRecyclerViewAdapter<String>(HeaderFullEmptyStateActivity.this, R.layout.test_item_content_list) {

            @Override
            public void convert(final ComViewHolder holder, String data, int type, int position) {
                TextView titleTv = holder.getView(R.id.title);
                titleTv.setText(data);
                Button delete = holder.getView(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ItemTouchHelperAdapter) recyclerview.getAdapter()).onItemDismiss(holder.getLayoutPosition());

                    }
                });
                Button top = holder.getView(R.id.on_top);
                top.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ItemTouchHelperAdapter) recyclerview.getAdapter()).onItemTop(holder.getLayoutPosition());

                    }
                });
            }
        };
        View emptyView = View.inflate(this, R.layout.common_empty, null);
        fullemptyStateAdapter = new FullEmptyStateRecyclerViewAdapter(originAdapetr, emptyView);

        headerFooterRecyclerViewAdapter = new HeaderFooterRecyclerViewAdapter(this, fullemptyStateAdapter);
        View headerView = View.inflate(this, R.layout.header_view, null);
        headerFooterRecyclerViewAdapter.addHeaderView(headerView);


        recyclerview.setAdapter(headerFooterRecyclerViewAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) recyclerview.getAdapter(), true, true);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerview.getPullableRecyclerView().getRecyclerView());
        recyclerview.setOnRefreshListener(this);
        List<String> titles = new ArrayList<>();
        titles.add("title1");
        titles.add("title2");
        titles.add("title3");
        titles.add("title4");
        titles.add("title5");
        titles.add("title6");
        titles.add("title7");
        titles.add("title8");
        titles.add("title9");
        titles.add("title10");

        originAdapetr.setGroup(titles);
        recyclerview.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        List<String> titles = new ArrayList<>();
        titles.add("title1");
        titles.add("title2");
        titles.add("title3");
        titles.add("title4");
        titles.add("title5");
        titles.add("title6");
        titles.add("title7");
        titles.add("title8");
        titles.add("title9");
        titles.add("title10");
        originAdapetr.setGroup(titles);
        recyclerview.getAdapter().notifyDataSetChanged();
        recyclerview.refreshFinish(PullToRefreshLayout.DONE);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        recyclerview.refreshFinish(PullToRefreshLayout.DONE);
    }
}
