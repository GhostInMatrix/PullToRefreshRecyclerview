package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shanjie on 2017/3/29.
 */
public abstract class ComRecyclerViewAdapter<E> extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    protected List<E> mGroup;
    protected Context mContext;
    protected int mLayoutId;

    public ComRecyclerViewAdapter(Context context, int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
        mGroup = new ArrayList<>();
    }

    public ComRecyclerViewAdapter(Context context, int layoutId, List<E> datas) {
        this(context, layoutId);
        if (datas == null)
            mGroup = new ArrayList<>();
        else
            mGroup = datas;
    }

    public void setGroup(List<E> group) {
        mGroup = group;
        notifyDataSetChanged();
    }

    /**
     * 置顶
     * @param fromPosition
     */
    @Override
    public void onItemTop(int fromPosition) {
        E data = mGroup.get(fromPosition);
        for (int i = fromPosition; i > 0; i--) {
            mGroup.set(i, mGroup.get(i - 1));
        }
        mGroup.set(0, data);
        notifyItemMoved(fromPosition, 0);
    }


    /**
     * 拖动交换
     * @param itemAPosition
     * @param itemBPosition
     */
    @Override
    public void onItemSwap(int itemAPosition, int itemBPosition) {
        Collections.swap(mGroup, itemAPosition, itemBPosition);
        notifyItemMoved(itemAPosition, itemBPosition);
    }

    /**
     * 删除
     * @param position
     */
    @Override
    public void onItemDismiss(int position) {
        mGroup.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ComViewHolder.getComViewHolder(mContext, mLayoutId, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        convert((ComViewHolder) holder, mGroup.get(position), getItemViewType(position), position);
    }

    public abstract void convert(ComViewHolder viewHolder, E data, int type, int position);


    @Override
    public int getItemCount() {
        return mGroup.size();
    }

    @Override
    public long getItemId(int position) {
        return getItemIdFromData(mGroup.get(position));
    }

    public long getItemIdFromData(E data) {
        return RecyclerView.NO_ID;
    }

}
