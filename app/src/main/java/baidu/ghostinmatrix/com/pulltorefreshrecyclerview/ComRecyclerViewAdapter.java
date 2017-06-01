package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    protected LayoutInflater mLayoutInflater;

    public ComRecyclerViewAdapter(Context context, int layoutId, List<E> datas) {
        this(context, layoutId);
        if (datas == null)
            mGroup = new ArrayList<>();
        else
            mGroup = datas;
    }

    public ComRecyclerViewAdapter(Context context, int layoutId) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mLayoutId = layoutId;
        mGroup = new ArrayList<>();
    }

    public void setGroup(List<E> group) {
        mGroup = group;
        notifyDataSetChanged();
    }

    @Override
    public void onItemTop(int fromPosition) {
        E data = mGroup.get(fromPosition);
        for (int i = fromPosition; i > 0; i--) {
            mGroup.set(i, mGroup.get(i - 1));
        }
        mGroup.set(0, data);
        notifyItemMoved(fromPosition, 0);
    }


    @Override
    public void onItemSwap(int itemAPosition, int itemBPosition) {
        Collections.swap(mGroup, itemAPosition, itemBPosition);
        notifyItemMoved(itemAPosition, itemBPosition);
    }

    @Override
    public long getItemId(int position) {
        return getItemIdFromData(mGroup.get(position));
    }

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

    @Override
    public int getItemCount() {
        return mGroup.size();
    }

    public long getItemIdFromData(E data) {
        return RecyclerView.NO_ID;
    }

    public abstract void convert(ComViewHolder viewHolder, E data, int type, int position);

}
