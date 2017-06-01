package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by shanjie on 2017/3/5.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperAdapter mAdapter;
    private boolean mCanDrag;
    private boolean mCanSwipe;
    private OnSelectEndListener mOnSelectEndListener;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, boolean canDrag, boolean canSwipe) {
        mAdapter = adapter;
        mCanDrag = canDrag;
        mCanSwipe = canSwipe;
    }

    public void setOnSelectEndListener(OnSelectEndListener onSelectEndListener) {
        mOnSelectEndListener = onSelectEndListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onItemSwap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return mCanDrag;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mCanSwipe;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (viewHolder == null) {
            if (mOnSelectEndListener != null) {
                mOnSelectEndListener.onSelectEnd();
            }
        } else {
            Log.e("ss","start");
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    public interface OnSelectEndListener {
        public void onSelectEnd();
    }
}
