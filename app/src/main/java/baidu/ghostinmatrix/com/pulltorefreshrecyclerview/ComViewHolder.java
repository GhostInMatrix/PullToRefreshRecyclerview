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
    private Context mContext;
    private View mConvertView;
    private String tag;

    public ComViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArrayCompat<>();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public static ComViewHolder getComViewHolder(Context context, int layoutId, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ComViewHolder(context, itemView, parent);
    }

    public static ComViewHolder getComViewHolder(Context context, View view, ViewGroup parent) {

        return new ComViewHolder(context, view, parent);
    }

    public <T extends View> T getView(int layoutId) {
        View view = mViews.get(layoutId);
        if (view == null) {
            view = mConvertView.findViewById(layoutId);
            mViews.put(layoutId, view);
        }
        return (T) view;
    }

    public View getmConvertView() {
        return mConvertView;
    }

}
