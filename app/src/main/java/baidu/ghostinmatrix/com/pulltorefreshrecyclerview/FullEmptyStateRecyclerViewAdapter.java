package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ghostinmatrix on 2018/1/12.
 */

public class FullEmptyStateRecyclerViewAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {
    private int emptyType = -10000;
    private RecyclerView.Adapter innerAdapter;
    private View mEmptyView;

    public FullEmptyStateRecyclerViewAdapter(RecyclerView.Adapter innerAdapter, View emptyView) {
        this.innerAdapter = innerAdapter;
        this.mEmptyView = emptyView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == emptyType) {
            ComViewHolder viewHolder = new ComViewHolder(parent.getContext(), mEmptyView, parent);
            viewHolder.setTag("emptyView");
            return viewHolder;
        } else
            return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ComViewHolder viewHolder = (ComViewHolder) holder;
        if (!"emptyView".equals(viewHolder.getTag())) {
            innerAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return innerAdapter.getItemCount() > 0 ? innerAdapter.getItemCount() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (innerAdapter.getItemCount() == 0) {
            return emptyType;
        } else {
            return 0;
        }
    }

    @Override
    public void onItemTop(int fromPosition) {
        //TODO:
    }

    @Override
    public void onItemDismiss(int position) {
        ((ItemTouchHelperAdapter) innerAdapter).onItemDismiss(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemSwap(int itemAPosition, int itemBPosition) {
        //TODO:
    }
}
