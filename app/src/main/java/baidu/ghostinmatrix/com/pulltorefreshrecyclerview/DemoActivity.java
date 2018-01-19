package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shanjie on 2017/4/24.
 */


public class DemoActivity extends Activity implements DemoContract.ViewContract, PullToRefreshRecyclerView.OnRefreshListener {

    PullToRefreshRecyclerView mRecyclerView;
    DemoPresenter mPresenter;
    ComRecyclerViewAdapter<String> innerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_content_list);
        mRecyclerView = findViewById(R.id.recycler_view);
        innerAdapter = new ComRecyclerViewAdapter<String>(this, R.layout.test_item_content_list) {

            @Override
            public void convert(final ComViewHolder holder, String data, int type, int position) {
                LinearLayout llCard = holder.getView(R.id.ll_card);
                llCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HeaderFullEmptyStateActivity.start(DemoActivity.this);
                    }
                });
                TextView titleTv = holder.getView(R.id.title);
                titleTv.setText(data);
                Button delete = holder.getView(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ItemTouchHelperAdapter) mRecyclerView.getAdapter()).onItemDismiss(holder.getLayoutPosition());
                    }
                });
                Button top = holder.getView(R.id.on_top);
                top.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ItemTouchHelperAdapter) mRecyclerView.getAdapter()).onItemTop(holder.getLayoutPosition());

                    }
                });
            }
        };

        HeaderFooterRecyclerViewAdapter headerFooterRecyclerViewAdapter = new HeaderFooterRecyclerViewAdapter(this, innerAdapter);
        View headerView = View.inflate(this, R.layout.test_header, null);
        headerFooterRecyclerViewAdapter.addHeaderView(headerView);
        View footerView = View.inflate(this, R.layout.test_footer, null);
        headerFooterRecyclerViewAdapter.addFooterView(footerView);
        mRecyclerView.setAdapter(headerFooterRecyclerViewAdapter);

        /**
         * 如果需要用手势，则需要下面代码
         */
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) mRecyclerView.getAdapter(), true, true);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView.getPullableRecyclerView().getRecyclerView());
        mRecyclerView.setOnRefreshListener(this);
        mPresenter = new DemoPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.getPullableRecyclerView().notifyNetState(NetStateView.DATA_STATUS_LOADING);
        mPresenter.getData();
    }

    @Override
    public void onDataRefresh(List<String> data, int netState) {
        innerAdapter.setGroup(data);
        mRecyclerView.getPullableRecyclerView().notifyNetState(netState);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.refreshFinish(PullToRefreshLayout.DONE);
    }

    @Override
    public void onDataLoad(List<String> data, int netState) {
        innerAdapter.setGroup(data);
        mRecyclerView.getPullableRecyclerView().notifyNetState(netState);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.loadmoreFinish(PullToRefreshLayout.DONE);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPresenter.getData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPresenter.loadData();
    }

    @Override
    protected void onDestroy() {
        mRecyclerView.setOnRefreshListener(null);
        super.onDestroy();
    }

}

