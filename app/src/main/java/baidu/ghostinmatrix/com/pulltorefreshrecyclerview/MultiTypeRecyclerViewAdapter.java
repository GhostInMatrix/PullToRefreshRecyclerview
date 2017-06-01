package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by shanjie on 2017/5/12.
 */
public abstract class MultiTypeRecyclerViewAdapter<E> extends ComRecyclerViewAdapter {

    private MultiTypeSupport mMultiTypeSupport;

    public MultiTypeRecyclerViewAdapter(Context context, MultiTypeSupport multiTypeSupport) {
        super(context, -1);
        mMultiTypeSupport = multiTypeSupport;
    }

    public MultiTypeRecyclerViewAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    public MultiTypeRecyclerViewAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mMultiTypeSupport.getLayoutId(viewType);
        return ComViewHolder.getComViewHolder(mContext, layoutId, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
    }

    @Override
    public int getItemViewType(int position) {
        return mMultiTypeSupport.getItemViewType(position, mGroup.get(position));
    }
}
