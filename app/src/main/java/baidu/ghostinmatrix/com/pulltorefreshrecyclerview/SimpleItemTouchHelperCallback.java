package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by shanjie on 2017/3/5.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperAdapter mAdapter; //一个实现了ItemTouchHelperAdapter接口协议的任意Adapter

    //控制两种手势的开关
    private boolean mCanDrag;
    private boolean mCanSwipe;

    //手势松开释放的监听
    private OnSelectEndListener mOnSelectEndListener;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, boolean canDrag, boolean canSwipe) {
        mAdapter = adapter;
        mCanDrag = canDrag;
        mCanSwipe = canSwipe;
    }

    public void setOnSelectEndListener(OnSelectEndListener onSelectEndListener) {
        mOnSelectEndListener = onSelectEndListener;
    }


    //默认支持竖直方向上下,水平方向左右动作
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    //用于两个item交换位置
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onItemSwap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    //用于横扫某个item,可根据direction自行定制,这里未区分
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    //控制能否长按拖动
    @Override
    public boolean isLongPressDragEnabled() {
        return mCanDrag;
    }

    //控制能否横扫
    @Override
    public boolean isItemViewSwipeEnabled() {
        return mCanSwipe;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }


    //判断手势是否放开对应item的ViewHolder,一般用于拖动情况中
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
        void onSelectEnd();
    }
}
