package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shanjie on 2017/5/10.
 */
public class HeaderFooterRecyclerViewAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private final static int HEADER_BASE_TYPE = 10000;
    private final static int FOOTER_BASE_TYPE = 20000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();
    private RecyclerView.Adapter mInnerAdapter;
    private Context mContext;

    public HeaderFooterRecyclerViewAdapter(Context context, RecyclerView.Adapter innerAdapter) {
        mContext = context;
        mInnerAdapter = innerAdapter;
    }

    public void addHeaderView(View headerView) {
        mHeaderViews.put(mHeaderViews.size() + HEADER_BASE_TYPE, headerView);
    }

    public void addFooterView(View footerView) {
        mFooterViews.put(mFooterViews.size() + FOOTER_BASE_TYPE, footerView);
    }

    public boolean isHeaderPosition(int position) {
        return position < mHeaderViews.size();
    }

    public boolean isFooterPosition(int position) {
        return position >= mInnerAdapter.getItemCount() + mHeaderViews.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return new ComViewHolder(mContext, mHeaderViews.get(viewType), parent);
        } else if (mFooterViews.get(viewType) != null) {
            return new ComViewHolder(mContext, mFooterViews.get(viewType), parent);
        } else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.e("shanjie01", "position:" + position + "——" + getItemCount());
        if (isHeaderPosition(position) || isFooterPosition(position)) {
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position - mHeaderViews.size());
    }

    @Override
    public int getItemCount() {
        return mHeaderViews.size() + mInnerAdapter.getItemCount() + mFooterViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderViews.keyAt(position);
        }
        if (isFooterPosition(position)) {
            return mFooterViews.keyAt(position - mHeaderViews.size() - mInnerAdapter.getItemCount());
        }
        return mInnerAdapter.getItemViewType(position - mHeaderViews.size());
    }

    @Override
    public void onItemTop(int fromPosition) {
        if (isHeaderPosition(fromPosition) || isFooterPosition(fromPosition)) {
            return;
        }
        ((ItemTouchHelperAdapter) mInnerAdapter).onItemTop(fromPosition);
        notifyItemMoved(fromPosition, mHeaderViews.size());
    }

    @Override
    public void onItemDismiss(int position) {
        if (isHeaderPosition(position) || isFooterPosition(position))
            return;
        ((ItemTouchHelperAdapter) mInnerAdapter).onItemDismiss(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemSwap(int itemAPosition, int itemBPosition) {
        if (isHeaderPosition(itemAPosition) || isFooterPosition(itemBPosition) || isFooterPosition(itemAPosition) || isFooterPosition(itemBPosition))
            return;
        ((ItemTouchHelperAdapter) mInnerAdapter).onItemSwap(itemAPosition, itemBPosition);
        notifyItemMoved(itemAPosition, itemBPosition);
    }

}
