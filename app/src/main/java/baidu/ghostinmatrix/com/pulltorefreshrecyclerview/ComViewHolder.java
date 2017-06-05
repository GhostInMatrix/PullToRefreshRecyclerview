package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shanjie on 2017/5/10.
 */
public class ComViewHolder extends RecyclerView.ViewHolder {
    private SparseArrayCompat<View> mViews;
    private View mConvertView;

    public ComViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArrayCompat<>();
    }

    public static ComViewHolder getComViewHolder(Context context, int layoutId, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ComViewHolder(context, itemView, parent);
    }

    /**
     * 缓存+提取
     * @param layoutId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int layoutId) {
        View view = mViews.get(layoutId);
        if (view == null) {
            view = mConvertView.findViewById(layoutId);
            mViews.put(layoutId, view);
        }
        return (T) view;
    }
}
